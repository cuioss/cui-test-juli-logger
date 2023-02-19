package io.cui.test.juli;

import static io.cui.test.juli.ConfigurationKeys.CONFIGURATION_KEY_ROOT_LOG_LEVEL;
import static io.cui.test.juli.ConfigurationKeys.LOGGER_PREFIX;
import static io.cui.test.juli.ConfigurationKeys.PROPERTY_FILE_PATH;
import static io.cui.tools.string.MoreStrings.isEmpty;
import static io.cui.tools.string.MoreStrings.nullToEmpty;

import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wraps the configuration / property management of the static resources, as there are the
 * {@value ConfigurationKeys#PROPERTY_FILE_PATH} and System-properties
 *
 * @author Oliver Wolff
 */
final class StaticLoggerConfigurator {

    /** The properties from the file system if set: see {@value ConfigurationKeys#PROPERTY_FILE_PATH} */
    private Properties fileSystemProperties;

    /** The storage for programmatically set properties */
    private final Map<String, String> defaultConfiguration = new ConcurrentHashMap<>();

    /**
     * Tries to load a property as String. The order is "Programmatically configured" ->
     * {@link System#getProperties()} file: {@value ConfigurationKeys#PROPERTY_FILE_PATH}
     * -> Default Configuration
     *
     * @param name of the property, must not be null
     * @return the string value of the property, if present, otherwise {@link Optional#empty()}
     */
    Optional<String> getStringProperty(final String name) {
        if (isEmpty(name)) {
            return Optional.empty();
        }
        if (System.getProperties().containsKey(name)) {
            return Optional.of(System.getProperty(name));
        }
        checkIfInitialized();
        if (fileSystemProperties.containsKey(name)) {
            return Optional.of(fileSystemProperties.getProperty(name));
        }
        if (defaultConfiguration.containsKey(name)) {
            return Optional.of(defaultConfiguration.get(name));
        }
        return Optional.empty();
    }

    /**
     * Tries to load a property as Boolean. The order is "Programmatically configured" ->
     * {@link System#getProperties()} file: {@value ConfigurationKeys#PROPERTY_FILE_PATH}
     * -> Default Configuration
     *
     * @param name of the property
     * @return the boolean value of the property, if present, otherwise {@link Optional#empty()}
     */
    Optional<Boolean> getBooleanProperty(final String name) {
        var propertyOption = getStringProperty(name);
        return propertyOption.map(Boolean::valueOf);
    }

    /**
     * @return a {@link Map} of logger configurations, the order is file:
     *         {@value ConfigurationKeys#PROPERTY_FILE_PATH} ->
     *         {@link System#getProperties()}. A logger configuration is assumed as String starting
     *         with {@value ConfigurationKeys#LOGGER_PREFIX}
     */
    @SuppressWarnings("squid:S3655") // owolff: Option is checked before putting into the map
    Map<String, TestLogLevel> getConfiguredLogger() {
        Map<String, TestLogLevel> found = new HashMap<>();
        Set<String> loggerStrings = new HashSet<>();
        checkIfInitialized();
        for (Object name : fileSystemProperties.keySet()) {
            if (startsWith( name, LOGGER_PREFIX)) {
                loggerStrings.add(name.toString());
            }
        }
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

    private void checkIfInitialized() {
        if (null == fileSystemProperties) {
            synchronized (StaticLoggerConfigurator.class) {
                fileSystemProperties = new Properties();
                loadPropertyFile();
                ConfigurationKeys.populateDefaults(defaultConfiguration);
            }
        }
    }

    private void loadPropertyFile() {
        try (var in = AccessController.doPrivileged((PrivilegedAction<InputStream>) () -> {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {
                return contextClassLoader
                        .getResourceAsStream(PROPERTY_FILE_PATH);
            }
            return ClassLoader.getSystemResourceAsStream(PROPERTY_FILE_PATH);
        })) {
            if (in != null) {
                fileSystemProperties.load(in);
            }
        } catch (IOException e1) {
            Logger.getLogger(getClass().getName()).log(Level.FINE, "Unable to load configuration", e1);
        }
    }

    static boolean startsWith(Object toBeChecked, String search) {
        if (null == toBeChecked) {
            return false;
        }
        return nullToEmpty(toBeChecked.toString()).startsWith(nullToEmpty(search));
    }

}
