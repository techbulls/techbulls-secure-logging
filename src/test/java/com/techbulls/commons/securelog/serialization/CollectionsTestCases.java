package com.techbulls.commons.securelog.serialization;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.techbulls.commons.securelog.annotation.LogSensitive;
import com.techbulls.commons.securelog.annotation.SecureLog;
import org.junit.Test;

import java.util.*;

public class CollectionsTestCases {
    @Test
    public void testSecureToString() throws JsonProcessingException, IllegalAccessException {
        CollectionTestPojo bean = new CollectionTestPojo();
        bean.setList(new ArrayList());
        bean.setQueue(new LinkedList<>());
        bean.setMap(new HashMap<>());
        bean.setSet(new HashSet());
        String safeToString = SecureLogUtils.safeToString(bean);
        System.out.println("TS01:"+safeToString);
        bean.setPublicData("PUBLIC");
        bean.setQueue(null);
        safeToString = SecureLogUtils.safeToString(bean);
        System.out.println("TS02:"+safeToString);
    }


    public interface View{
        public interface Show{}
        public interface Hide{}
    }

    public static class CollectionTestPojo {

        private String publicData;

        @LogSensitive(value = "XYZABCDE")
        private List list;

        @LogSensitive(value = "XYZABCDE")
        private Queue queue;

        @LogSensitive(value = "XYZABCDE")
        private Set set;

        @LogSensitive(value = "XYZABCDE")
        private Map<String,Integer> map;


        @JsonView(View.Hide.class)
        private String hidden;



        public String getPublicData() {
            return publicData;
        }

        public void setPublicData(String publicData) {
            this.publicData = publicData;
        }

        public List getList() {
            return list;
        }

        public void setList(List list) {
            this.list = list;
        }

        public Queue getQueue() {
            return queue;
        }

        public void setQueue(Queue queue) {
            this.queue = queue;
        }

        public Set getSet() {
            return set;
        }

        public void setSet(Set set) {
            this.set = set;
        }

        public Map<String, Integer> getMap() {
            return map;
        }

        public void setMap(Map<String, Integer> map) {
            this.map = map;
        }
    }
}