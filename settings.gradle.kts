rootProject.name = "accountLedgerCli"

include("common-lib:common-lib")
project(":common-lib:common-lib").projectDir = file("account-ledger-lib/common-lib/common-lib")

include(":account-ledger-lib:account-ledger-lib")

include(":cli-app")