/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.test.juli;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.logging.LogRecord;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestLogHandlerTest {

    static final String MESSAGE_PART = "essag";
    static final String MESSAGE = "message";
    static final String MESSAGE_2 = "message2";

    private TestLogHandler underTest;

    @BeforeEach
    void before() {
        underTest = new TestLogHandler();
    }

    @Test
    void shouldFilterByLogLevel() {
        underTest.publish(create(TestLogLevel.DEBUG, MESSAGE, null));
        underTest.publish(create(TestLogLevel.INFO, MESSAGE, null));
        underTest.publish(create(TestLogLevel.INFO, MESSAGE_2, null));
        assertEquals(0, underTest.resolveLogMessages(TestLogLevel.ERROR).size());
        assertEquals(1, underTest.resolveLogMessages(TestLogLevel.DEBUG).size());
        assertEquals(2, underTest.resolveLogMessages(TestLogLevel.INFO).size());
    }

    @Test
    void shouldResolveForLogger() {
        underTest.publish(create(TestLogLevel.DEBUG, MESSAGE, null));
        underTest.publish(create(TestLogLevel.INFO, MESSAGE, null));
        underTest.publish(create(TestLogLevel.INFO, MESSAGE_2, null));
        assertEquals(3, underTest.resolveLogMessagesForLogger(TestLogHandlerTest.class).size());
        assertEquals(0, underTest.resolveLogMessagesForLogger(TestLogHandler.class).size());

        assertEquals(2, underTest.resolveLogMessagesForLogger(TestLogLevel.INFO, TestLogHandlerTest.class).size());
        assertEquals(0, underTest.resolveLogMessagesForLogger(TestLogLevel.WARN, TestLogHandlerTest.class).size());
        assertEquals(0, underTest.resolveLogMessagesForLogger(TestLogLevel.INFO, TestLogHandler.class).size());
    }

    @Test
    void shouldFilterByLogLevelAndMessage() {
        underTest.publish(create(TestLogLevel.DEBUG, MESSAGE, null));
        underTest.publish(create(TestLogLevel.INFO, MESSAGE, null));
        underTest.publish(create(TestLogLevel.INFO, MESSAGE_2, null));
        assertEquals(0, underTest.resolveLogMessages(TestLogLevel.ERROR, MESSAGE).size());
        assertEquals(1, underTest.resolveLogMessages(TestLogLevel.DEBUG, MESSAGE).size());
        assertEquals(0, underTest.resolveLogMessages(TestLogLevel.DEBUG, MESSAGE_2).size());
        assertEquals(1, underTest.resolveLogMessages(TestLogLevel.INFO, MESSAGE_2).size());
    }

    @Test
    void shouldFilterByLogLevelAndMessageContains() {
        underTest.publish(create(TestLogLevel.INFO, MESSAGE, null));
        underTest.publish(create(TestLogLevel.INFO, MESSAGE_2, null));
        underTest.publish(create(TestLogLevel.INFO, null, null));
        assertEquals(2, underTest.resolveLogMessagesContaining(TestLogLevel.INFO, MESSAGE_PART).size());
        assertEquals(0, underTest.resolveLogMessagesContaining(TestLogLevel.INFO, "notIn").size());
    }

    @Test
    void shouldFilterByLogLevelAndMessageAndThrowable() {
        Throwable exception = new RuntimeException();
        Throwable exception2 = new IllegalArgumentException();
        underTest.publish(create(TestLogLevel.INFO, MESSAGE, exception));
        underTest.publish(create(TestLogLevel.INFO, MESSAGE, exception2));
        underTest.publish(create(TestLogLevel.INFO, MESSAGE, null));
        assertEquals(1, underTest.resolveLogMessages(TestLogLevel.INFO, MESSAGE, exception).size());
    }

    @Test
    void shouldFilterByLogLevelAndMessageAndThrowableClass() {
        Throwable exception = new RuntimeException();
        Throwable exception2 = new IllegalArgumentException();
        underTest.publish(create(TestLogLevel.INFO, MESSAGE, exception));
        underTest.publish(create(TestLogLevel.INFO, MESSAGE, exception2));
        underTest.publish(create(TestLogLevel.INFO, MESSAGE, null));
        assertEquals(1, underTest.resolveLogMessages(TestLogLevel.INFO, MESSAGE, exception.getClass()).size());
    }

    @Test
    void shouldClear() {
        underTest.publish(create(TestLogLevel.INFO, MESSAGE, null));
        assertEquals(1, underTest.resolveLogMessages(TestLogLevel.INFO).size());
        underTest.clearRecords();
        assertEquals(0, underTest.resolveLogMessages(TestLogLevel.INFO).size());
    }

    @Test
    void shouldHandleCloseAndFlush() {
        assertDoesNotThrow(() -> underTest.flush());
        assertDoesNotThrow(() -> underTest.close());
    }

    @Test
    void shouldIgnoreNullLogRecords() {
        underTest.publish(null);
        assertEquals(0, underTest.resolveLogMessages(TestLogLevel.TRACE).size());
    }

    @Test
    void shouldHandleToString() {
        assertNotNull(underTest.toString());
    }

    @Test
    void containsMessagePartAndThrowableClass() {
        underTest.publish(create(TestLogLevel.DEBUG, MESSAGE, new LoggerTestException()));
        var result = underTest.resolveLogMessagesContaining(TestLogLevel.DEBUG, "mess", LoggerTestException.class);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void containsMessagePartAndThrowable() {
        var ex = new LoggerTestException();
        underTest.publish(create(TestLogLevel.DEBUG, MESSAGE, ex));
        var result = underTest.resolveLogMessagesContaining(TestLogLevel.DEBUG, "mess", ex);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    static LogRecord create(TestLogLevel level, String message, Throwable throwable) {
        var record = new LogRecord(level.getJuliLevel(), message);
        record.setLoggerName(TestLogHandlerTest.class.getName());
        if (null != throwable) {
            record.setThrown(throwable);
        }
        return record;
    }

    public static final class LoggerTestException extends RuntimeException {

        private static final long serialVersionUID = -3887998205854429983L;

        public LoggerTestException() {
        }
    }
}
