package com.techbulls.commons.securelog.formatter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.techbulls.commons.securelog.annotation.MobileNumber;
import com.techbulls.commons.securelog.annotation.SecureLog;
import com.techbulls.commons.securelog.serialization.SecureJson;
import com.techbulls.commons.securelog.serialization.TestUtils;
import lombok.Data;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MobileFormatterTest {

    @Test
    public void testValidMobile_masked() throws JsonProcessingException {
        MobilePojo pojo = new MobilePojo();
        pojo.setMobile("9876543210");

        JsonNode node = TestUtils.asJsonNode(SecureJson.toJson(pojo));

        assertNotNull(node.get("mobile"));
        assertEquals("******3210", node.get("mobile").asText());
    }

    @Test
    public void testMobileWithCountryCode_masked() throws JsonProcessingException {
        MobilePojo pojo = new MobilePojo();
        pojo.setMobile("+919876543210");

        JsonNode node = TestUtils.asJsonNode(SecureJson.toJson(pojo));

        assertNotNull(node.get("mobile"));
        assertEquals("********3210", node.get("mobile").asText());
    }

    @Test
    public void testMobileWithLeadingZero_masked() throws JsonProcessingException {
        MobilePojo pojo = new MobilePojo();
        pojo.setMobile("09876543210");               // 11 digits with leading zero

        JsonNode node = TestUtils.asJsonNode(SecureJson.toJson(pojo));

        assertNotNull(node.get("mobile"));
        assertEquals("*******3210", node.get("mobile").asText());
    }

    @Test
    public void testInvalidMobile_tooShort_fallsBackToSecureValue() throws JsonProcessingException {
        MobilePojo pojo = new MobilePojo();
        pojo.setMobile("12345");                     // less than 10 digits

        JsonNode node = TestUtils.asJsonNode(SecureJson.toJson(pojo));

        assertNotNull(node.get("mobile"));
        assertEquals("XXXX", node.get("mobile").asText());
    }

    @Test
    public void testNullMobile_fallsBackToSecureValue() throws JsonProcessingException {
        MobilePojo pojo = new MobilePojo();
        pojo.setMobile(null);

        JsonNode node = TestUtils.asJsonNode(SecureJson.toJson(pojo));

        assertNotNull(node.get("mobile"));
        assertEquals("XXXX", node.get("mobile").asText());
    }

    @Data
    @SecureLog
    public static class MobilePojo {
        @MobileNumber
        private String mobile;
    }
}