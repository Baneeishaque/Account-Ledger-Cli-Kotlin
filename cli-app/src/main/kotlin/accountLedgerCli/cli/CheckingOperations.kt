package accountLedgerCli.cli

import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithContinuePrompt
import accountLedgerCli.cli.App.Companion.dateTimeString
import accountLedgerCli.cli.App.Companion.fromAccount
import accountLedgerCli.cli.App.Companion.toAccount
import accountLedgerCli.cli.App.Companion.viaAccount
import accountLedgerCli.to_utils.DateTimeUtils
import accountLedgerCli.utils.AccountUtils
import java.time.LocalDateTime

internal fun isAccountsAreAvailable(transactionTypeEnum: TransactionTypeEnum): Boolean {

    if (toAccount.id == 0) {

        println("Please choose deposit account...")
        return false

    } else if (fromAccount.id == 0) {

        println("Please choose from account...")
        return false

    } else if ((transactionTypeEnum == TransactionTypeEnum.VIA) && (viaAccount.id == 0)) {

        println("Please choose via. account...")
        return false
    }
    return true
}

internal fun transactionContinueCheck(
    userId: Int,
    username: String,
    transactionTypeEnum: TransactionTypeEnum
) {

    do {

        var menuItems = listOf(
            "\nUser : $username",
            "From Account - ${fromAccount.id} : ${fromAccount.fullName}",
        )
        if (transactionTypeEnum == TransactionTypeEnum.VIA) {
            menuItems = menuItems + listOf(
                "Via. Account - ${viaAccount.id} : ${viaAccount.fullName}",
            )
        }
        menuItems = menuItems + listOf(
            "To Account - ${toAccount.id} : ${toAccount.fullName}",
            "",
            "Continue (Y/N) : "
        )
        commandLinePrintMenuWithContinuePrompt.printMenuWithContinuePromptFromListOfCommands(menuItems)

        val input = readLine()
        when (input) {
            "Y", "" -> {

                addTransactionWithAccountAvailabilityCheck(
                    userId = userId,
                    username = username,
                    transactionTypeEnum = transactionTypeEnum
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
    userId: Int,
    username: String,
    transactionTypeEnum: TransactionTypeEnum
) {

    if (isAccountsAreAvailable(transactionTypeEnum)) {

        if (transactionTypeEnum == TransactionTypeEnum.VIA) {

            if (addTransactionStep2(
                    userId = userId,
                    username = username,
                    localFromAccount = fromAccount,
                    localToAccount = viaAccount,
                    transactionTypeEnum = transactionTypeEnum,
                    localViaAccount = AccountUtils.getBlankAccount()
                )
            ) {
                dateTimeString =
                    ((LocalDateTime.parse(dateTimeString, DateTimeUtils.normalDateTimePattern) as LocalDateTime)
                        .plusMinutes(5) as
                            LocalDateTime)
                        .format(DateTimeUtils.normalDateTimePattern)

                if (addTransactionStep2(
                        userId = userId,
                        username = username,
                        localFromAccount = viaAccount,
                        localToAccount = toAccount,
                        transactionTypeEnum = transactionTypeEnum,
                        localViaAccount = AccountUtils.getBlankAccount(),
                        isViaStep = true
                    )
                ) {
                    dateTimeString =
                        ((LocalDateTime.parse(dateTimeString, DateTimeUtils.normalDateTimePattern) as LocalDateTime)
                            .plusMinutes(5) as
                                LocalDateTime)
                            .format(DateTimeUtils.normalDateTimePattern)
                }
            }
        } else if (transactionTypeEnum == TransactionTypeEnum.NORMAL) {

            if (addTransactionStep2(
                    userId = userId,
                    username = username,
                    localFromAccount = fromAccount,
                    localToAccount = toAccount,
                    transactionTypeEnum = transactionTypeEnum,
                    localViaAccount = AccountUtils.getBlankAccount()
                )
            ) {
                dateTimeString =
                    ((LocalDateTime.parse(dateTimeString, DateTimeUtils.normalDateTimePattern) as LocalDateTime)
                        .plusMinutes(5) as
                            LocalDateTime)
                        .format(DateTimeUtils.normalDateTimePattern)
            }
        } else if (transactionTypeEnum == TransactionTypeEnum.TWO_WAY) {

            if (addTransactionStep2(
                    userId = userId,
                    username = username,
                    localFromAccount = fromAccount,
                    localToAccount = toAccount,
                    transactionTypeEnum = transactionTypeEnum,
                    localViaAccount = AccountUtils.getBlankAccount()
                )
            ) {
                dateTimeString =
                    ((LocalDateTime.parse(dateTimeString, DateTimeUtils.normalDateTimePattern) as LocalDateTime)
                        .plusMinutes(5) as
                            LocalDateTime)
                        .format(DateTimeUtils.normalDateTimePattern)

                if (addTransactionStep2(
                        userId = userId,
                        username = username,
                        localFromAccount = toAccount,
                        localToAccount = fromAccount,
                        transactionTypeEnum = transactionTypeEnum,
                        localViaAccount = AccountUtils.getBlankAccount(),
                        isTwoWayStep = true
                    )
                ) {
                    dateTimeString =
                        ((LocalDateTime.parse(dateTimeString, DateTimeUtils.normalDateTimePattern) as LocalDateTime)
                            .plusMinutes(5) as
                                LocalDateTime)
                            .format(DateTimeUtils.normalDateTimePattern)
                }
            }
        }
    } else {

        addTransaction(userId = userId, username = username, transactionTypeEnum = transactionTypeEnum)
    }
}

