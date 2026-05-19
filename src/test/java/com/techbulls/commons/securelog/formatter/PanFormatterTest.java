package com.techbulls.commons.securelog.formatter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.techbulls.commons.securelog.annotation.PanNumber;
import com.techbulls.commons.securelog.annotation.SecureLog;
import com.techbulls.commons.securelog.serialization.SecureJson;
import com.techbulls.commons.securelog.serialization.TestUtils;
import lombok.Data;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PanFormatterTest {
    @Test
    public void testPanPlainUppercase_masked() throws JsonProcessingException {
        assertPanMasked("ABCDE1234F");            // standard
    }

    @Test
    public void testPanPlainLowercase_masked() throws JsonProcessingException {
        assertPanMasked("abcde1234f");            // lowercase input
    }

    @Test
    public void testPanMixedCase_masked() throws JsonProcessingException {
        assertPanMasked("AbCdE1234f");            // mixed case
    }

    @Test
    public void testPanWithSpaces_masked() throws JsonProcessingException {
        assertPanMasked("ABCDE 1234 F");          // spaces stripped
    }

    @Test
    public void testPanWithHyphens_masked() throws JsonProcessingException {
        assertPanMasked("ABCDE-1234-F");          // hyphens stripped
    }

    @Test
    public void testPanInvalid_tooShort_fallsBackToSecureValue() throws JsonProcessingException {
        assertPanFallback("ABC123");              // too short
    }

    @Test
    public void testPanInvalid_wrongFormat_fallsBackToSecureValue() throws JsonProcessingException {
        assertPanFallback("1234567890");          // starts with digits
    }

    @Test
    public void testPanNull_fallsBackToSecureValue() throws JsonProcessingException {
        PanPojo pojo = new PanPojo();
        pojo.setPanNumber(null);

        JsonNode node = TestUtils.asJsonNode(SecureJson.toJson(pojo));
        assertField(node, "panNumber", "XXXX");
    }

// ── Helpers ───────────────────────────────────────────────────────────────────

    private void assertPanMasked(String input) throws JsonProcessingException {
        PanPojo pojo = new PanPojo();
        pojo.setPanNumber(input);

        JsonNode node = TestUtils.asJsonNode(SecureJson.toJson(pojo));
        assertField(node, "panNumber", "XXXXXX234F");
    }

    private void assertPanFallback(String input) throws JsonProcessingException {
        PanPojo pojo = new PanPojo();
        pojo.setPanNumber(input);

        JsonNode node = TestUtils.asJsonNode(SecureJson.toJson(pojo));
        assertField(node, "panNumber", "XXXX");
    }

    private void assertField(JsonNode node, String field, String expected) {
        assertNotNull("Field missing: " + field, node.get(field));
        assertEquals(expected, node.get(field).asText());
    }

    @Data
    @SecureLog
    public static class PanPojo {
        @PanNumber
        private String panNumber;
    }
}
