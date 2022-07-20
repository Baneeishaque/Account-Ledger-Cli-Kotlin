package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithContinuePrompt
import accountLedgerCli.cli.App.Companion.dateTimeString
import accountLedgerCli.cli.InsertOperations.addTransactionStep2
import accountLedgerCli.to_utils.DateTimeUtils
import accountLedgerCli.utils.AccountUtils
import accountLedgerCli.utils.DateUtils
import java.time.LocalDateTime

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
    dateTimeInText: String = DateUtils.getCurrentDateTimeText(),
    transactionParticulars: String = "",
    transactionAmount: Float = 0F

) {

    do {
        commandLinePrintMenuWithContinuePrompt.printMenuWithContinuePromptFromListOfCommands(
            listOfCommands = Screens.getUserWithCurrentAccountSelectionsAsText(

                username = username,
                fromAccount = fromAccount,
                viaAccount = viaAccount,
                toAccount = toAccount,
                transactionType = transactionType

            ) + listOf(
                "", "Continue (Y/N) : "
            )
        )
        val input: String? = readLine()
        when (input) {
            "Y", "" -> {

                addTransactionWithAccountAvailabilityCheck(

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
                return
            }

            "N" -> {
            }

            else -> invalidOptionMessage()
        }
    } while (input != "N")
}

internal fun addTransactionWithAccountAvailabilityCheck(

    userId: UInt,
    username: String,
    transactionType: TransactionTypeEnum,
    fromAccount: AccountResponse,
    viaAccount: AccountResponse,
    toAccount: AccountResponse,
    dateTimeInText: String = DateUtils.getCurrentDateTimeText(),
    transactionParticulars: String = "",
    transactionAmount: Float = 0F

) {
    if (transactionType == TransactionTypeEnum.VIA) {

        if (addTransactionStep2(
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
        ) {
            dateTimeString = ((LocalDateTime.parse(
                dateTimeString, DateTimeUtils.normalDateTimePattern
            ) as LocalDateTime).plusMinutes(5) as LocalDateTime).format(DateTimeUtils.normalDateTimePattern)

            if (addTransactionStep2(
                    userId = userId,
                    username = username,
                    fromAccount = viaAccount,
                    toAccount = toAccount,
                    transactionType = transactionType,
                    viaAccount = AccountUtils.blankAccount,
                    isViaStep = true,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )
            ) {
                dateTimeString = ((LocalDateTime.parse(
                    dateTimeString, DateTimeUtils.normalDateTimePattern
                ) as LocalDateTime).plusMinutes(5) as LocalDateTime).format(DateTimeUtils.normalDateTimePattern)
            }
        }
    } else if (transactionType == TransactionTypeEnum.NORMAL) {

        if (addTransactionStep2(
                userId = userId,
                username = username,
                fromAccount = fromAccount,
                toAccount = toAccount,
                transactionType = TransactionTypeEnum.NORMAL,
                viaAccount = AccountUtils.blankAccount,
                dateTimeInText = dateTimeInText,
                transactionParticulars = transactionParticulars,
                transactionAmount = transactionAmount
            )
        ) {
            dateTimeString = ((LocalDateTime.parse(
                dateTimeString, DateTimeUtils.normalDateTimePattern
            ) as LocalDateTime).plusMinutes(5) as LocalDateTime).format(DateTimeUtils.normalDateTimePattern)
        }
    } else if (transactionType == TransactionTypeEnum.TWO_WAY) {

        if (addTransactionStep2(
                userId = userId,
                username = username,
                fromAccount = fromAccount,
                toAccount = toAccount,
                transactionType = transactionType,
                viaAccount = AccountUtils.blankAccount,
                dateTimeInText = dateTimeInText,
                transactionParticulars = transactionParticulars,
                transactionAmount = transactionAmount
            )
        ) {
            dateTimeString = ((LocalDateTime.parse(
                dateTimeString, DateTimeUtils.normalDateTimePattern
            ) as LocalDateTime).plusMinutes(5) as LocalDateTime).format(DateTimeUtils.normalDateTimePattern)

            if (addTransactionStep2(
                    userId = userId,
                    username = username,
                    fromAccount = toAccount,
                    toAccount = fromAccount,
                    transactionType = transactionType,
                    viaAccount = AccountUtils.blankAccount,
                    isTwoWayStep = true,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )
            ) {
                dateTimeString = ((LocalDateTime.parse(
                    dateTimeString, DateTimeUtils.normalDateTimePattern
                ) as LocalDateTime).plusMinutes(5) as LocalDateTime).format(DateTimeUtils.normalDateTimePattern)
            }
        }
    }
}


