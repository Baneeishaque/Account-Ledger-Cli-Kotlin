package accountLedgerCli.cli

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.operations.addTransactionWithAccountAvailabilityCheck
import account.ledger.library.enums.TransactionTypeEnum
import account.ledger.library.models.InsertTransactionResult
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithContinuePrompt
import common.utils.library.utils.invalidOptionMessage

internal fun transactionContinueCheck(

    userId: UInt,
    username: String,
    transactionType: TransactionTypeEnum,
    fromAccount: AccountResponse,
    viaAccount: AccountResponse,
    toAccount: AccountResponse,
    dateTimeInText: String,
    transactionParticulars: String,
    transactionAmount: Float,
    isConsoleMode: Boolean,
    isDevelopmentMode: Boolean

): InsertTransactionResult {

    do {
        commandLinePrintMenuWithContinuePrompt.printMenuWithContinuePromptFromListOfCommands(
            listOfCommands = Screens.getUserWithCurrentAccountSelectionsAsText(

                username = username,
                fromAccount = fromAccount,
                viaAccount = viaAccount,
                toAccount = toAccount,
                transactionType = transactionType,
                userId = userId,
                isDevelopmentMode = isDevelopmentMode

            ) + listOf(
                "", "Continue (Y/N) : "
            )
        )
        when (readln()) {
            "Y", "" -> {

                return addTransactionWithAccountAvailabilityCheck(
                    userId = userId,
                    username = username,
                    transactionType = transactionType,
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount,
                    isConsoleMode = isConsoleMode,
                    isDevelopmentMode = isDevelopmentMode,
                    fromAccountMissingActions = { println("Please choose from account...") },
                    toAccountMissingActions = { println("Please choose deposit account...") },
                    viaAccountMissingActions = { println("Please choose via. account...") },
                    addTransactionStep2Operation = InsertOperationsInteractive::addTransactionStep2
                )
            }

            "N" -> {
                return InsertTransactionResult(
                    isSuccess = false,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount,
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount
                )
            }

            else -> invalidOptionMessage()
        }
    } while (true)
}