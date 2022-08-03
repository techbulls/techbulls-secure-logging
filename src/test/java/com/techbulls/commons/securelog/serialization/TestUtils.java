package com.techbulls.commons.securelog.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techbulls.commons.securelog.ValueFormatter;
import com.techbulls.commons.securelog.annotation.LogSensitive;
import org.junit.Assert;

import java.lang.reflect.Field;

public class TestUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String getExpectedValue(Object bean, Field f, LogSensitive annotation) throws IllegalAccessException {
        ValueFormatter formatter = SecureJson.instantiate(annotation.formatter());
        if(f.get(bean)!=null) {
            return formatter.format(f.get(bean).toString(), annotation.value());
        }else{
            return formatter.format("", annotation.value());
        }
    }

    public static void assertContainsNodeWithText(JsonNode node, String key, String value) {
        JsonNode child = node.get(key);
        Assert.assertNotNull("Unable to find key " + key + " in the given JSON", child);
        Assert.assertEquals(value, child.asText());
    }

    public static void assertContainsNodeWithoutText(JsonNode node, String key, String value) {
        JsonNode child = node.get(key);
        Assert.assertEquals(null,child);
    }

    public static void testObject(String safeToString,Object bean,Class T) throws JsonProcessingException, IllegalAccessException {
        JsonNode node = MAPPER.readTree(safeToString);
        Field[] fields = T.getDeclaredFields();
        int count=1;
        for (Field f : fields) {
            LogSensitive annotation = f.getAnnotation(LogSensitive.class);
            f.setAccessible(true);
            Object value = f.get(bean);
            if (annotation!=null) {
                String expected =TestUtils.getExpectedValue(bean, f, annotation);
                if (value != null && node.get(f.getName())!=null) {
//                    System.out.println(count+")Secure Field: ["+f.getType().getName()+"] "+f.getName()+" Expected:"+expected+"\tActual:"+node.get(f.getName()).asText());
                    TestUtils.assertContainsNodeWithText(node, f.getName(), expected);
                }else{
                    //System.out.println(count+")Secure Field NULL: ["+f.getType().getName()+"] "+f.getName()+" Expected:"+expected+"\tActual:"+node.get(f.getName()).asText());
                }
            }else{
                //System.out.println(count+")Public Field: ["+f.getType().getName()+"] "+f.getName());
            }
            count++;
        }
    }
    public static void assertNodeDoesNotExist(JsonNode root, String key) {
        JsonNode node = root.get(key);
        Assert.assertNull("Node " + key + " exists", node);
    }

    public static void assertNodeIsNull(JsonNode root, String key) {
        JsonNode node = root.get(key);
        Assert.assertNotNull("Unable to find key " + key + " in the given JSON", node);
        String value = node.asText();
        Assert.assertNull(value);
    }

    public static JsonNode asJsonNode(String json) throws JsonProcessingException {
        return MAPPER.readTree(json);
    }
}
