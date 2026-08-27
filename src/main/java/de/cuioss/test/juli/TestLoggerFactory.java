/*
 * Copyright © 2023-present CUI-OpenSource-Software (info@cuioss.de)
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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
     * Number of {@link #install()} calls not yet matched by an {@link #uninstall()}.
     * <p>
     * The JUnit 5 controller installs in {@code BeforeAllCallback} and uninstalls in
     * {@code AfterAllCallback}, and those fire once per <em>container</em>. A test class
     * with {@code @Nested} classes therefore produces nested, not sequential, calls: the
     * enclosing class installs, each nested class installs and uninstalls, and the
     * enclosing class uninstalls last. Removing the handler on the first inner
     * {@code uninstall()} would leave the remaining containers recording into nothing.
     */
    private static int installDepth;

    /**
     * Strong references to every {@link Logger} this factory has configured.
     * <p>
     * {@link java.util.logging.LogManager} holds its loggers <em>weakly</em>. Setting a level
     * on a logger nobody else references therefore survives only until the next GC: the
     * logger is collected and the next {@code Logger.getLogger(sameName)} returns a fresh
     * instance with a {@code null} level, so everything the level enabled is filtered out
     * before reaching a handler. That reads like a capture failure, not a configuration one,
     * and because it depends on GC timing it shows up as an unreproducible CI-only flake.
     * Pinning the loggers here keeps the configuration alive.
     */
    private static final Set<Logger> CONFIGURED_LOGGERS = ConcurrentHashMap.newKeySet();

    /**
     * Adds a {@link TestLogHandler} instance to jul's root logger. This method is
     * reentrant: the handler is added only once, and repeated calls keep the existing
     * instance and increment an installation depth. Each call must be matched by an
     * {@link #uninstall()}; only the outermost one removes the handler.
     */
    public static void install() {
        installDepth++;
        if (getTestHandlerOption().isEmpty()) {
            CONSOLE_HANDLER.saveLevel();
            getRootLogger().addHandler(new TestLogHandler());
        }
    }

    /**
     * Decrements the installation depth and, <em>only when it reaches zero</em>, removes
     * the previously installed {@link TestLogHandler} instance and restores the previously
     * stored {@link ConsoleHandler#getLevel()}. While an enclosing {@link #install()} is
     * still outstanding this call is a no-op, so the handler and everything it has captured
     * survive. A call without a matching {@link #install()} is harmless. See also
     * {@link #install()}.
     */
    public static void uninstall() {
        if (installDepth > 0) {
            installDepth--;
        }
        if (installDepth > 0) {
            // An enclosing container is still running - keep the handler and its records.
            return;
        }
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
            configure("", logLevel);
        }
        configure(loggerName, logLevel);
    }

    /**
     * Sets the level and keeps a strong reference, so the configuration cannot be lost to a
     * garbage collection of the weakly held {@link Logger}. See {@link #CONFIGURED_LOGGERS}.
     */
    private static void configure(String loggerName, TestLogLevel logLevel) {
        var logger = Logger.getLogger(loggerName);
        logger.setLevel(logLevel.getJuliLevel());
        CONFIGURED_LOGGERS.add(logger);
    }
}
