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
 * Annotations for marking classes and fields for secure JSON serialization.
 *
 * <h2>Annotations</h2>
 * <ul>
 *   <li>{@link com.techbulls.commons.securelog.annotation.SecureLog @SecureLog} &mdash;
 *       class-level annotation that enables secure serialization and configures output
 *       options such as pretty printing and
 *       {@link com.fasterxml.jackson.annotation.JsonView} filtering</li>
 *   <li>{@link com.techbulls.commons.securelog.annotation.LogSensitive @LogSensitive} &mdash;
 *       field-level annotation that marks a property as sensitive and specifies the mask
 *       value, custom {@link com.techbulls.commons.securelog.ValueFormatter}, and null-value
 *       handling behavior</li>
 * </ul>
 *
 * @see com.techbulls.commons.securelog.serialization.SecureJson
 */
package com.techbulls.commons.securelog.annotation;
