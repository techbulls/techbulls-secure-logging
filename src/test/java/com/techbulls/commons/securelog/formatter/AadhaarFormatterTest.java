package com.techbulls.commons.securelog.formatter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.techbulls.commons.securelog.annotation.Aadhaar;
import com.techbulls.commons.securelog.annotation.SecureLog;
import com.techbulls.commons.securelog.serialization.SecureJson;
import com.techbulls.commons.securelog.serialization.TestUtils;
import lombok.Data;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AadhaarFormatterTest {

    @Test
    public void testAadhaarPlain_masked() throws JsonProcessingException {
        assertAadhaarMasked("234567891234");           // plain
    }

    @Test
    public void testAadhaarWithSpaces_masked() throws JsonProcessingException {
        assertAadhaarMasked("2345 6789 1234");         // space-separated
    }

    @Test
    public void testAadhaarWithHyphens_masked() throws JsonProcessingException {
        assertAadhaarMasked("2345-6789-1234");         // hyphen-separated
    }

    @Test
    public void testAadhaarInvalidFormat_fallsBackToSecureValue() throws JsonProcessingException {
        assertAadhaarFallback("1234");                 // too short
    }

    @Test
    public void testAadhaarLetters_fallsBackToSecureValue() throws JsonProcessingException {
        assertAadhaarFallback("ABCD-EFGH-IJKL");      // non-numeric
    }

    @Test
    public void testAadhaarNull_fallsBackToSecureValue() throws JsonProcessingException {
        AadhaarPojo pojo = new AadhaarPojo();
        pojo.setAadhaarNumber(null);

        JsonNode node = TestUtils.asJsonNode(SecureJson.toJson(pojo));
        assertField(node, "aadhaarNumber", "XXXX");
    }


    private void assertAadhaarMasked(String input) throws JsonProcessingException {
        AadhaarPojo pojo = new AadhaarPojo();
        pojo.setAadhaarNumber(input);

        JsonNode node = TestUtils.asJsonNode(SecureJson.toJson(pojo));
        assertField(node, "aadhaarNumber", "XXXX-XXXX-1234");
    }

    private void assertAadhaarFallback(String input) throws JsonProcessingException {
        AadhaarPojo pojo = new AadhaarPojo();
        pojo.setAadhaarNumber(input);

        JsonNode node = TestUtils.asJsonNode(SecureJson.toJson(pojo));
        assertField(node, "aadhaarNumber", "XXXX");
    }

    private void assertField(JsonNode node, String field, String expected) {
        assertNotNull("Field missing: " + field, node.get(field));
        assertEquals(expected, node.get(field).asText());
    }


    @Data
    @SecureLog
    public static class AadhaarPojo {
        @Aadhaar
        private String aadhaarNumber;
    }
}
