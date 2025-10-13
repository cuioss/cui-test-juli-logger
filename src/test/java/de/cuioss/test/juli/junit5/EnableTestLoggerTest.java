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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;


import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.TestLoggerFactory;
import org.junit.jupiter.api.Test;

@EnableTestLogger(rootLevel = TestLogLevel.INFO, trace = LogAsserts.class, debug = TestLogLevel.class, info = TestLoggerFactory.class, warn = List.class, error = Set.class)
class EnableTestLoggerTest {

    @Test void shouldBeInstalledAndConfiguredAndEmptied() {
        assertTrue(TestLoggerFactory.getTestHandlerOption().isPresent());
        assertTrue(TestLoggerFactory.getTestHandler().getRecords().isEmpty());
    }

}
