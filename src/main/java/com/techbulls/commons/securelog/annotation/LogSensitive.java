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
import com.techbulls.commons.securelog.DefaultValueFormatter;
import com.techbulls.commons.securelog.ValueFormatter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * <h3>LogSensitive Annotation</h3>
 * A field-level annotation that marks a bean property as containing sensitive data that should be
 * masked when serialized to JSON via {@link com.techbulls.commons.securelog.serialization.SecureJson}.
 * <p>
 * During serialization, the {@link com.techbulls.commons.securelog.serialization.SecureLogBeanSerializerModifier}
 * detects this annotation on bean properties and wraps their serializers with
 * {@link com.techbulls.commons.securelog.serialization.SecurePropertySerializer}, which replaces
 * the actual field value with the configured mask string.
 * <p>
 * <b>Example usage:</b>
 * <pre>{@code
 * @SecureLog
 * public class PaymentInfo {
 *     private String merchantName;
 *
 *     @LogSensitive                          // masked as "XXXX"
 *     private String cardNumber;
 *
 *     @LogSensitive("***-**-")               // masked as "***-**-"
 *     private String ssn;
 *
 *     @LogSensitive(formatter = LastFourFormatter.class)  // custom formatting
 *     private String accountNumber;
 *
 *     @LogSensitive(secureNullValues = true)  // null values also masked as "XXXX"
 *     private String secretToken;
 * }
 * }</pre>
 *
 * @see SecureLog
 * @see com.techbulls.commons.securelog.ValueFormatter
 * @see com.techbulls.commons.securelog.DefaultValueFormatter
 * @version 0.1
 * @since 0.1
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogSensitive {

    /**
     * The mask string to substitute for the actual field value during serialization.
     * <p>
     * This value is passed to the {@link ValueFormatter#format(Object, String)} method as the
     * {@code secureValue} parameter. The default formatter ({@link DefaultValueFormatter}) returns
     * this string unchanged; custom formatters may use it as a template or prefix.
     *
     * @return the mask string to use; defaults to {@code "XXXX"}
     */
    String value() default "XXXX";

    /**
     * The {@link ValueFormatter} implementation class to use for producing the masked output.
     * <p>
     * The specified class must have a no-argument constructor (which may be non-public) and will
     * be instantiated reflectively via
     * {@link com.techbulls.commons.securelog.serialization.SecureJson#instantiate(Class)}.
     * <p>
     * Custom formatters can implement logic such as partial masking (e.g., showing the last 4
     * digits of a credit card number) by using both the original value and the mask string.
     *
     * @return the formatter class to use; defaults to {@link DefaultValueFormatter}
     * @see com.techbulls.commons.securelog.DefaultValueFormatter
     */
    Class<? extends ValueFormatter> formatter() default DefaultValueFormatter.class;

    /**
     * Whether {@code null} field values should also be masked with the configured mask string.
     * <p>
     * When {@code false} (the default), null fields are serialized as JSON {@code null} as usual.
     * When {@code true}, a {@link com.techbulls.commons.securelog.serialization.NullSecurePropertySerializer}
     * is assigned to the property, ensuring that even null values appear as the mask string in the output.
     * <p>
     * This is useful when the mere absence of a value could reveal sensitive information
     * (e.g., distinguishing between "no SSN on file" vs. "SSN present but masked").
     *
     * @return {@code true} to mask null values; {@code false} to serialize nulls as JSON {@code null}
     */
    boolean secureNullValues() default false;
}
