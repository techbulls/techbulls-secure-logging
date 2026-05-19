package com.techbulls.commons.securelog.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techbulls.commons.securelog.annotation.LogSensitive;
import com.techbulls.commons.securelog.annotation.SecureLog;
import org.junit.Test;

public class SecureNestedClassTest {
    @Test
    public void testSecureToString() throws JsonProcessingException, IllegalAccessException {
        DemoClass bean = new DemoClass();
        InnerClass ic=new InnerClass();
        ic.setSecret("test");
        ic.setValue("value");
        NestedInnerClass nic=new NestedInnerClass();
        nic.secret="sec";
        nic.value="val";
        ic.nestedInnerClass=nic;
        ic.nestedSecureInnerClass=nic;
        ic.setSecret("sec");
        ic.setValue("val123");
        bean.setInnerClass(ic);
        bean.setInnerSecureClass(ic);
        String  safeToString = SecureJson.toJson(bean);
        TestUtils.testObject(safeToString,bean,bean.getClass());
        System.out.println("TS01:"+safeToString);


        ObjectMapper mapper = new ObjectMapper();
        safeToString = SecureJson.toJson(mapper,bean,true,null);
        TestUtils.testObject(safeToString,bean,bean.getClass());
        System.out.println("TS02:"+safeToString);

    }

    public class DemoClass{
        private InnerClass innerClass;

        @LogSensitive(value="####")
        public InnerClass innerSecureClass;

        public InnerClass getInnerClass() {
            return innerClass;
        }

        public void setInnerClass(InnerClass innerClass) {
            this.innerClass = innerClass;
        }

        public InnerClass getInnerSecureClass() {
            return innerSecureClass;
        }

        public void setInnerSecureClass(InnerClass innerSecureClass) {
            this.innerSecureClass = innerSecureClass;
        }
    }


    @SecureLog(pretty = true)
    public class InnerClass{

        @LogSensitive(value = "YYYY")
        private String secret;

        @LogSensitive()
        private String value;


        public  NestedInnerClass nestedInnerClass;

        @LogSensitive()
        public  NestedInnerClass nestedSecureInnerClass;


        public String getSecret() {
            return secret;
        }


        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }


    @SecureLog(pretty = true)
    public class NestedInnerClass{
        @LogSensitive(value = "ZZZZ")
        public String secret;

        public String value;
    }
}
