package com.techbulls.commons.securelog;



public class Main {
    public static void main(String[] args) {
        System.out.println("In Main");
        TestPojo tb=new TestPojo();
        tb.setPublicData("public");
        tb.setConfidential("secret");

        System.out.println(tb);
    }
}
