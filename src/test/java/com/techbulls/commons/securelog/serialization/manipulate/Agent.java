package com.techbulls.commons.securelog.serialization.manipulate;
import java.lang.instrument.Instrumentation;

public class Agent {
    public static void premain(String args, Instrumentation instrumentation) {
        System.out.println("PRE MAIN");
        StringTransformer transformer = new StringTransformer();
        instrumentation.addTransformer(transformer);
    }

    public static void agentmain(String args, Instrumentation instrumentation) {
        System.out.println("PRE MAIN");
        StringTransformer transformer = new StringTransformer();
        instrumentation.addTransformer(transformer);
    }
}
