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
package com.techbulls.commons.securelog.slf4j;

import com.techbulls.commons.securelog.annotation.LogSensitive;
import com.techbulls.commons.securelog.annotation.SecureLog;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.Marker;

public class SecureLoggerTest {

    @SecureLog
    public static class SecureBean {
        private String name = "public";
        @LogSensitive
        private String secret = "sensitive-data";

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
    }

    public static class PlainBean {
        private String name = "plain";
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    @Test
    public void testSecureLoggerFactoryByName() {
        Logger logger = SecureLoggerFactory.getLogger("test");
        Assert.assertNotNull(logger);
        Assert.assertTrue(logger instanceof SecureLogger);
    }

    @Test
    public void testSecureLoggerFactoryByClass() {
        Logger logger = SecureLoggerFactory.getLogger(SecureLoggerTest.class);
        Assert.assertNotNull(logger);
        Assert.assertTrue(logger instanceof SecureLogger);
    }

    @Test
    public void testInfoWithSecureBean() {
        CaptureLogger capture = new CaptureLogger();
        SecureLogger logger = new SecureLogger(capture);

        SecureBean bean = new SecureBean();
        logger.info("User: {}", bean);

        String masked = (String) capture.lastArg;
        Assert.assertNotNull(masked);
        Assert.assertTrue(masked.contains("public"));
        Assert.assertFalse(masked.contains("sensitive-data"));
    }

    @Test
    public void testInfoWithPlainBean() {
        CaptureLogger capture = new CaptureLogger();
        SecureLogger logger = new SecureLogger(capture);

        PlainBean bean = new PlainBean();
        logger.info("User: {}", bean);

        Assert.assertSame(bean, capture.lastArg);
    }

    @Test
    public void testInfoWithNullArg() {
        CaptureLogger capture = new CaptureLogger();
        SecureLogger logger = new SecureLogger(capture);

        logger.info("User: {}", (Object) null);
        Assert.assertNull(capture.lastArg);
    }

    @Test
    public void testInfoVarargsWithMixedBeans() {
        CaptureLogger capture = new CaptureLogger();
        SecureLogger logger = new SecureLogger(capture);

        SecureBean secureBean = new SecureBean();
        PlainBean plainBean = new PlainBean();
        logger.info("Args: {} {} {}", secureBean, plainBean, "string");

        Object[] args = (Object[]) capture.lastArg;
        Assert.assertNotNull(args);
        Assert.assertEquals(3, args.length);
        Assert.assertTrue(args[0] instanceof String);
        Assert.assertTrue(((String) args[0]).contains("public"));
        Assert.assertSame(plainBean, args[1]);
        Assert.assertEquals("string", args[2]);
    }

    @Test
    public void testDebugWithSecureBean() {
        CaptureLogger capture = new CaptureLogger();
        SecureLogger logger = new SecureLogger(capture);

        SecureBean bean = new SecureBean();
        logger.debug("Debug: {}", bean);

        String masked = (String) capture.lastArg;
        Assert.assertNotNull(masked);
        Assert.assertTrue(masked.contains("public"));
        Assert.assertFalse(masked.contains("sensitive-data"));
    }

    @Test
    public void testWarnWithSecureBean() {
        CaptureLogger capture = new CaptureLogger();
        SecureLogger logger = new SecureLogger(capture);

        SecureBean bean = new SecureBean();
        logger.warn("Warn: {}", bean);

        String masked = (String) capture.lastArg;
        Assert.assertNotNull(masked);
        Assert.assertTrue(masked.contains("public"));
        Assert.assertFalse(masked.contains("sensitive-data"));
    }

    @Test
    public void testErrorWithSecureBean() {
        CaptureLogger capture = new CaptureLogger();
        SecureLogger logger = new SecureLogger(capture);

        SecureBean bean = new SecureBean();
        logger.error("Error: {}", bean);

        String masked = (String) capture.lastArg;
        Assert.assertNotNull(masked);
        Assert.assertTrue(masked.contains("public"));
        Assert.assertFalse(masked.contains("sensitive-data"));
    }

    @Test
    public void testTraceWithSecureBean() {
        CaptureLogger capture = new CaptureLogger();
        SecureLogger logger = new SecureLogger(capture);

        SecureBean bean = new SecureBean();
        logger.trace("Trace: {}", bean);

        String masked = (String) capture.lastArg;
        Assert.assertNotNull(masked);
        Assert.assertTrue(masked.contains("public"));
        Assert.assertFalse(masked.contains("sensitive-data"));
    }

    @Test
    public void testInfoTwoArgsWithSecureBean() {
        CaptureLogger capture = new CaptureLogger();
        SecureLogger logger = new SecureLogger(capture);

        SecureBean secureBean = new SecureBean();
        PlainBean plainBean = new PlainBean();
        logger.info("Two: {} {}", secureBean, plainBean);

        Assert.assertTrue(capture.arg1 instanceof String);
        Assert.assertTrue(((String) capture.arg1).contains("public"));
        Assert.assertFalse(((String) capture.arg1).contains("sensitive-data"));
        Assert.assertSame(plainBean, capture.arg2);
    }

    @Test
    public void testMessageOnlyNotAffected() {
        CaptureLogger capture = new CaptureLogger();
        SecureLogger logger = new SecureLogger(capture);

        logger.info("Simple message");

        Assert.assertEquals("Simple message", capture.lastMessage);
    }

    @Test
    public void testThrowablePassthrough() {
        CaptureLogger capture = new CaptureLogger();
        SecureLogger logger = new SecureLogger(capture);

        Exception ex = new RuntimeException("test exception");
        logger.error("Error occurred", ex);

        Assert.assertEquals("Error occurred", capture.lastMessage);
        Assert.assertSame(ex, capture.lastThrowable);
    }

    @Test
    public void testDelegateIsRealLogger() {
        SecureLogger logger = SecureLoggerFactory.getLogger("delegate-test");
        Assert.assertEquals("delegate-test", logger.getName());
    }

    // ==================== Capture Logger ====================

    private static class CaptureLogger implements Logger {
        Object lastArg;
        Object arg1;
        Object arg2;
        String lastMessage;
        Throwable lastThrowable;

        @Override public String getName() { return "capture"; }
        @Override public boolean isTraceEnabled() { return false; }
        @Override public boolean isTraceEnabled(Marker marker) { return false; }
        @Override public void trace(String msg) { this.lastMessage = msg; }
        @Override public void trace(String format, Object arg) { this.lastArg = arg; }
        @Override public void trace(String format, Object arg1, Object arg2) { this.arg1 = arg1; this.arg2 = arg2; }
        @Override public void trace(String format, Object... arguments) { this.lastArg = arguments; }
        @Override public void trace(String msg, Throwable t) { this.lastMessage = msg; this.lastThrowable = t; }
        @Override public void trace(Marker marker, String msg) {}
        @Override public void trace(Marker marker, String format, Object arg) {}
        @Override public void trace(Marker marker, String format, Object arg1, Object arg2) {}
        @Override public void trace(Marker marker, String format, Object... argArray) {}
        @Override public void trace(Marker marker, String msg, Throwable t) {}

        @Override public boolean isDebugEnabled() { return false; }
        @Override public boolean isDebugEnabled(Marker marker) { return false; }
        @Override public void debug(String msg) { this.lastMessage = msg; }
        @Override public void debug(String format, Object arg) { this.lastArg = arg; }
        @Override public void debug(String format, Object arg1, Object arg2) { this.arg1 = arg1; this.arg2 = arg2; }
        @Override public void debug(String format, Object... arguments) { this.lastArg = arguments; }
        @Override public void debug(String msg, Throwable t) { this.lastMessage = msg; this.lastThrowable = t; }
        @Override public void debug(Marker marker, String msg) {}
        @Override public void debug(Marker marker, String format, Object arg) {}
        @Override public void debug(Marker marker, String format, Object arg1, Object arg2) {}
        @Override public void debug(Marker marker, String format, Object... arguments) {}
        @Override public void debug(Marker marker, String msg, Throwable t) {}

        @Override public boolean isInfoEnabled() { return false; }
        @Override public boolean isInfoEnabled(Marker marker) { return false; }
        @Override public void info(String msg) { this.lastMessage = msg; }
        @Override public void info(String format, Object arg) { this.lastArg = arg; }
        @Override public void info(String format, Object arg1, Object arg2) { this.arg1 = arg1; this.arg2 = arg2; }
        @Override public void info(String format, Object... arguments) { this.lastArg = arguments; }
        @Override public void info(String msg, Throwable t) { this.lastMessage = msg; this.lastThrowable = t; }
        @Override public void info(Marker marker, String msg) {}
        @Override public void info(Marker marker, String format, Object arg) {}
        @Override public void info(Marker marker, String format, Object arg1, Object arg2) {}
        @Override public void info(Marker marker, String format, Object... arguments) {}
        @Override public void info(Marker marker, String msg, Throwable t) {}

        @Override public boolean isWarnEnabled() { return false; }
        @Override public boolean isWarnEnabled(Marker marker) { return false; }
        @Override public void warn(String msg) { this.lastMessage = msg; }
        @Override public void warn(String format, Object arg) { this.lastArg = arg; }
        @Override public void warn(String format, Object arg1, Object arg2) { this.arg1 = arg1; this.arg2 = arg2; }
        @Override public void warn(String format, Object... arguments) { this.lastArg = arguments; }
        @Override public void warn(String msg, Throwable t) { this.lastMessage = msg; this.lastThrowable = t; }
        @Override public void warn(Marker marker, String msg) {}
        @Override public void warn(Marker marker, String format, Object arg) {}
        @Override public void warn(Marker marker, String format, Object arg1, Object arg2) {}
        @Override public void warn(Marker marker, String format, Object... arguments) {}
        @Override public void warn(Marker marker, String msg, Throwable t) {}

        @Override public boolean isErrorEnabled() { return false; }
        @Override public boolean isErrorEnabled(Marker marker) { return false; }
        @Override public void error(String msg) { this.lastMessage = msg; }
        @Override public void error(String format, Object arg) { this.lastArg = arg; }
        @Override public void error(String format, Object arg1, Object arg2) { this.arg1 = arg1; this.arg2 = arg2; }
        @Override public void error(String format, Object... arguments) { this.lastArg = arguments; }
        @Override public void error(String msg, Throwable t) { this.lastMessage = msg; this.lastThrowable = t; }
        @Override public void error(Marker marker, String msg) {}
        @Override public void error(Marker marker, String format, Object arg) {}
        @Override public void error(Marker marker, String format, Object arg1, Object arg2) {}
        @Override public void error(Marker marker, String format, Object... arguments) {}
        @Override public void error(Marker marker, String msg, Throwable t) {}
    }
}
