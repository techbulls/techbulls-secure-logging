package com.techbulls.commons.securelog.annotation;

import com.techbulls.commons.securelog.DefaultValueFormatter;
import com.techbulls.commons.securelog.ValueFormatter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogSensitive {
    String value() default "XXXX";
    Class<? extends ValueFormatter> formatter() default DefaultValueFormatter.class;
}
