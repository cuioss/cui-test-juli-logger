/*
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
    void shouldReadConfiguration() {
        // Reset logger as preparation
        TestLogLevel.INFO.setAsRootLevel();
        TestLogLevel.INFO.addLogger(SOME_LOGGER);

        var someLogger = Logger.getLogger(SOME_LOGGER);
        var rootLogger = Logger.getLogger("");

        assertFalse(TestLogLevel.TRACE.isEnabled(someLogger));
        assertFalse(TestLogLevel.DEBUG.isEnabled(rootLogger));

        TestLoggerFactory.configureLogger();

    }
}
