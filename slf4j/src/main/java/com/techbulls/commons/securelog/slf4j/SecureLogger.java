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

import com.techbulls.commons.securelog.annotation.SecureLog;
import com.techbulls.commons.securelog.serialization.SecureJson;
import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * <h2>SecureLogger</h2>
 * A wrapper around an SLF4J {@link Logger} that transparently inspects log arguments
 * for the {@link SecureLog} annotation. When an argument's class is annotated with
 * {@link SecureLog}, it is converted via {@link SecureJson#toJson(Object)} before
 * being passed to the underlying logger. All other arguments pass through unchanged.
 * <p>
 * Instances are created via {@link SecureLoggerFactory}.
 *
 * @see SecureLoggerFactory
 * @see SecureJson
 * @see SecureLog
 * @since 0.3
 */
public class SecureLogger implements Logger {

    private final Logger delegate;

    SecureLogger(Logger delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    // ==================== TRACE ====================

    @Override
    public boolean isTraceEnabled() {
        return delegate.isTraceEnabled();
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return delegate.isTraceEnabled(marker);
    }

    @Override
    public void trace(String msg) {
        delegate.trace(msg);
    }

    @Override
    public void trace(String format, Object arg) {
        delegate.trace(format, transform(arg));
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        delegate.trace(format, transform(arg1), transform(arg2));
    }

    @Override
    public void trace(String format, Object... arguments) {
        delegate.trace(format, transformAll(arguments));
    }

    @Override
    public void trace(String msg, Throwable t) {
        delegate.trace(msg, t);
    }

    @Override
    public void trace(Marker marker, String msg) {
        delegate.trace(marker, msg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        delegate.trace(marker, format, transform(arg));
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        delegate.trace(marker, format, transform(arg1), transform(arg2));
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        delegate.trace(marker, format, transformAll(argArray));
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        delegate.trace(marker, msg, t);
    }

    // ==================== DEBUG ====================

    @Override
    public boolean isDebugEnabled() {
        return delegate.isDebugEnabled();
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return delegate.isDebugEnabled(marker);
    }

    @Override
    public void debug(String msg) {
        delegate.debug(msg);
    }

    @Override
    public void debug(String format, Object arg) {
        delegate.debug(format, transform(arg));
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        delegate.debug(format, transform(arg1), transform(arg2));
    }

    @Override
    public void debug(String format, Object... arguments) {
        delegate.debug(format, transformAll(arguments));
    }

    @Override
    public void debug(String msg, Throwable t) {
        delegate.debug(msg, t);
    }

    @Override
    public void debug(Marker marker, String msg) {
        delegate.debug(marker, msg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        delegate.debug(marker, format, transform(arg));
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        delegate.debug(marker, format, transform(arg1), transform(arg2));
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        delegate.debug(marker, format, transformAll(arguments));
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        delegate.debug(marker, msg, t);
    }

    // ==================== INFO ====================

    @Override
    public boolean isInfoEnabled() {
        return delegate.isInfoEnabled();
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return delegate.isInfoEnabled(marker);
    }

    @Override
    public void info(String msg) {
        delegate.info(msg);
    }

    @Override
    public void info(String format, Object arg) {
        delegate.info(format, transform(arg));
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        delegate.info(format, transform(arg1), transform(arg2));
    }

    @Override
    public void info(String format, Object... arguments) {
        delegate.info(format, transformAll(arguments));
    }

    @Override
    public void info(String msg, Throwable t) {
        delegate.info(msg, t);
    }

    @Override
    public void info(Marker marker, String msg) {
        delegate.info(marker, msg);
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        delegate.info(marker, format, transform(arg));
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        delegate.info(marker, format, transform(arg1), transform(arg2));
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        delegate.info(marker, format, transformAll(arguments));
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        delegate.info(marker, msg, t);
    }

    // ==================== WARN ====================

    @Override
    public boolean isWarnEnabled() {
        return delegate.isWarnEnabled();
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return delegate.isWarnEnabled(marker);
    }

    @Override
    public void warn(String msg) {
        delegate.warn(msg);
    }

    @Override
    public void warn(String format, Object arg) {
        delegate.warn(format, transform(arg));
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        delegate.warn(format, transform(arg1), transform(arg2));
    }

    @Override
    public void warn(String format, Object... arguments) {
        delegate.warn(format, transformAll(arguments));
    }

    @Override
    public void warn(String msg, Throwable t) {
        delegate.warn(msg, t);
    }

    @Override
    public void warn(Marker marker, String msg) {
        delegate.warn(marker, msg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        delegate.warn(marker, format, transform(arg));
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        delegate.warn(marker, format, transform(arg1), transform(arg2));
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        delegate.warn(marker, format, transformAll(arguments));
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        delegate.warn(marker, msg, t);
    }

    // ==================== ERROR ====================

    @Override
    public boolean isErrorEnabled() {
        return delegate.isErrorEnabled();
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return delegate.isErrorEnabled(marker);
    }

    @Override
    public void error(String msg) {
        delegate.error(msg);
    }

    @Override
    public void error(String format, Object arg) {
        delegate.error(format, transform(arg));
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        delegate.error(format, transform(arg1), transform(arg2));
    }

    @Override
    public void error(String format, Object... arguments) {
        delegate.error(format, transformAll(arguments));
    }

    @Override
    public void error(String msg, Throwable t) {
        delegate.error(msg, t);
    }

    @Override
    public void error(Marker marker, String msg) {
        delegate.error(marker, msg);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        delegate.error(marker, format, transform(arg));
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        delegate.error(marker, format, transform(arg1), transform(arg2));
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        delegate.error(marker, format, transformAll(arguments));
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        delegate.error(marker, msg, t);
    }

    // ==================== Argument transformation ====================

    private static Object transform(Object arg) {
        if (arg != null && arg.getClass().isAnnotationPresent(SecureLog.class)) {
            return SecureJson.toJson(arg);
        }
        return arg;
    }

    private static Object[] transformAll(Object... args) {
        Object[] transformed = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            transformed[i] = transform(args[i]);
        }
        return transformed;
    }
}
