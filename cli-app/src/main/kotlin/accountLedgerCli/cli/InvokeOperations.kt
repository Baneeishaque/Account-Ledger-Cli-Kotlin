package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt

internal fun automatedInsertTransaction(

    userId: UInt,
    eventDateTime: String,
    particulars: String,
    amount: Float,
    fromAccount: AccountResponse,
    toAccount: AccountResponse

): Boolean {

    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
        listOfCommands = listOf(
            "\nTime - $eventDateTime",
            "Withdraw Account - ${fromAccount.id} : ${fromAccount.fullName}",
            "Deposit Account - ${toAccount.id} : ${toAccount.fullName}",
            "Particulars - $particulars",
            "Amount - $amount"
        )
    )
    return InsertOperations.insertTransaction(
        userid = userId,
        eventDateTime = eventDateTime,
        particulars = particulars,
        amount = amount,
        fromAccount = fromAccount,
        toAccount = toAccount
    )
}