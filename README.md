<div align="center">

# 📒 Account Ledger CLI

### A powerful command-line accounting ledger application built with Kotlin

[![Open in Cloud Shell](https://gstatic.com/cloudssh/images/open-btn.svg)](https://ssh.cloud.google.com/cloudshell/editor?cloudshell_git_repo=https%3A%2F%2Fgithub.com%2FBaneeishaque%2FAccount-Ledger-Cli-Kotlin.git)
[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://github.com/Baneeishaque/Account-Ledger-Cli-Kotlin)

![GitHub Actions](https://github.com/Baneeishaque/Account-Ledger-Cli-Kotlin/workflows/Java%20CI%20with%20Gradle/badge.svg)
[![codecov](https://codecov.io/gh/Baneeishaque/Account-Ledger-Cli-Kotlin/branch/master/graph/badge.svg)](https://codecov.io/gh/Baneeishaque/Account-Ledger-Cli-Kotlin)
[![CodeFactor](https://www.codefactor.io/repository/github/baneeishaque/account-ledger-cli-kotlin/badge)](https://www.codefactor.io/repository/github/baneeishaque/account-ledger-cli-kotlin)

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Architecture](#-architecture)
- [Technology Stack](#-technology-stack)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Building the Project](#-building-the-project)
- [Running the Application](#-running-the-application)
- [CLI Commands](#-cli-commands)
- [GraalVM Native Image](#-graalvm-native-image)
- [Cloud Development Environments](#-cloud-development-environments)
- [CI/CD Pipelines](#-cicd-pipelines)
- [Project Structure](#-project-structure)
- [Contributing](#-contributing)
- [Development Guidelines](#-development-guidelines)
- [Testing](#-testing)
- [Troubleshooting](#-troubleshooting)

---

## 🔍 Overview

**Account Ledger CLI** is a comprehensive command-line accounting application designed for personal and small business financial management. Built entirely in Kotlin, it provides a robust, type-safe, and coroutine-powered solution for managing financial transactions, generating balance sheets, and maintaining ledger records.

The application connects to a remote API server for data persistence and supports various transaction types including normal transactions, via-transactions, two-way transactions, and specialized transaction types for cashback and coins-based rewards systems.

---

## ✨ Features

### Core Features
- 📊 **Account Management** - Create, view, and manage hierarchical account structures
- 💰 **Transaction Recording** - Record various transaction types with detailed particulars
- 📈 **Balance Sheets** - Generate comprehensive balance sheets with multiple refinement levels
- 🔍 **Transaction Search** - Search and filter transactions by date, account, or criteria
- 📋 **Ledger Views** - View transactions in ledger mode or credit-debit mode

### Advanced Features
- 🔄 **Via Transactions** - Support for intermediary account transactions
- ⚡ **Quick Transactions** - Pre-configured shortcuts for frequently used account pairs
- 📥 **Gist Integration** - Import/export ledger data via GitHub Gists
- 🏪 **Special Transactions** - Support for Bajaj Coins and Cashback transactions
- 📊 **Multiple Sheet Types** - Income, Expense, Profit, Debit, Credit, and Asset sheets

### Technical Features
- 🚀 **GraalVM Native Image Support** - Compile to native binary for faster startup
- 🔐 **Environment-based Configuration** - Secure configuration via `.env` files
- 🌐 **Ktor HTTP Client** - Modern, coroutine-based API communication
- 📝 **Comprehensive Logging** - Logback-based logging for debugging
- ✅ **Test Coverage** - JaCoCo-powered test coverage reporting

---

## 🏗 Architecture

This project follows a **multi-module Gradle architecture** for better separation of concerns and reusability:

```
Account-Ledger-Cli-Kotlin/
├── cli-app/                                    # Main CLI Application Module
│   └── src/main/kotlin/accountLedgerCli/
│       ├── cli/                                # CLI commands and screens
│       │   ├── App.kt                          # Application entry point
│       │   ├── Screens.kt                      # Interactive menu screens
│       │   └── sub_commands/                   # CLI subcommands
│       ├── enums/                              # CLI-specific enumerations
│       └── utils/                              # CLI utility functions
│
├── account-ledger-lib/                         # Git Submodule: Core Library
│   ├── account-ledger-lib/                     # Business Logic Library
│   │   └── src/main/kotlin/account/ledger/library/
│   │       ├── api/                            # API models and responses
│   │       ├── constants/                      # Application constants
│   │       ├── enums/                          # Business enumerations
│   │       ├── models/                         # Data models
│   │       ├── operations/                     # Business operations
│   │       ├── retrofit/                       # Retrofit API client
│   │       └── utils/                          # Utility functions
│   │
│   ├── common-lib/                             # Git Submodule: Common Utilities
│   │   └── common-lib/                         # Shared utilities library
│   │       └── src/main/kotlin/common/utils/library/
│   │           ├── cli/                        # CLI base classes
│   │           ├── constants/                  # Common constants
│   │           ├── enums/                      # Common enumerations
│   │           ├── models/                     # Common data models
│   │           └── utils/                      # Common utility functions
│   │
│   └── account-ledger-lib-multi-platform/      # Git Submodule: Multiplatform Library
│       └── lib/                                # Kotlin Multiplatform module
│           └── src/                            # Platform-specific code
│
└── api/                                        # Git Submodule: API Documentation
    └── *.http                                  # HTTP request files
```

### Module Descriptions

| Module | Description |
|--------|-------------|
| **`:cli-app`** | Main entry point containing the CLI interface, interactive menus, and user-facing commands |
| **`:account-ledger-lib:account-ledger-lib`** | Core business logic, API clients, data models, and operations |
| **`:common-lib:common-lib`** | Shared utilities, base classes, and common functionality |
| **`:account-ledger-lib-multi-platform:lib`** | Kotlin Multiplatform library for cross-platform compatibility |

---

## 🛠 Technology Stack

### Core Technologies
| Technology | Version | Purpose |
|------------|---------|---------|
| **Kotlin** | 2.2.20 | Primary programming language |
| **Gradle** | 8.14.3 | Build automation and dependency management |
| **JVM** | 21 | Target Java Virtual Machine version |

### Dependencies

#### HTTP & Networking
| Library | Version | Purpose |
|---------|---------|---------|
| **Ktor Client** | 3.3.1 | HTTP client with coroutines support |
| **Ktor Client CIO** | 3.3.1 | CIO engine for Ktor |
| **Ktor Content Negotiation** | 3.3.1 | Content serialization/deserialization |
| **Retrofit** | 3.0.0 | Type-safe HTTP client (alternative) |

#### Serialization & Data
| Library | Version | Purpose |
|---------|---------|---------|
| **Kotlinx Serialization JSON** | 1.9.0 | JSON serialization |
| **Kotlinx Coroutines Core** | 1.10.2 | Asynchronous programming |
| **Kotlinx CLI** | 0.3.6 | Command-line argument parsing |

#### Utilities
| Library | Version | Purpose |
|---------|---------|---------|
| **Dotenv Kotlin** | 6.5.1 | Environment variable management |
| **Kotlin CSV** | 1.10.0 | CSV file parsing |
| **J-Text-Utils** | 0.3.4 | Text formatting utilities |
| **Logback Classic** | 1.5.19 | Logging framework |

---

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

### Required
- **Java Development Kit (JDK) 21** or higher
  - Recommended: [Oracle JDK 21](https://www.oracle.com/java/technologies/downloads/#java21) or [Eclipse Temurin 21](https://adoptium.net/)
- **Git** with submodule support
- **Internet connection** for API communication

### Optional
- **GraalVM 21** (for native image compilation)
- **mise** or **asdf** (for version management)
- **Docker** (for containerized development)

### Verify Installation
```bash
# Check Java version
java -version

# Check Git version
git --version

# Check Gradle wrapper
./gradlew --version
```

---

## 📥 Installation

### 1. Clone the Repository

```bash
# Clone with submodules (recommended)
git clone --recursive https://github.com/Baneeishaque/Account-Ledger-Cli-Kotlin.git

# Navigate to the project directory
cd Account-Ledger-Cli-Kotlin
```

### 2. Initialize Submodules (if not cloned with --recursive)

```bash
# Initialize and update all submodules recursively
git submodule update --init --recursive
```

### 3. Verify Project Structure

```bash
# Ensure submodules are properly initialized
ls -la account-ledger-lib/
ls -la api/
```

---

## ⚙ Configuration

### Environment Variables

Create a `.env` file in the project root directory based on the `.env_sample` template:

```bash
cp .env_sample .env
```

Edit the `.env` file with your configuration:

```properties
# User Authentication
USER_NAME=your_username
PASSWORD=your_password
USER_ID=1

# Account Configuration
WALLET_ACCOUNT_ID=1
BANK_ACCOUNT_ID=2
BANK_ACCOUNT_NAME='Your Bank Name'

# Frequent Accounts (Quick Transaction Shortcuts)
FREQUENT_1_ACCOUNT_ID=3
FREQUENT_1_ACCOUNT_NAME='Frequent Account 1'
FREQUENT_2_ACCOUNT_ID=4
FREQUENT_2_ACCOUNT_NAME='Frequent Account 2'
FREQUENT_3_ACCOUNT_ID=5
FREQUENT_3_ACCOUNT_NAME='Frequent Account 3'

# Account Groups for Sheets
OPEN_BALANCE_ACCOUNT_IDS=10,11
MISC_INCOME_ACCOUNT_IDS=12,13
INVESTMENT_RETURNS_ACCOUNT_IDS=14
FAMILY_ACCOUNT_IDS=15
EXPENSE_ACCOUNT_IDS=16,17

# Sheet Configuration
EXPENSE_ACCOUNT_IDS_FOR_SHEET=16,17
INCOME_ACCOUNT_IDS_FOR_SHEET=18,19
EXPENSE_INCOME_IGNORE_ACCOUNT_IDS_FOR_SHEET=20
DEBIT_OR_CREDIT_ACCOUNT_IDS_FOR_SHEET=21,22
EXPENSE_INCOME_DEBIT_CREDIT_IGNORE_ACCOUNT_IDS_FOR_SHEET=23
ASSET_ACCOUNT_IDS_FOR_SHEET=24,25
EXPENSE_INCOME_DEBIT_CREDIT_ASSET_IGNORE_ACCOUNT_IDS_FOR_SHEET=26

# Bajaj Coins Configuration
BAJAJ_COINS_INCOME_ACCOUNT_ID=30
BAJAJ_COINS_WALLET_ACCOUNT_ID=31
BAJAJ_COINS_CONVERSION_RATE=4

# GitHub Gist Integration
GITHUB_TOKEN=your_github_personal_access_token
GIST_ID=your_gist_id
GIST_FILE_LINE_SEPARATOR="\n"

# Development Settings
IS_DEVELOPMENT_MODE=false
```

### Configuration Reference

| Variable | Description | Required |
|----------|-------------|----------|
| `USER_NAME` | Login username | Yes |
| `PASSWORD` | Login password | Yes |
| `USER_ID` | User identifier | Yes |
| `WALLET_ACCOUNT_ID` | Default wallet account | Yes |
| `BANK_ACCOUNT_ID` | Primary bank account | No |
| `GITHUB_TOKEN` | GitHub PAT for Gist operations | No |
| `IS_DEVELOPMENT_MODE` | Enable verbose logging | No |

---

## 🔨 Building the Project

### Standard Build

```bash
# Build all modules
./gradlew build

# Build with detailed output
./gradlew build --info

# Clean and rebuild
./gradlew clean build
```

### Build Specific Modules

```bash
# Build only CLI app
./gradlew :cli-app:build

# Build the JAR file
./gradlew :cli-app:jar
```

### Create Distribution

```bash
# Create TAR distribution
./gradlew distTar

# Create ZIP distribution
./gradlew distZip
```

### Build Output
- JAR file: `cli-app/build/libs/cli-app.jar`
- Distribution: `cli-app/build/distributions/`

---

## 🚀 Running the Application

### Using the Run Script (Recommended)

```bash
# Build and run
./runCli.bash
```

### Using Gradle

```bash
# Run with Gradle
./gradlew :cli-app:run --console=plain

# Run with arguments
./gradlew :cli-app:run --args="BalanceSheet -u username -p password"
```

### Using the JAR File

```bash
# Build the JAR first
./gradlew :cli-app:jar

# Run the JAR
java -jar cli-app/build/libs/cli-app.jar
```

### Interactive Mode

When run without arguments, the application starts in interactive mode:

```
Account Ledger
─────────────────────────────────────────────────
The identified user is [username]

1 : Login
2 : Registration
3 : List Users
4 : Balance Sheet for an User
5 : Balance Sheet for all Users
0 : Exit

Enter Your Choice :
```

---

## 🔧 CLI Commands

### Available Subcommands

The application supports multiple subcommands for non-interactive operation:

| Command | Description |
|---------|-------------|
| `BalanceSheet` | Generate balance sheet for a user |
| `Gist` | Import/merge ledger data from GitHub Gist (v1) |
| `GistV2` | Gist import version 2 |
| `GistV3` | Gist import version 3 |
| `GistV4` | Gist import version 4 |
| `GistV3ToV4` | Convert Gist data from v3 to v4 format |
| `InsertTransaction` | Insert a single transaction |
| `GetAccounts` | Retrieve user accounts |
| `GetAccountsUrl` | Get accounts API URL |
| `ViewTransactionsOfAnAccount` | View transactions for a specific account |

### Command Examples

#### Balance Sheet
```bash
# Generate balance sheet with default settings
java -jar cli-app/build/libs/cli-app.jar BalanceSheet -u username -p password

# With refinement level
java -jar cli-app/build/libs/cli-app.jar BalanceSheet -u username -p password -r without_expense_accounts

# With output format
java -jar cli-app/build/libs/cli-app.jar BalanceSheet -u username -p password -o json
```

**Refinement Levels:**
- `all` - All accounts
- `without_open_balances` - Exclude open balance accounts
- `without_misc_incomes` - Exclude misc income accounts
- `without_investment_returns` - Exclude investment returns
- `without_family_accounts` - Exclude family accounts
- `without_expense_accounts` - Exclude expense accounts

#### Insert Transaction
```bash
java -jar cli-app/build/libs/cli-app.jar InsertTransaction \
  UserId 1 \
  EventDateTime "2024-01-15 10:30:00" \
  Particulars "Monthly Salary" \
  Amount 50000.00 \
  FromAccountId 1 \
  ToAccountId 2
```

#### Gist Operations
```bash
# Import from Gist
java -jar cli-app/build/libs/cli-app.jar Gist -u username -p password -g gist_id -t github_token
```

---

## 🌟 GraalVM Native Image

Build a native executable for faster startup times and reduced memory footprint.

### Prerequisites

1. Install GraalVM 21 with Native Image:
```bash
# Using SDKMAN
sdk install java 21.0.2-graal

# Or using mise
mise install java@graalvm-21
```

2. Install Native Image component:
```bash
gu install native-image
```

### Build Native Image

```bash
# First, build the JAR
./gradlew :cli-app:jar

# Build native image
native-image \
  --static \
  --no-fallback \
  --allow-incomplete-classpath \
  -H:+AddAllCharsets \
  -H:EnableURLProtocols=http,https \
  -H:DynamicProxyConfigurationFiles="dynamic-proxies.json" \
  -H:+ReportExceptionStackTraces \
  -jar cli-app/build/libs/cli-app.jar \
  AccountLedgerCli.bin
```

### Run Native Binary

```bash
./AccountLedgerCli.bin
```

### Build Options

| Option | Description |
|--------|-------------|
| `--static` | Create a statically linked executable |
| `--no-fallback` | Fail if native image can't be built |
| `-H:+AddAllCharsets` | Include all character sets |
| `-H:EnableURLProtocols` | Enable HTTP/HTTPS protocols |
| `-H:DynamicProxyConfigurationFiles` | Specify dynamic proxy configuration |

---

## ☁ Cloud Development Environments

### Gitpod

Click the button below to open the project in Gitpod:

[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://github.com/Baneeishaque/Account-Ledger-Cli-Kotlin)

The `.gitpod.yml` configuration provides:
- Pre-configured VNC desktop environment
- JetBrains IntelliJ IDEA support
- BashHub integration for command history
- Docker support

### Google Cloud Shell

[![Open in Cloud Shell](https://gstatic.com/cloudssh/images/open-btn.svg)](https://ssh.cloud.google.com/cloudshell/editor?cloudshell_git_repo=https%3A%2F%2Fgithub.com%2FBaneeishaque%2FAccount-Ledger-Cli-Kotlin.git)

---

## 🔄 CI/CD Pipelines

### GitHub Actions

The project uses GitHub Actions for continuous integration:

```yaml
# .github/workflows/gradle.yml
- Builds on: ubuntu-latest
- JDK: Oracle JDK 21
- Triggers: Push and PR to master
- Steps: Checkout, Setup JDK, Build with Gradle
```

### Azure Pipelines

Windows builds are configured via `azure-pipelines-windows.yml`:
- Gradle caching for faster builds
- TAR distribution creation
- Artifact publishing

### Travis CI

Legacy CI configuration in `.travis.yml`:
- Codecov integration for coverage reporting

---

## 📁 Project Structure

```
Account-Ledger-Cli-Kotlin/
├── 📁 cli-app/                    # Main application module
│   ├── 📁 src/main/kotlin/        # Kotlin source files
│   └── 📄 build.gradle.kts        # Module build configuration
│
├── 📁 account-ledger-lib/         # Core library submodule
│   ├── 📁 account-ledger-lib/     # Business logic module
│   ├── 📁 common-lib/             # Common utilities submodule
│   └── 📁 account-ledger-lib-multi-platform/ # Multiplatform module
│
├── 📁 api/                        # API documentation submodule
├── 📁 gradle/                     # Gradle wrapper and version catalog
│   ├── 📄 wrapper/                # Gradle wrapper files
│   └── 📄 libs.versions.toml      # Dependency version catalog
│
├── 📁 .github/                    # GitHub configurations
│   └── 📁 workflows/              # GitHub Actions workflows
│
├── 📄 build.gradle.kts            # Root build configuration
├── 📄 settings.gradle.kts         # Gradle settings
├── 📄 gradle.properties           # Gradle properties
├── 📄 .env_sample                 # Environment variables template
├── 📄 .gitmodules                 # Git submodules configuration
├── 📄 .gitpod.yml                 # Gitpod configuration
├── 📄 dynamic-proxies.json        # GraalVM proxy configuration
├── 📄 mise.toml                   # mise version manager config
└── 📄 runCli.bash                 # Quick run script
```

---

## 🤝 Contributing

We welcome contributions! Please follow these steps:

### Getting Started

1. **Fork the repository** on GitHub

2. **Clone your fork**:
```bash
git clone --recursive https://github.com/YOUR_USERNAME/Account-Ledger-Cli-Kotlin.git
cd Account-Ledger-Cli-Kotlin
```

3. **Create a feature branch**:
```bash
git checkout -b feature/your-feature-name
```

4. **Make your changes** following the coding guidelines

5. **Test your changes**:
```bash
./gradlew build
./gradlew test
```

6. **Commit your changes**:
```bash
git commit -m "feat: add your feature description"
```

7. **Push to your fork**:
```bash
git push origin feature/your-feature-name
```

8. **Create a Pull Request** on GitHub

### Commit Message Convention

Follow [Conventional Commits](https://www.conventionalcommits.org/):

| Type | Description |
|------|-------------|
| `feat` | New feature |
| `fix` | Bug fix |
| `docs` | Documentation changes |
| `style` | Code style changes (formatting, etc.) |
| `refactor` | Code refactoring |
| `test` | Adding or updating tests |
| `chore` | Maintenance tasks |

Example:
```
feat: add support for CSV transaction import
fix: resolve date parsing issue in transaction view
docs: update installation instructions
```

### Pull Request Guidelines

- Ensure all tests pass
- Update documentation if needed
- Keep changes focused and atomic
- Provide a clear description of changes

---

## 💻 Development Guidelines

### Code Style

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add KDoc comments for public APIs
- Keep functions focused and small

### IDE Setup

**Recommended: IntelliJ IDEA**

1. Open the project in IntelliJ IDEA
2. Import as Gradle project
3. Enable Kotlin plugin
4. Set Project SDK to JDK 21

**VS Code**

Required extensions:
- Kotlin Language
- Gradle for Java

### Gradle Configuration

The project uses Gradle Kotlin DSL with version catalogs:

```kotlin
// gradle/libs.versions.toml - Centralized dependency versions
// build.gradle.kts - Root configuration
// cli-app/build.gradle.kts - Module configuration
```

### Performance Settings

`gradle.properties` contains optimized settings:
```properties
org.gradle.vfs.watch=true
org.gradle.unsafe.configuration-cache=true
org.gradle.jvmargs=-Xmx1152M
kotlin.compiler.preciseCompilationResultsBackup=true
```

---

## 🧪 Testing

### Running Tests

```bash
# Run all tests
./gradlew test

# Run tests with coverage
./gradlew test jacocoTestReport

# Run specific module tests
./gradlew :cli-app:test
```

### Test Coverage

JaCoCo is configured for test coverage reporting:

```bash
# Generate coverage report
./gradlew jacocoTestReport

# View report
open cli-app/build/reports/jacoco/test/html/index.html
```

Coverage reports are automatically uploaded to [Codecov](https://codecov.io/gh/Baneeishaque/Account-Ledger-Cli-Kotlin).

### Writing Tests

Tests use Kotlin Test framework:

```kotlin
class ExampleTest {
    @Test
    fun `should perform expected behavior`() {
        // Arrange
        val input = "test"
        
        // Act
        val result = processInput(input)
        
        // Assert
        assertEquals(expected, result)
    }
}
```

---

## 🔧 Troubleshooting

### Common Issues

#### Submodules Not Initialized
```bash
# Error: Missing account-ledger-lib directory
# Solution:
git submodule update --init --recursive
```

#### Java Version Mismatch
```bash
# Error: Unsupported class file major version
# Solution: Ensure JDK 21 is installed and set as JAVA_HOME
export JAVA_HOME=/path/to/jdk-21
```

#### Gradle Build Fails
```bash
# Clear Gradle cache and rebuild
./gradlew clean
rm -rf ~/.gradle/caches/
./gradlew build
```

#### Environment Variables Not Loaded
```bash
# Ensure .env file exists in project root
cp .env_sample .env
# Edit with your values
```

#### Native Image Build Fails
```bash
# Ensure GraalVM is properly installed
java -version  # Should show GraalVM
native-image --version

# Install native-image component
gu install native-image
```

### Getting Help

- 📖 Check existing [Issues](https://github.com/Baneeishaque/Account-Ledger-Cli-Kotlin/issues)
- 🐛 Report bugs by creating a new issue
- 💬 Start a discussion for questions

---

## 📊 Code Quality

### Static Analysis

The project is monitored by:
- **CodeFactor** - Automated code review
- **Codecov** - Test coverage tracking

### Quality Metrics

- Kotlin 2.2 language features enabled
- Strict compiler options (configurable)
- Centralized dependency management

---

## 📜 Acknowledgments

### Dependencies

This project relies on excellent open-source libraries:
- [Ktor](https://ktor.io/) - Asynchronous HTTP client
- [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) - JSON handling
- [Kotlinx CLI](https://github.com/Kotlin/kotlinx-cli) - Command-line parsing
- [Dotenv Kotlin](https://github.com/cdimascio/dotenv-kotlin) - Environment management

### Related Repositories

- [Account-Ledger-Library-Kotlin-Gradle](https://github.com/Baneeishaque/Account-Ledger-Library-Kotlin-Gradle) - Core library
- [Common-Utils-Library-Kotlin-Gradle](https://github.com/Baneeishaque/Common-Utils-Library-Kotlin-Gradle) - Common utilities
- [Account-Ledger-Library-Kotlin-Native](https://github.com/Baneeishaque/Account-Ledger-Library-Kotlin-Native) - Multiplatform library

---

<div align="center">

**Made with ❤️ and Kotlin**

⭐ Star this repository if you find it helpful!

</div>
