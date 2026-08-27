/*
 * Copyright © 2023-present CUI-OpenSource-Software (info@cuioss.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.test.juli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.logging.Level;
import java.util.logging.Logger;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pins that a configured level survives garbage collection.
 *
 * <p>{@code java.util.logging.LogManager} holds its {@link Logger} instances
 * <em>weakly</em>. Setting a level on a logger nobody else references therefore
 * survives only until the next GC: the logger is collected, and the following
 * {@code Logger.getLogger(sameName)} hands back a fresh instance whose level is
 * {@code null}. Everything the configured level was meant to enable is then
 * silently dropped — the record never reaches a handler, so it looks like a
 * capture failure rather than a configuration one.</p>
 *
 * <p>Whether it bites depends on GC timing, which is why this surfaced as a CI-only
 * failure in a downstream project and could not be reproduced locally.</p>
 */
@DisplayName("Configured log levels survive garbage collection")
class TestLoggerFactoryGcTest {

    private static final String LOGGER_NAME = "de.cuioss.test.juli.gc.SomeProductionClass";

    @AfterEach
    void tearDown() {
        TestLoggerFactory.getTestHandlerOption().ifPresent(h -> TestLoggerFactory.uninstall());
    }

    /**
     * Provokes collection of any weakly held logger. A plain {@code System.gc()} is a
     * hint, so allocate as well to make the collection actually happen.
     */
    private static void provokeGc() {
        for (var i = 0; i < 5; i++) {
            System.gc();
            var ballast = new byte[8 * 1024 * 1024];
            assertTrue(ballast.length > 0);
        }
    }

    @Test
    @DisplayName("a level set through TestLogLevel is not lost to GC")
    void levelSurvivesGarbageCollection() {
        TestLogLevel.TRACE.addLogger(LOGGER_NAME);
        assertEquals(Level.FINER, Logger.getLogger(LOGGER_NAME).getLevel(),
                "precondition: the level has to be applied in the first place");

        provokeGc();

        assertEquals(Level.FINER, Logger.getLogger(LOGGER_NAME).getLevel(),
                "the configured level must survive GC - LogManager holds loggers weakly, so "
                        + "setting a level without keeping a reference silently loses it");
        assertTrue(Logger.getLogger(LOGGER_NAME).isLoggable(Level.FINE),
                "a logger configured to TRACE must still consider FINE loggable after GC");
    }

    @Test
    @DisplayName("a record logged after GC is still captured")
    void recordAfterGarbageCollectionIsCaptured() {
        TestLoggerFactory.install();
        TestLogLevel.TRACE.addLogger(LOGGER_NAME);

        provokeGc();

        // Resolve the logger the way production code does - freshly, by name.
        Logger.getLogger(LOGGER_NAME).fine("captured after gc");

        assertEquals(1,
                TestLoggerFactory.getTestHandler()
                        .resolveLogMessages(TestLogLevel.DEBUG, "captured after gc").size(),
                "the record must reach the handler after GC; if the level was lost, JUL filters "
                        + "it out before any handler sees it and the failure looks like a "
                        + "capture problem instead of a configuration one");
    }
}
