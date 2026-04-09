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
package com.techbulls.commons.securelog.annotation;

import com.techbulls.commons.securelog.formatter.EmailFormatter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h2>Email Annotation</h2>
 * A convenience field-level annotation that masks email addresses, revealing only the first
 * character of the local part and the full domain.
 * <p>
 * This is a meta-annotation over {@link LogSensitive} with {@link EmailFormatter} pre-configured.
 * It provides a more readable and discoverable alternative to
 * {@code @LogSensitive(formatter = EmailFormatter.class)}.
 * <p>
 * <b>Example usage:</b>
 * <pre>{@code
 * @SecureLog
 * public class UserProfile {
 *     @Email
 *     private String emailAddress;  // "john.doe@gmail.com" → "j****@gmail.com"
 * }
 * }</pre>
 *
 * @see LogSensitive
 * @see EmailFormatter
 * @version 0.3
 * @since 0.3
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@LogSensitive(formatter = EmailFormatter.class)
public @interface Email {
}
