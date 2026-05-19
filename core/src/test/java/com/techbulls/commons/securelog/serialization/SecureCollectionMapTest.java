package com.techbulls.commons.securelog.serialization;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.techbulls.commons.securelog.annotation.LogSensitive;
import com.techbulls.commons.securelog.annotation.SecureLog;
import org.junit.Test;

import java.util.*;
public class SecureCollectionMapTest {


    @Test
    public void testSecureToString() throws JsonProcessingException, IllegalAccessException {
        CollectionTestPojo bean = new CollectionTestPojo();
        bean.setSecretQueue(new LinkedList<>());
        bean.setSecretMap(new HashMap<>());
        bean.setSecretSet(new HashSet());
        String safeToString = SecureJson.toJson(bean);
        TestUtils.testObject(safeToString,bean,bean.getClass());
        System.out.println("TS01:"+safeToString);

        bean.setPublicData("PUBLIC");
        bean.setSecretQueue(null);
        InnerClass ic=new InnerClass();
        ic.setSecret("test");
        ic.setValue("value");
        InnerClass ic2=new InnerClass();
        ic2.setSecret("test");
        ic2.setValue("ABCD");
        bean.setInnerClass(ic);
        List<InnerClass> icl=new LinkedList<>();
        ic.nestedInnerClass=new NestedInnerClass();
        ic.setSecret("sec");
        ic.setValue("val123");
        ic.nestedSecureInnerClass=new NestedInnerClass();
        bean.setInnerClass(ic);
        icl.add(ic);
        icl.add(ic2);
        bean.setInnerClassList(icl);
        bean.innerClassSecureList=icl;
        bean.innerSecureClass=new InnerClass();
        safeToString = SecureJson.toJson(bean);
        TestUtils.testObject(safeToString,bean,bean.getClass());
        System.out.println("TS02:"+safeToString);
    }



    public class CollectionTestPojo {
        private String publicData;

        @LogSensitive(value = "####")
        private List secretList;

        @LogSensitive(value = "####")
        private Queue secretQueue;

        @LogSensitive(value = "####")
        private Set secretSet;

        @LogSensitive(value = "####")
        private Map<String,Integer> secretMap;

        private InnerClass innerClass;

        @LogSensitive(value="####")
        public InnerClass innerSecureClass;

        @LogSensitive(value="####")
        public List<InnerClass> innerClassSecureList;

        private List<InnerClass> innerClassList;

        private String privateData;

        @LogSensitive(value="####")
        private String privateSecureData;

        public String getPublicData() {
            return publicData;
        }

        public void setPublicData(String publicData) {
            this.publicData = publicData;
        }
        public List getSecretList() {
            return secretList;
        }
        public void setSecretList(List secretList) {
            this.secretList = secretList;
        }
        public Queue getSecretQueue() {
            return secretQueue;
        }
        public void setSecretQueue(Queue secretQueue) {
            this.secretQueue = secretQueue;
        }
        public Set getSecretSet() {
            return secretSet;
        }
        public void setSecretSet(Set secretSet) {
            this.secretSet = secretSet;
        }
        public Map<String, Integer> getSecretMap() {
            return secretMap;
        }
        public void setSecretMap(Map<String, Integer> secretMap) {
            this.secretMap = secretMap;
        }
        public InnerClass getInnerClass() { return innerClass; }
        public void setInnerClass(InnerClass innerClass) { this.innerClass = innerClass; }

        public List<InnerClass> getInnerClassList() { return innerClassList; }

        public void setInnerClassList(List<InnerClass> innerClassList) {this.innerClassList = innerClassList;}
    }


    @SecureLog(pretty = true)
    public class InnerClass{

        @LogSensitive(value = "YYYY")
        private String secret;

        @LogSensitive()
        private String value;


        public NestedInnerClass nestedInnerClass;

        @LogSensitive()
        public NestedInnerClass nestedSecureInnerClass;


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
        private String secret;

        private String value;
    }


}
