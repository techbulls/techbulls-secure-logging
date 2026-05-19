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
 * <h2>EmailFormatter</h2>
 * A {@link ValueFormatter} that masks the local part of an email address while preserving the
 * domain. The first character of the local part is kept visible, followed by a fixed mask of
 * {@code ****}, then the full {@code @domain} portion.
 * <p>
 * <b>Examples:</b>
 * <pre>{@code
 * "john.doe@gmail.com"   → "j****@gmail.com"
 * "a@example.com"        → "a****@example.com"
 * "user@sub.domain.com"  → "u****@sub.domain.com"
 * }</pre>
 * <p>
 * If the input is {@code null}, does not contain an {@code @} sign, or has an empty local part,
 * the configured {@code secureValue} is returned as a fallback.
 *
 * @see com.techbulls.commons.securelog.annotation.LogSensitive
 * @version 0.3
 * @since 0.3
 */
public final class EmailFormatter implements ValueFormatter {

    /** Creates a new {@code EmailFormatter} instance. */
    public EmailFormatter() {
    }

    /**
     * Masks the local part of an email address, preserving the first character and the full domain.
     *
     * @param value       the original email address value
     * @param secureValue the fallback mask string, returned when the input is {@code null}
     *                    or not a valid email format
     * @return the masked email in {@code j****@domain.com} format, or {@code secureValue}
     *         if the input cannot be meaningfully masked
     */
    @Override
    public String format(Object value, String secureValue) {
        if (value == null) {
            return secureValue;
        }

        String str = value.toString();
        int atIndex = str.lastIndexOf('@');
        if (atIndex <= 0) {
            return secureValue;
        }

        String domain = str.substring(atIndex);
        return str.charAt(0) + "****" + domain;
    }
}
