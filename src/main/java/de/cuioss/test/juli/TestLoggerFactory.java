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

import static de.cuioss.tools.string.MoreStrings.isEmpty;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;


import lombok.experimental.UtilityClass;

/**
 * Central entry point for handling {@link TestLogHandler}
 *
 *
 * @author Oliver Wolff
 *
 */
@UtilityClass
@SuppressWarnings("java:S4792") // owolff: Changing the logger is the actual idea of this type, not
// a security issue
public class TestLoggerFactory {

    private static final StaticLoggerConfigurator configuration = new StaticLoggerConfigurator();

    private static final ConsoleHandlerModifier CONSOLE_HANDLER = new ConsoleHandlerModifier();

    /**
     * Adds a {@link TestLogHandler} instance to jul's root logger. This method is
     * reentrant, it ensures the {@link TestLogHandler} is installed only once
     */
    public static void install() {
        if (getTestHandlerOption().isEmpty()) {
            CONSOLE_HANDLER.saveLevel();
            getRootLogger().addHandler(new TestLogHandler());
        }
    }

    /**
     * Removes previously installed {@link TestLogHandler} instance and restores the
     * previously stored {@link ConsoleHandler#getLevel()}. See also
     * {@link #install()}.
     */
    public static void uninstall() {
        CONSOLE_HANDLER.restoreLevel();
        var testHandlerOption = getTestHandlerOption();
        testHandlerOption.ifPresent(testLogHandler -> getRootLogger().removeHandler(testLogHandler));
    }

    /**
     * Configures the logger sub-system according to the configuration found within
     * {@link System#getProperties()} and / or the file "cui_logger.properties"
     * usually located directly in "src/test/resources".
     */
    public static void configureLogger() {
        // Set Root logger
        var rootLevel = configuration.getRootLevel();
        rootLevel.setAsRootLevel();
        CONSOLE_HANDLER.adjustLevel(rootLevel);
        // Set concrete logger
        for (Entry<String, TestLogLevel> entry : configuration.getConfiguredLogger().entrySet()) {
            entry.getValue().addLogger(entry.getKey());
        }
    }

    private static Logger getRootLogger() {
        return LogManager.getLogManager().getLogger("");
    }

    private static List<Handler> getHandler() {
        return Arrays.asList(getRootLogger().getHandlers());
    }

    /**
     * @return the configured {@link TestLogHandler}
     * @throws AssertionError in case no {@link TestLogHandler} could be found. This
     *                        is usually the case if {@link #install()} was not
     *                        called prior to this request
     */
    public static TestLogHandler getTestHandler() {
        return getTestHandlerOption().orElseThrow(
                () -> new AssertionError("Unable to access de.cuioss.test.juli.TestLogHandler. Used properly?"));
    }

    /**
     * @return the configured {@link TestLogHandler} if present
     */
    public static Optional<TestLogHandler> getTestHandlerOption() {
        for (Handler handler : getHandler()) {
            if (handler instanceof TestLogHandler logHandler) {
                return Optional.of(logHandler);
            }
        }
        return Optional.empty();
    }

    /**
     * Convenient method for setting a Log-Level in context of the given
     * {@link TestLogLevel}
     *
     * @param logLevel   to be set
     * @param loggerName if it is {@code null} or empty it will set the root-logger
     *                   for the actual Log-Level
     */
    public static void addLogger(TestLogLevel logLevel, String loggerName) {
        CONSOLE_HANDLER.adjustLevel(logLevel);
        if (isEmpty(loggerName)) {
            Logger.getLogger("").setLevel(logLevel.getJuliLevel());
        }
        Logger.getLogger(loggerName).setLevel(logLevel.getJuliLevel());
    }
}
