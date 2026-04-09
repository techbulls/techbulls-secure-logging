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
import org.junit.Test;

import static org.junit.Assert.*;

public class NullSecurePropertySerializerTest {

    @Test
    public void testNullValueIsMasked() throws JsonProcessingException {
        SecureNullBean bean = new SecureNullBean();
        bean.setName("visible");
        bean.setSecret(null);

        String json = SecureJson.toJson(bean);
        JsonNode node = new ObjectMapper().readTree(json);

        assertEquals("XXXX", node.get("secret").asText());
        assertFalse(node.get("secret").isNull());
    }

    @Test
    public void testNullValueWithCustomMask() throws JsonProcessingException {
        CustomMaskNullBean bean = new CustomMaskNullBean();
        bean.setName("visible");
        bean.setSecret(null);

        String json = SecureJson.toJson(bean);
        JsonNode node = new ObjectMapper().readTree(json);

        assertEquals("***", node.get("secret").asText());
        assertFalse(node.get("secret").isNull());
    }

    @Test
    public void testNullValueWithCustomFormatter() throws JsonProcessingException {
        CustomFormatterNullBean bean = new CustomFormatterNullBean();
        bean.setName("visible");
        bean.setSecret(null);

        String json = SecureJson.toJson(bean);
        JsonNode node = new ObjectMapper().readTree(json);

        assertEquals("NULL_MASKED", node.get("secret").asText());
        assertFalse(node.get("secret").isNull());
    }

    @Test
    public void testSecureNullValuesFalse_WritesJsonNull() throws JsonProcessingException {
        DefaultNullBean bean = new DefaultNullBean();
        bean.setName("visible");
        bean.setSecret(null);

        String json = SecureJson.toJson(bean);
        JsonNode node = new ObjectMapper().readTree(json);

        assertTrue(node.get("secret").isNull());
    }

    // --- Test beans ---

    @SecureLog
    public static class SecureNullBean {
        private String name;

        @LogSensitive(secureNullValues = true)
        private String secret;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
    }

    @SecureLog
    public static class CustomMaskNullBean {
        private String name;

        @LogSensitive(value = "***", secureNullValues = true)
        private String secret;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
    }

    @SecureLog
    public static class CustomFormatterNullBean {
        private String name;

        @LogSensitive(formatter = NullMaskFormatter.class, secureNullValues = true)
        private String secret;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
    }

    @SecureLog
    public static class DefaultNullBean {
        private String name;

        @LogSensitive
        private String secret;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
    }

    public static class NullMaskFormatter implements ValueFormatter {
        @Override
        public String format(Object value, String secureValue) {
            return value == null ? "NULL_MASKED" : secureValue;
        }
    }
}
