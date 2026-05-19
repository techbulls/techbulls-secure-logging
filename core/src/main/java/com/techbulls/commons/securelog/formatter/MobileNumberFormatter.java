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
package com.techbulls.commons.securelog.formatter;

import com.techbulls.commons.securelog.ValueFormatter;

/**
 * <h2>MobileFormatter</h2>
 * A {@link ValueFormatter} that masks a mobile number, exposing only the last 4 digits.
 * Handles optional country code prefixes (e.g. {@code +91}, {@code 0091}).
 * <p>
 * <b>Examples:</b>
 * <pre>{@code
 * "9876543210"    → "******3210"
 * "+919876543210" → "******3210"
 * "09876543210"   → "******3210"
 * }</pre>
 * <p>
 * If the input is {@code null}, blank, or does not contain between 10 and 13 digits
 * after stripping formatting characters, the configured {@code secureValue} is returned
 * as a fallback.
 *
 * @see com.techbulls.commons.securelog.annotation.LogSensitive
 * @version 0.3
 * @since 0.3
 */
public final class MobileNumberFormatter implements ValueFormatter {

    /** Creates a new {@code MobileFormatter} instance. */
    public MobileNumberFormatter() {
    }

    /**
     * Masks a mobile number, preserving only the last 4 digits.
     *
     * @param value       the original mobile number (with or without country code/spaces/hyphens)
     * @param secureValue the fallback mask string, returned when the input is {@code null}
     *                    or not a valid mobile number format
     * @return the masked number in {@code ******3210} format, or {@code secureValue}
     *         if the input cannot be meaningfully masked
     */
    @Override
    public String format(Object value, String secureValue) {
        if (value == null) {
            return secureValue;
        }

        String digits = value.toString().replaceAll("[\\s\\-().+]", "");
        if (!digits.matches("\\d{10,13}")) {
            return secureValue;
        }

        String last4 = digits.substring(digits.length() - 4);
        String mask  = "*".repeat(digits.length() - 4);
        return mask + last4;
    }
}