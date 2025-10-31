# Account Ledger CLI

## Project Overview

This project is a command-line accounting ledger application written in Kotlin. It utilizes a multi-module Gradle setup to structure the codebase. The primary components are:

-   **`:cli-app`**: The main entry point and user interface for the command-line application.
-   **`:account-ledger-lib`**: A library containing the core business logic for the ledger.
-   **`:common-lib`**: A library for common data structures and utilities.
-   **`:account-ledger-lib-multi-platform`**: A multi-platform library, suggesting potential for cross-platform compatibility.

The application is designed to be run from the command line and appears to interact with a remote API for data storage and retrieval, as indicated by the Ktor client dependencies.

## Building and Running

### Standard Build

To build the application and its dependencies, run the following Gradle command from the project root:

```bash
./gradlew build
```

### Running the Application

The application can be run using the provided shell script, which first builds the JAR file and then executes it:

```bash
./runCli.bash
```

Alternatively, you can run the commands from the script manually:

```bash
./gradlew :cli-app:jar
java -jar cli-app/build/libs/cli-app.jar
```

### GraalVM Native Image

The `README.md` provides a command to build a GraalVM native image, which can provide faster startup times. This requires having GraalVM configured on your system.

```bash
native-image --static --no-fallback --allow-incomplete-classpath -H:+AddAllCharsets -H:EnableURLProtocols=http,https -H:DynamicProxyConfigurationFiles="dynamic-proxies.json" -H:+ReportExceptionStackTraces -jar cli-app/build/libs/cli-app.jar AccountLedgerCli.bin
```

## Development Conventions

-   **Build System:** The project uses Gradle for dependency management and build automation. Key build configuration is located in the `build.gradle.kts` files within the root and module directories.
-   **Testing:** The project is set up with Jacoco for test coverage reporting. Tests can be run as part of the standard build process.
-   **Dependencies:** The project uses `libs.versions.toml` in the `gradle` directory to manage dependency versions centrally.
-   **CI/CD:** The presence of `.github/workflows/gradle.yml`, `.travis.yml`, and `azure-pipelines-windows.yml` indicates that Continuous Integration is set up on multiple platforms.
