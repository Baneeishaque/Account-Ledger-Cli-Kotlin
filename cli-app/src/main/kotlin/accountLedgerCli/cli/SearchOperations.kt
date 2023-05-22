package accountLedgerCli.cli

import account.ledger.library.api.response.AccountResponse
import account_ledger_library.constants.Constants
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import account.ledger.library.utils.AccountUtils
import common.utils.library.utils.InteractiveUtils
import common.utils.library.constants.Constants as CommonConstants

fun searchAccount(

    userAccountsMap: LinkedHashMap<UInt, AccountResponse>,
    isDevelopmentMode: Boolean

): UInt {

    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
        listOf("\nEnter Search Key : ")
    )
    val searchKeyInput: String = readln()

    val searchResult: LinkedHashMap<UInt, AccountResponse> = searchOnHashMapValues(

        hashMap = userAccountsMap,
        searchKey = searchKeyInput,
        isDevelopmentMode = isDevelopmentMode
    )

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

            val input: String = readln()

            if (input == "1") return searchAccount(
                userAccountsMap = userAccountsMap,
                isDevelopmentMode = isDevelopmentMode
            )
            else if (input != "0") InteractiveUtils.invalidOptionMessage()

        } while (input != "0")

    } else {

        do {
            commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                listOf(
                    "\nSearch Results",
                    AccountUtils.userAccountsToStringFromList(

                        accounts = searchResult.values.toList()
                    ),
                    "1 - Choose Deposit Account - By Index Number",
                    "0 - Back",
                    "",
                    "Enter Your Choice : "
                )
            )
            val input: String = readln()
            if (input == "1") {

                return getValidIndexWithInputPrompt(

                    map = searchResult,
                    itemSpecification = Constants.accountText,
                    items = AccountUtils.userAccountsToStringFromList(

                        accounts = searchResult.values.toList()
                    ),
                    backValue = 0u
                )
            } else if (input != "0") {

                InteractiveUtils.invalidOptionMessage()
            }
        } while (input != "0")
    }
    return 0u
}

// TODO : Make Generic function
private fun searchOnHashMapValues(

    hashMap: LinkedHashMap<UInt, AccountResponse>,
    searchKey: String,
    isDevelopmentMode: Boolean

): LinkedHashMap<UInt, AccountResponse> {

    if (isDevelopmentMode) {

        println(
            "Map to Search\n${CommonConstants.dashedLineSeparator}\n${
                AccountUtils.userAccountsToStringFromList(
                    accounts = hashMap.values.toList()
                )
            }"
        )
        println("searchKey = $searchKey")
    }

    val result = LinkedHashMap<UInt, AccountResponse>()
    hashMap.forEach { account ->

        if (account.value.fullName.contains(other = searchKey, ignoreCase = true)) {

            result[account.key] = account.value
        }
    }
    return result
}
