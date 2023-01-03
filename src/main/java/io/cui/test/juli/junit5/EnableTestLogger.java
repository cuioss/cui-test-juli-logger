package io.cui.test.juli.junit5;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.logging.Level;

import org.junit.jupiter.api.extension.ExtendWith;

import io.cui.test.juli.LogAsserts;
import io.cui.test.juli.TestLogLevel;
import io.cui.test.juli.TestLoggerFactory;

/**
 * Meta-annotation that allows test classes to be extended with {@link TestLoggerController}
 * instead of using {@code @ExtendWith(TestLoggerController.class)}.
 * <p>
 * Used on a Junit 5 test this annotation ensures that the test-logger / assertion system is
 * initialized properly, see {@link TestLoggerFactory#install()}, and
 * {@link TestLoggerFactory#configureLogger()}, and the actual log-statements are cleared
 * before each test. After all tests the test-logger is uninstalled again, see
 * {@link TestLoggerFactory#uninstall()}.
 * </p>
 * <p>
 * Use the annotations for specifying the log-level to be set for the concrete unit-tests. The level
 * defined within this annotation will overwrite settings found either within
 * {@link System#getProperty(String)} and "cui_logger.properties"
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
     * @return the types for which {@link TestLogLevel#TRACE} will be enabled, which implicitly maps
     *         to {@link Level#FINEST}
     */
    Class<?>[] trace() default {};

    /**
     * @return the types for which {@link TestLogLevel#DEBUG} will be enabled, which implicitly maps
     *         to {@link Level#FINE}
     */
    Class<?>[] debug() default {};

    /**
     * @return the types for which {@link TestLogLevel#INFO} will be enabled, which implicitly
     *         maps to {@link Level#INFO}
     */
    Class<?>[] info() default {};

    /**
     * @return the types for which {@link TestLogLevel#WARN} will be enabled, which implicitly maps
     *         to {@link Level#WARNING}
     */
    Class<?>[] warn() default {};

    /**
     * @return the types for which {@link TestLogLevel#ERROR} will be enabled, which implicitly maps
     *         to {@link Level#SEVERE}
     */
    Class<?>[] error() default {};
}
