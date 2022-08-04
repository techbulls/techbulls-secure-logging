package com.techbulls.commons.securelog.serialization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.techbulls.commons.securelog.annotation.LogSensitive;
import com.techbulls.commons.securelog.annotation.SecureLog;
import org.junit.Test;

import static com.techbulls.commons.securelog.serialization.TestUtils.assertContainsNodeWithText;
import static com.techbulls.commons.securelog.serialization.TestUtils.assertContainsNodeWithoutText;

public class SecureVisibilityTest {

    @Test
    public void testSafeToStringWithVisibility() throws JsonProcessingException, IllegalAccessException {
        AnyPojo ap=new AnyPojo();
        ap.protectedData="Protected Data";
        ap.publicData="Public Data";
        ap.privateData="Private Data";

        String json = SecureJson.toJson(ap);
        System.out.println("TS01:" + json);
        JsonNode root = TestUtils.asJsonNode(json);
        assertContainsNodeWithText(root, "protectedData", ap.protectedData);
        assertContainsNodeWithText(root, "publicData", ap.publicData);
        assertContainsNodeWithText(root, "privateData", ap.privateData);

        NonPrivate np=new NonPrivate();
        np.protectedData="Protected Data";
        np.publicData="Public Data";
        np.privateData="Private Data";

        json = SecureJson.toJson(np);
        System.out.println("TS02:" + json);
        root = TestUtils.asJsonNode(json);
        assertContainsNodeWithText(root, "protectedData", np.protectedData);
        assertContainsNodeWithText(root, "publicData", "XXXX");
        assertContainsNodeWithoutText(root, "privateData", np.privateData);

    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public class AnyPojo {
        private String privateData;
        //@LogSensitive(value = "ZZZZ")
        protected String protectedData;
        public String publicData;
    }

    @SecureLog
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NON_PRIVATE)
    public class NonPrivate {
        //@LogSensitive(value = "ZZZZ")
        private String privateData;
        protected String protectedData;
        @LogSensitive
        public String publicData;
    }
}
