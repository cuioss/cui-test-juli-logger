package io.cui.test.juli;

import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import io.cui.test.juli.junit5.EnableTestLogger;

@EnableTestLogger
class LoggerTest {

    private static final Logger LOG = Logger.getLogger(LoggerTest.class.getName());

    @Test
    public void doLog() {
        LOG.log(TestLogLevel.INFO.getJuliLevel(), "Hello");
        TestLogLevel.DEBUG.addLogger(getClass());
        LOG.log(TestLogLevel.DEBUG.getJuliLevel(), "Debug");
    }
}
