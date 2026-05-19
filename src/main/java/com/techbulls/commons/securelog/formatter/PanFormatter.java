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
 * <h2>PanFormatter</h2>
 * A {@link ValueFormatter} that masks a PAN (Permanent Account Number), exposing only
 * the last 4 characters.
 * <p>
 * A valid PAN follows the format: {@code AAAAA0000A} — 5 letters, 4 digits, 1 letter (10 chars total).
 * <p>
 * <b>Examples:</b>
 * <pre>{@code
 * "ABCDE1234F" → "XXXXXX234F"
 * "abcde1234f" → "XXXXXX234F"
 * }</pre>
 * <p>
 * If the input is {@code null} or does not match the standard PAN format,
 * the configured {@code secureValue} is returned as a fallback.
 *
 * @see com.techbulls.commons.securelog.annotation.LogSensitive
 * @version 0.3
 * @since 0.3
 */
public final class PanFormatter implements ValueFormatter {

    /** Creates a new {@code PanFormatter} instance. */
    public PanFormatter() {
    }

    /**
     * Masks a PAN, preserving only the last 4 characters.
     *
     * @param value       the original PAN value
     * @param secureValue the fallback mask string, returned when the input is {@code null}
     *                    or not a valid PAN format
     * @return the masked PAN in {@code XXXXXX234F} format, or {@code secureValue}
     *         if the input cannot be meaningfully masked
     */
    @Override
    public String format(Object value, String secureValue) {
        if (value == null) {
            return secureValue;
        }

        String pan = value.toString()
                .replaceAll("[\\s\\-]", "")
                .toUpperCase();

        if (!pan.matches("[A-Z]{5}[0-9]{4}[A-Z]")) {
            return secureValue;
        }

        return "XXXXXX" + pan.substring(6);
    }
}