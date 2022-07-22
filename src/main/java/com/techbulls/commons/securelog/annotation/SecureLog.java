package com.techbulls.commons.securelog.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SecureLog {
    boolean pretty() default false;

    Class<?> view() default Default.class;

    class Default {

    }
}
