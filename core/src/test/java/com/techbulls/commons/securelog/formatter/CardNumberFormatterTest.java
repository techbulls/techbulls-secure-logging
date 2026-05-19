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

import org.junit.Assert;
import org.junit.Test;

public class CardNumberFormatterTest {

    private final CardNumberFormatter formatter = new CardNumberFormatter();
    private static final String SECURE_VALUE = "XXXX";

    @Test
    public void testStandard16DigitCard() {
        Assert.assertEquals("XXXX-XXXX-XXXX-1111", formatter.format("4111111111111111", SECURE_VALUE));
    }

    @Test
    public void testCardWithDashes() {
        Assert.assertEquals("XXXX-XXXX-XXXX-1111", formatter.format("4111-1111-1111-1111", SECURE_VALUE));
    }

    @Test
    public void testCardWithSpaces() {
        Assert.assertEquals("XXXX-XXXX-XXXX-1111", formatter.format("4111 1111 1111 1111", SECURE_VALUE));
    }

    @Test
    public void test15DigitCard() {
        // Amex-style: 15 digits → 11 masked + 4 revealed, grouped by 4 from left
        Assert.assertEquals("XXXX-XXXX-XXX0-005", formatter.format("378282246310005", SECURE_VALUE));
    }

    @Test
    public void testShortInput() {
        Assert.assertEquals(SECURE_VALUE, formatter.format("1234", SECURE_VALUE));
    }

    @Test
    public void testVeryShortInput() {
        Assert.assertEquals(SECURE_VALUE, formatter.format("12", SECURE_VALUE));
    }

    @Test
    public void testNullInput() {
        Assert.assertEquals(SECURE_VALUE, formatter.format(null, SECURE_VALUE));
    }

    @Test
    public void testEmptyInput() {
        Assert.assertEquals(SECURE_VALUE, formatter.format("", SECURE_VALUE));
    }

    @Test
    public void testNonNumericCharactersMixed() {
        Assert.assertEquals("XXXX-XXXX-XXXX-1111", formatter.format("4111.1111.1111.1111", SECURE_VALUE));
    }

    @Test
    public void test5DigitInput() {
        // 5 digits: 1 masked + 4 revealed → "X-2345" wait, grouping: "X2345" grouped by 4 from left
        // chars: X,2,3,4,5 → "X234-5"
        Assert.assertEquals("X234-5", formatter.format("12345", SECURE_VALUE));
    }
}
