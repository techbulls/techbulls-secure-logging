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
 * <h3>MaskAlphaValueFormatter Class</h3>
 * The Alpha value formatter that masks the alphabets [A-Za-z] with 'X' character
 * <p>
 * @see com.techbulls.commons.securelog.ValueFormatter
 * @author Vaibhav Ghatge
 * @version 1.0.0
 * @since  01 August 2022
 * */
public class MaskedAlphaValueFormatter implements ValueFormatter {
    /**
     * This method will return the formatted masked value
     * @param value This is the value that needs to be formatted
     * @param secureValue This is the formatted value
     * @return String This is the string value after formatting
     * */
    @Override
    public String format(Object value, String secureValue) {
        return value!=null?value.toString().replaceAll("[A-Za-z]","X"):secureValue;
    }
}
