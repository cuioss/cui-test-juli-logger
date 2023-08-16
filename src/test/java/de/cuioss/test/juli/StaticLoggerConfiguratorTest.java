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

    @BeforeEach
    void before() {
        System.clearProperty(BOOLEAN_SYTEM_PROPERTY_NAME);
        underTest = new StaticLoggerConfigurator();
    }

    @Test
    void shouldReadSystemProperty() {
        assertFalse(underTest.getStringProperty(BOOLEAN_SYTEM_PROPERTY_NAME).isPresent());
        System.setProperty(BOOLEAN_SYTEM_PROPERTY_NAME, "true");
        assertTrue(underTest.getStringProperty(BOOLEAN_SYTEM_PROPERTY_NAME).isPresent());
        assertTrue(underTest.getBooleanProperty(BOOLEAN_SYTEM_PROPERTY_NAME).isPresent());
        assertEquals("true", underTest.getStringProperty(BOOLEAN_SYTEM_PROPERTY_NAME).get());
    }

    @Test
    void shouldDetermineLoggerFromSystemProperty() {
        final String testLogger = "test.logger";
        System.setProperty(LOGGER_PREFIX + testLogger, BOOLEAN_SYTEM_PROPERTY_NAME);
        assertEquals(1, underTest.getConfiguredLogger().size());
    }
}
