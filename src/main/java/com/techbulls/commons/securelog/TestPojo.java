package com.techbulls.commons.securelog;

import com.techbulls.commons.securelog.annotation.LogSensitive;
import com.techbulls.commons.securelog.annotation.SecureClass;


@SecureClass()
public class TestPojo {
    private String publicData;
    @LogSensitive(value = "XYZABCDE")
    private String confidential;

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

    @Override
    public String toString() {
        return "TestPojo{" +
                "publicData='" + publicData + '\'' +
                ", confidential='" + confidential + '\'' +
                '}';
    }
//    public String toString(){
//        try {
//            return SecureLogUtils.safeToString(this);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
}
