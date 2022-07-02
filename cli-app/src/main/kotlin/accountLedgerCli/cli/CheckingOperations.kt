package accountLedgerCli.cli

import accountLedgerCli.to_utils.DateTimeUtils
import accountLedgerCli.utils.AccountUtils
import java.time.LocalDateTime

internal fun isAccountsAreAvailable(transactionType: TransactionType): Boolean {

    if (toAccount.id == 0) {

        println("Please choose deposit account...")
        return false

    } else if (fromAccount.id == 0) {

        println("Please choose from account...")
        return false

    } else if ((transactionType == TransactionType.VIA) && (viaAccount.id == 0)) {

        println("Please choose via. account...")
        return false
    }
    return true
}

internal fun transactionContinueCheck(
    userId: Int,
    username: String,
    transactionType: TransactionType
) {

    do {

        var menuItems = listOf(
            "\nUser : $username",
            "From Account - ${fromAccount.id} : ${fromAccount.fullName}",
        )
        if (transactionType == TransactionType.VIA) {
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
                    transactionType = transactionType
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
    transactionType: TransactionType
) {

    if (isAccountsAreAvailable(transactionType)) {

        if (transactionType == TransactionType.VIA) {

            if (addTransactionStep2(
                    userId = userId,
                    username = username,
                    localFromAccount = fromAccount,
                    localToAccount = viaAccount,
                    transactionType = transactionType,
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
                        transactionType = transactionType,
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
        } else if (transactionType == TransactionType.NORMAL) {

            if (addTransactionStep2(
                    userId = userId,
                    username = username,
                    localFromAccount = fromAccount,
                    localToAccount = toAccount,
                    transactionType = transactionType,
                    localViaAccount = AccountUtils.getBlankAccount()
                )
            ) {
                dateTimeString =
                    ((LocalDateTime.parse(dateTimeString, DateTimeUtils.normalDateTimePattern) as LocalDateTime)
                        .plusMinutes(5) as
                            LocalDateTime)
                        .format(DateTimeUtils.normalDateTimePattern)
            }
        } else if (transactionType == TransactionType.TWO_WAY) {

            if (addTransactionStep2(
                    userId = userId,
                    username = username,
                    localFromAccount = fromAccount,
                    localToAccount = toAccount,
                    transactionType = transactionType,
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
                        transactionType = transactionType,
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

        addTransaction(userId = userId, username = username, transactionType = transactionType)
    }
}

