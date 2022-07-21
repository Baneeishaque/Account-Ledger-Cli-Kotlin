package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt

internal fun searchAccount(userAccountsMap: LinkedHashMap<UInt, AccountResponse>): UInt {

    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
        listOf("\nEnter Search Key : ")
    )
    val searchKeyInput: String = readLine()!!
    val searchResult: LinkedHashMap<UInt, AccountResponse> =
        searchOnHashMapValues(hashMap = userAccountsMap, searchKey = searchKeyInput)
    if (searchResult.isEmpty()) {

        do {
            commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                listOf(
                    "No Matches....",
                    "1 - Try Again",
                    "0 - Back",
                    "",
                    "Enter Your Choice : "
                )
            )
            val input: String = readLine()!!
            if (input == "1") return searchAccount(userAccountsMap = userAccountsMap)
            else if (input != "0") invalidOptionMessage()
        } while (input != "0")
    } else {

        do {
            commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                listOf(
                    "\nSearch Results",
                    userAccountsToStringFromLinkedHashMap(userAccountsMap = searchResult),
                    "1 - Choose Deposit Account - By Index Number",
                    "0 - Back",
                    "",
                    "Enter Your Choice : "
                )
            )
            val input: String = readLine()!!
            if (input == "1") {
                return getValidIndex(
                    map = searchResult,
                    itemSpecification = Constants.accountText,
                    items = userAccountsToStringFromLinkedHashMap(userAccountsMap = searchResult)
                )
            } else if (input != "0") {
                invalidOptionMessage()
            }
        } while (input != "0")
    }
    return 0u
}

private fun searchOnHashMapValues(
    hashMap: LinkedHashMap<UInt, AccountResponse>,
    searchKey: String
): LinkedHashMap<UInt, AccountResponse> {

    val result = LinkedHashMap<UInt, AccountResponse>()
    hashMap.forEach { account ->
        if (account.value.fullName.contains(other = searchKey, ignoreCase = true)) {

            result[account.key] = account.value
        }
    }
    return result
}
