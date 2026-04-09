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

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.techbulls.commons.securelog.annotation.LogSensitive;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * <h2>SecureLogBeanSerializerModifier</h2>
 * A Jackson {@link BeanSerializerModifier} that intercepts the bean serialization pipeline to detect
 * fields annotated with {@link LogSensitive} and wrap their serializers with masking behavior.
 * <p>
 * During serialization setup, Jackson invokes this modifier for each bean type. For every
 * {@link com.fasterxml.jackson.databind.ser.BeanPropertyWriter} that carries a {@link LogSensitive}
 * annotation, this modifier:
 * <ul>
 *   <li>Wraps the property's existing serializer with a {@link SecurePropertySerializer} that
 *       replaces the actual value with the configured mask during serialization.</li>
 *   <li>If {@link LogSensitive#secureNullValues()} is {@code true}, additionally assigns a
 *       {@link NullSecurePropertySerializer} to handle {@code null} values for that property.</li>
 * </ul>
 * <p>
 * This modifier is registered with the {@link com.fasterxml.jackson.databind.ObjectMapper} by
 * {@link SecureJson} as part of a custom {@link com.fasterxml.jackson.databind.ser.SerializerFactory}.
 *
 * @see SecurePropertySerializer
 * @see NullSecurePropertySerializer
 * @see SecureJson
 * @see LogSensitive
 * @version 0.1
 * @since 0.1
 */
final class SecureLogBeanSerializerModifier extends BeanSerializerModifier {

    /** Creates a new {@code SecureLogBeanSerializerModifier} instance. */
    SecureLogBeanSerializerModifier() {
    }

    /**
     * Inspects each bean property for the {@link LogSensitive} annotation and, if present,
     * replaces the property's serializer with a {@link SecurePropertySerializer} that masks the value.
     * <p>
     * For properties where {@link LogSensitive#secureNullValues()} is {@code true}, a
     * {@link NullSecurePropertySerializer} is also assigned to ensure that even {@code null} values
     * are written as the mask string rather than as JSON {@code null}.
     *
     * @param config         the active serialization configuration
     * @param beanDesc       the description of the bean type being serialized
     * @param beanProperties the list of property writers for the bean; each writer corresponds to
     *                       one serializable property of the bean
     * @return the (potentially modified) list of {@link BeanPropertyWriter} instances with secure
     *         serializers assigned where applicable
     */
    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        for (BeanPropertyWriter writer : beanProperties) {
            LogSensitive annotation = findLogSensitive(writer);
            if (annotation != null) {
                JsonSerializer<Object> delegate = writer.getSerializer();
                SecurePropertySerializer<Object> serializer = new SecurePropertySerializer<>(delegate, annotation);
                NullSecurePropertySerializer<Object> nullSerializer=new NullSecurePropertySerializer<>(annotation);
                if (annotation.secureNullValues()) {
                    writer.assignNullSerializer(nullSerializer);
                }

                writer.assignSerializer(serializer);
            }
        }
        return beanProperties;
    }

    /**
     * Resolves the {@link LogSensitive} annotation for a bean property, supporting both direct
     * annotation and meta-annotation (an annotation that is itself annotated with {@link LogSensitive}).
     * <p>
     * Direct annotations take precedence over meta-annotations. Only a single level of
     * meta-annotation lookup is performed.
     *
     * @param writer the bean property writer to inspect
     * @return the resolved {@link LogSensitive} annotation, or {@code null} if not found
     */
    private static LogSensitive findLogSensitive(BeanPropertyWriter writer) {
        LogSensitive direct = writer.getAnnotation(LogSensitive.class);
        if (direct != null) {
            return direct;
        }

        AnnotatedElement element = writer.getMember().getAnnotated();
        LogSensitive meta = findMetaLogSensitive(element);
        if (meta != null) {
            return meta;
        }

        // If the member is a getter method, also check the corresponding field
        if (element instanceof Method) {
            try {
                Field field = ((Method) element).getDeclaringClass().getDeclaredField(writer.getName());
                return findMetaLogSensitive(field);
            } catch (NoSuchFieldException e) {
                // No matching field, property may be getter-only
            }
        }

        return null;
    }

    private static LogSensitive findMetaLogSensitive(AnnotatedElement element) {
        for (Annotation ann : element.getAnnotations()) {
            LogSensitive meta = ann.annotationType().getAnnotation(LogSensitive.class);
            if (meta != null) {
                return meta;
            }
        }
        return null;
    }

}
