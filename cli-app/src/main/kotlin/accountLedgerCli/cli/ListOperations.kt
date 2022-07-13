package accountLedgerCli.cli

import accountLedgerCli.utils.ApiUtils

internal fun listAccountsTop(username: String, userId: UInt) {

    handleAccountsResponseAndPrintMenu(
        apiResponse = getAccounts(userId = userId), username = username, userId = userId
    )
}

internal fun listAccountsFull(username: String, userId: UInt) {

    handleAccountsResponseAndPrintMenu(
        apiResponse = ApiUtils.getAccountsFull(userId = userId), username = username, userId = userId
    )
}
