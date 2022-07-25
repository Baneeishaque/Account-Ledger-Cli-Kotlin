package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.AccountsResponse
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.cli.App.Companion.userAccountsMap
import accountLedgerCli.constants.Constants
import accountLedgerCli.enums.GetAccountsApiCallPurposeEnum
import accountLedgerCli.enums.HandleAccountsApiResponseResult
import accountLedgerCli.models.InsertTransactionResult
import accountLedgerCli.models.ViewTransactionsOutput
import accountLedgerCli.utils.AccountUtils

internal fun handleAccountsResponseAndPrintMenu(

    apiResponse: Result<AccountsResponse>,
    username: String,
    userId: UInt,
    viaAccount: AccountResponse,
    toAccount: AccountResponse,
    dateTimeInText: String,
    transactionParticulars: String,
    transactionAmount: Float

): InsertTransactionResult {

    var insertTransactionResult = InsertTransactionResult(
        isSuccess = false,
        dateTimeInText = dateTimeInText,
        transactionParticulars = transactionParticulars,
        transactionAmount = transactionAmount
    )

    if (handleAccountsResponse(apiResponse)) {

        do {
            commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                listOf(
                    "\nUser : $username",
                    "Accounts",
                    userAccountsToStringFromListPair(userAccountsList = userAccountsMap.toList().takeLast(10)),
                    "1 - Choose Account - By Index Number",
                    "2 - Choose Account - By Search",
                    "3 - Add Account",
                    "0 - Back",
                    "",
                    "Enter Your Choice : "
                )
            )

            val processChildAccountScreenInputResult: ViewTransactionsOutput = processChildAccountScreenInput(

                userAccountsMap = userAccountsMap,
                userId = userId,
                username = username,
                viaAccount = viaAccount,
                toAccount = toAccount,
                dateTimeInText = insertTransactionResult.dateTimeInText,
                transactionParticulars = insertTransactionResult.transactionParticulars,
                transactionAmount = insertTransactionResult.transactionAmount
            )
            insertTransactionResult = processChildAccountScreenInputResult.addTransactionResult

        } while (processChildAccountScreenInputResult.output != "0")
    }
    return insertTransactionResult
}

internal fun handleAccountsResponse(apiResponse: Result<AccountsResponse>): Boolean {

    if (apiResponse.isFailure) {

        println("Error : ${(apiResponse.exceptionOrNull() as Exception).localizedMessage}")
        do {
            print("Retry (Y/N) ? : ")
            when (readLine()) {
                "Y", "" -> {
                    return handleAccountsResponse(apiResponse)
                }

                "N" -> {
                    return false

                }

                else -> println("Invalid option, try again...")
            }
        } while (true)
    } else {

        val localAccountsResponseWithStatus: AccountsResponse = apiResponse.getOrNull() as AccountsResponse
        return if (localAccountsResponseWithStatus.status == 1u) {

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
    purpose: GetAccountsApiCallPurposeEnum

): HandleAccountsApiResponseResult {

    if (apiResponse.isFailure) {

        println("Error : ${(apiResponse.exceptionOrNull() as Exception).localizedMessage}")
        do {
            print("Retry (Y/N) ? : ")
            when (readLine()!!) {

                "Y", "" -> {
                    return handleAccountsApiResponse(apiResponse = apiResponse, purpose = purpose)
                }

                "N" -> {
                    return HandleAccountsApiResponseResult(isAccountIdSelected = false)
                }

                else -> println("Invalid option, try again...")
            }
        } while (true)

    } else {

        val accountsResponseResult: AccountsResponse = apiResponse.getOrNull() as AccountsResponse
        if (accountsResponseResult.status == 1u) {

            println("No Accounts...")

        } else {

            val userAccountsMapLocal: LinkedHashMap<UInt, AccountResponse> =
                AccountUtils.prepareUserAccountsMap(accountsResponseResult.accounts)
            do {
                commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                    listOf(
                        "\nAccounts",
                        userAccountsToStringFromLinkedHashMap(
                            userAccountsMap = userAccountsMapLocal
                        ),
                        "1 - Choose $purpose Account - By Index Number",
                        "2 - Search $purpose Account - By Part Of Name",
                        "0 - Back",
                        "",
                        "Enter Your Choice : "
                    )
                )
                val choice: String = readLine()!!
                when (choice) {
                    "1" -> {
                        return getHandleAccountsResponseFromApiResult(
                            selectedAccountId = getValidIndex(
                                map = userAccountsMap,
                                itemSpecification = Constants.accountText,
                                items = userAccountsToStringFromLinkedHashMap(userAccountsMap = userAccountsMap)
                            )
                        )
                    }

                    "2" -> {
                        return getHandleAccountsResponseFromApiResult(selectedAccountId = searchAccount(userAccountsMap = userAccountsMap))
                    }

                    "0" -> {
                    }

                    else -> invalidOptionMessage()
                }
            } while (choice != "0")
        }
    }
    return HandleAccountsApiResponseResult(isAccountIdSelected = false)
}

private fun getHandleAccountsResponseFromApiResult(selectedAccountId: UInt): HandleAccountsApiResponseResult {

    if (selectedAccountId != 0u) {

        return HandleAccountsApiResponseResult(
            isAccountIdSelected = true,
            selectedAccount = userAccountsMap[selectedAccountId]!!
        )
    }
    return HandleAccountsApiResponseResult(isAccountIdSelected = false)
}