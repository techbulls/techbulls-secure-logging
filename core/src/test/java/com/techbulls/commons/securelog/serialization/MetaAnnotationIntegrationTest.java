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
import com.techbulls.commons.securelog.annotation.CardNumber;
import com.techbulls.commons.securelog.annotation.Email;
import com.techbulls.commons.securelog.annotation.LogSensitive;
import com.techbulls.commons.securelog.annotation.SecureLog;
import org.junit.Assert;
import org.junit.Test;

public class MetaAnnotationIntegrationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testCardNumberAnnotation() throws JsonProcessingException {
        PaymentBean bean = new PaymentBean();
        bean.setCardNumber("4111111111111111");
        bean.setEmail("john.doe@gmail.com");
        bean.setName("John Doe");
        bean.setSecret("topsecret");

        String json = SecureJson.toJson(bean);
        JsonNode node = mapper.readTree(json);

        Assert.assertEquals("XXXX-XXXX-XXXX-1111", node.get("cardNumber").asText());
    }

    @Test
    public void testEmailAnnotation() throws JsonProcessingException {
        PaymentBean bean = new PaymentBean();
        bean.setCardNumber("4111111111111111");
        bean.setEmail("john.doe@gmail.com");
        bean.setName("John Doe");
        bean.setSecret("topsecret");

        String json = SecureJson.toJson(bean);
        JsonNode node = mapper.readTree(json);

        Assert.assertEquals("j****@gmail.com", node.get("email").asText());
    }

    @Test
    public void testDirectLogSensitiveStillWorks() throws JsonProcessingException {
        PaymentBean bean = new PaymentBean();
        bean.setCardNumber("4111111111111111");
        bean.setEmail("john.doe@gmail.com");
        bean.setName("John Doe");
        bean.setSecret("topsecret");

        String json = SecureJson.toJson(bean);
        JsonNode node = mapper.readTree(json);

        Assert.assertEquals("***", node.get("secret").asText());
    }

    @Test
    public void testUnannotatedFieldPassesThrough() throws JsonProcessingException {
        PaymentBean bean = new PaymentBean();
        bean.setCardNumber("4111111111111111");
        bean.setEmail("john.doe@gmail.com");
        bean.setName("John Doe");
        bean.setSecret("topsecret");

        String json = SecureJson.toJson(bean);
        JsonNode node = mapper.readTree(json);

        Assert.assertEquals("John Doe", node.get("name").asText());
    }

    @Test
    public void testNullCardNumberField() throws JsonProcessingException {
        PaymentBean bean = new PaymentBean();
        bean.setCardNumber(null);
        bean.setEmail("john@example.com");
        bean.setName("Jane");
        bean.setSecret("s");

        String json = SecureJson.toJson(bean);
        JsonNode node = mapper.readTree(json);

        Assert.assertTrue(node.get("cardNumber").isNull());
    }

    @SecureLog
    public static class PaymentBean {
        private String name;

        @CardNumber
        private String cardNumber;

        @Email
        private String email;

        @LogSensitive("***")
        private String secret;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCardNumber() {
            return cardNumber;
        }

        public void setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }
}
