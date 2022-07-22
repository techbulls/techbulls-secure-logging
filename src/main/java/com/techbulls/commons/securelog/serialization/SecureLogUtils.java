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

public class SecureLogUtils {
    private static final ThreadLocal<ObjectMapper> TL = ThreadLocal.withInitial(() -> {
        SecureLogBeanSerializerModifier serializerModifier = new SecureLogBeanSerializerModifier();
        SerializerFactory serializerFactory = BeanSerializerFactory.instance.withSerializerModifier(serializerModifier);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializerFactory(serializerFactory);
        return mapper;
    });

    public static String safeToString(Object bean) throws JsonProcessingException {
        Class<?> cls = bean.getClass();
        SecureLog annotation = cls.getAnnotation(SecureLog.class);
        Class<?> view = annotation.view();
        boolean pretty = annotation != null && annotation.pretty();
        return safeToString(bean, pretty, view);
    }

    public static String safeToString(Object bean, boolean prettyPrint, Class<?> view) throws JsonProcessingException {
        ObjectWriter writer = prettyPrint? TL.get().writerWithDefaultPrettyPrinter() : TL.get().writer();
        if (view != SecureLog.Default.class) {
            writer = writer.withView(view);
        }
        return writer.writeValueAsString(bean);
    }

    static ValueFormatter instantiate(Class<? extends ValueFormatter> cls) {
        try {
            Constructor<? extends ValueFormatter> constructor = cls.getDeclaredConstructor();
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
