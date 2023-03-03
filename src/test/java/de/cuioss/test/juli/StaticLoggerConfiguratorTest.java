package de.cuioss.test.juli;

import static de.cuioss.test.juli.ConfigurationKeys.CONFIGURATION_KEY_ROOT_LOG_LEVEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StaticLoggerConfiguratorTest {

    private static final String BOOLEAN_FILE_PROPERTY_NAME = "some.test.property";
    private static final String BOOLEAN_SYTEM_PROPERTY_NAME = "some.system.property";
    private StaticLoggerConfigurator underTest;

    @BeforeEach
    void before() {
        System.clearProperty(BOOLEAN_SYTEM_PROPERTY_NAME);
        underTest = new StaticLoggerConfigurator();
    }

    @Test
    void shouldHandleFileProperty() {
        assertTrue(underTest.getStringProperty(BOOLEAN_FILE_PROPERTY_NAME).isPresent());
        assertFalse(underTest.getStringProperty(BOOLEAN_SYTEM_PROPERTY_NAME).isPresent());

        assertFalse(underTest.getStringProperty(null).isPresent());
        assertEquals("true", underTest.getStringProperty(BOOLEAN_FILE_PROPERTY_NAME).get());
        assertEquals(Boolean.TRUE, underTest.getBooleanProperty(BOOLEAN_FILE_PROPERTY_NAME).get());
    }

    @Test
    void shouldReadSystemProperty() {
        assertFalse(underTest.getStringProperty(BOOLEAN_SYTEM_PROPERTY_NAME).isPresent());
        System.setProperty(BOOLEAN_SYTEM_PROPERTY_NAME, BOOLEAN_FILE_PROPERTY_NAME);
        assertTrue(underTest.getStringProperty(BOOLEAN_SYTEM_PROPERTY_NAME).isPresent());
        assertEquals(BOOLEAN_FILE_PROPERTY_NAME,
                underTest.getStringProperty(BOOLEAN_SYTEM_PROPERTY_NAME).get());
    }

    @Test
    void shouldReadDefaults() {
        assertTrue(underTest.getStringProperty(CONFIGURATION_KEY_ROOT_LOG_LEVEL)
                .isPresent());
    }

    @Test
    void shouldReadLogger() {
        assertFalse(underTest.getConfiguredLogger().isEmpty());
        assertEquals("some.logger", underTest.getConfiguredLogger().keySet().iterator().next());

        System.setProperty("cui.logger.other.logger", "warn");
        assertEquals(2, underTest.getConfiguredLogger().size());
    }
}
