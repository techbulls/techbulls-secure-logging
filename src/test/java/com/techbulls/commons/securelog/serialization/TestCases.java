package com.techbulls.commons.securelog.serialization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.techbulls.commons.securelog.annotation.LogSensitive;
import com.techbulls.commons.securelog.annotation.SecureLog;
import org.junit.Test;

import java.util.*;

import static com.techbulls.commons.securelog.serialization.TestUtils.assertContainsNodeWithText;
import static com.techbulls.commons.securelog.serialization.TestUtils.assertContainsNodeWithoutText;

public class TestCases {
    @Test
    public void testSecureToString() throws JsonProcessingException, IllegalAccessException {
        CollectionTestPojo bean = new CollectionTestPojo();
        //bean.setList(new ArrayList());
        bean.setSecretQueue(new LinkedList<>());
        bean.setSecretMap(new HashMap<>());
        bean.setSecretSet(new HashSet());
        String safeToString = SecureLogUtils.safeToString(bean);
        bean.setHidden("test");
        //TestUtils.testObject(safeToString,bean,bean.getClass());
        System.out.println("TS01:"+safeToString);

        bean.setPublicData("PUBLIC");
        bean.setSecretQueue(null);
        bean.setHidden("test");
        InnerClass ic=new InnerClass();
        ic.setSecret("test");
        ic.setValue("value");
        InnerClass ic2=new InnerClass();
        ic2.setSecret("test");
        ic2.setValue("xx");
        bean.setInnerClass(ic);
        List<InnerClass> icl=new LinkedList<>();
        ic.nestedInnerClass=new NestedInnerClass();
        ic.setSecret("sec");
        ic.setValue("val");
        ic.nestedSecureInnerClass=new NestedInnerClass();
        bean.setInnerClass(ic);
        icl.add(ic);
        icl.add(ic2);
        bean.setInnerClassList(icl);
        bean.innerClassSecureList=icl;
        bean.innerSecureClass=new InnerClass();
        safeToString = SecureLogUtils.safeToString(bean);
        TestUtils.testObject(safeToString,bean,bean.getClass());
        System.out.println("TS02:"+safeToString);
    }

    @Test
    public void testSafeToStringWithVisibility() throws JsonProcessingException, IllegalAccessException {
        AnyPojo ap=new AnyPojo();
        ap.protectedData="Protected Data";
        ap.publicData="Public Data";
        ap.privateData="Private Data";

        String json = SecureLogUtils.safeToString(ap);
        System.out.println("TS01: " + json);
        JsonNode root = TestUtils.asJsonNode(json);
        assertContainsNodeWithText(root, "protectedData", ap.protectedData);
        assertContainsNodeWithText(root, "publicData", ap.publicData);
        assertContainsNodeWithText(root, "privateData", ap.privateData);

        NonPrivate np=new NonPrivate();
        np.protectedData="Protected Data";
        np.publicData="Public Data";
        np.privateData="Private Data";

        json = SecureLogUtils.safeToString(np);
        System.out.println("TS02: " + json);
        root = TestUtils.asJsonNode(json);
        assertContainsNodeWithText(root, "protectedData", np.protectedData);
        assertContainsNodeWithText(root, "publicData", np.publicData);
        assertContainsNodeWithoutText(root, "privateData", np.privateData);


    }


    public interface View{
        public interface Show{}
        public interface Hide{}
    }

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AnyPojo {
    private String privateData;
    protected String protectedData;
    public String publicData;
}

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NON_PRIVATE)
public class NonPrivate {

    private String privateData;
    protected String protectedData;
    public String publicData;
}

    //@SecureLog(pretty = true,view = View.Hide.class)
@SecureLog
//@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CollectionTestPojo {

        private String publicData;

        @LogSensitive(value = "XXXX")
        private List secretList;

        @LogSensitive(value = "XXXX")
        private Queue secretQueue;

        @LogSensitive(value = "XXXX")
        private Set secretSet;

        @LogSensitive(value = "XXXX")
        private Map<String,Integer> secretMap;


        @JsonView(View.Hide.class)
        @LogSensitive(value = "XXXX",secureNullValues=true)
        private String hidden;

        private InnerClass innerClass;

        @LogSensitive(value="XXXX")
        public InnerClass innerSecureClass;

        @LogSensitive(value="XXXX")
        public List<InnerClass> innerClassSecureList;

        private List<InnerClass> innerClassList;

        private String privateData;

        @LogSensitive(value="XXXX")
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

        public String getHidden() { return hidden;  }
        public void setHidden(String hidden) { this.hidden = hidden;  }

        public InnerClass getInnerClass() { return innerClass; }
        public void setInnerClass(InnerClass innerClass) { this.innerClass = innerClass; }

        public List<InnerClass> getInnerClassList() { return innerClassList; }

        public void setInnerClassList(List<InnerClass> innerClassList) {this.innerClassList = innerClassList;}
    }
    @SecureLog(pretty = true)
    public class InnerClass{

        @LogSensitive(value = "YYYY")
        private String secret;

        private String value;


        public NestedInnerClass nestedInnerClass;

        @LogSensitive(value = "YYYY")
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