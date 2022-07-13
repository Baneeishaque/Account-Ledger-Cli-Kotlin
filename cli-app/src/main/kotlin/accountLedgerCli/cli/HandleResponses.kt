package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountsResponse
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.cli.App.Companion.userAccountsMap
import accountLedgerCli.utils.AccountUtils

internal fun handleAccountsResponseAndPrintMenu(
    apiResponse: Result<AccountsResponse>,
    username: String,
    userId: UInt
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

internal fun handleAccountsResponse(apiResponse: Result<AccountsResponse>): Boolean {

    if (apiResponse.isFailure) {

        println("Error : ${(apiResponse.exceptionOrNull() as Exception).localizedMessage}")
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

        val localAccountsResponseWithStatus = apiResponse.getOrNull() as AccountsResponse
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
    apiResponse: Result<AccountsResponse>,
    purpose: String
): Boolean {

    if (apiResponse.isFailure) {

        println("Error : ${(apiResponse.exceptionOrNull() as Exception).localizedMessage}")
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

        val accountsResponseResult = apiResponse.getOrNull() as AccountsResponse
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
                                    getValidIndex(
                                        map = userAccountsMap,
                                        itemSpecification = Constants.accountText,
                                        items = userAccountsToStringFromLinkedHashMap(userAccountsMap = userAccountsMap)
                                    ), userAccountsMap
                                )
                            ) {

                                return true
                            }
                        } else if (purpose == "From") {

                            if (handleFromAccountSelection(
                                    getValidIndex(
                                        map = userAccountsMap,
                                        itemSpecification = Constants.accountText,
                                        items = userAccountsToStringFromLinkedHashMap(userAccountsMap = userAccountsMap)
                                    ), userAccountsMap
                                )
                            ) {

                                return true
                            }
                        } else if (purpose == "Via.") {

                            if (handleViaAccountSelection(
                                    getValidIndex(
                                        map = userAccountsMap,
                                        itemSpecification = Constants.accountText,
                                        items = userAccountsToStringFromLinkedHashMap(userAccountsMap = userAccountsMap)
                                    ), userAccountsMap
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
