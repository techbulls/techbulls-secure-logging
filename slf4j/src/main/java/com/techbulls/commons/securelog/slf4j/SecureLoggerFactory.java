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

import org.slf4j.LoggerFactory;

/**
 * <h2>SecureLoggerFactory</h2>
 * Static factory for creating {@link SecureLogger} instances that automatically mask
 * sensitive data in objects annotated with
 * {@link com.techbulls.commons.securelog.annotation.SecureLog} before delegating
 * to an underlying SLF4J {@link org.slf4j.Logger}.
 * <p>
 * Mirrors the API of {@link org.slf4j.LoggerFactory} to provide a familiar
 * developer experience.
 * <p>
 * <b>Usage:</b>
 * <pre>{@code
 * SecureLogger logger = SecureLoggerFactory.getLogger(MyService.class);
 * logger.info("Processing user: {}", user); // auto-masked if @SecureLog
 * }</pre>
 *
 * @see SecureLogger
 * @see org.slf4j.LoggerFactory
 * @since 0.3
 */
public final class SecureLoggerFactory {

    private SecureLoggerFactory() {
    }

    /**
     * Returns a {@link SecureLogger} named according to the given class's name.
     *
     * @param clazz the class for which the logger should be named
     * @return a {@link SecureLogger} wrapping an SLF4J logger
     */
    public static SecureLogger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    /**
     * Returns a {@link SecureLogger} with the specified name.
     *
     * @param name the name of the logger
     * @return a {@link SecureLogger} wrapping an SLF4J logger
     */
    public static SecureLogger getLogger(String name) {
        return new SecureLogger(LoggerFactory.getLogger(name));
    }
}
