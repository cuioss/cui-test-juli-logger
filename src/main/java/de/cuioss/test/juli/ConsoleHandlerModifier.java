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
     * Saves the current {@link Level} of the {@link ConsoleHandler} for later restoring
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
            if (handler instanceof ConsoleHandler) {
                return Optional.of((ConsoleHandler) handler);
            }
        }
        return Optional.empty();

    }

}
