package com.techbulls.commons.securelog.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techbulls.commons.securelog.ValueFormatter;
import com.techbulls.commons.securelog.annotation.LogSensitive;
import org.junit.Assert;

import java.lang.reflect.Field;

public class TestUtils {

    public static String getExpectedValue(Object bean, Field f, LogSensitive annotation) throws IllegalAccessException {
        ValueFormatter formatter = SecureLogUtils.instantiate(annotation.formatter());
        return formatter.format(f.get(bean).toString(), annotation.value());
    }

    public static void assertContainsNodeWithText(JsonNode node, String key, String value) {
        JsonNode child = node.get(key);
        Assert.assertNotNull(child);
        Assert.assertEquals(value, child.asText());
    }

    public static void testObject(String safeToString,Object bean,Class T) throws JsonProcessingException, IllegalAccessException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(safeToString);
        Field[] fields = T.getDeclaredFields();
        int count=1;
        System.out.println("Fields");
        for (Field f : fields) {
            LogSensitive annotation = f.getAnnotation(LogSensitive.class);
            f.setAccessible(true);
            Object value = f.get(bean);
            if (annotation!=null) {
                if (value != null) {
                    String expected =TestUtils.getExpectedValue(bean, f, annotation);
                    System.out.println(count+")Secure Field: ["+f.getType().getName()+"] "+f.getName()+" Expected:"+expected+"\tActual:"+node.get(f.getName()).asText());
                    TestUtils.assertContainsNodeWithText(node, f.getName(), expected);
                }else{
                    System.out.println(count+")Secure Field NULL: ["+f.getType().getName()+"] "+f.getName());
                }
            }else{
                System.out.println(count+")Public Field: ["+f.getType().getName()+"] "+f.getName());
            }
            count++;
        }
    }
}