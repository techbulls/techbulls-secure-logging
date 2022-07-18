package com.techbulls.commons.securelog.serialization;

import com.techbulls.commons.securelog.annotation.LogSensitive;

public class TestBean {
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
}
