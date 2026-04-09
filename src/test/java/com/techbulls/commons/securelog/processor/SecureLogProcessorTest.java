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

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SecureLogProcessorTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testWarningWhenToStringMissing() {
        String source = ""
                + "import com.techbulls.commons.securelog.annotation.SecureLog;\n"
                + "@SecureLog\n"
                + "public class MissingToString {\n"
                + "    private String name;\n"
                + "}\n";

        List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(source, "MissingToString", false);

        List<Diagnostic<? extends JavaFileObject>> warnings = diagnostics.stream()
                .filter(d -> d.getKind() == Diagnostic.Kind.WARNING
                        || d.getKind() == Diagnostic.Kind.MANDATORY_WARNING)
                .filter(d -> d.getMessage(null).contains("@SecureLog"))
                .collect(Collectors.toList());

        Assert.assertFalse("Expected a warning for missing toString()", warnings.isEmpty());
    }

    @Test
    public void testNoWarningWhenToStringPresent() {
        String source = ""
                + "import com.techbulls.commons.securelog.annotation.SecureLog;\n"
                + "@SecureLog\n"
                + "public class HasToString {\n"
                + "    private String name;\n"
                + "    @Override\n"
                + "    public String toString() {\n"
                + "        return \"HasToString\";\n"
                + "    }\n"
                + "}\n";

        List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(source, "HasToString", false);

        List<Diagnostic<? extends JavaFileObject>> relevant = diagnostics.stream()
                .filter(d -> d.getKind() == Diagnostic.Kind.WARNING
                        || d.getKind() == Diagnostic.Kind.ERROR)
                .filter(d -> d.getMessage(null).contains("@SecureLog"))
                .collect(Collectors.toList());

        Assert.assertTrue("Expected no warning when toString() is present", relevant.isEmpty());
    }

    @Test
    public void testErrorInStrictMode() {
        String source = ""
                + "import com.techbulls.commons.securelog.annotation.SecureLog;\n"
                + "@SecureLog\n"
                + "public class StrictMissing {\n"
                + "    private String name;\n"
                + "}\n";

        List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(source, "StrictMissing", true);

        List<Diagnostic<? extends JavaFileObject>> errors = diagnostics.stream()
                .filter(d -> d.getKind() == Diagnostic.Kind.ERROR)
                .filter(d -> d.getMessage(null).contains("@SecureLog"))
                .collect(Collectors.toList());

        Assert.assertFalse("Expected an error in strict mode for missing toString()", errors.isEmpty());
    }

    @Test
    public void testAbstractClassSkipped() {
        String source = ""
                + "import com.techbulls.commons.securelog.annotation.SecureLog;\n"
                + "@SecureLog\n"
                + "public abstract class AbstractBean {\n"
                + "    private String name;\n"
                + "}\n";

        List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(source, "AbstractBean", false);

        List<Diagnostic<? extends JavaFileObject>> relevant = diagnostics.stream()
                .filter(d -> d.getKind() == Diagnostic.Kind.WARNING
                        || d.getKind() == Diagnostic.Kind.ERROR)
                .filter(d -> d.getMessage(null).contains("@SecureLog"))
                .collect(Collectors.toList());

        Assert.assertTrue("Expected no warning for abstract class", relevant.isEmpty());
    }

    @Test
    public void testSuppressWarningsSkipped() {
        String source = ""
                + "import com.techbulls.commons.securelog.annotation.SecureLog;\n"
                + "@SecureLog\n"
                + "@SuppressWarnings(\"techbulls.securelog\")\n"
                + "public class SuppressedBean {\n"
                + "    private String name;\n"
                + "}\n";

        List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(source, "SuppressedBean", false);

        List<Diagnostic<? extends JavaFileObject>> relevant = diagnostics.stream()
                .filter(d -> d.getKind() == Diagnostic.Kind.WARNING
                        || d.getKind() == Diagnostic.Kind.ERROR)
                .filter(d -> d.getMessage(null).contains("@SecureLog"))
                .collect(Collectors.toList());

        Assert.assertTrue("Expected no warning when @SuppressWarnings is present", relevant.isEmpty());
    }

    @Test
    public void testInheritedToStringNotSufficient() {
        String source = ""
                + "import com.techbulls.commons.securelog.annotation.SecureLog;\n"
                + "class BaseWithToString {\n"
                + "    @Override\n"
                + "    public String toString() {\n"
                + "        return \"base\";\n"
                + "    }\n"
                + "}\n"
                + "@SecureLog\n"
                + "class ChildWithoutToString extends BaseWithToString {\n"
                + "    private String name;\n"
                + "}\n";

        // Use the child class name for the file — both classes are in the same file
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compile(source, "BaseWithToString", false);

        List<Diagnostic<? extends JavaFileObject>> warnings = diagnostics.stream()
                .filter(d -> d.getKind() == Diagnostic.Kind.WARNING
                        || d.getKind() == Diagnostic.Kind.MANDATORY_WARNING)
                .filter(d -> d.getMessage(null).contains("@SecureLog"))
                .collect(Collectors.toList());

        Assert.assertFalse("Expected a warning even when parent has toString()", warnings.isEmpty());
    }

    private List<Diagnostic<? extends JavaFileObject>> compile(String source, String className, boolean strict) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();

        JavaFileObject sourceFile = new SimpleJavaFileObject(
                URI.create("string:///" + className + ".java"),
                JavaFileObject.Kind.SOURCE
        ) {
            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return source;
            }
        };

        List<String> options = new java.util.ArrayList<>();
        options.add("-d");
        options.add(tempFolder.getRoot().getAbsolutePath());
        if (strict) {
            options.add("-Atechbulls.securelog.strict=true");
        }

        JavaCompiler.CompilationTask task = compiler.getTask(
                null,
                null,
                diagnosticCollector,
                options,
                null,
                Collections.singletonList(sourceFile)
        );

        task.setProcessors(Collections.singletonList(new SecureLogProcessor()));
        task.call();

        return diagnosticCollector.getDiagnostics();
    }
}
