# Account Ledger Cli
[![Ready-to-Code](https://img.shields.io/badge/Gitpod-Ready--to--Code-blue?logo=gitpod)](https://gitpod.io/#https://github.com/Baneeishaque/Account-Ledger-Cli-Kotlin-Gradle)
![GitHub Actions](https://github.com/Baneeishaque/Account-Ledger-Cli-Kotlin-Gradle/workflows/Java%20CI%20with%20Gradle/badge.svg)
![Travis (.com)](https://img.shields.io/travis/com/Baneeishaque/Account-Ledger-Cli-Kotlin-Gradle?logo=travis)
[![codecov](https://codecov.io/gh/Baneeishaque/Account-Ledger-Cli-Kotlin-Gradle/branch/master/graph/badge.svg)](https://codecov.io/gh/Baneeishaque/Account-Ledger-Cli-Kotlin-Gradle) 
[![CodeFactor](https://www.codefactor.io/repository/github/baneeishaque/account-ledger-cli-kotlin-gradle/badge)](https://www.codefactor.io/repository/github/baneeishaque/account-ledger-cli-kotlin-gradle)

<!-- https://codecov.io/gh/Baneeishaque/Account-Ledger-Cli-Kotlin-Gradle/branch/master/graphs/sunburst
https://codecov.io/gh/Baneeishaque/Account-Ledger-Cli-Kotlin-Gradle/branch/master/graphs/icicle.svg
https://codecov.io/gh/Baneeishaque/Account-Ledger-Cli-Kotlin-Gradle/branch/master/graphs/tree.svg
https://codecov.io/gh/Baneeishaque/Account-Ledger-Cli-Kotlin-Gradle/branch/master/graphs/commits.svg -->

## To Build Native Image Via. GraalVM Native Image Builder
**`native-image --static --no-fallback --allow-incomplete-classpath -H:+AddAllCharsets -H:EnableURLProtocols=http,https -H:DynamicProxyConfigurationFiles="dynamic-proxies.json" -H:+ReportExceptionStackTraces -jar cli-app/build/libs/cli-app.jar AccountLedgerCli.bin`**
