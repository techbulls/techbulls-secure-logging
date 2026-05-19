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

public class EmailFormatterTest {

    private final EmailFormatter formatter = new EmailFormatter();
    private static final String SECURE_VALUE = "XXXX";

    @Test
    public void testStandardEmail() {
        Assert.assertEquals("j****@gmail.com", formatter.format("john.doe@gmail.com", SECURE_VALUE));
    }

    @Test
    public void testSingleCharLocalPart() {
        Assert.assertEquals("j****@gmail.com", formatter.format("j@gmail.com", SECURE_VALUE));
    }

    @Test
    public void testEmailWithSubdomain() {
        Assert.assertEquals("u****@sub.domain.com", formatter.format("user@sub.domain.com", SECURE_VALUE));
    }

    @Test
    public void testNoAtSign() {
        Assert.assertEquals(SECURE_VALUE, formatter.format("notanemail", SECURE_VALUE));
    }

    @Test
    public void testNullInput() {
        Assert.assertEquals(SECURE_VALUE, formatter.format(null, SECURE_VALUE));
    }

    @Test
    public void testEmptyLocalPart() {
        Assert.assertEquals(SECURE_VALUE, formatter.format("@gmail.com", SECURE_VALUE));
    }

    @Test
    public void testEmailWithPlusAddressing() {
        Assert.assertEquals("u****@gmail.com", formatter.format("user+tag@gmail.com", SECURE_VALUE));
    }
}
