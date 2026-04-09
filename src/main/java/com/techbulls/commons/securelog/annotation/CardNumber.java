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

import com.techbulls.commons.securelog.formatter.CardNumberFormatter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h2>CardNumber Annotation</h2>
 * A convenience field-level annotation that masks card numbers, revealing only the last 4 digits
 * in dash-separated groups of 4.
 * <p>
 * This is a meta-annotation over {@link LogSensitive} with {@link CardNumberFormatter} pre-configured.
 * It provides a more readable and discoverable alternative to
 * {@code @LogSensitive(formatter = CardNumberFormatter.class)}.
 * <p>
 * <b>Example usage:</b>
 * <pre>{@code
 * @SecureLog
 * public class PaymentInfo {
 *     @CardNumber
 *     private String cardNumber;  // "4111111111111111" → "XXXX-XXXX-XXXX-1111"
 * }
 * }</pre>
 *
 * @see LogSensitive
 * @see CardNumberFormatter
 * @version 0.3
 * @since 0.3
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@LogSensitive(formatter = CardNumberFormatter.class)
public @interface CardNumber {
}
