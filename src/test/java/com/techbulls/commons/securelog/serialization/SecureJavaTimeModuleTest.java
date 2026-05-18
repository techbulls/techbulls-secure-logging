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
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SecureJavaTimeModuleTest {

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 5, 15, 10, 30, 45);
    private static final LocalDate    BIRTH_DATE  = LocalDate.of(1998, 1, 20);
    private static final LocalTime    LOGIN_TIME  = LocalTime.of(9, 15, 10);

    // ── Default mapper ────────────────────────────────────────────────────────
    @Test
    public void testNonsensitiveTimeFields_DefaultMapper() throws JsonProcessingException {
        JsonNode node = toNode(SecureJson.toJson(buildNonsensitivePojo()));
        assertNonsensitiveTimeFields(node);
        assertMasked(node, "secret");
    }

    // ── Custom mapper ─────────────────────────────────────────────────────────
    @Test
    public void testSensitiveTimeFields_CustomMapper() throws JsonProcessingException {
        JsonNode node = toNode(SecureJson.toJson(new ObjectMapper(), buildSensitivePojo(), false, SecureLog.Default.class));
        assertSensitiveTimeFields(node);
        assertMasked(node, "secret");
    }

    @Test
    public void testNonsensitiveTimeFields_CustomMapper() throws JsonProcessingException {
        JsonNode node = toNode(SecureJson.toJson(new ObjectMapper(), buildNonsensitivePojo(), false, SecureLog.Default.class));
        assertNonsensitiveTimeFields(node);
        assertMasked(node, "secret");
    }

    // ── Assertions ────────────────────────────────────────────────────────────
    private void assertSensitiveTimeFields(JsonNode node) {
        assertMasked(node, "createdAt");
        assertMasked(node, "birthDate");
        assertMasked(node, "loginTime");
    }

    private void assertNonsensitiveTimeFields(JsonNode node) {
        assertField(node, "createdAt", "2026-05-15T10:30:45");
        assertField(node, "birthDate", "1998-01-20");
        assertField(node, "loginTime", "09:15:10");
    }

    private void assertMasked(JsonNode node, String field) {
        assertField(node, field, "XXXX");
    }

    private void assertField(JsonNode node, String field, String expected) {
        assertNotNull("Field missing: " + field, node.get(field));
        assertEquals(expected, node.get(field).asText());
    }

    // ── Builders ──────────────────────────────────────────────────────────────
    private JavaTimePojoWithSensitiveDateAndTime buildSensitivePojo() {
        JavaTimePojoWithSensitiveDateAndTime pojo = new JavaTimePojoWithSensitiveDateAndTime();
        populateCommonFields(pojo::setName, pojo::setCreatedAt, pojo::setBirthDate, pojo::setLoginTime, pojo::setSecret);
        return pojo;
    }

    private JavaTimePojoWithNonsensitiveDateAndTime buildNonsensitivePojo() {
        JavaTimePojoWithNonsensitiveDateAndTime pojo = new JavaTimePojoWithNonsensitiveDateAndTime();
        populateCommonFields(pojo::setName, pojo::setCreatedAt, pojo::setBirthDate, pojo::setLoginTime, pojo::setSecret);
        return pojo;
    }

    private void populateCommonFields(
            Consumer<String> setName,
            Consumer<LocalDateTime> setCreatedAt,
            Consumer<LocalDate>     setBirthDate,
            Consumer<LocalTime>     setLoginTime,
            Consumer<String>        setSecret
    ) {
        setName.accept("Test User");
        setCreatedAt.accept(CREATED_AT);
        setBirthDate.accept(BIRTH_DATE);
        setLoginTime.accept(LOGIN_TIME);
        setSecret.accept("my-secret");
    }

    @Data @SecureLog
    public static class JavaTimePojoWithSensitiveDateAndTime {
        private String name;
        @LogSensitive private LocalDateTime createdAt;
        @LogSensitive private LocalDate     birthDate;
        @LogSensitive private LocalTime     loginTime;
        @LogSensitive private String        secret;
    }

    @Data @SecureLog
    public static class JavaTimePojoWithNonsensitiveDateAndTime {
        private String        name;
        private LocalDateTime createdAt;
        private LocalDate     birthDate;
        private LocalTime     loginTime;
        @LogSensitive private String secret;
    }

    private JsonNode toNode(String json) throws JsonProcessingException {
        return TestUtils.asJsonNode(json);
    }
}