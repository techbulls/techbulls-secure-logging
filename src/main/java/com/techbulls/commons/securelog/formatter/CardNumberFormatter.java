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
 * <h2>CardNumberFormatter</h2>
 * A {@link ValueFormatter} that masks all but the last 4 digits of a card number and formats
 * the output in dash-separated groups of 4.
 * <p>
 * Non-digit characters in the input are stripped before masking. The masked digits are replaced
 * with {@code X} and the result is formatted in groups of 4 separated by dashes.
 * <p>
 * <b>Examples:</b>
 * <pre>{@code
 * "4111111111111111"   → "XXXX-XXXX-XXXX-1111"
 * "4111-1111-1111-1111" → "XXXX-XXXX-XXXX-1111"
 * "4111 1111 1111 1111" → "XXXX-XXXX-XXXX-1111"
 * "378282246310005"     → "XXXX-XXXX-XXX-0005"
 * }</pre>
 * <p>
 * If the input is {@code null}, empty, or contains 4 or fewer digits, the configured
 * {@code secureValue} is returned as a fallback.
 * <p>
 * This formatter ignores the {@code secureValue} parameter for valid card numbers — the mask
 * character is always {@code X} and the format is always dashed groups of 4.
 *
 * @see com.techbulls.commons.securelog.annotation.LogSensitive
 * @version 0.3
 * @since 0.3
 */
public final class CardNumberFormatter implements ValueFormatter {

    /** Creates a new {@code CardNumberFormatter} instance. */
    public CardNumberFormatter() {
    }

    /**
     * Masks all but the last 4 digits of the card number and formats in dashed groups of 4.
     *
     * @param value       the original card number value; may contain dashes, spaces, or other
     *                    non-digit separators which are stripped before processing
     * @param secureValue the fallback mask string, returned when the input is {@code null},
     *                    empty, or too short to partially mask
     * @return the masked card number in {@code XXXX-XXXX-XXXX-1111} format, or {@code secureValue}
     *         if the input cannot be meaningfully masked
     */
    @Override
    public String format(Object value, String secureValue) {
        if (value == null) {
            return secureValue;
        }

        String digits = value.toString().replaceAll("[^0-9]", "");
        if (digits.length() <= 4) {
            return secureValue;
        }

        int maskLen = digits.length() - 4;
        String lastFour = digits.substring(digits.length() - 4);

        char[] masked = new char[digits.length()];
        for (int i = 0; i < maskLen; i++) {
            masked[i] = 'X';
        }
        lastFour.getChars(0, 4, masked, maskLen);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < masked.length; i++) {
            if (i > 0 && i % 4 == 0) {
                sb.append('-');
            }
            sb.append(masked[i]);
        }
        return sb.toString();
    }
}
