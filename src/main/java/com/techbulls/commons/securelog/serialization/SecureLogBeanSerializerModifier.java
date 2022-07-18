package com.techbulls.commons.securelog.serialization;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.techbulls.commons.securelog.annotation.LogSensitive;

import java.lang.reflect.Field;
import java.util.List;

public class SecureLogBeanSerializerModifier extends BeanSerializerModifier {
    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        for (BeanPropertyWriter writer : beanProperties) {
            LogSensitive annotation = writer.getAnnotation(LogSensitive.class);

            if (annotation != null) {
                JsonSerializer<Object> delegate = writer.getSerializer();
                String secureValue = annotation.value();
                SecurePropertySerializer<Object> serializer = new SecurePropertySerializer<Object>(delegate, secureValue);
                writer.assignSerializer(serializer);
            }
        }

        return beanProperties;
    }

    @Override
    public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
        return super.modifySerializer(config, beanDesc, serializer);
    }
}
