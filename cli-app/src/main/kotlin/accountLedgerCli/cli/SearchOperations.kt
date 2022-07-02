package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse

internal fun searchAccount(userAccountsMap: LinkedHashMap<Int, AccountResponse>): Int {

    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
        listOf("\nEnter Search Key : ")
    )
    val searchKeyInput = readLine()
    val searchResult =
        searchOnHashMapValues(hashMap = userAccountsMap, searchKey = searchKeyInput!!)
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
            val input = readLine()
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
            val input = readLine()
            if (input == "1") return chooseAccountByIndex(searchResult)
            else if (input != "0") invalidOptionMessage()
        } while (input != "0")
    }
    return 0
}

private fun searchOnHashMapValues(
    hashMap: LinkedHashMap<Int, AccountResponse>,
    searchKey: String
): LinkedHashMap<Int, AccountResponse> {

    val result = LinkedHashMap<Int, AccountResponse>()
    hashMap.forEach { account ->
        if (account.value.fullName.contains(other = searchKey, ignoreCase = true)) {

            result[account.key] = account.value
        }
    }
    return result
}
