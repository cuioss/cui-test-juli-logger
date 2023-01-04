package io.cui.test.juli;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
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
    void containsMessagePartAndThrowableClass(){
        underTest.publish(create(TestLogLevel.DEBUG, MESSAGE, new LoggerTestException()));
        List<LogRecord> result = underTest.resolveLogMessagesContaining(
            TestLogLevel.DEBUG,
            "mess",
            LoggerTestException.class);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void containsMessagePartAndThrowable(){
        LoggerTestException ex = new LoggerTestException();
        underTest.publish(create(TestLogLevel.DEBUG, MESSAGE, ex));
        List<LogRecord> result = underTest.resolveLogMessagesContaining(TestLogLevel.DEBUG, "mess", ex);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    static LogRecord create(TestLogLevel level, String message, Throwable throwable) {
        LogRecord record = new LogRecord(level.getJuliLevel(), message);
        record.setLoggerName(TestLogHandlerTest.class.getName());
        if (null != throwable) {
            record.setThrown(throwable);
        }
        return record;
    }

    public static final class LoggerTestException extends RuntimeException {
        public LoggerTestException() {
        }
    }
}
