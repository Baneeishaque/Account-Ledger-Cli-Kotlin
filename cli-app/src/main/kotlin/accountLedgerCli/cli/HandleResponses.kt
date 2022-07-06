package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.AccountsResponse
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.cli.App.Companion.userAccountsMap
import accountLedgerCli.retrofit.ResponseHolder
import accountLedgerCli.utils.AccountUtils

internal fun handleAccountsResponseAndPrintMenu(
    apiResponse: ResponseHolder<AccountsResponse>,
    username: String,
    userId: Int
) {

    if (handleAccountsResponse(apiResponse)) {

        do {
            commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                listOf(
                    "\nUser : $username",
                    "Accounts",
                    userAccountsToStringFromLinkedHashMapLimitedTo10(
                        userAccountsMap = userAccountsMap
                    ),
                    "1 - Choose Account - By Index Number",
                    "2 - Choose Account - By Search",
                    "3 - Add Account",
                    "0 - Back",
                    "",
                    "Enter Your Choice : "
                )
            )

            val choice =
                processChildAccountScreenInput(
                    userAccountsMap = userAccountsMap, userId = userId, username = username
                )
        } while (choice != "0")
    }
}

internal fun handleAccountsResponse(apiResponse: ResponseHolder<AccountsResponse>): Boolean {

    if (apiResponse.isError()) {

        println("Error : ${(apiResponse.getValue() as Exception).localizedMessage}")
        //        do {
        //            print("Retry (Y/N) ? : ")
        //            val input = readLine()
        //            when (input) {
        //                "Y", "" -> {
        //                    return handleAccountsResponse(apiResponse)
        //                }
        //                "N" -> {
        //                }
        //                else -> println("Invalid option, try again...")
        //            }
        //        } while (input != "N")
        return false
    } else {

        val localAccountsResponseWithStatus = apiResponse.getValue() as AccountsResponse
        return if (localAccountsResponseWithStatus.status == 1) {

            println("No Accounts...")
            false

        } else {

            userAccountsMap = AccountUtils.prepareUserAccountsMap(localAccountsResponseWithStatus.accounts)
            true
        }
    }
}

internal fun handleAccountsApiResponse(
    apiResponse: ResponseHolder<AccountsResponse>,
    purpose: String
): Boolean {

    if (apiResponse.isError()) {

        println("Error : ${(apiResponse.getValue() as Exception).localizedMessage}")
        //        do {
        //            print("Retry (Y/N) ? : ")
        //            val input = readLine()
        //            when (input) {
        //                "Y", "" -> {
        //                    login()
        //                    return
        //                }
        //                "N" -> {
        //                }
        //                else -> println("Invalid option, try again...")
        //            }
        //        } while (input != "N")
    } else {

        val accountsResponseResult = apiResponse.getValue() as AccountsResponse
        if (accountsResponseResult.status == 1) {

            println("No Accounts...")

        } else {

            userAccountsMap = AccountUtils.prepareUserAccountsMap(accountsResponseResult.accounts)
            do {
                commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                    listOf(
                        "\nAccounts",
                        userAccountsToStringFromLinkedHashMap(
                            userAccountsMap = userAccountsMap
                        ),
                        "1 - Choose $purpose Account - By Index Number",
                        "2 - Search $purpose Account - By Part Of Name",
                        "0 - Back",
                        "",
                        "Enter Your Choice : "
                    )
                )
                val choice = readLine()
                when (choice) {
                    "1" -> {
                        if (purpose == "To") {

                            if (handleToAccountSelection(
                                    chooseAccountByIndex(userAccountsMap), userAccountsMap
                                )
                            ) {

                                return true
                            }
                        } else if (purpose == "From") {

                            if (handleFromAccountSelection(
                                    chooseAccountByIndex(userAccountsMap), userAccountsMap
                                )
                            ) {

                                return true
                            }
                        } else if (purpose == "Via.") {

                            if (handleViaAccountSelection(
                                    chooseAccountByIndex(userAccountsMap), userAccountsMap
                                )
                            ) {

                                return true
                            }
                        }
                    }
                    "2" -> {
                        if (purpose == "To") {

                            if (handleToAccountSelection(
                                    searchAccount(userAccountsMap), userAccountsMap
                                )
                            ) {

                                return true
                            }
                        } else if (purpose == "From") {

                            if (handleFromAccountSelection(
                                    searchAccount(userAccountsMap), userAccountsMap
                                )
                            ) {

                                return true
                            }
                        } else if (purpose == "Via.") {

                            if (handleViaAccountSelection(
                                    searchAccount(userAccountsMap), userAccountsMap
                                )
                            ) {

                                return true
                            }
                        }
                    }
                    "0" -> {
                    }
                    else -> invalidOptionMessage()
                }
            } while (choice != "0")
        }
    }
    return false
}
