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

import static de.cuioss.test.juli.LogAsserts.assertLogMessagePresent;
import static de.cuioss.test.juli.LogAsserts.assertLogMessagePresentContaining;
import static de.cuioss.test.juli.LogAsserts.assertNoLogMessagePresent;
import static de.cuioss.test.juli.LogAsserts.assertSingleLogMessagePresent;
import static de.cuioss.test.juli.LogAsserts.assertSingleLogMessagePresentContaining;
import static de.cuioss.test.juli.TestLogHandlerTest.MESSAGE;
import static de.cuioss.test.juli.TestLogHandlerTest.MESSAGE_PART;
import static de.cuioss.test.juli.TestLogHandlerTest.create;
import static de.cuioss.test.juli.TestLogLevel.DEBUG;
import static de.cuioss.test.juli.TestLogLevel.INFO;
import static org.junit.jupiter.api.Assertions.assertThrows;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LogAssertsTest {

    private final RuntimeException runtimeException = /*~~(TODO: Use specific not RuntimeException. Suppress: // cui-rewrite:disable InvalidExceptionUsageRecipe)~~>*/new RuntimeException();
    private final IllegalArgumentException illegalArgumentException = new IllegalArgumentException();

    @BeforeAll static void beforeAll() {
        TestLoggerFactory.install();
    }

    @AfterAll static void afterAll() {
        TestLoggerFactory.uninstall();
    }

    @BeforeEach void beforeEach() {
        var handler = TestLoggerFactory.getTestHandler();
        handler.clearRecords();
        handler.publish(create(DEBUG, MESSAGE, runtimeException));
        handler.publish(create(INFO, MESSAGE, runtimeException));
        handler.publish(create(INFO, MESSAGE, illegalArgumentException));
    }

    @Test void shouldAssertLogMessageExact() {
        assertLogMessagePresent(DEBUG, MESSAGE);
        assertLogMessagePresent(INFO, MESSAGE);

        assertSingleLogMessagePresent(DEBUG, MESSAGE);

        assertThrows(AssertionError.class, () -> assertSingleLogMessagePresent(INFO, MESSAGE));
    }

    @Test void shouldAssertNoLogMessagePresent() {
        assertNoLogMessagePresent(INFO, "Not there");

        assertThrows(AssertionError.class, () -> assertNoLogMessagePresent(INFO, MESSAGE));
    }

    @Test void shouldAssertNoMessageForLoggerPresent() {
        assertNoLogMessagePresent(TestLogLevel.TRACE, TestLogLevelTest.class);

        assertThrows(AssertionError.class, () -> assertNoLogMessagePresent(INFO, TestLogHandlerTest.class));
    }

    @Test void shouldAssertLogMessageWithException() {
        assertLogMessagePresent(DEBUG, MESSAGE, runtimeException);
        assertLogMessagePresent(INFO, MESSAGE, runtimeException);
        assertLogMessagePresent(INFO, MESSAGE, illegalArgumentException);

        assertSingleLogMessagePresent(DEBUG, MESSAGE, runtimeException);

        assertThrows(AssertionError.class,
                () -> assertSingleLogMessagePresent(DEBUG, MESSAGE, illegalArgumentException));
    }

    @Test void shouldAssertLogMessageWithExceptionClass() {
        assertLogMessagePresent(DEBUG, MESSAGE, runtimeException.getClass());
        assertLogMessagePresent(INFO, MESSAGE, runtimeException.getClass());
        assertLogMessagePresent(INFO, MESSAGE, illegalArgumentException.getClass());

        assertSingleLogMessagePresent(DEBUG, MESSAGE, runtimeException.getClass());

        assertThrows(AssertionError.class,
                () -> assertSingleLogMessagePresent(DEBUG, MESSAGE, IllegalArgumentException.class));
    }

    @Test void shouldAssertLogMessageContains() {
        assertLogMessagePresentContaining(DEBUG, MESSAGE_PART);
        assertLogMessagePresentContaining(INFO, MESSAGE_PART);

        assertSingleLogMessagePresentContaining(DEBUG, MESSAGE_PART);

        assertThrows(AssertionError.class, () -> assertSingleLogMessagePresentContaining(INFO, MESSAGE));
    }

    @Test void shouldAssertLogMsgContainingThrowable() {
        LogAsserts.assertLogMessagePresentContaining(DEBUG, MESSAGE_PART, runtimeException);
    }

    @Test void shouldAssertSingleLogMsgContainingThrowable() {
        LogAsserts.assertSingleLogMessagePresentContaining(DEBUG, MESSAGE_PART, runtimeException);
    }

    @Test void shouldAssertLogMsgContainingThrowableClass() {
        LogAsserts.assertLogMessagePresentContaining(DEBUG, MESSAGE_PART, runtimeException.getClass());
    }

    @Test void shouldAssertSingleLogMsgContainingThrowableClass() {
        LogAsserts.assertSingleLogMessagePresentContaining(DEBUG, MESSAGE_PART, runtimeException.getClass());
    }
}
