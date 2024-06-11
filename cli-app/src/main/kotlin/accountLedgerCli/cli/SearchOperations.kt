package accountLedgerCli.cli

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.enums.AccountsListSortMode
import account.ledger.library.utils.AccountUtils
import account_ledger_library.constants.ConstantsNative
import common.utils.library.constants.CommonConstants
import common.utils.library.utils.InteractiveUtils
import common.utils.library.utils.ListUtils

fun searchAccount(

    userAccountsMap: LinkedHashMap<UInt, AccountResponse>,
    isDevelopmentMode: Boolean,
    searchMode: AccountsListSortMode = AccountsListSortMode.BASED_ON_ID

): UInt {

    App.commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
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
            App.commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
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
            App.commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                listOf(
                    "\nSearch Results",
                    AccountUtils.userAccountsToStringFromList(

                        accounts = searchResult.values.toList(),
                        sortMode = searchMode
                    ),
                    "1 - Choose Deposit Account - By Index Number",
                    "0 - Back",
                    "",
                    "Enter Your Choice : "
                )
            )
            val input: String = readln()
            if (input == "1") {

                return ListUtils.getValidIndexFromCollectionWithSelectionPromptAndZeroAsBack(

                    map = searchResult,
                    itemSpecification = ConstantsNative.accountText,
                    items = AccountUtils.userAccountsToStringFromList(

                        accounts = searchResult.values.toList()
                    )
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
