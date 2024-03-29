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
package com.techbulls.commons.securelog.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techbulls.commons.securelog.ValueFormatter;
import com.techbulls.commons.securelog.annotation.LogSensitive;
import com.techbulls.commons.securelog.annotation.SecureLog;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;

public class SecureJsonTest {
    @Test
    public void testSecureToString() throws JsonProcessingException, IllegalAccessException {
        TestBean bean = new TestBean();
        bean.setPublicString("public");
        bean.setPublicInt(456);
        bean.setSecureString("secret");
        bean.setFormattedSecureString("ABCdefGHI");
        bean.setSecureInt(null);

        String safeToString = SecureJson.toJson(bean);

        System.out.println("ToString: " + bean.toString());
        System.out.println("Safe toString(): " + safeToString);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(safeToString);

        Field[] fields = TestBean.class.getDeclaredFields();

        for (Field f : fields) {
            LogSensitive annotation = f.getAnnotation(LogSensitive.class);
            f.setAccessible(true);
            Object value = f.get(bean);
            if (value != null) {
                String expected = value.toString();
                if (annotation != null) {
                    expected = getExpectedValue(bean, f, annotation);
                    assertContainsNodeWithText(node, f.getName(), expected);
                }
            }
        }
    }

    private String getExpectedValue(Object bean, Field f, LogSensitive annotation) throws IllegalAccessException {
        ValueFormatter formatter = SecureJson.instantiate(annotation.formatter());
        return formatter.format(f.get(bean).toString(), annotation.value());
    }

    private void assertContainsNodeWithText(JsonNode node, String key, String value) {
        JsonNode child = node.get(key);
        Assert.assertNotNull(child);
        Assert.assertEquals(value, child.asText());
    }

    @SecureLog
    public static class TestBean {
        private String publicString;

        private Integer publicInt;

        @LogSensitive(value = "xxxxxxx")
        private String secureString;

        @LogSensitive
        private Integer secureInt;

        @LogSensitive(formatter = ExampleValueFormatter.class)
        private String formattedSecureString;

        public String getPublicString() {
            return publicString;
        }

        public void setPublicString(String publicString) {
            this.publicString = publicString;
        }

        public String getSecureString() {
            return secureString;
        }

        public void setSecureString(String secureString) {
            this.secureString = secureString;
        }

        public Integer getSecureInt() {
            return secureInt;
        }

        public void setSecureInt(Integer secureInt) {
            this.secureInt = secureInt;
        }

        public Integer getPublicInt() {
            return publicInt;
        }

        public void setPublicInt(Integer publicInt) {
            this.publicInt = publicInt;
        }

        public String getFormattedSecureString() {
            return formattedSecureString;
        }

        public void setFormattedSecureString(String formattedSecureString) {
            this.formattedSecureString = formattedSecureString;
        }
    }

    public static class ExampleValueFormatter implements ValueFormatter {
        @Override
        public String format(Object value, String secureValue) {
            return value.toString().replaceAll("[A-Z]", "X");
        }
    }


}
