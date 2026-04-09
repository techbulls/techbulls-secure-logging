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
 * <h2>SecurePropertySerializer</h2>
 * A Jackson {@link JsonSerializer} that wraps a delegate serializer and replaces the actual field
 * value with a masked string during JSON serialization. This is the core serializer responsible
 * for enforcing sensitive data masking on fields annotated with {@link LogSensitive}.
 * <p>
 * This serializer follows the <em>Decorator pattern</em>: it wraps the original (delegate)
 * serializer for a bean property and intercepts the {@link #serialize} and {@link #serializeWithType}
 * methods to write the masked value instead of the real one. All other serializer behavior
 * (unwrapping, filtering, type handling, format visitors, etc.) is delegated transparently to the
 * original serializer to preserve compatibility with Jackson features such as {@code @JsonFilter},
 * {@code @JsonUnwrapped}, and object identity.
 * <p>
 * Instances are created by {@link SecureLogBeanSerializerModifier#changeProperties} and assigned
 * to {@link com.fasterxml.jackson.databind.ser.BeanPropertyWriter} instances for annotated fields.
 *
 * <h3>Example</h3>
 * For a field annotated with {@code @LogSensitive("****")}, this serializer will write
 * {@code "****"} (or a custom-formatted value) regardless of the field's actual runtime value.
 *
 * @param <T> the type of the value being serialized
 * @see NullSecurePropertySerializer
 * @see SecureLogBeanSerializerModifier
 * @see LogSensitive
 * @see ValueFormatter
 * @version 0.1
 * @since 0.1
 */
final class SecurePropertySerializer<T> extends JsonSerializer<T> {

    /**
     * The original serializer for the property, to which non-serialization behavior is delegated.
     * May be {@code null} if Jackson has not yet resolved the serializer at modifier time.
     */
    private final JsonSerializer<T> delegate;

    /** The formatter used to produce the masked output from the original value and mask string. */
    private final ValueFormatter formatter;

    /** The mask value to substitute for the real value, as specified by {@link LogSensitive#value()}. */
    private final String secureValue;

    /**
     * Constructs a new {@code SecurePropertySerializer} wrapping the given delegate serializer
     * with masking behavior derived from the provided {@link LogSensitive} annotation.
     * <p>
     * The formatter class specified by {@link LogSensitive#formatter()} is instantiated reflectively
     * via {@link SecureJson#instantiate(Class)}, and the mask value is read from
     * {@link LogSensitive#value()}.
     *
     * @param delegate   the original Jackson serializer for the property; may be {@code null} if
     *                   the serializer has not yet been resolved by Jackson at the time the modifier runs
     * @param annotation the {@link LogSensitive} annotation present on the field being serialized
     */
    SecurePropertySerializer(JsonSerializer<T> delegate, LogSensitive annotation) {
        this.delegate = delegate;
        this.formatter = SecureJson.instantiate(annotation.formatter());
        this.secureValue = annotation.value();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to the wrapped serializer's unwrapping logic to maintain compatibility with
     * {@code @JsonUnwrapped} annotations.
     *
     * @param unwrapper the name transformer for unwrapped property naming
     * @return the unwrapping serializer from the delegate
     */
    @Override
    public JsonSerializer<T> unwrappingSerializer(NameTransformer unwrapper) {
        return delegate.unwrappingSerializer(unwrapper);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to the wrapped serializer to allow Jackson to replace the inner delegate.
     *
     * @param delegatee the new delegate serializer to assign
     * @return the result of the delegate's {@code replaceDelegatee} call
     */
    @Override
    public JsonSerializer<T> replaceDelegatee(JsonSerializer<?> delegatee) {
        return delegate.replaceDelegatee(delegatee);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to the wrapped serializer to maintain compatibility with {@code @JsonFilter}.
     *
     * @param filterId the filter identifier to associate with this serializer
     * @return the filtered serializer from the delegate
     */
    @Override
    public JsonSerializer<?> withFilterId(Object filterId) {
        return delegate.withFilterId(filterId);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to the wrapped serializer to report the correct handled type.
     *
     * @return the Java class handled by the delegate serializer
     */
    @Override
    public Class<T> handledType() {
        return delegate.handledType();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to the wrapped serializer to determine if the value should be considered empty.
     *
     * @param provider the serializer provider for context
     * @param value    the value to check for emptiness
     * @return {@code true} if the delegate considers the value empty
     */
    @Override
    public boolean isEmpty(SerializerProvider provider, T value) {
        return delegate.isEmpty(provider, value);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to the wrapped serializer to indicate whether object identity is used.
     *
     * @return {@code true} if the delegate uses object identity references
     */
    @Override
    public boolean usesObjectId() {
        return delegate.usesObjectId();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to the wrapped serializer to indicate whether this is an unwrapping serializer.
     *
     * @return {@code true} if the delegate is an unwrapping serializer
     */
    @Override
    public boolean isUnwrappingSerializer() {
        return delegate.isUnwrappingSerializer();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the original delegate serializer, allowing Jackson to traverse the serializer chain.
     *
     * @return the wrapped delegate serializer
     */
    @Override
    public JsonSerializer<?> getDelegatee() {
        return delegate.getDelegatee();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to the wrapped serializer to enumerate the property writers.
     *
     * @return an iterator over the delegate's property writers
     */
    @Override
    public Iterator<PropertyWriter> properties() {
        return delegate.properties();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to the wrapped serializer for JSON Schema and format visitor support.
     *
     * @param visitor the format visitor wrapper to accept
     * @param type    the Java type being visited
     * @throws JsonMappingException if the delegate encounters a mapping error
     */
    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType type) throws JsonMappingException {
        delegate.acceptJsonFormatVisitor(visitor, type);
    }

    /**
     * Serializes the field value by writing the formatted mask string instead of the actual value.
     * <p>
     * This is the primary masking point: regardless of what {@code value} contains, the output
     * will be the result of {@link ValueFormatter#format(Object, String)} applied to the original
     * value and the configured mask string. For the default formatter, this simply writes the mask
     * (e.g., {@code "XXXX"}); custom formatters may produce derived values (e.g., partial masking).
     *
     * @param value       the actual field value (not written to output; passed to the formatter for
     *                    potential use in custom formatting logic)
     * @param gen         the Jackson {@link JsonGenerator} used to write JSON output
     * @param serializers the provider for accessing serialization configuration
     * @throws IOException if an I/O error occurs during JSON generation
     */
    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
       gen.writeString(formatter.format(value, secureValue));
    }

    /**
     * Serializes the field value with type information by writing the formatted mask string.
     * <p>
     * Behaves identically to {@link #serialize(Object, JsonGenerator, SerializerProvider)} — the
     * type serializer parameter is intentionally ignored because the masked output is always a
     * plain string value, regardless of the original type or polymorphic type metadata.
     *
     * @param value       the actual field value (passed to the formatter, not written directly)
     * @param gen         the Jackson {@link JsonGenerator} used to write JSON output
     * @param serializers the provider for accessing serialization configuration
     * @param typeSer     the type serializer for polymorphic type handling (ignored for masked output)
     * @throws IOException if an I/O error occurs during JSON generation
     */
    @Override
    public void serializeWithType(T value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        gen.writeString(formatter.format(value, secureValue));
    }
}
