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
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.techbulls.commons.securelog.ValueFormatter;
import com.techbulls.commons.securelog.annotation.LogSensitive;

import java.io.IOException;


/**
 * <h3>NullSecurePropertySerializer</h3>
 * A Jackson {@link JsonSerializer} responsible for serializing {@code null} values of fields
 * annotated with {@link LogSensitive} when {@link LogSensitive#secureNullValues()} is set to {@code true}.
 * <p>
 * When a sensitive field contains a {@code null} value, this serializer replaces it with the
 * configured mask value (e.g., {@code "XXXX"}) instead of writing a JSON {@code null} literal.
 * This prevents information leakage through the presence or absence of values in log output.
 * <p>
 * This serializer is assigned to bean properties by {@link SecureLogBeanSerializerModifier} via
 * {@link com.fasterxml.jackson.databind.ser.BeanPropertyWriter#assignNullSerializer(JsonSerializer)}.
 *
 * @param <T> the type of the value being serialized (typically {@code Object} since this handles null cases)
 * @see SecurePropertySerializer
 * @see SecureLogBeanSerializerModifier
 * @see LogSensitive#secureNullValues()
 * @version 0.1
 * @since 0.1
 */
public class NullSecurePropertySerializer<T> extends JsonSerializer<T> {

    /** The formatter used to produce the masked output string from the original value and mask. */
    private final ValueFormatter formatter;

    /** The mask value to substitute for null fields, as specified by {@link LogSensitive#value()}. */
    private final String secureValue;

    /**
     * Constructs a new {@code NullSecurePropertySerializer} using configuration from the given
     * {@link LogSensitive} annotation.
     * <p>
     * The formatter class specified by {@link LogSensitive#formatter()} is instantiated reflectively
     * via {@link SecureJson#instantiate(Class)}, and the mask value is read from
     * {@link LogSensitive#value()}.
     *
     * @param annotation the {@link LogSensitive} annotation present on the field being serialized
     */
    public NullSecurePropertySerializer(LogSensitive annotation) {
        this.formatter = SecureJson.instantiate(annotation.formatter());
        this.secureValue = annotation.value();
    }

    /**
     * Serializes a null field value by writing the formatted mask string instead of a JSON {@code null}.
     * <p>
     * The actual value passed to this method is typically {@code null} (since this serializer is
     * specifically assigned as a null serializer). The {@link ValueFormatter#format(Object, String)}
     * method is invoked with the original value and the configured mask, and the resulting string
     * is written to the JSON output.
     *
     * @param o                  the original field value (typically {@code null})
     * @param jsonGenerator      the Jackson {@link JsonGenerator} used to write JSON output
     * @param serializerProvider the provider for accessing serialization configuration
     * @throws IOException if an I/O error occurs during JSON generation
     */
    @Override
    public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(formatter.format(o, secureValue));
    }

}
