package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse

internal fun ex13(
    userId: Int,
    username: String,
    localFromAccount: AccountResponse,
    localToAccount: AccountResponse,
    transactionType: TransactionType,
    localViaAccount: AccountResponse
): Boolean {
    exchangeFromAndToAccounts()
    return invokeAddTransactionStep2(
        userId = userId,
        username = username,
        localFromAccount = localFromAccount,
        localToAccount = localToAccount,
        transactionType = transactionType,
        localViaAccount = localViaAccount
    )
}

internal fun ex12(
    userId: Int,
    username: String,
    localFromAccount: AccountResponse,
    localToAccount: AccountResponse,
    transactionType: TransactionType,
    localViaAccount: AccountResponse
): Boolean {
    exchangeFromAndViaAccounts()
    return invokeAddTransactionStep2(
        userId = userId,
        username = username,
        localFromAccount = localFromAccount,
        localToAccount = localToAccount,
        transactionType = transactionType,
        localViaAccount = localViaAccount
    )
}

internal fun ex23(
    userId: Int,
    username: String,
    localFromAccount: AccountResponse,
    localToAccount: AccountResponse,
    transactionType: TransactionType,
    localViaAccount: AccountResponse
): Boolean {
    exchangeToAndViaAccounts()
    return invokeAddTransactionStep2(
        userId = userId,
        username = username,
        localFromAccount = localFromAccount,
        localToAccount = localToAccount,
        transactionType = transactionType,
        localViaAccount = localViaAccount
    )
}
