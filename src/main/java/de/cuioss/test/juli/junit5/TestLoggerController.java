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
package de.cuioss.test.juli.junit5;

import static de.cuioss.test.juli.TestLoggerFactory.addLogger;

import java.util.Optional;


import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.TestLoggerFactory;
import de.cuioss.tools.reflect.MoreReflection;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Extension for setting up the {@link TestLoggerFactory} properly
 *
 * @author Oliver Wolff
 *
 */
public class TestLoggerController implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback {

    @Override public void beforeEach(ExtensionContext context) {
        TestLoggerFactory.configureLogger();
        TestLoggerFactory.getTestHandler().clearRecords();
        Class<?> testClass = context.getTestClass()
                .orElseThrow(() -> new IllegalStateException("Unable to determine Test-class"));
        Optional<EnableTestLogger> annotation = MoreReflection.extractAnnotation(testClass, EnableTestLogger.class);
        annotation.ifPresent(this::handleEnableTestLoggerAnnotation);
    }

    @Override public void afterAll(ExtensionContext context) {
        TestLoggerFactory.uninstall();
    }

    @Override public void beforeAll(ExtensionContext context) {
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
