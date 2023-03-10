package de.cuioss.test.juli.junit5;

import static de.cuioss.test.juli.TestLoggerFactory.addLogger;

import java.util.Optional;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.TestLoggerFactory;
import de.cuioss.tools.reflect.MoreReflection;

/**
 * Extension for setting up the {@link TestLoggerFactory} properly
 *
 * @author Oliver Wolff
 *
 */
public class TestLoggerController implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        TestLoggerFactory.configureLogger();
        TestLoggerFactory.getTestHandler().clearRecords();
        Class<?> testClass = context.getTestClass()
                .orElseThrow(() -> new IllegalStateException("Unable to determine Test-class"));
        Optional<EnableTestLogger> annotation = MoreReflection.extractAnnotation(testClass,
                EnableTestLogger.class);
        annotation.ifPresent(this::handleEnableTestLoggerAnnotation);
    }

    @Override
    public void afterAll(ExtensionContext context) {
        TestLoggerFactory.uninstall();
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        TestLoggerFactory.install();
    }

    private void handleEnableTestLoggerAnnotation(EnableTestLogger annotation) {
        addLogger(annotation.rootLevel(), "");

        for (Class<?> clazz : annotation.trace()) {
            TestLogLevel.TRACE.addLogger(clazz);
        }
        for (Class<?> clazz : annotation.debug()) {
            TestLogLevel.DEBUG.addLogger(clazz);
        }
        for (Class<?> clazz : annotation.info()) {
            TestLogLevel.INFO.addLogger(clazz);
        }
        for (Class<?> clazz : annotation.warn()) {
            TestLogLevel.WARN.addLogger(clazz);
        }
        for (Class<?> clazz : annotation.error()) {
            TestLogLevel.ERROR.addLogger(clazz);
        }
    }
}
