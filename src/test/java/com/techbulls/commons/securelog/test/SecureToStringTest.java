package com.techbulls.commons.securelog.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techbulls.commons.securelog.annotation.LogSensitive;
import com.techbulls.commons.securelog.serialization.SecureLogUtils;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;

public class SecureToStringTest {
    @Test
    public void testSecureToString() throws JsonProcessingException, IllegalAccessException {
        TestBean bean = new TestBean();
        bean.setPublicData("public");
        bean.setConfidential("secret");
        bean.setSecureInt(1234);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(SecureLogUtils.safeToString(bean));

        Field[] fields = TestBean.class.getDeclaredFields();

        for (Field f : fields) {
            LogSensitive annotation = f.getAnnotation(LogSensitive.class);
            f.setAccessible(true);
            Object value = f.get(bean);
            if (value != null) {
                String expected = value.toString();
                if (annotation != null) {
                    expected = annotation.value();
                    assertContainsNodeWithText(node, f.getName(), expected);
                }
            }
        }
    }

    private void assertContainsNodeWithText(JsonNode node, String key, String value) {
        JsonNode child = node.get(key);
        Assert.assertNotNull(child);
        Assert.assertEquals(value, child.asText());
    }

    public static class TestBean {
        private String publicData;
        @LogSensitive(value = "xxxxxxx")
        private String confidential;

        @LogSensitive(value = "XXX")
        private Integer secureInt;

        public String getPublicData() {
            return publicData;
        }

        public void setPublicData(String publicData) {
            this.publicData = publicData;
        }

        public String getConfidential() {
            return confidential;
        }

        public void setConfidential(String confidential) {
            this.confidential = confidential;
        }

        public Integer getSecureInt() {
            return secureInt;
        }

        public void setSecureInt(Integer secureInt) {
            this.secureInt = secureInt;
        }
    }

}
