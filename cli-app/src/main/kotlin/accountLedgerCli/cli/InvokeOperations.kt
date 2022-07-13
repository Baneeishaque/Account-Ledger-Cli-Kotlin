package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt

internal fun invokeAutomatedInsertTransaction(
    userId: UInt,
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
    userId: UInt,
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
    userId: UInt,
    username: String,
    localFromAccount: AccountResponse,
    localToAccount: AccountResponse,
    transactionTypeEnum: TransactionTypeEnum,
    localViaAccount: AccountResponse
): Boolean {
    return addTransactionStep2(
        userId = userId,
        username = username,
        localFromAccount = localFromAccount,
        localToAccount = localToAccount,
        transactionTypeEnum = transactionTypeEnum,
        localViaAccount = localViaAccount
    )
}
