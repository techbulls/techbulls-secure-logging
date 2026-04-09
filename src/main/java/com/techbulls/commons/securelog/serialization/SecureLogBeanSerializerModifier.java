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

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.techbulls.commons.securelog.annotation.LogSensitive;

import java.util.List;

/**
 * <h2>SecureLogBeanSerializerModifier</h2>
 * A Jackson {@link BeanSerializerModifier} that intercepts the bean serialization pipeline to detect
 * fields annotated with {@link LogSensitive} and wrap their serializers with masking behavior.
 * <p>
 * During serialization setup, Jackson invokes this modifier for each bean type. For every
 * {@link com.fasterxml.jackson.databind.ser.BeanPropertyWriter} that carries a {@link LogSensitive}
 * annotation, this modifier:
 * <ul>
 *   <li>Wraps the property's existing serializer with a {@link SecurePropertySerializer} that
 *       replaces the actual value with the configured mask during serialization.</li>
 *   <li>If {@link LogSensitive#secureNullValues()} is {@code true}, additionally assigns a
 *       {@link NullSecurePropertySerializer} to handle {@code null} values for that property.</li>
 * </ul>
 * <p>
 * This modifier is registered with the {@link com.fasterxml.jackson.databind.ObjectMapper} by
 * {@link SecureJson} as part of a custom {@link com.fasterxml.jackson.databind.ser.SerializerFactory}.
 *
 * @see SecurePropertySerializer
 * @see NullSecurePropertySerializer
 * @see SecureJson
 * @see LogSensitive
 * @version 0.1
 * @since 0.1
 */
final class SecureLogBeanSerializerModifier extends BeanSerializerModifier {

    /** Creates a new {@code SecureLogBeanSerializerModifier} instance. */
    SecureLogBeanSerializerModifier() {
    }

    /**
     * Inspects each bean property for the {@link LogSensitive} annotation and, if present,
     * replaces the property's serializer with a {@link SecurePropertySerializer} that masks the value.
     * <p>
     * For properties where {@link LogSensitive#secureNullValues()} is {@code true}, a
     * {@link NullSecurePropertySerializer} is also assigned to ensure that even {@code null} values
     * are written as the mask string rather than as JSON {@code null}.
     *
     * @param config         the active serialization configuration
     * @param beanDesc       the description of the bean type being serialized
     * @param beanProperties the list of property writers for the bean; each writer corresponds to
     *                       one serializable property of the bean
     * @return the (potentially modified) list of {@link BeanPropertyWriter} instances with secure
     *         serializers assigned where applicable
     */
    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        for (BeanPropertyWriter writer : beanProperties) {
            LogSensitive annotation = writer.getAnnotation(LogSensitive.class);
            if (annotation != null) {
                JsonSerializer<Object> delegate = writer.getSerializer();
                SecurePropertySerializer<Object> serializer = new SecurePropertySerializer<>(delegate, annotation);
                NullSecurePropertySerializer<Object> nullSerializer=new NullSecurePropertySerializer<>(annotation);
                if (annotation.secureNullValues()) {
                    writer.assignNullSerializer(nullSerializer);
                }

                writer.assignSerializer(serializer);
            }
        }
        return beanProperties;
    }

}
