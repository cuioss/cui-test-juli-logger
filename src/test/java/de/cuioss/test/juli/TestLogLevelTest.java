/**
 * Copyright © 2025 CUI-OpenSource-Software (info@cuioss.de)
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.logging.Level;
import java.util.logging.Logger;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestLogLevelTest {

    private final String loggerBase = getClass().getName() + ".";

    @BeforeEach void before() {
        TestLogLevel.INFO.setAsRootLevel().addLogger(getClass());
    }

    @Test void shouldHandleLogLevel() {
        var name = loggerBase + "a";
        var logger = Logger.getLogger(name);

        assertEquals(logger.isLoggable(Level.INFO), TestLogLevel.INFO.isEnabled(logger));
        assertTrue(TestLogLevel.INFO.isEnabled(logger));
        assertFalse(TestLogLevel.DEBUG.isEnabled(logger));

        // Set Trace for concrete Logger
        TestLogLevel.TRACE.addLogger(name);
        assertTrue(TestLogLevel.TRACE.isEnabled(logger));

    }

    @Test void shouldHandleLogLevelRoot() {

        var rootLogger = Logger.getLogger("");
        // Set Root-level to debug
        TestLogLevel.DEBUG.setAsRootLevel();
        assertTrue(TestLogLevel.DEBUG.isEnabled(rootLogger));
        assertFalse(TestLogLevel.TRACE.isEnabled(rootLogger));
    }

    @Test void shouldHandleLogLevelAsClass() {
        var logger = Logger.getLogger(getClass().getName());
        TestLogLevel.INFO.addLogger(getClass());
        assertTrue(TestLogLevel.INFO.isEnabled(logger));

        TestLogLevel.WARN.addLogger(getClass());
        assertFalse(TestLogLevel.INFO.isEnabled(logger));

        var rootLogger = Logger.getLogger("");
        assertFalse(TestLogLevel.DEBUG.isEnabled(rootLogger));
        // Use null as class argument
        TestLogLevel.DEBUG.addLogger((Class<?>) null);
        assertTrue(TestLogLevel.DEBUG.isEnabled(rootLogger));
    }

    @Test void shouldParseLoglevel() {
        assertEquals(TestLogLevel.ERROR,
                TestLogLevel.getLevelOrDefault(TestLogLevel.ERROR.toString(), TestLogLevel.DEBUG));
        assertEquals(TestLogLevel.DEBUG, TestLogLevel.getLevelOrDefault("", TestLogLevel.DEBUG));
        assertEquals(TestLogLevel.DEBUG, TestLogLevel.getLevelOrDefault("notThere", TestLogLevel.DEBUG));
    }

}
