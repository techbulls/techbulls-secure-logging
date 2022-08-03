/*
 *    Copyright 2022 TechBulls SoftTech
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.techbulls.commons.securelog.serialization;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.techbulls.commons.securelog.annotation.LogSensitive;
import com.techbulls.commons.securelog.annotation.SecureLog;
import org.junit.Test;

import java.util.*;

public class TestCases {
    @Test
    public void testSecureToString() throws JsonProcessingException, IllegalAccessException {
        CollectionTestPojo bean = new CollectionTestPojo();
        //bean.setList(new ArrayList());
        bean.setSecretQueue(new LinkedList<>());
        bean.setSecretMap(new HashMap<>());
        bean.setSecretSet(new HashSet());
        String safeToString = SecureJson.toJson(bean);
        TestUtils.testObject(safeToString,bean,bean.getClass());
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
        safeToString = SecureJson.toJson(bean);
        TestUtils.testObject(safeToString,bean,bean.getClass());
        System.out.println("TS02:"+safeToString);
    }


    public interface View{
        public interface Show{}
        public interface Hide{}
    }

    @SecureLog(pretty = true)
    public static class CollectionTestPojo {

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
        private String hidden;

        private InnerClass innerClass;

        @LogSensitive(value="XXXX")
        public InnerClass innerSecureClass;

        @LogSensitive(value="XXXX")
        public List<InnerClass> innerClassSecureList;

        private List<InnerClass> innerClassList;

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