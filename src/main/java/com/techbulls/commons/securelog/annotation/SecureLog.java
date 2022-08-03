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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * <h3>SecureLog Annotation</h3>
 * This is a class level annotation that will allow us to set view for
 * secure logging and also to specify pretty print json
 * <p>
 * @version 0.1
 * @since 0.1
 * */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SecureLog {
    /**
     * Enables pretty json output.
     * */
    boolean pretty() default false;

    /**
     * A JsonView to be used when converting to json. If not provided, does not use a specific view.
     * */
    Class<?> view() default Default.class;


    class Default {

    }
}
