package com.techbulls.commons.securelog.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * <h3>SecureLog Annotation</h3>
 * This is a class level annotation that will allow us to set view for
 * secure logging and also to specify pretty print json
 * <p>
 * @author Kalpak Gadre
 * @version 1.0.0
 * @since  01 August 2022
 *
 * DefaultValueFormatter
 * */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SecureLog {
    boolean pretty() default false;

    Class<?> view() default Default.class;


    class Default {

    }
}
