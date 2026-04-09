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
 * <h2>DefaultValueFormatter Class</h2>
 * The default {@link ValueFormatter} implementation that performs full-replacement masking: the
 * original field value is completely discarded and replaced with the configured mask string.
 * <p>
 * This formatter is used when no custom {@link ValueFormatter} is specified in the
 * {@link com.techbulls.commons.securelog.annotation.LogSensitive} annotation. For example, a field
 * annotated with {@code @LogSensitive} (using the default mask {@code "XXXX"}) will always be
 * serialized as {@code "XXXX"} regardless of its actual value.
 * <p>
 * To implement partial masking or other custom strategies, create a class that implements
 * {@link ValueFormatter} and specify it via
 * {@link com.techbulls.commons.securelog.annotation.LogSensitive#formatter()}.
 *
 * @see ValueFormatter
 * @see com.techbulls.commons.securelog.annotation.LogSensitive
 * @version 0.1
 * @since 0.1
 */

public final class DefaultValueFormatter implements ValueFormatter {

    /** Creates a new {@code DefaultValueFormatter} instance. */
    public DefaultValueFormatter() {
    }

    /**
     * Returns the mask string unchanged, completely replacing the original value.
     * <p>
     * The {@code value} parameter is intentionally ignored — this formatter provides full
     * replacement masking where no part of the original value is preserved in the output.
     *
     * @param value       the original field value (ignored by this implementation)
     * @param secureValue the mask string specified by the
     *                    {@link com.techbulls.commons.securelog.annotation.LogSensitive} annotation
     * @return the {@code secureValue} mask string, unchanged
     */
    @Override
    public String format(Object value, String secureValue) {
        return secureValue;
    }
}
