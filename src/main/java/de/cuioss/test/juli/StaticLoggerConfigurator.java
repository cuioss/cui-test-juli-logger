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

import static de.cuioss.test.juli.ConfigurationKeys.CONFIGURATION_KEY_ROOT_LOG_LEVEL;
import static de.cuioss.test.juli.ConfigurationKeys.LOGGER_PREFIX;
import static de.cuioss.tools.string.MoreStrings.isEmpty;
import static de.cuioss.tools.string.MoreStrings.nullToEmpty;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Wraps the configuration / property management of the static resources, from
 * System-properties
 *
 * @author Oliver Wolff
 */
final class StaticLoggerConfigurator {

    /** The storage for programmatically set properties */
    private final Map<String, String> defaultConfiguration = new ConcurrentHashMap<>();

    /**
     * Tries to load a property as String. The order is "Programmatically
     * configured" -> {@link System#getProperties()} -> Default Configuration
     *
     * @param name of the property, must not be null
     * @return the string value of the property, if present, otherwise
     *         {@link Optional#empty()}
     */
    Optional<String> getStringProperty(final String name) {
        if (isEmpty(name)) {
            return Optional.empty();
        }
        if (System.getProperties().containsKey(name)) {
            return Optional.of(System.getProperty(name));
        }
        if (defaultConfiguration.containsKey(name)) {
            return Optional.of(defaultConfiguration.get(name));
        }
        return Optional.empty();
    }

    /**
     * Tries to load a property as Boolean. The order is "Programmatically
     * configured" -> {@link System#getProperties()} -> Default Configuration
     *
     * @param name of the property
     * @return the boolean value of the property, if present, otherwise
     *         {@link Optional#empty()}
     */
    Optional<Boolean> getBooleanProperty(final String name) {
        var propertyOption = getStringProperty(name);
        return propertyOption.map(Boolean::valueOf);
    }

    /**
     * @return a {@link Map} of logger configurations, the order is
     *         {@link System#getProperties()}. A logger configuration is assumed as
     *         String starting with {@value ConfigurationKeys#LOGGER_PREFIX}
     */
    @SuppressWarnings("squid:S3655") // owolff: Option is checked before putting into the map
    Map<String, TestLogLevel> getConfiguredLogger() {
        Map<String, TestLogLevel> found = new HashMap<>();
        Set<String> loggerStrings = new HashSet<>();
        for (Object name : System.getProperties().keySet()) {
            if (startsWith(name, LOGGER_PREFIX)) {
                loggerStrings.add(name.toString());
            }
        }
        for (String config : loggerStrings) {
            found.put(config.substring(LOGGER_PREFIX.length()),
                    TestLogLevel.getLevelOrDefault(getStringProperty(config).get(), TestLogLevel.INFO));
        }
        return found;
    }

    TestLogLevel getRootLevel() {
        var configured = getStringProperty(CONFIGURATION_KEY_ROOT_LOG_LEVEL).orElse("");
        return TestLogLevel.getLevelOrDefault(configured, TestLogLevel.INFO);
    }

    static boolean startsWith(Object toBeChecked, String search) {
        if (null == toBeChecked) {
            return false;
        }
        return nullToEmpty(toBeChecked.toString()).startsWith(nullToEmpty(search));
    }

}
