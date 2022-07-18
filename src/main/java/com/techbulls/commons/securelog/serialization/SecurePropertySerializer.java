package com.techbulls.commons.securelog.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.util.NameTransformer;

import java.io.IOException;
import java.util.Iterator;

public class SecurePropertySerializer<T> extends JsonSerializer<T> {
    private final JsonSerializer<T> delegate;
    private final String secureValue;

    public SecurePropertySerializer(JsonSerializer<T> delegate, String secureValue) {
        this.delegate = delegate;
        this.secureValue = secureValue;
    }

    @Override
    public JsonSerializer<T> unwrappingSerializer(NameTransformer unwrapper) {
        return delegate.unwrappingSerializer(unwrapper);
    }

    @Override
    public JsonSerializer<T> replaceDelegatee(JsonSerializer<?> delegatee) {
        return delegate.replaceDelegatee(delegatee);
    }

    @Override
    public JsonSerializer<?> withFilterId(Object filterId) {
        return delegate.withFilterId(filterId);
    }

    @Override
    public void serializeWithType(T value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        gen.writeString(secureValue);
    }

    @Override
    public Class<T> handledType() {
        return delegate.handledType();
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, T value) {
        return delegate.isEmpty(provider, value);
    }

    @Override
    public boolean usesObjectId() {
        return delegate.usesObjectId();
    }

    @Override
    public boolean isUnwrappingSerializer() {
        return delegate.isUnwrappingSerializer();
    }

    @Override
    public JsonSerializer<?> getDelegatee() {
        return delegate.getDelegatee();
    }

    @Override
    public Iterator<PropertyWriter> properties() {
        return delegate.properties();
    }

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType type) throws JsonMappingException {
        delegate.acceptJsonFormatVisitor(visitor, type);
    }

    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(secureValue);
    }
}
