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

import static de.cuioss.tools.string.MoreStrings.isEmpty;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


import lombok.Getter;

/**
 * Handler for storing and querying {@link LogRecord}s
 *
 * @author Oliver Wolff
 *
 */
public class TestLogHandler extends Handler {

    private static final String TEST_LOG_LEVEL_MUST_NOT_BE_NULL = "TestLogLevel must not be null";

    private static final String LOGGER_MUST_NOT_BE_NULL = "Logger must not be null";

    private static final String MESSAGE_MUST_NOT_BE_NULL = "Message must not be null";

    private static final String THROWABLE_CLASS_MUST_NOT_BE_NULL = "ThrowableClass must not be null";

    private static final String THROWABLE_MUST_NOT_BE_NULL = "Throwable must not be null";

    @Getter
    private final List<LogRecord> records = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void publish(LogRecord logRecord) {
        // Silently ignore null records.
        if (logRecord == null) {
            return;
        }
        records.add(logRecord);

    }

    @Override
    public void close() {
        // There is no need to close
    }

    @Override
    public void flush() {
        // There is no need to flush
    }

    /**
     * @param level          to be checked for message, must not be null
     * @param message        to be checked, must not be null
     * @param throwableClass to be checked, must not be null
     * @return a {@link List} of found {@link LogRecord}s
     */
    public List<LogRecord> resolveLogMessages(TestLogLevel level, String message,
            Class<? extends Throwable> throwableClass) {
        assertNotNull(throwableClass, THROWABLE_CLASS_MUST_NOT_BE_NULL);
        return resolveLogMessages(level, message).stream().filter(r -> logRecordContains(r, throwableClass)).toList();
    }

    /**
     * @param level     to be checked for message, must not be null
     * @param message   to be checked, must not be null
     * @param throwable to be checked, must not be null
     * @return a {@link List} of found {@link LogRecord}s
     */
    public List<LogRecord> resolveLogMessages(TestLogLevel level, String message, Throwable throwable) {
        assertNotNull(throwable, THROWABLE_MUST_NOT_BE_NULL);
        return resolveLogMessages(level, message).stream().filter(r -> logRecordContains(r, throwable)).toList();
    }

    /**
     * @param level   to be checked for message, must not be null
     * @param message to be checked, must not be null
     * @return a {@link List} of found {@link LogRecord}s
     */
    public List<LogRecord> resolveLogMessages(TestLogLevel level, String message) {
        assertNotNull(message, MESSAGE_MUST_NOT_BE_NULL);
        return resolveLogMessages(level).stream().filter(r -> message.equals(r.getMessage())).toList();
    }

    /**
     * @param logger to be checked, must not be null
     * @return a {@link List} of found {@link LogRecord}s
     */
    public List<LogRecord> resolveLogMessagesForLogger(String logger) {
        assertFalse(isEmpty(logger), LOGGER_MUST_NOT_BE_NULL);
        return records.stream().filter(r -> logger.equalsIgnoreCase(r.getLoggerName())).toList();
    }

    /**
     * @param level  to be checked for message, must not be null
     * @param logger to be checked, must not be null
     * @return a {@link List} of found {@link LogRecord}s
     */
    public List<LogRecord> resolveLogMessagesForLogger(TestLogLevel level, String logger) {
        assertNotNull(level, TEST_LOG_LEVEL_MUST_NOT_BE_NULL);
        return resolveLogMessagesForLogger(logger).stream().filter(r -> level.getJuliLevel().equals(r.getLevel()))
                .toList();
    }

    /**
     * @param level  to be checked for message, must not be null
     * @param logger to be checked, must not be null
     * @return a {@link List} of found {@link LogRecord}s
     */
    public List<LogRecord> resolveLogMessagesForLogger(TestLogLevel level, Class<?> logger) {
        assertNotNull(level, TEST_LOG_LEVEL_MUST_NOT_BE_NULL);
        assertNotNull(logger, LOGGER_MUST_NOT_BE_NULL);
        return resolveLogMessagesForLogger(level, logger.getName());
    }

    /**
     * @param logger to be checked, must not be null
     * @return a {@link List} of found {@link LogRecord}s
     */
    public List<LogRecord> resolveLogMessagesForLogger(Class<?> logger) {
        assertNotNull(logger, LOGGER_MUST_NOT_BE_NULL);
        return resolveLogMessagesForLogger(logger.getName());
    }

    /**
     * @param level       to be checked for message, must not be null
     * @param messagePart to be checked, must not be null. Compared to
     *                    {@link TestLogHandler#resolveLogMessages(TestLogLevel, String)}
     *                    this method check whether the given text is contained
     *                    within a {@link LogRecord}
     * @return a {@link List} of found {@link LogRecord}s
     */
    public List<LogRecord> resolveLogMessagesContaining(TestLogLevel level, String messagePart) {
        assertNotNull(messagePart, MESSAGE_MUST_NOT_BE_NULL);
        return resolveLogMessages(level).stream().filter(r -> logRecordContains(r, messagePart)).toList();
    }

    /**
     * @param level       to be checked for message, must not be null
     * @param messagePart to be checked, must not be null. Compared to
     *                    {@link TestLogHandler#resolveLogMessages(TestLogLevel, String)}
     *                    this method check whether the given text is contained
     *                    within a {@link LogRecord}
     * @param throwable   to be looked for
     * @return a {@link List} of found {@link LogRecord}s
     */
    public List<LogRecord> resolveLogMessagesContaining(TestLogLevel level, String messagePart, Throwable throwable) {
        assertNotNull(throwable, THROWABLE_MUST_NOT_BE_NULL);
        return resolveLogMessagesContaining(level, messagePart).stream().filter(r -> logRecordContains(r, throwable))
                .toList();
    }

    /**
     * @param level          to be checked for message, must not be null
     * @param messagePart    to be checked, must not be null. Compared to
     *                       {@link TestLogHandler#resolveLogMessages(TestLogLevel, String)}
     *                       this method check whether the given text is contained
     *                       within a {@link LogRecord}
     * @param throwableClass to be looked for
     * @return a {@link List} of found {@link LogRecord}s
     */
    public List<LogRecord> resolveLogMessagesContaining(TestLogLevel level, String messagePart,
            Class<? extends Throwable> throwableClass) {
        assertNotNull(throwableClass, THROWABLE_CLASS_MUST_NOT_BE_NULL);
        return resolveLogMessagesContaining(level, messagePart).stream()
                .filter(r -> logRecordContains(r, throwableClass)).toList();
    }

    /**
     * @param level to be checked for message, must not be null
     * @return a {@link List} of found {@link LogRecord}s
     */
    public List<LogRecord> resolveLogMessages(TestLogLevel level) {
        assertNotNull(level, TEST_LOG_LEVEL_MUST_NOT_BE_NULL);
        synchronized (records) {
            return records.stream().filter(r -> logRecordContains(r, level)).toList();
        }
    }

    /**
     * Clears the contained records
     */
    public void clearRecords() {
        records.clear();
    }

    private static boolean logRecordContains(LogRecord logRecord, String messagePart) {
        final var msg = logRecord.getMessage();
        return null != msg && msg.contains(messagePart);
    }

    private static boolean logRecordContains(LogRecord logRecord, Class<? extends Throwable> throwableClass) {
        var thrown = logRecord.getThrown();
        return null != thrown && thrown.getClass().equals(throwableClass);
    }

    private static boolean logRecordContains(LogRecord logRecord, Throwable throwable) {
        var thrown = logRecord.getThrown();
        return null != thrown && thrown.equals(throwable);
    }

    private static boolean logRecordContains(LogRecord logRecord, TestLogLevel level) {
        var loggedLevel = logRecord.getLevel();
        return null != loggedLevel && loggedLevel.equals(level.getJuliLevel());
    }

    @Override
    public String toString() {
        return getClass().getName() + " with " + getRecords().size() + " entries";
    }

    /**
     * @return String representation of the records within this handler.
     */
    public String getRecordsAsString() {
        if (records.isEmpty()) {
            return "No log messages available";
        }
        List<LogRecord> all = new ArrayList<>(records);

        all.sort(Comparator.comparing(l -> l.getLevel().intValue()));

        List<String> elements = new ArrayList<>();

        all.forEach(
                l -> elements.add(TestLogLevel.parse(l.getLevel()) + ": " + l.getLoggerName() + "-" + l.getMessage()));
        var builder = new StringBuilder();
        builder.append("Available Messages:");
        for (String element : elements) {
            builder.append("\n").append(element);
        }
        return builder.toString();
    }
}
