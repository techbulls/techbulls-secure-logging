package com.techbulls.commons.securelog.serialization.manipulate;

import com.techbulls.commons.securelog.serialization.SecureLogUtilsTest;
import com.techbulls.commons.securelog.serialization.TestBean;

public class Main {
    public static void main(String[] args) {
        TestBean tb=new TestBean();
        SecureLogUtilsTest.TestBean bean = new SecureLogUtilsTest.TestBean();
        bean.setPublicString("public");
        bean.setPublicInt(456);
        bean.setSecureString("secret");
        bean.setFormattedSecureString("ABCdefGHI");
        bean.setSecureInt(1234);
        System.out.println(tb);
    }
}
