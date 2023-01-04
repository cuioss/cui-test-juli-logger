package io.cui.test.juli;

import static io.cui.test.juli.TestLoggerFactory.getTestHandler;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.logging.LogRecord;

import io.cui.test.juli.junit5.EnableTestLogger;
import lombok.experimental.UtilityClass;

/**
 * Provides a number of asserts against the {@link LogRecord}s gathered by {@link TestLogHandler}.
 * Caution: In order to use the asserts the {@link TestLogHandler} must be properly configured by
 * calling {@link TestLoggerFactory#install()} prior to usage. Usually this is done by
 * {@link EnableTestLogger}
 *
 * @author Oliver Wolff
 *
 */
@UtilityClass
public class LogAsserts {

    private static final String ALL_LOGS = ", All recorded Logs:\n";
    private static final String MESSAGE_EXACTLY = " and message is exactly=";
    private static final String MESSAGE_CONTAINS = " and message containing=";
    private static final String AND_THROWABLE = " and throwable=";
    private static final String NO_LOG_MESSAGE_FOUND_WITH_LEVEL = "No log message found with level=";

    private static final String AT_LEAST_ONE_LOG_MESSAGE_FOUND_WITH_LEVEL =
        "At least one log message found with level=";
    private static final String NO_SINGLE_MESSAGE_FOUND_WITH_LEVEL = "Expected one message to be found with level=";

    /**
     * Asserts whether at least one {@link LogRecord} for the given parameter is present
     *
     * @param logLevel to be checked, must not be null
     * @param message to be checked, must not be null
     */
    public static void assertLogMessagePresent(TestLogLevel logLevel, String message) {
        TestLogHandler testHandler = getTestHandler();
        List<LogRecord> messages = testHandler.resolveLogMessages(logLevel, message);
        String assertionMessage = NO_LOG_MESSAGE_FOUND_WITH_LEVEL + logLevel + MESSAGE_EXACTLY + message
                + ALL_LOGS + testHandler.getRecordsAsString();

        assertNotEquals(0, messages.size(), assertionMessage);
    }

    /**
     * Asserts whether no {@link LogRecord} for the given parameter is present
     *
     * @param logLevel to be checked, must not be null
     * @param messagePart to be checked, must not be null
     */
    public static void assertNoLogMessagePresent(TestLogLevel logLevel, String messagePart) {
        TestLogHandler testHandler = getTestHandler();
        List<LogRecord> messages = testHandler.resolveLogMessagesContaining(logLevel, messagePart);
        String assertionMessage =
            AT_LEAST_ONE_LOG_MESSAGE_FOUND_WITH_LEVEL + logLevel + MESSAGE_EXACTLY + messagePart + ALL_LOGS
                    + testHandler.getRecordsAsString();

        assertTrue(messages.isEmpty(), assertionMessage);
    }

    /**
     * Asserts whether no {@link LogRecord} for the given parameter is present
     *
     * @param logLevel to be checked, must not be null
     * @param logger to be checked, must not be null
     */
    public static void assertNoLogMessagePresent(TestLogLevel logLevel, Class<?> logger) {
        TestLogHandler testHandler = getTestHandler();
        List<LogRecord> messages = testHandler.resolveLogMessagesForLogger(logLevel, logger);
        String assertionMessage =
            AT_LEAST_ONE_LOG_MESSAGE_FOUND_WITH_LEVEL + logLevel + ", on logger= " + logger + ALL_LOGS
                    + testHandler.getRecordsAsString();

        assertEquals(0, messages.size(), assertionMessage);
    }

    /**
     * Asserts whether exactly one {@link LogRecord} for the given parameter is present
     *
     * @param logLevel to be checked, must not be null
     * @param message to be checked, must not be null
     */
    public static void assertSingleLogMessagePresent(TestLogLevel logLevel, String message) {
        TestLogHandler testHandler = getTestHandler();
        List<LogRecord> records = testHandler.resolveLogMessages(logLevel, message);
        String assertionMessage = NO_SINGLE_MESSAGE_FOUND_WITH_LEVEL + logLevel + MESSAGE_EXACTLY + message + ALL_LOGS
                + testHandler.getRecordsAsString();

        assertEquals(1, records.size(), assertionMessage);
    }

    /**
     * Asserts whether at least one {@link LogRecord} for the given parameter is present
     *
     * @param logLevel to be checked, must not be null
     * @param message to be checked, must not be null
     * @param throwable to be checked, must not be null
     */
    public static void assertLogMessagePresent(TestLogLevel logLevel, String message, Throwable throwable) {
        TestLogHandler testHandler = getTestHandler();
        List<LogRecord> messages = testHandler.resolveLogMessages(logLevel, message, throwable);
        String assertionMessage =
            NO_LOG_MESSAGE_FOUND_WITH_LEVEL + logLevel + MESSAGE_EXACTLY + message + AND_THROWABLE + throwable
                    + ALL_LOGS + testHandler.getRecordsAsString();

        assertNotEquals(0, messages.size(), assertionMessage);
    }

    /**
     * Asserts whether exactly one {@link LogRecord} for the given parameter is present
     *
     * @param logLevel to be checked, must not be null
     * @param message to be checked, must not be null
     * @param throwable to be checked, must not be null
     */
    public static void assertSingleLogMessagePresent(TestLogLevel logLevel, String message, Throwable throwable) {
        TestLogHandler testHandler = getTestHandler();
        List<LogRecord> records = testHandler.resolveLogMessages(logLevel, message, throwable);
        String assertionMessage =
            NO_SINGLE_MESSAGE_FOUND_WITH_LEVEL + logLevel + MESSAGE_EXACTLY + message + AND_THROWABLE + throwable
                    + ALL_LOGS + testHandler.getRecordsAsString();

        assertEquals(1, records.size(), assertionMessage);
    }

    /**
     * Asserts whether at least one {@link LogRecord} for the given parameter is present
     *
     * @param logLevel to be checked, must not be null
     * @param message to be checked, must not be null
     * @param throwableClass to be checked, must not be null
     */
    public static void assertLogMessagePresent(TestLogLevel logLevel, String message,
            Class<? extends Throwable> throwableClass) {
        TestLogHandler testHandler = getTestHandler();
        List<LogRecord> messages = testHandler.resolveLogMessages(logLevel, message, throwableClass);
        String assertionMessage = NO_LOG_MESSAGE_FOUND_WITH_LEVEL + logLevel + MESSAGE_EXACTLY + message + AND_THROWABLE
                + throwableClass + ALL_LOGS + testHandler.getRecordsAsString();

        assertNotEquals(0, messages.size(), assertionMessage);
    }

    /**
     * Asserts whether exactly one {@link LogRecord} for the given parameter is present
     *
     * @param logLevel to be checked, must not be null
     * @param message to be checked, must not be null
     * @param throwableClass to be checked, must not be null
     */
    public static void assertSingleLogMessagePresent(TestLogLevel logLevel, String message,
            Class<? extends Throwable> throwableClass) {
        TestLogHandler testHandler = getTestHandler();
        List<LogRecord> records = testHandler.resolveLogMessages(logLevel, message, throwableClass);
        String assertionMessage =
            NO_SINGLE_MESSAGE_FOUND_WITH_LEVEL + logLevel + MESSAGE_EXACTLY + message + AND_THROWABLE
                    + throwableClass
                    + ALL_LOGS + testHandler.getRecordsAsString();

        assertEquals(1, records.size(), assertionMessage);
    }

    /**
     * Asserts whether at least one {@link LogRecord} for the given parameter is present
     *
     * @param logLevel to be checked, must not be null
     * @param messagePart to be checked, must not be null
     */
    public static void assertLogMessagePresentContaining(TestLogLevel logLevel, String messagePart) {
        TestLogHandler testHandler = getTestHandler();
        List<LogRecord> messages = testHandler.resolveLogMessagesContaining(logLevel, messagePart);
        String assertionMessage = NO_LOG_MESSAGE_FOUND_WITH_LEVEL + logLevel + MESSAGE_CONTAINS + messagePart + ALL_LOGS
                + testHandler.getRecordsAsString();

        assertNotEquals(0, messages.size(), assertionMessage);
    }

    /**
     * Asserts whether exactly one {@link LogRecord} for the given parameter is present
     *
     * @param logLevel to be checked, must not be null
     * @param messagePart to be checked, must not be null
     */
    public static void assertSingleLogMessagePresentContaining(TestLogLevel logLevel, String messagePart) {
        TestLogHandler testHandler = getTestHandler();
        List<LogRecord> records = testHandler.resolveLogMessagesContaining(logLevel, messagePart);
        String assertionMessage =
            NO_SINGLE_MESSAGE_FOUND_WITH_LEVEL + logLevel + MESSAGE_CONTAINS + messagePart + ALL_LOGS
                    + testHandler.getRecordsAsString();

        assertEquals(1, records.size(), assertionMessage);
    }

    /**
     * Asserts whether at least one {@link LogRecord} for the given parameter is present
     * 
     * @param logLevel to be checked, must not be null
     * @param messagePart to be checked, must not be null
     * @param throwable to be checked, must not be null
     */
    public static void assertLogMessagePresentContaining(TestLogLevel logLevel,
            String messagePart,
            Throwable throwable) {
        TestLogHandler testHandler = getTestHandler();
        List<LogRecord> records = testHandler.resolveLogMessagesContaining(logLevel, messagePart, throwable);
        assertNotEquals(0, records.size(),
                NO_LOG_MESSAGE_FOUND_WITH_LEVEL + logLevel
                        + MESSAGE_CONTAINS + messagePart
                        + AND_THROWABLE + throwable
                        + ALL_LOGS + testHandler.getRecordsAsString());
    }

    /**
     * Asserts whether at least one {@link LogRecord} for the given parameter is present
     * 
     * @param logLevel to be checked, must not be null
     * @param messagePart to be checked, must not be null
     * @param throwableClass to be checked, must not be null
     */
    public static void assertLogMessagePresentContaining(TestLogLevel logLevel,
            String messagePart,
            Class<? extends Throwable> throwableClass) {
        TestLogHandler testHandler = getTestHandler();
        List<LogRecord> records = testHandler.resolveLogMessagesContaining(logLevel, messagePart, throwableClass);
        assertNotEquals(0, records.size(),
                NO_LOG_MESSAGE_FOUND_WITH_LEVEL + logLevel
                        + MESSAGE_CONTAINS + messagePart
                        + AND_THROWABLE + throwableClass
                        + ALL_LOGS + testHandler.getRecordsAsString());
    }

    /**
     * Asserts whether exactly one {@link LogRecord} for the given parameter is present
     * 
     * @param logLevel to be checked, must not be null
     * @param messagePart to be checked, must not be null
     * @param throwable to be checked, must not be null
     */
    public static void assertSingleLogMessagePresentContaining(TestLogLevel logLevel,
            String messagePart,
            Throwable throwable) {
        TestLogHandler testHandler = getTestHandler();
        List<LogRecord> records = testHandler.resolveLogMessagesContaining(logLevel, messagePart, throwable);
        assertEquals(1, records.size(),
                NO_LOG_MESSAGE_FOUND_WITH_LEVEL + logLevel
                        + MESSAGE_CONTAINS + messagePart
                        + AND_THROWABLE + throwable
                        + ALL_LOGS + testHandler.getRecordsAsString());
    }

    /**
     * Asserts whether exactly one {@link LogRecord} for the given parameter is present
     * 
     * @param logLevel to be checked, must not be null
     * @param messagePart to be checked, must not be null
     * @param throwableClass to be checked, must not be null
     */
    public static void assertSingleLogMessagePresentContaining(TestLogLevel logLevel,
            String messagePart,
            Class<? extends Throwable> throwableClass) {
        TestLogHandler testHandler = getTestHandler();
        List<LogRecord> records = testHandler.resolveLogMessagesContaining(logLevel, messagePart, throwableClass);
        assertEquals(1, records.size(),
                NO_LOG_MESSAGE_FOUND_WITH_LEVEL + logLevel
                        + MESSAGE_CONTAINS + messagePart
                        + AND_THROWABLE + throwableClass
                        + ALL_LOGS + testHandler.getRecordsAsString());
    }
}
