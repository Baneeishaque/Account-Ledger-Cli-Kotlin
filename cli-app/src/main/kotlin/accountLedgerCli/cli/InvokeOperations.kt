package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse

internal fun invokeAutomatedInsertTransaction(
    userId: Int,
    eventDateTime: String,
    particulars: String,
    amount: Float,
    localFromAccount: AccountResponse,
    localToAccount: AccountResponse,
): Boolean {
    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
        listOf(
            "\nTime - $eventDateTime",
            "Withdraw Account - ${localFromAccount.id} : ${localFromAccount.fullName}",
            "Deposit Account - ${localToAccount.id} : ${localToAccount.fullName}",
            "Particulars - $particulars",
            "Amount - $amount"
        )
    )
    return invokeInsertTransaction(
        userId = userId, eventDateTime = eventDateTime, particulars = particulars,
        amount = amount, localFromAccount = localFromAccount, localToAccount = localToAccount
    )
}

internal fun invokeInsertTransaction(
    userId: Int,
    eventDateTime: String,
    particulars: String,
    amount: Float,
    localFromAccount: AccountResponse,
    localToAccount: AccountResponse
): Boolean {
    if (insertTransaction(
            userid = userId,
            eventDateTime = eventDateTime,
            particulars = particulars,
            amount = amount,
            localFromAccount = localFromAccount,
            localToAccount = localToAccount
        )
    ) {
        return true
    }
    return false
}

internal fun invokeAddTransactionStep2(
    userId: Int,
    username: String,
    localFromAccount: AccountResponse,
    localToAccount: AccountResponse,
    transactionType: TransactionType,
    localViaAccount: AccountResponse
): Boolean {
    return addTransactionStep2(
        userId = userId,
        username = username,
        localFromAccount = localFromAccount,
        localToAccount = localToAccount,
        transactionType = transactionType,
        localViaAccount = localViaAccount
    )
}
