package com.techbulls.commons.securelog.serialization;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.techbulls.commons.securelog.annotation.SecureLog;
import org.junit.Test;

public class SecureFilterTest {

    @Test
    public void testSecureToString() throws JsonProcessingException, IllegalAccessException {
        ObjectMapper mapper = new ObjectMapper();
        Student student = new Student(1,13, "Mark");

        FilterProvider filters = new SimpleFilterProvider() .addFilter(
                "nameFilter", SimpleBeanPropertyFilter.filterOutAllExcept("name"));

        String jsonString = SecureJson.toJson(mapper, student, true, SecureLog.Default.class);
        System.out.println(jsonString);

    }


    @JsonFilter("nameFilter")
    class Student {
        public int id;
        public int rollNo;
        public String name;

        Student(int id, int rollNo, String name) {
            this.id = id;
            this.rollNo = rollNo;
            this.name = name;
        }
    }
}
