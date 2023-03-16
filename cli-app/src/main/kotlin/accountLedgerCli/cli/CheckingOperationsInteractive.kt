package accountLedgerCli.cli

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.cli.isAccountsAreAvailable
import account.ledger.library.enums.TransactionTypeEnum
import account.ledger.library.models.InsertTransactionResult
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithContinuePrompt
import common.utils.library.utils.DateTimeUtils
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
                    isDevelopmentMode = isDevelopmentMode
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

internal fun addTransactionWithAccountAvailabilityCheck(

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

    if (isAccountsAreAvailable(
            transactionType = transactionType,
            fromAccount = fromAccount,
            viaAccount = viaAccount,
            toAccount = toAccount,
            fromAccountMissingActions = { println("Please choose from account...") },
            toAccountMissingActions = { println("Please choose deposit account...") },
            viaAccountMissingActions = { println("Please choose via. account...") }
        ) == 0
    ) {
        when (transactionType) {

            TransactionTypeEnum.NORMAL -> {

                val addTransactionStep2Result: InsertTransactionResult = InsertOperationsInteractive.addTransactionStep2(

                    userId = userId,
                    username = username,
                    transactionType = TransactionTypeEnum.NORMAL,
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount,
                    isConsoleMode = isConsoleMode,
                    isDevelopmentMode = isDevelopmentMode
                )
                if (addTransactionStep2Result.isSuccess) {

                    return InsertTransactionResult(

                        isSuccess = true,
                        dateTimeInText = DateTimeUtils.add5MinutesToDateTimeInText(dateTimeInText = addTransactionStep2Result.dateTimeInText),
                        transactionParticulars = addTransactionStep2Result.transactionParticulars,
                        transactionAmount = addTransactionStep2Result.transactionAmount,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount
                    )
                }
            }

            TransactionTypeEnum.VIA -> {

                var addTransactionStep2Result: InsertTransactionResult = InsertOperationsInteractive.addTransactionStep2(
                    userId = userId,
                    username = username,
                    transactionType = TransactionTypeEnum.VIA,
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount,
                    isConsoleMode = isConsoleMode,
                    isDevelopmentMode = isDevelopmentMode
                )
                if (addTransactionStep2Result.isSuccess) {

                    addTransactionStep2Result = InsertOperationsInteractive.addTransactionStep2(

                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.VIA,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        isViaStep = true,
                        dateTimeInText = DateTimeUtils.add5MinutesToDateTimeInText(dateTimeInText = addTransactionStep2Result.dateTimeInText),
                        transactionParticulars = addTransactionStep2Result.transactionParticulars,
                        transactionAmount = addTransactionStep2Result.transactionAmount,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                    if (addTransactionStep2Result.isSuccess
                    ) {
                        return InsertTransactionResult(
                            isSuccess = true,
                            dateTimeInText = DateTimeUtils.add5MinutesToDateTimeInText(dateTimeInText = addTransactionStep2Result.dateTimeInText),
                            transactionParticulars = addTransactionStep2Result.transactionParticulars,
                            transactionAmount = addTransactionStep2Result.transactionAmount,
                            fromAccount = fromAccount,
                            viaAccount = viaAccount,
                            toAccount = toAccount
                        )
                    }
                }
            }

            TransactionTypeEnum.TWO_WAY -> {

                var addTransactionStep2Result: InsertTransactionResult = InsertOperationsInteractive.addTransactionStep2(

                    userId = userId,
                    username = username,
                    transactionType = TransactionTypeEnum.TWO_WAY,
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount,
                    isConsoleMode = isConsoleMode,
                    isDevelopmentMode = isDevelopmentMode
                )
                if (addTransactionStep2Result.isSuccess) {

                    addTransactionStep2Result = InsertOperationsInteractive.addTransactionStep2(

                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.TWO_WAY,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        isTwoWayStep = true,
                        dateTimeInText = DateTimeUtils.add5MinutesToDateTimeInText(dateTimeInText = addTransactionStep2Result.dateTimeInText),
                        transactionParticulars = addTransactionStep2Result.transactionParticulars,
                        transactionAmount = addTransactionStep2Result.transactionAmount,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                    if (addTransactionStep2Result.isSuccess) {

                        return InsertTransactionResult(
                            isSuccess = true,
                            dateTimeInText = DateTimeUtils.add5MinutesToDateTimeInText(dateTimeInText = addTransactionStep2Result.dateTimeInText),
                            transactionParticulars = addTransactionStep2Result.transactionParticulars,
                            transactionAmount = addTransactionStep2Result.transactionAmount,
                            fromAccount = fromAccount,
                            viaAccount = viaAccount,
                            toAccount = toAccount
                        )
                    }
                }
            }
        }
    }
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