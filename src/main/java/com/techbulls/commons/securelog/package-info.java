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

/**
 * Provides a library for masking sensitive field values during JSON serialization,
 * enabling safe logging of Java objects that contain personally identifiable information
 * (PII) or other confidential data.
 *
 * <h2>Overview</h2>
 * This library uses an annotation-driven approach integrated with Jackson to intercept
 * JSON serialization and replace sensitive field values with configurable mask strings.
 * The primary workflow is:
 * <ol>
 *   <li>Annotate a class with
 *       {@link com.techbulls.commons.securelog.annotation.SecureLog @SecureLog}</li>
 *   <li>Annotate sensitive fields with
 *       {@link com.techbulls.commons.securelog.annotation.LogSensitive @LogSensitive}</li>
 *   <li>Override {@code toString()} to delegate to
 *       {@link com.techbulls.commons.securelog.serialization.SecureJson#toJson(Object)
 *       SecureJson.toJson(this)}</li>
 * </ol>
 *
 * <h2>Packages</h2>
 * <ul>
 *   <li>{@link com.techbulls.commons.securelog.annotation} &mdash; annotations for marking
 *       classes and fields for secure serialization</li>
 *   <li>{@link com.techbulls.commons.securelog.serialization} &mdash; Jackson serializer
 *       infrastructure that performs the actual masking</li>
 *   <li>{@link com.techbulls.commons.securelog.processor} &mdash; compile-time annotation
 *       processor for verifying {@code toString()} is overridden</li>
 * </ul>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * @SecureLog
 * public class User {
 *     private String name;
 *
 *     @LogSensitive
 *     private String ssn;
 *
 *     @Override
 *     public String toString() {
 *         return SecureJson.toJson(this);
 *     }
 * }
 *
 * // Output: {"name":"John","ssn":"XXXX"}
 * }</pre>
 *
 * @see com.techbulls.commons.securelog.annotation.SecureLog
 * @see com.techbulls.commons.securelog.annotation.LogSensitive
 * @see com.techbulls.commons.securelog.serialization.SecureJson
 */
package com.techbulls.commons.securelog;
