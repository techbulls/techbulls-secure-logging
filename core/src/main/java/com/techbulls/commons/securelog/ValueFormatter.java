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
package com.techbulls.commons.securelog;

/**
 * <h2>ValueFormatter Interface</h2>
 * The base interface for formatting sensitive field values during secure JSON serialization.
 * Implementations define how the original field value and the configured mask string are combined
 * to produce the final masked output.
 * <p>
 * <b>Contract:</b> Implementations must have a no-argument constructor (which may be non-public),
 * as they are instantiated reflectively by
 * {@link com.techbulls.commons.securelog.serialization.SecureJson#instantiate(Class)}.
 * <p>
 * <b>Built-in implementation:</b>
 * <ul>
 *   <li>{@link DefaultValueFormatter} — returns the mask string unchanged, ignoring the original value</li>
 * </ul>
 * <p>
 * <b>Custom formatter example:</b>
 * <pre>{@code
 * public class LastFourFormatter implements ValueFormatter {
 *     public String format(Object value, String secureValue) {
 *         if (value == null) return secureValue;
 *         String str = value.toString();
 *         if (str.length() <= 4) return secureValue;
 *         return secureValue + str.substring(str.length() - 4);
 *     }
 * }
 * // Usage: @LogSensitive(value = "****", formatter = LastFourFormatter.class)
 * // Input: "4111111111111111" → Output: "****1111"
 * }</pre>
 *
 * @see DefaultValueFormatter
 * @see com.techbulls.commons.securelog.annotation.LogSensitive#formatter()
 * @version 0.1
 * @since 0.1
 */
public interface ValueFormatter {

    /**
     * Formats a sensitive field value into its masked representation.
     * <p>
     * Implementations receive both the original runtime value and the mask string configured
     * via {@link com.techbulls.commons.securelog.annotation.LogSensitive#value()}, and may
     * combine them in any way appropriate for the masking strategy (e.g., full replacement,
     * partial masking, or pattern-based substitution).
     *
     * @param value       the original field value at runtime; may be {@code null} if the field
     *                    is null and {@link com.techbulls.commons.securelog.annotation.LogSensitive#secureNullValues()}
     *                    is {@code true}
     * @param secureValue the mask string specified by the {@link com.techbulls.commons.securelog.annotation.LogSensitive}
     *                    annotation (e.g., {@code "XXXX"})
     * @return the formatted masked string to write in the JSON output; must not be {@code null}
     */
    String format(Object value, String secureValue);
}
