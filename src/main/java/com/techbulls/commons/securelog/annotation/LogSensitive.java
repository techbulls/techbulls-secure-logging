package com.techbulls.commons.securelog.annotation;
import com.techbulls.commons.securelog.DefaultValueFormatter;
import com.techbulls.commons.securelog.ValueFormatter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * <h3>LogSensitive Annotation</h3>
 * This is a field level annotation that will enable masking the field value as per configuration
 * <p>
 * @author Kalpak Gadre
 * @version 1.0.0
 * @since  01 August 2022
 * */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogSensitive {
    /**
     * Mask value | <b>Default XXXX</b>
     * */
    String value() default "XXXX";
    /**
     * Formatter to be used for masking | <b>Default DefaultValueFormatter</b>
     * @see com.techbulls.commons.securelog.DefaultValueFormatter
     * */
    Class<? extends ValueFormatter> formatter() default DefaultValueFormatter.class;

    /**
     * Boolean to specify if null values are to be masked | <b>Default false<b/>
     * */
    boolean secureNullValues() default false;
}
