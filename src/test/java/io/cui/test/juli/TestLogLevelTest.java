package io.cui.test.juli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestLogLevelTest {

    private final String loggerBase = getClass().getName() + ".";

    @BeforeEach
    void before() {
        TestLogLevel.INFO.setAsRootLevel().addLogger(getClass());
    }

    @Test
    void shouldHandleLogLevel() {
        String name = loggerBase + "a";
        Logger logger = Logger.getLogger(name);

        assertEquals(logger.isLoggable(Level.INFO), TestLogLevel.INFO.isEnabled(logger));
        assertTrue(TestLogLevel.INFO.isEnabled(logger));
        assertFalse(TestLogLevel.DEBUG.isEnabled(logger));

        // Set Trace for concrete Logger
        TestLogLevel.TRACE.addLogger(name);
        assertTrue(TestLogLevel.TRACE.isEnabled(logger));

    }

    @Test
    void shouldHandleLogLevelRoot() {

        Logger rootLogger = Logger.getLogger("");
        // Set Root-level to debug
        TestLogLevel.DEBUG.setAsRootLevel();
        assertTrue(TestLogLevel.DEBUG.isEnabled(rootLogger));
        assertFalse(TestLogLevel.TRACE.isEnabled(rootLogger));
    }

    @Test
    void shouldHandleLogLevelAsClass() {
        Logger logger = Logger.getLogger(getClass().getName());
        TestLogLevel.INFO.addLogger(getClass());
        assertTrue(TestLogLevel.INFO.isEnabled(logger));

        TestLogLevel.WARN.addLogger(getClass());
        assertFalse(TestLogLevel.INFO.isEnabled(logger));

        Logger rootLogger = Logger.getLogger("");
        assertFalse(TestLogLevel.DEBUG.isEnabled(rootLogger));
        // Use null as class argument
        TestLogLevel.DEBUG.addLogger((Class<?>) null);
        assertTrue(TestLogLevel.DEBUG.isEnabled(rootLogger));
    }

    @Test
    void shouldParseLoglevel() {
        assertEquals(TestLogLevel.ERROR,
                TestLogLevel.getLevelOrDefault(TestLogLevel.ERROR.toString(), TestLogLevel.DEBUG));
        assertEquals(TestLogLevel.DEBUG,
                TestLogLevel.getLevelOrDefault("", TestLogLevel.DEBUG));
        assertEquals(TestLogLevel.DEBUG,
                TestLogLevel.getLevelOrDefault("notThere", TestLogLevel.DEBUG));
    }

}
