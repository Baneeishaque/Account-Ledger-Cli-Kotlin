package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.UserResponse
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithTryPrompt
import accountLedgerCli.cli.App.Companion.dateTimeString
import accountLedgerCli.to_utils.InputUtils
import accountLedgerCli.utils.ApiUtils

internal fun chooseAccountByIndex(userAccountsMap: LinkedHashMap<UInt, AccountResponse>): UInt {

    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
        listOf(
            "\nAccounts",
            userAccountsToStringFromLinkedHashMap(userAccountsMap = userAccountsMap),
            "Enter Account Index, or O to back : A"
        )
    )
    val accountIdInput: String = readLine()!!
    if (accountIdInput == "0") return 0u
    val accountId: UInt = InputUtils.getValidInt(accountIdInput, "Invalid Account Index")
    return getValidAccountIndex(userAccountsMap = userAccountsMap, accountId = accountId)
}

internal fun chooseUserByIndex(usersMap: LinkedHashMap<UInt, UserResponse>): UInt {

    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
        listOf(
            "\nUsers",
            usersToStringFromLinkedHashMap(
                usersMap = usersMap
            ),
            "Enter User Index, or O to back : A"
        )
    )
    val userIdInput: String = readLine()!!
    if (userIdInput == "0") return 0u
    val userId: UInt = InputUtils.getValidInt(userIdInput, "Invalid User Index")
    return getValidUserIndex(usersMap = usersMap, userId = userId)
}

internal fun getValidAccountIndex(userAccountsMap: LinkedHashMap<UInt, AccountResponse>, accountId: UInt): UInt {
    if (userAccountsMap.containsKey(accountId)) {

        return accountId
    }
    commandLinePrintMenuWithTryPrompt.printMenuWithTryPromptFromListOfCommands(
        listOf("Invalid Account Index, Try again ? (Y/N) : ")
    )
    return when (readLine()) {
        "Y", "" -> {
            getValidAccountIndex(userAccountsMap = userAccountsMap, accountId = accountId)
        }

        "N" -> {
            0u
        }

        else -> {

            commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                listOf("Invalid Entry...")
            )
            getValidAccountIndex(userAccountsMap = userAccountsMap, accountId = accountId)
        }
    }
}

internal fun getValidUserIndex(usersMap: LinkedHashMap<UInt, UserResponse>, userId: UInt): UInt {
    if (usersMap.containsKey(userId)) {

        return userId
    }
    commandLinePrintMenuWithTryPrompt.printMenuWithTryPromptFromListOfCommands(
        listOf("Invalid Account Index, Try again ? (Y/N) : ")
    )
    return when (readLine()) {
        "Y", "" -> {
            getValidUserIndex(usersMap = usersMap, userId = userId)
        }

        "N" -> {
            0u
        }

        else -> {

            commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                listOf("Invalid Entry...")
            )
            getValidUserIndex(usersMap = usersMap, userId = userId)
        }
    }
}

internal fun chooseDepositTop(userId: UInt): Boolean {

    return handleAccountsApiResponse(apiResponse = getAccounts(userId = userId), purpose = "To")
}

internal fun chooseDepositFull(userId: UInt): Boolean {

    return handleAccountsApiResponse(apiResponse = ApiUtils.getAccountsFull(userId = userId), purpose = "To")
}

internal fun chooseFromAccountTop(userId: UInt): Boolean {

    return handleAccountsApiResponse(apiResponse = getAccounts(userId = userId), purpose = "From")
}

internal fun chooseFromAccountFull(userId: UInt): Boolean {

    return handleAccountsApiResponse(apiResponse = ApiUtils.getAccountsFull(userId), purpose = "From")
}

internal fun chooseViaAccountFull(userId: UInt): Boolean {

    return handleAccountsApiResponse(apiResponse = ApiUtils.getAccountsFull(userId), purpose = "Via.")
}

internal fun enterDateWithTime(transactionTypeEnum: TransactionTypeEnum): String {

    print(
        "$dateTimeString Correct? (Y/N), D+Tr to increase 1 Day with Time Reset, D+ to increase 1 Day, D2+Tr to increase 2 Days with Time Reset, D2+ to increase 2 Days,${if (transactionTypeEnum == TransactionTypeEnum.VIA) " Ex12 to exchange From & Via A/Cs, Ex23 to exchange Via & To A/Cs, Ex13 to exchange From & To A/Cs" else " Ex to exchange From & To A/Cs"} or B to Back : "
    )
    when (readLine()) {
        "Y", "" -> {

            return dateTimeString
        }

        "N" -> {

            return InputUtils.getValidDateTimeInNormalPattern()
        }

        "D+Tr" -> {

            return "D+Tr"
        }

        "D+" -> {

            return "D+"
        }

        "D2+Tr" -> {

            return "D2+Tr"
        }

        "D2+" -> {

            return "D2+"
        }

        "Ex" -> {

            if (transactionTypeEnum == TransactionTypeEnum.VIA) {
                invalidOptionMessage()
                return enterDateWithTime(transactionTypeEnum = transactionTypeEnum)
            }
            return "Ex"
        }

        "Ex12" -> {

            if (transactionTypeEnum != TransactionTypeEnum.VIA) {
                invalidOptionMessage()
                return enterDateWithTime(transactionTypeEnum = transactionTypeEnum)
            }
            return "Ex12"
        }

        "Ex23" -> {

            if (transactionTypeEnum != TransactionTypeEnum.VIA) {
                invalidOptionMessage()
                return enterDateWithTime(transactionTypeEnum = transactionTypeEnum)
            }
            return "Ex23"
        }

        "Ex13" -> {

            if (transactionTypeEnum != TransactionTypeEnum.VIA) {
                invalidOptionMessage()
                return enterDateWithTime(transactionTypeEnum = transactionTypeEnum)
            }
            return "Ex13"
        }

        "B" -> {

            return "B"
        }

        else -> {

            invalidOptionMessage()
            return enterDateWithTime(transactionTypeEnum = transactionTypeEnum)
        }
    }
}
