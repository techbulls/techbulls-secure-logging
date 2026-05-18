package com.techbulls.commons.securelog.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techbulls.commons.securelog.annotation.LogSensitive;
import com.techbulls.commons.securelog.annotation.SecureLog;
import lombok.Data;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SecureJavaTimeModuleTest {

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 5, 15, 10, 30, 45);
    private static final LocalDate BIRTH_DATE = LocalDate.of(1998, 1, 20);
    private static final LocalTime LOGIN_TIME = LocalTime.of(9, 15, 10);

    @Test
    public void testJavaTimeSerializationWithDefaultMapper() throws JsonProcessingException {
        JsonNode node = TestUtils.asJsonNode(SecureJson.toJson(buildPojo()));
        assertTimeFields(node);
        assertEquals("XXXX", node.get("secret").asText());
    }

    @Test
    public void testJavaTimeSerializationWithCustomMapper() throws JsonProcessingException {
        JsonNode node = TestUtils.asJsonNode(
                SecureJson.toJson(new ObjectMapper(), buildPojo(), false, SecureLog.Default.class)
        );
        assertTimeFields(node);
        assertEquals("XXXX", node.get("secret").asText());
    }

    private void assertTimeFields(JsonNode node) {
        assertNotNull(node.get("createdAt"));
        assertNotNull(node.get("birthDate"));
        assertNotNull(node.get("loginTime"));
        assertEquals("XXXX", node.get("createdAt").asText());
        assertEquals("1998-01-20", node.get("birthDate").asText());
        assertEquals("09:15:10", node.get("loginTime").asText());
    }

    private JavaTimePojo buildPojo() {
        JavaTimePojo pojo = new JavaTimePojo();
        pojo.setName("Test User");
        pojo.setCreatedAt(CREATED_AT);
        pojo.setBirthDate(BIRTH_DATE);
        pojo.setLoginTime(LOGIN_TIME);
        pojo.setSecret("my-secret");
        return pojo;
    }

    @Data
    @SecureLog
    public static class JavaTimePojo {
        private String name;
        @LogSensitive private LocalDateTime createdAt;
        private LocalDate birthDate;
        private LocalTime loginTime;
        @LogSensitive private String secret;
    }
}