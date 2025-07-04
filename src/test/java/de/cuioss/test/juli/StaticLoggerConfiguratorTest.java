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

import static de.cuioss.test.juli.ConfigurationKeys.LOGGER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StaticLoggerConfiguratorTest {

    private static final String BOOLEAN_SYTEM_PROPERTY_NAME = "some.system.property";
    private StaticLoggerConfigurator underTest;

    @BeforeEach void before() {
        System.clearProperty(BOOLEAN_SYTEM_PROPERTY_NAME);
        underTest = new StaticLoggerConfigurator();
    }

    @Test void shouldReadSystemProperty() {
        assertFalse(underTest.getStringProperty(BOOLEAN_SYTEM_PROPERTY_NAME).isPresent());
        System.setProperty(BOOLEAN_SYTEM_PROPERTY_NAME, "true");
        assertTrue(underTest.getStringProperty(BOOLEAN_SYTEM_PROPERTY_NAME).isPresent());
        assertTrue(underTest.getBooleanProperty(BOOLEAN_SYTEM_PROPERTY_NAME).isPresent());
        assertEquals("true", underTest.getStringProperty(BOOLEAN_SYTEM_PROPERTY_NAME).get());
    }

    @Test void shouldDetermineLoggerFromSystemProperty() {
        final var testLogger = "test.logger";
        System.setProperty(LOGGER_PREFIX + testLogger, BOOLEAN_SYTEM_PROPERTY_NAME);
        assertEquals(1, underTest.getConfiguredLogger().size());
    }
}
