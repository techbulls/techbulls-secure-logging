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

public class FirstNCharsFormatterTest {

    private final FirstNCharsFormatter formatter = new FirstNCharsFormatter();

    @Test
    public void testFirstFourChars() {
        Assert.assertEquals("1234*****", formatter.format("123456789", "XXXX"));
    }

    @Test
    public void testFirstOneChar() {
        Assert.assertEquals("1********", formatter.format("123456789", "X"));
    }

    @Test
    public void testFirstSixChars() {
        Assert.assertEquals("123456***", formatter.format("123456789", "XXXXXX"));
    }

    @Test
    public void testValueShorterThanN() {
        Assert.assertEquals("XXXX", formatter.format("AB", "XXXX"));
    }

    @Test
    public void testValueEqualToN() {
        Assert.assertEquals("XXXX", formatter.format("ABCD", "XXXX"));
    }

    @Test
    public void testNullInput() {
        Assert.assertEquals("XXXX", formatter.format(null, "XXXX"));
    }

    @Test
    public void testFiveCharValueWithN4() {
        Assert.assertEquals("1234*", formatter.format("12345", "XXXX"));
    }
}
