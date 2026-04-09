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
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.techbulls.commons.securelog.annotation.LogSensitive;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;

import static org.junit.Assert.*;

public class SecurePropertySerializerDelegationTest {

    private StubJsonSerializer stubDelegate;
    private SecurePropertySerializer<Object> serializer;

    @Before
    public void setUp() throws NoSuchFieldException {
        stubDelegate = new StubJsonSerializer();
        LogSensitive annotation = AnnotationHolder.class
                .getDeclaredField("annotatedField")
                .getAnnotation(LogSensitive.class);
        serializer = new SecurePropertySerializer<>(stubDelegate, annotation);
    }

    @Test
    public void testUnwrappingSerializerDelegates() {
        JsonSerializer<Object> result = serializer.unwrappingSerializer(NameTransformer.NOP);
        assertTrue(stubDelegate.unwrappingSerializerCalled);
        assertSame(stubDelegate.cannedUnwrappingSerializer, result);
    }

    @Test
    public void testReplaceDelegateeDelegates() {
        JsonSerializer<Object> replacement = new StubJsonSerializer();
        JsonSerializer<Object> result = serializer.replaceDelegatee(replacement);
        assertTrue(stubDelegate.replaceDelegateeCalled);
        assertSame(stubDelegate.cannedReplaceDelegatee, result);
    }

    @Test
    public void testWithFilterIdDelegates() {
        JsonSerializer<?> result = serializer.withFilterId("myFilter");
        assertTrue(stubDelegate.withFilterIdCalled);
        assertSame(stubDelegate.cannedWithFilterId, result);
    }

    @Test
    public void testHandledTypeDelegates() {
        Class<Object> result = serializer.handledType();
        assertTrue(stubDelegate.handledTypeCalled);
        assertEquals(String.class, result);
    }

    @Test
    public void testIsEmptyDelegates() {
        boolean result = serializer.isEmpty(null, "someValue");
        assertTrue(stubDelegate.isEmptyCalled);
        assertTrue(result);
    }

    @Test
    public void testUsesObjectIdDelegates() {
        boolean result = serializer.usesObjectId();
        assertTrue(stubDelegate.usesObjectIdCalled);
        assertTrue(result);
    }

    @Test
    public void testIsUnwrappingSerializerDelegates() {
        boolean result = serializer.isUnwrappingSerializer();
        assertTrue(stubDelegate.isUnwrappingSerializerCalled);
        assertTrue(result);
    }

    @Test
    public void testGetDelegateeDelegates() {
        JsonSerializer<?> result = serializer.getDelegatee();
        assertTrue(stubDelegate.getDelegateeCalled);
        assertSame(stubDelegate.cannedDelegatee, result);
    }

    @Test
    public void testPropertiesDelegates() {
        Iterator<PropertyWriter> result = serializer.properties();
        assertTrue(stubDelegate.propertiesCalled);
        assertSame(stubDelegate.cannedProperties, result);
    }

    @Test
    public void testAcceptJsonFormatVisitorDelegates() throws JsonMappingException {
        serializer.acceptJsonFormatVisitor(null, null);
        assertTrue(stubDelegate.acceptJsonFormatVisitorCalled);
    }

    // --- Helper: class with @LogSensitive field for annotation access ---

    private static class AnnotationHolder {
        @LogSensitive
        private String annotatedField;
    }

    // --- Stub delegate that records calls and returns canned values ---

    @SuppressWarnings("unchecked")
    private static class StubJsonSerializer extends JsonSerializer<Object> {

        boolean unwrappingSerializerCalled;
        boolean replaceDelegateeCalled;
        boolean withFilterIdCalled;
        boolean handledTypeCalled;
        boolean isEmptyCalled;
        boolean usesObjectIdCalled;
        boolean isUnwrappingSerializerCalled;
        boolean getDelegateeCalled;
        boolean propertiesCalled;
        boolean acceptJsonFormatVisitorCalled;

        final JsonSerializer<Object> cannedUnwrappingSerializer = new StubMinimalSerializer();
        final JsonSerializer<Object> cannedReplaceDelegatee = new StubMinimalSerializer();
        final JsonSerializer<Object> cannedWithFilterId = new StubMinimalSerializer();
        final JsonSerializer<Object> cannedDelegatee = new StubMinimalSerializer();
        final Iterator<PropertyWriter> cannedProperties = Collections.emptyIterator();

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            // not used in delegation tests
        }

        @Override
        public JsonSerializer<Object> unwrappingSerializer(NameTransformer unwrapper) {
            unwrappingSerializerCalled = true;
            return cannedUnwrappingSerializer;
        }

        @Override
        public JsonSerializer<Object> replaceDelegatee(JsonSerializer<?> delegatee) {
            replaceDelegateeCalled = true;
            return cannedReplaceDelegatee;
        }

        @Override
        public JsonSerializer<?> withFilterId(Object filterId) {
            withFilterIdCalled = true;
            return cannedWithFilterId;
        }

        @Override
        @SuppressWarnings("rawtypes")
        public Class handledType() {
            handledTypeCalled = true;
            return String.class;
        }

        @Override
        public boolean isEmpty(SerializerProvider provider, Object value) {
            isEmptyCalled = true;
            return true;
        }

        @Override
        public boolean usesObjectId() {
            usesObjectIdCalled = true;
            return true;
        }

        @Override
        public boolean isUnwrappingSerializer() {
            isUnwrappingSerializerCalled = true;
            return true;
        }

        @Override
        public JsonSerializer<?> getDelegatee() {
            getDelegateeCalled = true;
            return cannedDelegatee;
        }

        @Override
        public Iterator<PropertyWriter> properties() {
            propertiesCalled = true;
            return cannedProperties;
        }

        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType type) throws JsonMappingException {
            acceptJsonFormatVisitorCalled = true;
        }
    }

    private static class StubMinimalSerializer extends JsonSerializer<Object> {
        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            // minimal stub
        }
    }
}
