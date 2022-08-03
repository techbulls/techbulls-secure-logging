package com.techbulls.commons.securelog.serialization;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.techbulls.commons.securelog.annotation.LogSensitive;

import java.util.List;
/**
 * <h3>SecureLogBeanSerializerModifier Class</h3>
 * The class extends BeanSerializerModifier  to provide custom Serializer to BeanPropertyWriter
 * based on annotations
 * <p>
 * @see com.techbulls.commons.securelog.ValueFormatter
 * @see com.fasterxml.jackson.databind.JsonSerializer
 * @see com.fasterxml.jackson.databind.ser.BeanSerializerModifier
 * @see com.techbulls.commons.securelog.annotation.LogSensitive
 * @see com.techbulls.commons.securelog.annotation.SecureLog
 * @see com.techbulls.commons.securelog.serialization.SecureLogBeanSerializerModifier
 * @see com.techbulls.commons.securelog.serialization.NullSecurePropertySerializer
 * @author Kalpak Gadre
 * @version 1.0.0
 * @since  01 August 2022
 * */
public class SecureLogBeanSerializerModifier extends BeanSerializerModifier {

    /**
     * This method is overridden to assign secure serializers to BeanPropertyWriter
     * @see com.techbulls.commons.securelog.serialization.SecureLogBeanSerializerModifier
     * @see com.techbulls.commons.securelog.serialization.NullSecurePropertySerializer
     * */
    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        for (BeanPropertyWriter writer : beanProperties) {
            LogSensitive annotation = writer.getAnnotation(LogSensitive.class);
            if (annotation != null) {
                JsonSerializer<Object> delegate = writer.getSerializer();
                SecurePropertySerializer<Object> serializer = new SecurePropertySerializer<Object>(delegate, annotation);

                if (annotation.secureNullValues()) {
                    writer.assignNullSerializer(new NullSecurePropertySerializer(annotation));
                }

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
