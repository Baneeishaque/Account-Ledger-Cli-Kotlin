rootProject.name = "accountLedgerCli"

include("common-lib:lib")
project(":common-lib:lib").projectDir = file("account-ledger-lib/common-lib/lib")

include(":account-ledger-lib:lib")

include(":cli-app")