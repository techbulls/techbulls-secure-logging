/*
 *    Copyright 2022 TechBulls SoftTech
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
  * @version 0.1
 * @since  0.1
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
     * Boolean to specify if null values are to be masked | <b>Default false</b>
     * */
    boolean secureNullValues() default false;
}
