package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse

internal fun ex13(
    userId: Int,
    username: String,
    localFromAccount: AccountResponse,
    localToAccount: AccountResponse,
    transactionTypeEnum: TransactionTypeEnum,
    localViaAccount: AccountResponse
): Boolean {
    exchangeFromAndToAccounts()
    return invokeAddTransactionStep2(
        userId = userId,
        username = username,
        localFromAccount = localFromAccount,
        localToAccount = localToAccount,
        transactionTypeEnum = transactionTypeEnum,
        localViaAccount = localViaAccount
    )
}

internal fun ex12(
    userId: Int,
    username: String,
    localFromAccount: AccountResponse,
    localToAccount: AccountResponse,
    transactionTypeEnum: TransactionTypeEnum,
    localViaAccount: AccountResponse
): Boolean {
    exchangeFromAndViaAccounts()
    return invokeAddTransactionStep2(
        userId = userId,
        username = username,
        localFromAccount = localFromAccount,
        localToAccount = localToAccount,
        transactionTypeEnum = transactionTypeEnum,
        localViaAccount = localViaAccount
    )
}

internal fun ex23(
    userId: Int,
    username: String,
    localFromAccount: AccountResponse,
    localToAccount: AccountResponse,
    transactionTypeEnum: TransactionTypeEnum,
    localViaAccount: AccountResponse
): Boolean {
    exchangeToAndViaAccounts()
    return invokeAddTransactionStep2(
        userId = userId,
        username = username,
        localFromAccount = localFromAccount,
        localToAccount = localToAccount,
        transactionTypeEnum = transactionTypeEnum,
        localViaAccount = localViaAccount
    )
}
