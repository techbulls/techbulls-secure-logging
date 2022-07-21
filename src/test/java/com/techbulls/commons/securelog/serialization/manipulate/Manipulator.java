package com.techbulls.commons.securelog.serialization.manipulate;

import com.techbulls.commons.securelog.serialization.SecureLogUtilsTest;
import com.techbulls.commons.securelog.serialization.TestBean;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

import java.lang.reflect.Method;

public class Manipulator {
    public static void main(String[] args) throws Exception {
//        ClassPool cp = ClassPool.getDefault();
//        cp.importPackage("com.techbulls.commons.securelog.serialization");
//        CtClass testClass = cp.get("com.techbulls.commons.securelog.serialization.TestBean");
//        testClass.addInterface(cp.get("com.techbulls.commons.securelog.serialization.manipulate.OverrideString"));
//        testClass.addMethod(CtNewMethod.make(
//                "
//                public String toString(){" +
//                        "return \"test\";" +
//                        "}",
//                testClass
//        ));
//        testClass.toClass().newInstance();
//        TestBean tb=new TestBean();
//        SecureLogUtilsTest.TestBean bean = new SecureLogUtilsTest.TestBean();
//        bean.setPublicString("public");
//        bean.setPublicInt(456);
//        bean.setSecureString("secret");
//        bean.setFormattedSecureString("ABCdefGHI");
//        bean.setSecureInt(1234);
//        if(tb instanceof OverrideString){
//            System.out.println("Works");
//        }
//        System.out.println("Safe toString(): " + tb.toString());
//

        ClassPool cp = ClassPool.getDefault();
        cp.importPackage("com.techbulls.commons.securelog.serialization");
        CtClass testClass = cp.get("com.techbulls.commons.securelog.serialization.TestBean");
        testClass.addMethod(CtNewMethod.make(
                "public String toString(){" +
                        "System.out.println(SecureLogUtils.safeToString(this));"+
                        "return \"ad\"+SecureLogUtils.safeToString(this);" +
                        "}",
                testClass
        ));
        Class clazz = testClass.toClass();
        Method[] declaredMethods = clazz.getMethods();
        for (Method method : declaredMethods) {
            System.out.println("method = " + method);
        }
//        CtMethod m = testClass.getDeclaredMethod("toString");
//        m.insertBefore("{ System.out.println(\"test\");}");
        testClass.writeFile();



        Util.invokeViaReflection(clazz, "toString");


    }

    @Override
    public String toString() {
        return "Manipulator{}";
    }
}
