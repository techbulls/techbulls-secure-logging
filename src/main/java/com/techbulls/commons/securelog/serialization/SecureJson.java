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
/**
 * <h3>SecureJson Class</h3>
 * This class uses ObjectMapper to write serialized values as per annotations
 * <p>
 * @see com.techbulls.commons.securelog.ValueFormatter
 * @see com.fasterxml.jackson.databind.JsonSerializer
 * @see com.techbulls.commons.securelog.annotation.LogSensitive
 * @see com.techbulls.commons.securelog.annotation.SecureLog
 * @see com.techbulls.commons.securelog.serialization.SecureLogBeanSerializerModifier
 * @see com.techbulls.commons.securelog.serialization.NullSecurePropertySerializer
 * @author Kalpak Gadre
 * @version 1.0.0
 * @since  01 August 2022
 * */

public class SecureJson {

    private static ObjectMapper mapper;

    private static Object mutex = new Object();

    /**
     * Initializing object mapper
     * */
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


    /**This method will set the SerializerFactory configured with SecureLogBeanSerializerModifier to the object mapper
     * */
    private static ObjectMapper initMapper(ObjectMapper m) {
        SecureLogBeanSerializerModifier serializerModifier = new SecureLogBeanSerializerModifier();
        SerializerFactory serializerFactory = BeanSerializerFactory.instance.withSerializerModifier(serializerModifier);
        m.setSerializerFactory(serializerFactory);
        return m;
    }


    /**This method returns the writer object based on prettyPrint property
     * @param mapper <b>ObjectMapper</b>
     * @param prettyPrint <b>boolean</b> denoting weather prettyPrint writer is to be returned
     * @return ObjectWriter
     * */
    private static ObjectWriter objectWriter(ObjectMapper mapper, boolean prettyPrint) {
        return prettyPrint ? mapper.writerWithDefaultPrettyPrinter() : mapper().writer();
    }

    /**This method accept the bean objects are return the json string with masked values
     * @param bean <b>Object</b> that needs to converted to secure JSON string
     * @return String
     * */
    public static String toJson(Object bean) throws JsonProcessingException {
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

    /**This method accept the bean objects, prettyPrint property and Class view
     * and return the secure string as per
     * @param bean <b>Object</b> that needs to converted to secure JSON string
     * @return String
     * */
    public static String toJson(Object bean, boolean prettyPrint, Class<?> view) throws JsonProcessingException {
        return toJson(mapper(), bean, prettyPrint, view);
    }

    /**This method accepts the bean object, prettyPrint property and Class view
     * and return the secure string
     * @param bean <b>Object</b> that needs to converted to secure JSON string
     * @param prettyPrint <b>boolean</b> that needs to converted to secure JSON string
     * @return String
     * */
    public static String toJson(ObjectMapper mapper, Object bean, boolean prettyPrint, Class<?> view){
        ObjectWriter writer = objectWriter(mapper, prettyPrint);
        if (view != SecureLog.Default.class) {
            writer = writer.withView(view);
        }
        try {
            return writer.writeValueAsString(bean);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    /**This method accepts the bean object, prettyPrint property and Class view
     * and return the secure string
     * @param cls <b>ValueFormatter</b> formatter that needs to be instantiated
     * @return ValueFormatter
     * */
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
