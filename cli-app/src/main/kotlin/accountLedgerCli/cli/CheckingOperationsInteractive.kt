package accountLedgerCli.cli

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.enums.TransactionTypeEnum
import account.ledger.library.models.InsertTransactionResult
import account.ledger.library.operations.CheckingOperations
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithContinuePrompt
import common.utils.library.utils.InteractiveUtils

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

                return CheckingOperations.addTransactionWithAccountAvailabilityCheck(

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
                    addTransactionOperation = InsertOperationsInteractive::insertTransactionVariantsInteractive
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

            else -> InteractiveUtils.invalidOptionMessage()
        }
    } while (true)
}