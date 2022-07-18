package com.techbulls.commons.securelog.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.SerializerFactory;

public class SecureLogUtils {
    public static String safeToString(Object bean) throws JsonProcessingException {
        SecureLogBeanSerializerModifier searlizerModifier = new SecureLogBeanSerializerModifier();
        SerializerFactory serializerFactory = BeanSerializerFactory.instance.withSerializerModifier(searlizerModifier);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializerFactory(serializerFactory);
        return mapper.writeValueAsString(bean);
    }
}
