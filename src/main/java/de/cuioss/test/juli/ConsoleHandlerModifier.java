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

import java.util.Optional;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;

/**
 * Simplifies the access on an (optional) existing {@link ConsoleHandler}
 *
 * @author Oliver Wolff
 *
 */
class ConsoleHandlerModifier {

    private Level initialHandlerLevel;

    /**
     * Saves the current {@link Level} of the {@link ConsoleHandler} for later
     * restoring
     */
    void saveLevel() {
        var consoleHandler = getConsoleHandler();
        consoleHandler.ifPresent(handler -> initialHandlerLevel = handler.getLevel());
    }

    /**
     * Adjusts the {@link Level} of the wrapped {@link ConsoleHandler}
     *
     * @param level to be set
     */
    void adjustLevel(TestLogLevel level) {
        var consoleHandler = getConsoleHandler();
        if (consoleHandler.isPresent()) {
            var current = consoleHandler.get().getLevel();
            var newLevel = level.getJuliLevel();
            if (newLevel.intValue() < current.intValue()) {
                consoleHandler.get().setLevel(newLevel);
            }
        }

    }

    /**
     * Restores the previously stored {@link Level} of the {@link ConsoleHandler}
     */
    void restoreLevel() {
        var consoleHandler = getConsoleHandler();
        if (consoleHandler.isPresent() && null != initialHandlerLevel
                && initialHandlerLevel.intValue() != consoleHandler.get().getLevel().intValue()) {
            consoleHandler.get().setLevel(initialHandlerLevel);
            initialHandlerLevel = null;
        }
    }

    /**
     * @return the configured {@link ConsoleHandler} if present
     */
    static Optional<ConsoleHandler> getConsoleHandler() {
        for (Handler handler : LogManager.getLogManager().getLogger("").getHandlers()) {
            if (handler instanceof ConsoleHandler consoleHandler) {
                return Optional.of(consoleHandler);
            }
        }
        return Optional.empty();

    }

}
