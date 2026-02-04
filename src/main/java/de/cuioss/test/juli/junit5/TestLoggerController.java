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

import static de.cuioss.test.juli.TestLoggerFactory.addLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    @Override
    public void beforeEach(ExtensionContext context) {
        TestLoggerFactory.configureLogger();
        TestLoggerFactory.getTestHandler().clearRecords();

        // Collect annotations from parent classes (outer test classes) to current class
        List<EnableTestLogger> annotations = collectAnnotationsFromHierarchy(context);

        // Apply annotations in order: parent first, then child (so child can override)
        for (EnableTestLogger annotation : annotations) {
            handleEnableTestLoggerAnnotation(annotation);
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        TestLoggerFactory.uninstall();
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        TestLoggerFactory.install();
    }

    /**
     * Collects @EnableTestLogger annotations from the entire test class hierarchy,
     * starting from the outermost parent class down to the current test class.
     * This enables nested test classes to inherit configuration from their parent classes.
     *
     * @param context the current extension context
     * @return list of annotations in parent-to-child order
     */
    private List<EnableTestLogger> collectAnnotationsFromHierarchy(ExtensionContext context) {
        List<EnableTestLogger> annotations = new ArrayList<>();

        // Walk up the context hierarchy to collect all test classes
        ExtensionContext current = context;
        while (current != null) {
            current.getTestClass().ifPresent(testClass -> {
                Optional<EnableTestLogger> annotation = MoreReflection.extractAnnotation(testClass, EnableTestLogger.class);
                annotation.ifPresent(annotations::add);
            });
            current = current.getParent().orElse(null);
        }

        // Reverse the list so we apply parent annotations first
        Collections.reverse(annotations);

        return annotations;
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
