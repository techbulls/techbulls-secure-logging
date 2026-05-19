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
 * <h2>AadhaarFormatter</h2>
 * A {@link ValueFormatter} that masks an Aadhaar number, exposing only the last 4 digits.
 * <p>
 * <b>Examples:</b>
 * <pre>{@code
 * "234567891234"   → "XXXX-XXXX-1234"
 * "2345 6789 1234" → "XXXX-XXXX-1234"
 * "2345-6789-1234" → "XXXX-XXXX-1234"
 * }</pre>
 * <p>
 * If the input is {@code null}, blank, or does not contain exactly 12 digits after
 * stripping spaces and hyphens, the configured {@code secureValue} is returned as a fallback.
 *
 * @see com.techbulls.commons.securelog.annotation.LogSensitive
 * @version 0.3
 * @since 0.3
 */
public final class AadhaarFormatter implements ValueFormatter {

    /** Creates a new {@code AadhaarFormatter} instance. */
    public AadhaarFormatter() {
    }

    /**
     * Masks an Aadhaar number, preserving only the last 4 digits.
     *
     * @param value       the original Aadhaar number (with or without spaces/hyphens)
     * @param secureValue the fallback mask string, returned when the input is {@code null}
     *                    or not a valid 12-digit Aadhaar format
     * @return the masked Aadhaar in {@code XXXX-XXXX-1234} format, or {@code secureValue}
     *         if the input cannot be meaningfully masked
     */
    @Override
    public String format(Object value, String secureValue) {
        if (value == null) {
            return secureValue;
        }

        String digits = value.toString().replaceAll("[\\s\\-]", "");
        if (!digits.matches("\\d{12}")) {
            return secureValue;
        }

        return "XXXX-XXXX-" + digits.substring(8);
    }
}