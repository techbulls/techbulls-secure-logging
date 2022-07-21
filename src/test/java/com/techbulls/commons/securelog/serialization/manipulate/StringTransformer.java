package com.techbulls.commons.securelog.serialization.manipulate;
import javassist.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
public class StringTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(//region other parameters
                            ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            //endregion
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            ClassPool cp = ClassPool.getDefault();
            cp.importPackage("com.techbulls.commons.securelog.serialization");
            CtClass testClass = cp.get("com.techbulls.commons.securelog.serialization.TestBean");

            testClass.addMethod(CtNewMethod.make(
                    "public string toString(){" +
                            "return \"test\");" +
                            "}",
                    testClass
            ));
            Class clazz = testClass.toClass();

            Method[] declaredMethods = clazz.getMethods();
            for (Method method : declaredMethods) {
                System.out.println("method = " + method);
            }
            return testClass.toBytecode();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classfileBuffer;
    }
}
