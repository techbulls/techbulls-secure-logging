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
package com.techbulls.commons.securelog.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.techbulls.commons.securelog.ValueFormatter;
import com.techbulls.commons.securelog.annotation.LogSensitive;

import java.io.IOException;

/**
 * <h3>NullSecurePropertySerializer Class</h3>
 * This class extends JsonSerializer class to provide custom serialization of null values
 * <p>
 * @see com.techbulls.commons.securelog.ValueFormatter
 * @see com.fasterxml.jackson.databind.JsonSerializer
 * @see com.techbulls.commons.securelog.annotation.LogSensitive
 * @author Vaibhav Ghatge
 * @version 1.0.0
 * @since  01 August 2022
 * */
public class NullSecurePropertySerializer<T> extends JsonSerializer<T> {
    /**The Value formatter used for formatting
     **/
    private final ValueFormatter formatter;
    /**The Secure value based on annotation
     **/
    private final String secureValue;

    /**
     * The parameterised constructor
     * @param  annotation <b>LogSensitive</b> to set secure value and formatter
     **/
    public NullSecurePropertySerializer(LogSensitive annotation) {
        this.formatter = SecureJson.instantiate(annotation.formatter());
        this.secureValue = annotation.value();
    }

    @Override
    public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(formatter.format(o, secureValue));
    }

}
