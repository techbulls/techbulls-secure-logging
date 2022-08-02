package com.techbulls.commons.securelog.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.techbulls.commons.securelog.ValueFormatter;
import com.techbulls.commons.securelog.annotation.LogSensitive;

import java.io.IOException;

public class NullSecurePropertySerializer<T> extends JsonSerializer<T> {

    private final ValueFormatter formatter;

    private final String secureValue;

    public NullSecurePropertySerializer(LogSensitive annotation) {
        this.formatter = SecureLogUtils.instantiate(annotation.formatter());
        this.secureValue = annotation.value();
    }

    @Override
    public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(formatter.format(o, secureValue));
    }
    @Override
    public void serializeWithType(T value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        gen.writeString(formatter.format(value, secureValue));
    }
}
