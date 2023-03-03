package de.cuioss.test.juli;

import static de.cuioss.tools.string.MoreStrings.isEmpty;
import static java.util.Objects.requireNonNull;

import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Defines the log-levels with implicit mapping to JUL log level according to:
 * <a href=
 * "https://www.slf4j.org/apidocs/org/slf4j/bridge/SLF4JBridgeHandler.html">SLF4JBridgeHandler</a>
 *
 * @author Oliver Wolff
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum TestLogLevel {

    /** Trace Level, maps to {@link Level#FINER}. */
    TRACE(Level.FINER),
    /** Debug Level, maps to {@link Level#FINE}. */
    DEBUG(Level.FINE),
    /** Info Level, maps to {@link Level#INFO}. */
    INFO(Level.INFO),
    /** Warn Level, maps to {@link Level#WARNING}. */
    WARN(Level.WARNING),
    /** Error Level, maps to {@link Level#SEVERE}. */
    ERROR(Level.SEVERE);

    @Getter
    private final Level juliLevel;

    /**
     *
     * @param logger to be checked, must not be null
     * @return {@code true} if the log-level is enabled on the logger, false otherwise
     */
    boolean isEnabled(Logger logger) {
        return logger.isLoggable(getJuliLevel());
    }

    /**
     * Convenient method for setting the root-logger-level in the context of the current
     * {@link TestLogLevel}
     *
     * @return the {@link TestLogLevel} itself in order to us is in a fluent way
     */
    public TestLogLevel setAsRootLevel() {
        return addLogger("");
    }

    /**
     * Convenient method for setting a Log-Level in the context of the current {@link TestLogLevel}
     *
     * @param className if it is {@code null} it will set the root-logger for the actual
     *            Log-Level
     * @return the {@link TestLogLevel} itself in order to us is in a fluent way
     */
    public TestLogLevel addLogger(Class<?> className) {
        if (null == className) {
            return addLogger("");
        }
        return addLogger(className.getName());
    }

    /**
     * Convenient method for setting a Log-Level in context of the current {@link TestLogLevel}
     *
     * @param loggerName if it is {@code null} or empty it will set the root-logger for the actual
     *            Log-Level
     * @return the {@link TestLogLevel} itself in order to us is in a fluent way
     */
    public TestLogLevel addLogger(String loggerName) {
        TestLoggerFactory.addLogger(this, loggerName);
        return this;
    }

    /**
     * @param level to be parsed, must not be null
     * @return the mapped level or {@link TestLogLevel#INFO} if the level can not be mapped
     */
    public static TestLogLevel parse(Level level) {
        requireNonNull(level);
        for (TestLogLevel testLogLevel : TestLogLevel.values()) {
            if (testLogLevel.juliLevel.equals(level)) {
                return testLogLevel;
            }
        }
        return TestLogLevel.INFO;
    }

    /**
     * Factory method for deriving a {@link TestLogLevel} from a given String.
     *
     * @param levelAsAString The String representation of the desired {@link TestLogLevel}
     * @param defaultLevel must not be null
     * @return the desired {@link TestLogLevel} if levelAsAString is null
     *         or empty or is not a defined {@link TestLogLevel} the method will return the given
     *         defaultLevel
     */
    static TestLogLevel getLevelOrDefault(final String levelAsAString,
            final TestLogLevel defaultLevel) {
        requireNonNull(defaultLevel);
        if (isEmpty(levelAsAString)) {
            return defaultLevel;
        }
        try {
            return TestLogLevel.valueOf(levelAsAString.toUpperCase());
        } catch (IllegalArgumentException e) {
            var message = String.format("Unable to determine logger, expected one of %s, but was %s",
                    EnumSet.allOf(TestLogLevel.class), levelAsAString);
            Logger.getLogger(TestLogLevel.class.getName()).log(Level.FINE, message, e);
            return defaultLevel;
        }
    }
}
