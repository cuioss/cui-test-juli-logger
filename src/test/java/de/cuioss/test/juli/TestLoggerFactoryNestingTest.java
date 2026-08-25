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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins the install/uninstall pairing of {@link TestLoggerFactory}.
 *
 * <p>{@code TestLoggerController} implements {@code BeforeAllCallback} and
 * {@code AfterAllCallback}. Those fire once per <em>container</em>, so a test
 * class with {@code @Nested} classes installs and uninstalls several times:
 * once for the enclosing class and once for every nested class. The calls are
 * therefore nested, not sequential, and the handler must survive until the
 * outermost one completes.</p>
 */
@DisplayName("TestLoggerFactory install/uninstall nesting")
class TestLoggerFactoryNestingTest {

    @AfterEach
    void tearDown() {
        // Drain any depth left behind by a failing expectation.
        for (var i = 0; i < 10 && TestLoggerFactory.getTestHandlerOption().isPresent(); i++) {
            TestLoggerFactory.uninstall();
        }
    }

    @Test
    @DisplayName("handler survives an inner uninstall while an outer install is still active")
    void handlerSurvivesInnerUninstall() {
        TestLoggerFactory.install();   // enclosing class: beforeAll
        TestLoggerFactory.install();   // nested class: beforeAll
        TestLoggerFactory.uninstall(); // nested class: afterAll

        assertTrue(TestLoggerFactory.getTestHandlerOption().isPresent(),
                "handler must remain installed while the enclosing container is still running");

        TestLoggerFactory.uninstall(); // enclosing class: afterAll
        assertFalse(TestLoggerFactory.getTestHandlerOption().isPresent(),
                "handler must be removed once the outermost container completes");
    }

    @Test
    @DisplayName("records survive an inner uninstall")
    void recordsSurviveInnerUninstall() {
        TestLoggerFactory.install();
        TestLoggerFactory.install();
        TestLoggerFactory.uninstall();

        // Reinstalling must not swap in a fresh handler and drop what was captured.
        var handler = TestLoggerFactory.getTestHandler();
        TestLoggerFactory.install();
        assertTrue(handler == TestLoggerFactory.getTestHandler(),
                "re-entrant install must keep the existing handler instance");

        TestLoggerFactory.uninstall();
        TestLoggerFactory.uninstall();
    }

    @Test
    @DisplayName("a stray uninstall does not drive the depth negative")
    void strayUninstallDoesNotUnderflow() {
        TestLoggerFactory.uninstall(); // no matching install

        TestLoggerFactory.install();
        assertTrue(TestLoggerFactory.getTestHandlerOption().isPresent(),
                "install after an unmatched uninstall must still install");

        TestLoggerFactory.uninstall();
        assertFalse(TestLoggerFactory.getTestHandlerOption().isPresent(),
                "a single install must be undone by a single uninstall");
    }
}
