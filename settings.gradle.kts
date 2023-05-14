rootProject.name = "Account-Ledger-Cli-Kotlin"

include(":cli-app")
include(":account-ledger-lib:account-ledger-lib")
include("common-lib:common-lib")
project(":common-lib:common-lib").projectDir = file("account-ledger-lib/common-lib/common-lib")
