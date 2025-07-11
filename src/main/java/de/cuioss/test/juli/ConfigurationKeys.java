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

    /**
     * Defines the property prefix for all properties related to configuring the
     * test-logger.
     */
    private static final String PROPERTY_PREFIX = "cui.logging.";

    /**
     * Defines the property prefix for all properties related to configuring certain
     * concrete logger.
     */
    static final String LOGGER_PREFIX = "cui.logger.";

    /** The key for the default log-level */
    static final String CONFIGURATION_KEY_ROOT_LOG_LEVEL = PROPERTY_PREFIX + "root_log_level";

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
