package com.techbulls.commons.securelog.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.techbulls.commons.securelog.ValueFormatter;
import com.techbulls.commons.securelog.annotation.LogSensitive;
import com.techbulls.commons.securelog.annotation.SecureLog;
import lombok.Getter;
import lombok.Setter;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class SecureJsonConcurrencyTest {
    @Test
    public void testConcurrentToJson() {
        int iterations = 1000;
        int threadCount = 30;

        CountDownLatch latch = new CountDownLatch(threadCount);

        List<SecureToJsonRunner> runners = new ArrayList<>(threadCount);
        List<Thread> threads = new ArrayList<>(threadCount);
        for (int i = 1; i<= threadCount; i++) {
            SecureToJsonRunner runner = new SecureToJsonRunner(iterations, latch);
            runners.add(runner);
            threads.add(new Thread(runner));
        }

        threads.forEach(t -> t.start());

        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Assert.fail("Failed waiting for a thread to exit");
            }
        });

        Assert.assertEquals(
                "One or more threads reported errors when generating toJson concurrently",
                0,
                runners.stream().filter(r -> r.isFailuresReported()).count()
        );
    }

    private static class SecureToJsonRunner implements Runnable {
        private final int iterations;

        private final CountDownLatch latch;

        @Getter
        private boolean failuresReported = false;

        private SecureToJsonRunner(int iterations, CountDownLatch latch) {
            this.iterations = iterations;
            this.latch = latch;
        }

        @Override
        public void run() {
            latch.countDown();
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
                failuresReported = true;
                return;
            }

            for (int i = 1; i<= iterations; i++) {
                try {
                    A a = new A();
                    a.setA("a");
                    a.setB("b");
                    a.setC("c");
                    a.setD("d");

                    String json = SecureJson.toJson(a);
                    JsonNode root = TestUtils.asJsonNode(json);
                    TestUtils.assertContainsNodeWithText(root, "a", "XXXX");
                    TestUtils.assertContainsNodeWithText(root, "b", "bXX");
                    TestUtils.assertContainsNodeWithText(root, "c", "XXXX");
                    TestUtils.assertContainsNodeWithText(root, "d", "d");

                    B b = new B();
                    b.setA("a");
                    b.setB("b");
                    b.setC("c");
                    b.setD("d");

                    json = SecureJson.toJson(b);
                    root = TestUtils.asJsonNode(json);
                    TestUtils.assertContainsNodeWithText(root, "a", "XXXX");
                    TestUtils.assertContainsNodeWithText(root, "b", "bXX");
                    TestUtils.assertContainsNodeWithText(root, "c", "XXXX");
                    TestUtils.assertContainsNodeWithText(root, "d", "d");

                    C c = new C();
                    c.setA("a");
                    c.setB("b");
                    c.setC("c");
                    c.setD("d");

                    json = SecureJson.toJson(c);
                    root = TestUtils.asJsonNode(json);
                    TestUtils.assertContainsNodeWithText(root, "a", "XXXX");
                    TestUtils.assertContainsNodeWithText(root, "b", "bXX");
                    TestUtils.assertContainsNodeWithText(root, "c", "XXXX");
                    TestUtils.assertContainsNodeWithText(root, "d", "d");
                } catch (JsonProcessingException jpe) {
                    jpe.printStackTrace();
                    failuresReported = true;
                    break;
                }
            }
        }
    }

    private static class TestFormatter implements ValueFormatter {

        @Override
        public String format(Object value, String secureValue) {
            return value.toString() + "XX";
        }
    }

    @SecureLog
    @Getter
    @Setter
    private static class A {
        @LogSensitive
        private String a;

        @LogSensitive(formatter = TestFormatter.class)
        private String b;

        @LogSensitive
        private String c;

        private String d;
    }

    @SecureLog
    @Getter
    @Setter
    private static class B {
        @LogSensitive
        private String a;

        @LogSensitive(formatter = TestFormatter.class)
        private String b;

        @LogSensitive
        private String c;

        private String d;
    }

    @SecureLog
    @Getter
    @Setter
    private static class C {
        @LogSensitive
        private String a;

        @LogSensitive(formatter = TestFormatter.class)
        private String b;

        @LogSensitive
        private String c;

        private String d;
    }
}
