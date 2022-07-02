package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.UserResponse
import accountLedgerCli.to_utils.InputUtils
import accountLedgerCli.utils.ApiUtils

internal fun chooseAccountByIndex(userAccountsMap: LinkedHashMap<Int, AccountResponse>): Int {

    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
        listOf(
            "\nAccounts",
            userAccountsToStringFromLinkedHashMap(userAccountsMap = userAccountsMap),
            "Enter Account Index, or O to back : A"
        )
    )
    val accountIdInput = readLine()!!
    if (accountIdInput == "0") return 0
    val accountId = InputUtils.getValidInt(accountIdInput, "Invalid Account Index")
    return getValidAccountIndex(userAccountsMap = userAccountsMap, accountId = accountId)
}

internal fun chooseUserByIndex(usersMap: LinkedHashMap<Int, UserResponse>): Int {

    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
        listOf(
            "\nUsers",
            usersToStringFromLinkedHashMap(
                usersMap = usersMap
            ),
            "Enter User Index, or O to back : A"
        )
    )
    val userIdInput = readLine()!!
    if (userIdInput == "0") return 0
    val userId = InputUtils.getValidInt(userIdInput, "Invalid User Index")
    return getValidUserIndex(usersMap = usersMap, userId = userId)
}

internal fun getValidAccountIndex(userAccountsMap: LinkedHashMap<Int, AccountResponse>, accountId: Int): Int {
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
            0
        }
        else -> {

            commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                listOf("Invalid Entry...")
            )
            getValidAccountIndex(userAccountsMap = userAccountsMap, accountId = accountId)
        }
    }
}

internal fun getValidUserIndex(usersMap: LinkedHashMap<Int, UserResponse>, userId: Int): Int {
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
            0
        }
        else -> {

            commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                listOf("Invalid Entry...")
            )
            getValidUserIndex(usersMap = usersMap, userId = userId)
        }
    }
}

internal fun chooseDepositTop(userId: Int): Boolean {

    return handleAccountsApiResponse(apiResponse = getAccounts(userId = userId), purpose = "To")
}

internal fun chooseDepositFull(userId: Int): Boolean {

    return handleAccountsApiResponse(apiResponse = ApiUtils.getAccountsFull(userId = userId), purpose = "To")
}

internal fun chooseFromAccountTop(userId: Int): Boolean {

    return handleAccountsApiResponse(apiResponse = getAccounts(userId = userId), purpose = "From")
}

internal fun chooseFromAccountFull(userId: Int): Boolean {

    return handleAccountsApiResponse(apiResponse = ApiUtils.getAccountsFull(userId), purpose = "From")
}

internal fun chooseViaAccountFull(userId: Int): Boolean {

    return handleAccountsApiResponse(apiResponse = ApiUtils.getAccountsFull(userId), purpose = "Via.")
}

internal fun enterDateWithTime(transactionType: TransactionType): String {

    print(
        "$dateTimeString Correct? (Y/N), D+Tr to increase 1 Day with Time Reset, D+ to increase 1 Day, D2+Tr to increase 2 Days with Time Reset, D2+ to increase 2 Days,${if (transactionType == TransactionType.VIA) " Ex12 to exchange From & Via A/Cs, Ex23 to exchange Via & To A/Cs, Ex13 to exchange From & To A/Cs" else " Ex to exchange From & To A/Cs"} or B to Back : "
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

            if (transactionType == TransactionType.VIA) {
                invalidOptionMessage()
                return enterDateWithTime(transactionType = transactionType)
            }
            return "Ex"
        }
        "Ex12" -> {

            if (transactionType != TransactionType.VIA) {
                invalidOptionMessage()
                return enterDateWithTime(transactionType = transactionType)
            }
            return "Ex12"
        }
        "Ex23" -> {

            if (transactionType != TransactionType.VIA) {
                invalidOptionMessage()
                return enterDateWithTime(transactionType = transactionType)
            }
            return "Ex23"
        }
        "Ex13" -> {

            if (transactionType != TransactionType.VIA) {
                invalidOptionMessage()
                return enterDateWithTime(transactionType = transactionType)
            }
            return "Ex13"
        }
        "B" -> {

            return "B"
        }
        else -> {

            invalidOptionMessage()
            return enterDateWithTime(transactionType = transactionType)
        }
    }
}
