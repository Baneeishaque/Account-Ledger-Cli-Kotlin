package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithContinuePrompt
import accountLedgerCli.enums.TransactionTypeEnum
import accountLedgerCli.models.InsertTransactionResult
import accountLedgerCli.to_utils.DateTimeUtils
import accountLedgerCli.to_utils.invalidOptionMessage

internal fun isAccountsAreAvailable(

    transactionType: TransactionTypeEnum,
    fromAccount: AccountResponse,
    viaAccount: AccountResponse,
    toAccount: AccountResponse

): Boolean {

    if (toAccount.id == 0u) {

        println("Please choose deposit account...")
        return false

    } else if (fromAccount.id == 0u) {

        println("Please choose from account...")
        return false

    } else if ((transactionType == TransactionTypeEnum.VIA) && (viaAccount.id == 0u)) {

        println("Please choose via. account...")
        return false
    }
    return true
}

internal fun transactionContinueCheck(

    userId: UInt,
    username: String,
    transactionType: TransactionTypeEnum,
    fromAccount: AccountResponse,
    viaAccount: AccountResponse,
    toAccount: AccountResponse,
    dateTimeInText: String,
    transactionParticulars: String,
    transactionAmount: Float

): InsertTransactionResult {

    do {
        commandLinePrintMenuWithContinuePrompt.printMenuWithContinuePromptFromListOfCommands(
            listOfCommands = Screens.getUserWithCurrentAccountSelectionsAsText(

                username = username,
                fromAccount = fromAccount,
                viaAccount = viaAccount,
                toAccount = toAccount,
                transactionType = transactionType,
                userId = userId

            ) + listOf(
                "", "Continue (Y/N) : "
            )
        )
        when (readLine()!!) {
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
                    transactionAmount = transactionAmount
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
    transactionAmount: Float

): InsertTransactionResult {

    if (isAccountsAreAvailable(
            transactionType = transactionType,
            fromAccount = fromAccount,
            viaAccount = viaAccount,
            toAccount = toAccount
        )
    ) {
        when (transactionType) {

            TransactionTypeEnum.NORMAL -> {

                val addTransactionStep2Result: InsertTransactionResult = InsertOperations.addTransactionStep2(

                    userId = userId,
                    username = username,
                    transactionType = TransactionTypeEnum.NORMAL,
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
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

                var addTransactionStep2Result: InsertTransactionResult = InsertOperations.addTransactionStep2(
                    userId = userId,
                    username = username,
                    transactionType = TransactionTypeEnum.VIA,
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )
                if (addTransactionStep2Result.isSuccess) {

                    addTransactionStep2Result = InsertOperations.addTransactionStep2(

                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.VIA,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        isViaStep = true,
                        dateTimeInText = DateTimeUtils.add5MinutesToDateTimeInText(dateTimeInText = addTransactionStep2Result.dateTimeInText),
                        transactionParticulars = addTransactionStep2Result.transactionParticulars,
                        transactionAmount = addTransactionStep2Result.transactionAmount
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

                var addTransactionStep2Result: InsertTransactionResult = InsertOperations.addTransactionStep2(

                    userId = userId,
                    username = username,
                    transactionType = TransactionTypeEnum.TWO_WAY,
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )
                if (addTransactionStep2Result.isSuccess) {

                    addTransactionStep2Result = InsertOperations.addTransactionStep2(

                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.TWO_WAY,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        isTwoWayStep = true,
                        dateTimeInText = DateTimeUtils.add5MinutesToDateTimeInText(dateTimeInText = addTransactionStep2Result.dateTimeInText),
                        transactionParticulars = addTransactionStep2Result.transactionParticulars,
                        transactionAmount = addTransactionStep2Result.transactionAmount
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

