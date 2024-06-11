rootProject.name = "Account-Ledger-Cli-Kotlin"

include(":cli-app")
include(":account-ledger-lib:account-ledger-lib")
include("common-lib:common-lib")
project(":common-lib:common-lib").projectDir = file("account-ledger-lib/common-lib/common-lib")
include("account-ledger-lib-multi-platform:lib")
project(":account-ledger-lib-multi-platform:lib").projectDir =
    file("account-ledger-lib/account-ledger-lib-multi-platform/lib")

pluginManagement {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
        }
        google()
    }
}
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
        }
        maven {
            url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        }
        google()
    }
}
