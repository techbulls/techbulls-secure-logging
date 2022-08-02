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

public class SecureJson {
    private static ObjectMapper mapper;

    private static Object mutex = new Object();

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


    private static ObjectMapper initMapper(ObjectMapper m) {
        SecureLogBeanSerializerModifier serializerModifier = new SecureLogBeanSerializerModifier();
        SerializerFactory serializerFactory = BeanSerializerFactory.instance.withSerializerModifier(serializerModifier);
        m.setSerializerFactory(serializerFactory);
        return m;
    }

    private static ObjectWriter objectWriter(ObjectMapper mapper, boolean prettyPrint) {
        return prettyPrint ? mapper.writerWithDefaultPrettyPrinter() : mapper().writer();
    }

    public static String toJson(Object bean) throws JsonProcessingException {
        Class<?> cls = bean.getClass();
        SecureLog annotation = cls.getAnnotation(SecureLog.class);
        Class<?> view =null;
        boolean pretty =false;
        if(annotation!=null) {
            view = annotation.view();
            pretty = annotation != null && annotation.pretty();
        }
        return toJson(bean, pretty, view);
    }

    public static String toJson(Object bean, boolean prettyPrint, Class<?> view) throws JsonProcessingException {
        return toJson(mapper(), bean, prettyPrint, view);
    }

    public static String toJson(ObjectMapper mapper, Object bean, boolean prettyPrint, Class<?> view) throws JsonProcessingException {
        ObjectWriter writer = objectWriter(mapper, prettyPrint);
        if (view != SecureLog.Default.class) {
            writer = writer.withView(view);
        }
        return writer.writeValueAsString(bean);
    }

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
