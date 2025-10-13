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
package de.cuioss.test.juli.junit5;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.logging.Level;


import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.TestLoggerFactory;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Meta-annotation that allows test classes to be extended with
 * {@link TestLoggerController} instead of using
 * {@code @ExtendWith(TestLoggerController.class)}.
 * <p>
 * Used on a Junit 5 test this annotation ensures that the test-logger /
 * assertion system is initialized properly, see
 * {@link TestLoggerFactory#install()}, and
 * {@link TestLoggerFactory#configureLogger()}, and the actual log-statements
 * are cleared before each test. After all tests the test-logger is uninstalled
 * again, see {@link TestLoggerFactory#uninstall()}.
 * </p>
 * <p>
 * Use the annotations for specifying the log-level to be set for the concrete
 * unit-tests. The level defined within this annotation will overwrite settings
 * found either within {@link System#getProperty(String)} and
 * "cui_logger.properties"
 * </p>
 * <p>
 * Use {@link LogAsserts} to make assertions to logged data.
 * </p>
 *
 * @author Oliver Wolff
 *
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@ExtendWith(TestLoggerController.class)
public @interface EnableTestLogger {

    /**
     * @return The {@link TestLogLevel} to be set before each test. It defaults to
     *         {@link TestLogLevel#INFO}
     */
    TestLogLevel rootLevel() default TestLogLevel.INFO;

    /**
     * @return the types for which {@link TestLogLevel#TRACE} will be enabled, which
     *         implicitly maps to {@link Level#FINEST}
     */
    Class<?>[] trace() default {};

    /**
     * @return the types for which {@link TestLogLevel#DEBUG} will be enabled, which
     *         implicitly maps to {@link Level#FINE}
     */
    Class<?>[] debug() default {};

    /**
     * @return the types for which {@link TestLogLevel#INFO} will be enabled, which
     *         implicitly maps to {@link Level#INFO}
     */
    Class<?>[] info() default {};

    /**
     * @return the types for which {@link TestLogLevel#WARN} will be enabled, which
     *         implicitly maps to {@link Level#WARNING}
     */
    Class<?>[] warn() default {};

    /**
     * @return the types for which {@link TestLogLevel#ERROR} will be enabled, which
     *         implicitly maps to {@link Level#SEVERE}
     */
    Class<?>[] error() default {};
}
