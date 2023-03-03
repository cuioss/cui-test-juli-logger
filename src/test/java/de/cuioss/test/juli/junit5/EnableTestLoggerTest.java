package de.cuioss.test.juli.junit5;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.TestLoggerFactory;

@EnableTestLogger(rootLevel = TestLogLevel.INFO, trace = LogAsserts.class, debug = TestLogLevel.class,
        info = TestLoggerFactory.class, warn = List.class, error = Set.class)
class EnableTestLoggerTest {

    @Test
    void shouldBeInstalledAndConfiguredAndEmptied() {
        assertTrue(TestLoggerFactory.getTestHandlerOption().isPresent());
        assertTrue(TestLoggerFactory.getTestHandler().getRecords().isEmpty());
    }

}
