# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

cui-test-juli-logger is a Java library (Java 21+) that provides testing utilities for Java Util Logging (JUL) in JUnit 5 tests. It allows capturing, configuring, and asserting log messages during unit tests.

**Maven Coordinates**: `de.cuioss.test:cui-test-juli-logger`

## Build Commands

Use Maven Wrapper (`./mvnw`) for all Maven operations:

```bash
# Build the project
./mvnw clean install

# Run tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=TestLoggerFactoryTest

# Run a specific test method
./mvnw test -Dtest=TestLoggerFactoryTest#shouldInstallHandler

# Skip tests
./mvnw clean install -DskipTests

# Generate site documentation
./mvnw site
```


## Git Workflow

This repository has branch protection on `main`. Direct pushes to `main` are never allowed. Always use this workflow:

1. Create a feature branch: `git checkout -b <branch-name>`
2. Commit changes: `git add <files> && git commit -m "<message>"`
3. Push the branch: `git push -u origin <branch-name>`
4. Create a PR: `gh pr create --head <branch-name> --base main --title "<title>" --body "<body>"`
5. Enable auto-merge: `gh pr merge --auto --squash --delete-branch`
6. Wait for merge (check every ~60s): `while gh pr view --json state -q '.state' | grep -q OPEN; do sleep 60; done`
7. Return to main: `git checkout main && git pull`

## Architecture

### Core Components

1. **TestLogHandler** (`TestLogHandler.java`)
   - Custom JUL Handler that stores LogRecords in memory
   - Provides query methods to find log messages by level, logger name, message content, or throwables
   - Thread-safe with synchronized collections

2. **TestLoggerFactory** (`TestLoggerFactory.java`)
   - Central entry point for managing the TestLogHandler lifecycle
   - `install()`: Adds TestLogHandler to JUL root logger (reentrant)
   - `uninstall()`: Removes TestLogHandler and restores ConsoleHandler level
   - `configureLogger()`: Applies configuration from System properties or `cui_logger.properties`
   - `getTestHandler()`: Retrieves the active TestLogHandler

3. **LogAsserts** (`LogAsserts.java`)
   - Static utility methods for asserting log statements in tests
   - Methods like `assertLogMessagePresent()`, `assertSingleLogMessagePresent()`, `assertNoLogMessagePresent()`
   - Provides detailed assertion messages including all captured logs

4. **TestLogLevel** (`TestLogLevel.java`)
   - Enum mapping test log levels to JUL levels following SLF4J bridge conventions:
     - TRACE → Level.FINER
     - DEBUG → Level.FINE
     - INFO → Level.INFO
     - WARN → Level.WARNING
     - ERROR → Level.SEVERE
   - Fluent API for setting log levels: `TestLogLevel.DEBUG.addLogger(MyClass.class)`

### JUnit 5 Integration

- **@EnableTestLogger** annotation for declarative setup
  - Supports configuration: `@EnableTestLogger(rootLevel = TestLogLevel.DEBUG, trace = MyClass.class)`
- **TestLoggerController** JUnit extension handles lifecycle:
  - `beforeAll`: Installs TestLogHandler
  - `beforeEach`: Configures loggers and clears records
  - `afterAll`: Uninstalls TestLogHandler

### Configuration System

**StaticLoggerConfigurator** loads configuration from:
1. Programmatic configuration (highest priority)
2. System properties
3. Default configuration file `cui_logger.properties` in `src/test/resources`

Configuration keys defined in `ConfigurationKeys.java`:
- `cui.test.juli.root.level`: Root logger level
- `cui.test.juli.logger.<logger-name>`: Specific logger levels

## Development Conventions

- **Project uses Lombok**: Code uses `@UtilityClass`, `@Getter`, `@RequiredArgsConstructor`
- **Parent POM**: `de.cuioss:cui-java-parent:1.3.1` provides shared configuration
- **Dependency**: Uses `de.cuioss:cui-java-tools` for string utilities
- **Module name**: `de.cuioss.test.juli` (automatic module)
- **License**: Apache 2.0

## Testing Workflow

Typical test structure using this library:

```java
@EnableTestLogger
class MyTest {
    @Test
    void shouldLogDebugMessage() {
        // Arrange
        TestLogLevel.DEBUG.addLogger(MyClass.class);

        // Act
        myClass.doSomething();

        // Assert
        LogAsserts.assertLogMessagePresent(TestLogLevel.DEBUG, "expected message");
    }
}
```

## Key Design Patterns

- **Factory Pattern**: TestLoggerFactory manages TestLogHandler instances
- **JUnit Extension Pattern**: TestLoggerController implements JUnit 5 lifecycle callbacks
- **Static Utility Classes**: LogAsserts provides assertion helpers
- **Handler Pattern**: TestLogHandler extends java.util.logging.Handler
- **Configuration Hierarchy**: Multiple configuration sources with defined precedence

## Git Workflow

All cuioss repositories have branch protection on `main`. Direct pushes to `main` are never allowed. Always use this workflow:

1. Create a feature branch: `git checkout -b <branch-name>`
2. Commit changes: `git add <files> && git commit -m "<message>"`
3. Push the branch: `git push -u origin <branch-name>`
4. Create a PR: `gh pr create --repo cuioss/cui-test-juli-logger --head <branch-name> --base main --title "<title>" --body "<body>"`
5. Wait for CI + Gemini review (waits until checks complete): `gh pr checks --watch`
6. **Handle Gemini review comments** — fetch with `gh api repos/cuioss/cui-test-juli-logger/pulls/<pr-number>/comments` and for each:
   - If clearly valid and fixable: fix it, commit, push, then reply explaining the fix and resolve the comment
   - If disagree or out of scope: reply explaining why, then resolve the comment
   - If uncertain (not 100% confident): **ask the user** before acting
   - Every comment MUST get a reply (reason for fix or reason for not fixing) and MUST be resolved
7. Do **NOT** enable auto-merge unless explicitly instructed. Wait for user approval.
8. Return to main: `git checkout main && git pull`
