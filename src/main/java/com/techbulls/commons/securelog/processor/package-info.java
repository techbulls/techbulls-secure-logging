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
 * Compile-time annotation processing for {@link com.techbulls.commons.securelog.annotation.SecureLog @SecureLog}.
 *
 * <p>Contains {@link com.techbulls.commons.securelog.processor.SecureLogProcessor}, which
 * verifies at compile time that classes annotated with {@code @SecureLog} have a directly
 * declared {@code toString()} method. A warning is emitted by default; passing the compiler
 * option {@code -Atechbulls.securelog.strict=true} upgrades it to a compile error.
 *
 * @see com.techbulls.commons.securelog.annotation.SecureLog
 * @see com.techbulls.commons.securelog.processor.SecureLogProcessor
 */
package com.techbulls.commons.securelog.processor;
