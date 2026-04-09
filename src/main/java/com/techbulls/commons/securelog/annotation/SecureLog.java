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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * <h3>SecureLog Annotation</h3>
 * A class-level annotation that marks a Java bean for secure JSON serialization via
 * {@link com.techbulls.commons.securelog.serialization.SecureJson}. When present, it provides
 * configuration for how the object should be rendered as a masked JSON string.
 * <p>
 * This annotation is typically used in conjunction with {@link LogSensitive} field-level annotations
 * and a {@code toString()} method that delegates to
 * {@link com.techbulls.commons.securelog.serialization.SecureJson#toJson(Object)}.
 * <p>
 * <b>Example usage:</b>
 * <pre>{@code
 * @SecureLog(pretty = true)
 * public class CustomerRecord {
 *     private String name;
 *
 *     @LogSensitive("***")
 *     private String creditCardNumber;
 *
 *     @Override
 *     public String toString() {
 *         return SecureJson.toJson(this);
 *     }
 * }
 * }</pre>
 *
 * @see LogSensitive
 * @see com.techbulls.commons.securelog.serialization.SecureJson
 * @version 0.1
 * @since 0.1
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SecureLog {

    /**
     * Enables pretty-printed (indented) JSON output for improved readability in logs.
     * <p>
     * When set to {@code true}, the JSON output will be formatted with indentation and line breaks.
     * Defaults to {@code false} for compact single-line output.
     *
     * @return {@code true} if pretty-printed output is desired; {@code false} for compact output
     */
    boolean pretty() default false;

    /**
     * Specifies a {@link com.fasterxml.jackson.annotation.JsonView} class to apply during serialization.
     * <p>
     * When set to a view class other than {@link Default}, only fields annotated with a matching
     * {@code @JsonView} will be included in the output. This allows fine-grained control over
     * which fields appear in the secure log output.
     * <p>
     * Defaults to {@link Default}, which means no view filtering is applied and all fields are serialized.
     *
     * @return the {@link com.fasterxml.jackson.annotation.JsonView} class to apply, or {@link Default}
     *         for no view filtering
     */
    Class<?> view() default Default.class;

    /**
     * A sentinel class used as the default value for {@link #view()}, indicating that no
     * {@link com.fasterxml.jackson.annotation.JsonView} filtering should be applied during
     * serialization. When {@code view()} is set to this class, all fields are included in the output.
     */
    class Default {

    }
}
