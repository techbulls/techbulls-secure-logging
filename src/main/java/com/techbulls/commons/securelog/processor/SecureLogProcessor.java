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
package com.techbulls.commons.securelog.processor;

import com.techbulls.commons.securelog.annotation.SecureLog;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Arrays;
import java.util.Set;

/**
 * A compile-time annotation processor that verifies classes annotated with {@link SecureLog}
 * have directly declared a {@code toString()} method.
 * <p>
 * This processor emits a {@link Diagnostic.Kind#WARNING} by default when a concrete class
 * annotated with {@code @SecureLog} does not override {@code toString()}. The warning can be
 * upgraded to a compile error by passing the compiler option
 * {@code -Atechbulls.securelog.strict=true}.
 * <p>
 * Abstract classes, interfaces, and enums are skipped. Classes annotated with
 * {@code @SuppressWarnings("techbulls.securelog")} are also skipped.
 * <p>
 * The processor does not claim the {@code @SecureLog} annotation, allowing other processors
 * to process it as well.
 *
 * @see SecureLog
 * @since 0.2
 */
@SupportedAnnotationTypes("com.techbulls.commons.securelog.annotation.SecureLog")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@SupportedOptions({"techbulls.securelog.strict"})
public class SecureLogProcessor extends AbstractProcessor {

    static final String MESSAGE = "Class annotated with @SecureLog does not override toString(). "
            + "Add: @Override public String toString() { return SecureJson.toJson(this); }";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(SecureLog.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                continue;
            }

            if (element.getModifiers().contains(Modifier.ABSTRACT)) {
                continue;
            }

            if (isSuppressed(element)) {
                continue;
            }

            if (!hasToString(element)) {
                Diagnostic.Kind kind = isStrictMode() ? Diagnostic.Kind.ERROR : Diagnostic.Kind.WARNING;
                processingEnv.getMessager().printMessage(kind, MESSAGE, element);
            }
        }

        return false;
    }

    private boolean hasToString(Element classElement) {
        for (Element enclosed : classElement.getEnclosedElements()) {
            if (enclosed.getKind() != ElementKind.METHOD) {
                continue;
            }
            ExecutableElement method = (ExecutableElement) enclosed;
            if (method.getSimpleName().contentEquals("toString")
                    && method.getParameters().isEmpty()
                    && method.getReturnType().toString().equals("java.lang.String")) {
                return true;
            }
        }
        return false;
    }

    private boolean isSuppressed(Element element) {
        SuppressWarnings suppressWarnings = element.getAnnotation(SuppressWarnings.class);
        if (suppressWarnings == null) {
            return false;
        }
        return Arrays.asList(suppressWarnings.value()).contains("techbulls.securelog");
    }

    private boolean isStrictMode() {
        String strict = processingEnv.getOptions().get("techbulls.securelog.strict");
        return "true".equalsIgnoreCase(strict);
    }
}
