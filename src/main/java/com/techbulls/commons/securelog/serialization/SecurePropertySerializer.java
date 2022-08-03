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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.techbulls.commons.securelog.ValueFormatter;
import com.techbulls.commons.securelog.annotation.LogSensitive;

import java.io.IOException;
import java.util.Iterator;

/**
 * <h3>SecurePropertySerializer Class</h3>
 * This class extends JsonSerializer to provide custom serialization of object
 * <p>
 * @see com.techbulls.commons.securelog.ValueFormatter
 * @see com.fasterxml.jackson.databind.JsonSerializer
 * @see com.techbulls.commons.securelog.annotation.LogSensitive
 * @author Kalpak Gadre
 * @version 1.0.0
 * @since  01 August 2022
 * */

public class SecurePropertySerializer<T> extends JsonSerializer<T> {
    private final JsonSerializer<T> delegate;
    /**The Value formatter used for formatting
     * */
    private final ValueFormatter formatter;
    /**The Secure value based on annotation
     * */
    private final String secureValue;

    /**
     * The constructor to set up initial properties to the serializer
     * @param delegate <b>JsonSerializer</b>
     * @param annotation <b>LogSensitive</b>
     *                  The annotation the provides with the secure value and formatter
     * */

    public SecurePropertySerializer(JsonSerializer<T> delegate, LogSensitive annotation) {
        this.delegate = delegate;
        this.formatter = SecureJson.instantiate(annotation.formatter());
        this.secureValue = annotation.value();
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

    /**
     * To serialize the value based on formatter and secure value
    */
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
       gen.writeString(formatter.format(value, secureValue));
    }

    @Override
    public void serializeWithType(T value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        gen.writeString(formatter.format(value, secureValue));
    }
}
