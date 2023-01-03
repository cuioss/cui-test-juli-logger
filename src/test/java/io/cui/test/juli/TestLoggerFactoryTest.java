package io.cui.test.juli;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

class TestLoggerFactoryTest {

    /** . */
    private static final String SOME_LOGGER = "some.logger";

    @Test
    void shouldInstallAndUninstall() {
        assertFalse(TestLoggerFactory.getTestHandlerOption().isPresent());
        TestLoggerFactory.install();
        assertTrue(TestLoggerFactory.getTestHandlerOption().isPresent());
        // Install should be reentrant
        TestLoggerFactory.install();
        assertTrue(TestLoggerFactory.getTestHandlerOption().isPresent());
        TestLoggerFactory.uninstall();
        assertFalse(TestLoggerFactory.getTestHandlerOption().isPresent());
        // Uninstall should be reentrant
        TestLoggerFactory.uninstall();
        assertFalse(TestLoggerFactory.getTestHandlerOption().isPresent());
    }

    @Test
    void shouldHandleMissingInstallation() {
        assertThrows(AssertionError.class, TestLoggerFactory::getTestHandler);
        TestLoggerFactory.install();
        assertNotNull(TestLoggerFactory.getTestHandler());

        TestLoggerFactory.uninstall();
        assertThrows(AssertionError.class, TestLoggerFactory::getTestHandler);
    }

    @Test
    public void shouldReadConfiguration() {
        // Reset logger as preparation
        TestLogLevel.INFO.setAsRootLevel();
        TestLogLevel.INFO.addLogger(SOME_LOGGER);

        Logger someLogger = Logger.getLogger(SOME_LOGGER);
        Logger rootLogger = Logger.getLogger("");

        assertFalse(TestLogLevel.TRACE.isEnabled(someLogger));
        assertFalse(TestLogLevel.DEBUG.isEnabled(rootLogger));

        TestLoggerFactory.configureLogger();
        assertTrue(TestLogLevel.TRACE.isEnabled(someLogger));
        assertTrue(TestLogLevel.DEBUG.isEnabled(rootLogger));

    }
}
