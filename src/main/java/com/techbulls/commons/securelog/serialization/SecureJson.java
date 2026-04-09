/*
 *    Copyright 2022 TechBulls SoftTech
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.techbulls.commons.securelog.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.techbulls.commons.securelog.ValueFormatter;
import com.techbulls.commons.securelog.annotation.SecureLog;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

/**
 * <h3>SecureJson Class</h3>
 * The public API entry point for converting Java objects to JSON strings with sensitive field
 * values masked. This class manages a shared {@link ObjectMapper} configured with the
 * {@link SecureLogBeanSerializerModifier} that detects {@link com.techbulls.commons.securelog.annotation.LogSensitive}
 * annotations and applies masking during serialization.
 * <p>
 * <b>Typical usage:</b>
 * <pre>{@code
 * // In your model class:
 * @SecureLog
 * public class User {
 *     private String name;
 *
 *     @LogSensitive
 *     private String ssn;
 *
 *     @Override
 *     public String toString() {
 *         return SecureJson.toJson(this);
 *     }
 * }
 *
 * // Output: {"name":"John","ssn":"XXXX"}
 * }</pre>
 * <p>
 * <b>Thread safety:</b> This class uses double-checked locking with a mutex to ensure that the
 * shared {@link ObjectMapper} is initialized exactly once in a thread-safe manner. Custom
 * {@link ObjectMapper} instances passed to {@link #toJson(ObjectMapper, Object, boolean, Class)}
 * are tracked in a set to avoid redundant initialization.
 * <p>
 * @see com.techbulls.commons.securelog.ValueFormatter
 * @see com.fasterxml.jackson.databind.JsonSerializer
 * @see com.techbulls.commons.securelog.annotation.LogSensitive
 * @see com.techbulls.commons.securelog.annotation.SecureLog
 * @see SecureLogBeanSerializerModifier
 * @see NullSecurePropertySerializer
 * @version 0.1
 * @since 0.1
 */

public class SecureJson {

    /** The shared, lazily initialized {@link ObjectMapper} configured with secure serialization. */
    private static ObjectMapper mapper;

    /**
     * Tracks {@link ObjectMapper} instances that have already been configured with the
     * {@link SecureLogBeanSerializerModifier}, preventing redundant initialization when
     * the same mapper is passed to {@link #toJson(ObjectMapper, Object, boolean, Class)} multiple times.
     */
    private static final Set<ObjectMapper> MAPPERS_ALREADY_INITIALIZED = new HashSet<>();

    /** Mutex object used for double-checked locking during lazy initialization of {@link #mapper}. */
    private static Object mutex = new Object();

    /**
     * Returns the shared {@link ObjectMapper} instance, initializing it lazily on first access.
     * <p>
     * Uses double-checked locking to ensure thread-safe initialization: the mapper is checked
     * outside the synchronized block for performance, and re-checked inside to prevent duplicate
     * initialization by concurrent threads.
     *
     * @return the configured {@link ObjectMapper} with secure serialization support
     */
    private static ObjectMapper mapper() {
        if (mapper == null) {
            synchronized (mutex) {
                // Double check after acquiring mutex that the mapper was not initialized.
                if (mapper == null) {
                    mapper = initMapper(new ObjectMapper());
                }
            }
        }

        return mapper;
    }


    /**
     * Configures the given {@link ObjectMapper} with a {@link com.fasterxml.jackson.databind.ser.SerializerFactory}
     * that includes the {@link SecureLogBeanSerializerModifier} for detecting and masking sensitive fields.
     * <p>
     * If the mapper has already been initialized (tracked via {@link #MAPPERS_ALREADY_INITIALIZED}),
     * this method is a no-op, ensuring that the modifier is not applied more than once to the same mapper.
     *
     * @param m the {@link ObjectMapper} to configure with secure serialization support
     * @return the same {@link ObjectMapper} instance, now configured (or already configured)
     */
    private static ObjectMapper initMapper(ObjectMapper m) {
        if (!MAPPERS_ALREADY_INITIALIZED.contains(m)) {
            SecureLogBeanSerializerModifier serializerModifier = new SecureLogBeanSerializerModifier();
            SerializerFactory serializerFactory = BeanSerializerFactory.instance.withSerializerModifier(serializerModifier);
            m.setSerializerFactory(serializerFactory);
            MAPPERS_ALREADY_INITIALIZED.add(m);
        }
        return m;
    }


    /**
     * Creates an {@link ObjectWriter} from the given mapper, optionally configured for pretty-printed output.
     *
     * @param mapper      the {@link ObjectMapper} to create the writer from
     * @param prettyPrint {@code true} to produce indented, human-readable JSON output;
     *                    {@code false} for compact single-line output
     * @return an {@link ObjectWriter} configured according to the pretty-print preference
     */
    private static ObjectWriter objectWriter(ObjectMapper mapper, boolean prettyPrint) {
        return prettyPrint ? mapper.writerWithDefaultPrettyPrinter() : mapper.writer();
    }

    /**
     * Converts the given bean to a JSON string with sensitive fields masked according to their
     * {@link com.techbulls.commons.securelog.annotation.LogSensitive} annotations.
     * <p>
     * If the bean's class is annotated with {@link SecureLog}, the pretty-print and
     * {@link com.fasterxml.jackson.annotation.JsonView} settings are read from that annotation.
     * Otherwise, compact output with no specific view is used.
     *
     * @param bean the object to serialize to a masked JSON string
     * @return the JSON string representation with sensitive fields replaced by their mask values
     * @throws RuntimeException if Jackson encounters a {@link com.fasterxml.jackson.core.JsonProcessingException}
     *                          during serialization
     */
    public static String toJson(Object bean){
        Class<?> cls = bean.getClass();
        SecureLog annotation = cls.getAnnotation(SecureLog.class);
        Class<?> view =null;
        boolean pretty =false;
        if(annotation!=null) {
            view = annotation.view();
            pretty = annotation.pretty();
        }
        return toJson(bean, pretty, view);
    }

    /**
     * Converts the given bean to a JSON string with sensitive fields masked, using the specified
     * pretty-print and {@link com.fasterxml.jackson.annotation.JsonView} settings.
     * <p>
     * This overload uses the shared internal {@link ObjectMapper}. To supply a custom mapper
     * (e.g., one configured with {@code @JsonFilter}), use
     * {@link #toJson(ObjectMapper, Object, boolean, Class)}.
     *
     * @param bean        the object to serialize to a masked JSON string
     * @param prettyPrint {@code true} for indented output; {@code false} for compact output
     * @param view        the {@link com.fasterxml.jackson.annotation.JsonView} class to apply during
     *                    serialization, or {@link SecureLog.Default} to serialize all fields
     * @return the JSON string representation with sensitive fields replaced by their mask values
     * @throws RuntimeException if Jackson encounters a {@link com.fasterxml.jackson.core.JsonProcessingException}
     *                          during serialization
     */
    public static String toJson(Object bean, boolean prettyPrint, Class<?> view){
        return toJson(mapper(), bean, prettyPrint, view);
    }

    /**
     * Converts the given bean to a JSON string with sensitive fields masked, using a custom
     * {@link ObjectMapper} along with the specified pretty-print and
     * {@link com.fasterxml.jackson.annotation.JsonView} settings.
     * <p>
     * The provided mapper is automatically initialized with the {@link SecureLogBeanSerializerModifier}
     * on first use (and tracked to avoid redundant initialization on subsequent calls). This allows
     * callers to supply mappers configured with custom features such as {@code @JsonFilter} or
     * custom serialization modules while still benefiting from secure masking.
     * <p>
     * If the {@code view} parameter is set to {@link SecureLog.Default}, no specific
     * {@link com.fasterxml.jackson.annotation.JsonView} is applied and all fields are serialized.
     *
     * @param mapper      the {@link ObjectMapper} to use for serialization; will be configured
     *                    with secure masking support if not already initialized
     * @param bean        the object to serialize to a masked JSON string
     * @param prettyPrint {@code true} for indented output; {@code false} for compact output
     * @param view        the {@link com.fasterxml.jackson.annotation.JsonView} class to apply, or
     *                    {@link SecureLog.Default} to serialize without view filtering
     * @return the JSON string representation with sensitive fields replaced by their mask values
     * @throws RuntimeException if Jackson encounters a {@link com.fasterxml.jackson.core.JsonProcessingException}
     *                          during serialization
     */
    public static String toJson(ObjectMapper mapper, Object bean, boolean prettyPrint, Class<?> view){
        ObjectWriter writer = objectWriter(initMapper(mapper), prettyPrint);
        if (view != SecureLog.Default.class) {
            writer = writer.withView(view);
        }
        try {
            return writer.writeValueAsString(bean);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Reflectively instantiates a {@link ValueFormatter} implementation using its no-argument constructor.
     * <p>
     * The constructor is made accessible via {@link Constructor#setAccessible(boolean)} to support
     * formatters with non-public constructors. An {@link IllegalStateException} is thrown if the
     * class does not have a no-arg constructor or if instantiation fails.
     *
     * @param cls the {@link ValueFormatter} implementation class to instantiate
     * @return a new instance of the specified formatter class
     * @throws IllegalStateException if the class has no no-arg constructor, or if reflective
     *                               instantiation fails due to access, invocation, or instantiation errors
     */
    static ValueFormatter instantiate(Class<? extends ValueFormatter> cls) {
        try {
            Constructor<? extends ValueFormatter> constructor = cls.getDeclaredConstructor();
            constructor.setAccessible(true);
            if (constructor.getParameterCount() > 0) {
                throw new IllegalStateException(cls.getName() + " should have a null-arg constructor");
            }
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Unable to find any constructor for class " + cls.getName());
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Unable to instantiate object of class " + cls.getName() + " reflectively", e);
        }
    }
}
