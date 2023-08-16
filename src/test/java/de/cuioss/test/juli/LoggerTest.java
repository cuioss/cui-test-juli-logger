/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.test.juli;

import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import de.cuioss.test.juli.junit5.EnableTestLogger;

@EnableTestLogger
class LoggerTest {

    private static final String DEBUG = "Debug";
    private static final String HELLO = "Hello";
    private static final Logger LOG = Logger.getLogger(LoggerTest.class.getName());

    @Test
    void doLog() {
        LOG.log(TestLogLevel.INFO.getJuliLevel(), HELLO);
        LogAsserts.assertSingleLogMessagePresent(TestLogLevel.INFO, HELLO);
        TestLogLevel.DEBUG.addLogger(getClass());
        LOG.log(TestLogLevel.DEBUG.getJuliLevel(), DEBUG);
        LogAsserts.assertSingleLogMessagePresent(TestLogLevel.DEBUG, DEBUG);
    }
}
