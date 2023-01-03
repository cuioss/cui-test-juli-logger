package io.cui.test.juli.junit5;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.cui.test.juli.TestLoggerFactory;

@ExtendWith(TestLoggerController.class)
class TestLoggerControllerTest {

    @Test
    void shouldBeInstalledAndConfiguredAndEmptied() {
        assertTrue(TestLoggerFactory.getTestHandlerOption().isPresent());
        assertTrue(TestLoggerFactory.getTestHandler().getRecords().isEmpty());
    }

}
