package io.cui.test.juli;

import java.util.Map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Provides Keys / values for the configuration
 *
 * @author Oliver Wolff
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ConfigurationKeys {

    /** Defines the property prefix for all properties related to configuring the test-logger. */
    private static final String PROPERTY_PREFIX = "cui.logging.";

    /**
     * Defines the property prefix for all properties related to configuring certain concrete
     * logger.
     */
    static final String LOGGER_PREFIX = "cui.logger.";

    /** Defines the name / path of the configuration file: cui_logger.properties */
    static final String PROPERTY_FILE_PATH = "cui_logger.properties";

    /** The key for the default log-level */
    static final String CONFIGURATION_KEY_ROOT_LOG_LEVEL =
        PROPERTY_PREFIX + "root_log_level";

    /** The value for the default log-level: {@link TestLogLevel#INFO} */
    static final String CONFIGURATION_DEFAULT_ROOT_LOG_LEVEL = TestLogLevel.INFO.toString();

    /**
     * Populates a given not null mutable map with the defaults.
     *
     * @param defaultMap to be populated
     */
    static void populateDefaults(final Map<String, String> defaultMap) {
        defaultMap.put(CONFIGURATION_KEY_ROOT_LOG_LEVEL, CONFIGURATION_DEFAULT_ROOT_LOG_LEVEL);
    }
}
