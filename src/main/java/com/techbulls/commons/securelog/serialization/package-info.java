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

/**
 * Jackson serialization infrastructure that performs sensitive field masking.
 *
 * <h2>Serialization Pipeline</h2>
 * The masking pipeline is wired together as follows:
 * <ol>
 *   <li>{@link com.techbulls.commons.securelog.serialization.SecureJson SecureJson} &mdash;
 *       the public API entry point; manages a shared
 *       {@link com.fasterxml.jackson.databind.ObjectMapper} configured with the secure
 *       serializer modifier</li>
 *   <li>{@link com.techbulls.commons.securelog.serialization.SecureLogBeanSerializerModifier
 *       SecureLogBeanSerializerModifier} &mdash; a Jackson
 *       {@link com.fasterxml.jackson.databind.ser.BeanSerializerModifier} that detects
 *       {@link com.techbulls.commons.securelog.annotation.LogSensitive @LogSensitive}
 *       annotations on bean properties and wraps their serializers</li>
 *   <li>{@link com.techbulls.commons.securelog.serialization.SecurePropertySerializer
 *       SecurePropertySerializer} &mdash; a decorator serializer that replaces the actual
 *       field value with the configured mask string via a
 *       {@link com.techbulls.commons.securelog.ValueFormatter}</li>
 *   <li>{@link com.techbulls.commons.securelog.serialization.NullSecurePropertySerializer
 *       NullSecurePropertySerializer} &mdash; handles masking of {@code null} values when
 *       {@link com.techbulls.commons.securelog.annotation.LogSensitive#secureNullValues()}
 *       is enabled</li>
 * </ol>
 *
 * @see com.techbulls.commons.securelog.annotation
 * @see com.techbulls.commons.securelog.ValueFormatter
 */
package com.techbulls.commons.securelog.serialization;
