package com.techbulls.commons.securelog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.techbulls.commons.securelog.annotation.LogSensitive;
import com.techbulls.commons.securelog.annotation.SecureClass;
import com.techbulls.commons.securelog.serialization.SecureLogUtils;


@SecureClass()
public class TestPojo {
    private String publicData;
    @LogSensitive(value = "XXXX")
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

}
