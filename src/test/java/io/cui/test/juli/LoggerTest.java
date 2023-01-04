package io.cui.test.juli;

import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import io.cui.test.juli.junit5.EnableTestLogger;

@EnableTestLogger
class LoggerTest {

    private static final String DEBUG = "Debug";
    private static final String HELLO = "Hello";
    private static final Logger LOG = Logger.getLogger(LoggerTest.class.getName());

    @Test
    void doLog() {
        LOG.log(TestLogLevel.INFO.getJuliLevel(), HELLO);
        LogAsserts.assertSingleLogMessagePresent(TestLogLevel.INFO, HELLO);
        TestLogLevel.DEBUG.addLogger(getClass());
        LOG.log(TestLogLevel.DEBUG.getJuliLevel(), DEBUG);
        LogAsserts.assertSingleLogMessagePresent(TestLogLevel.DEBUG, DEBUG);
    }
}
