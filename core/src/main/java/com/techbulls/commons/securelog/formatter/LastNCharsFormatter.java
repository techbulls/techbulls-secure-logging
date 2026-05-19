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
 * <h2>LastNCharsFormatter</h2>
 * A {@link ValueFormatter} that reveals the last N characters of a value and masks the rest
 * with asterisks ({@code *}), where N is determined by the length of the configured
 * {@code secureValue} string.
 * <p>
 * <b>Convention:</b> The number of characters in the {@code secureValue} annotation attribute
 * controls how many trailing characters are revealed. For example, a {@code secureValue} of
 * {@code "XXXX"} (4 characters) reveals the last 4 characters.
 * <p>
 * <b>Examples</b> (with {@code secureValue = "XXXX"}, so N = 4):
 * <pre>{@code
 * "123456789" → "*****6789"
 * "ABCD"      → "XXXX"       (value length equals N, returns secureValue)
 * "AB"        → "XXXX"       (value shorter than N, returns secureValue)
 * }</pre>
 * <p>
 * If the input is {@code null} or its string representation is shorter than or equal to N,
 * the configured {@code secureValue} is returned as a fallback.
 *
 * @see com.techbulls.commons.securelog.annotation.LogSensitive
 * @version 0.3
 * @since 0.3
 */
public final class LastNCharsFormatter implements ValueFormatter {

    /** Creates a new {@code LastNCharsFormatter} instance. */
    public LastNCharsFormatter() {
    }

    /**
     * Reveals the last N characters of the value (where N = {@code secureValue.length()})
     * and masks the remaining leading characters with asterisks.
     *
     * @param value       the original field value
     * @param secureValue the mask string whose length determines how many trailing characters
     *                    to reveal; also used as the fallback for {@code null} or short values
     * @return the partially masked string, or {@code secureValue} if the input cannot be
     *         meaningfully masked
     */
    @Override
    public String format(Object value, String secureValue) {
        if (value == null) {
            return secureValue;
        }

        String str = value.toString();
        int n = secureValue.length();
        if (str.length() <= n) {
            return secureValue;
        }

        int maskLen = str.length() - n;
        StringBuilder sb = new StringBuilder(str.length());
        for (int i = 0; i < maskLen; i++) {
            sb.append('*');
        }
        sb.append(str, str.length() - n, str.length());
        return sb.toString();
    }
}
