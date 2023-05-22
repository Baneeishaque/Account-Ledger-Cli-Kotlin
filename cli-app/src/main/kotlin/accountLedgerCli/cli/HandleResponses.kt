package accountLedgerCli.cli

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.api.response.AccountsResponse
import account_ledger_library.constants.Constants
import account.ledger.library.enums.AccountTypeEnum
import account.ledger.library.enums.HandleAccountsApiResponseResult
import account.ledger.library.models.InsertTransactionResult
import account.ledger.library.models.ViewTransactionsOutput
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import account.ledger.library.utils.AccountUtils
import common.utils.library.models.IsOkModel
import common.utils.library.utils.EnumUtils
import common.utils.library.utils.InteractiveUtils
import common.utils.library.constants.Constants as CommonConstants
import common.utils.library.utils.HandleResponses as CommonHandleResponses

object HandleResponses {

    internal fun handleAccountsResponseAndPrintMenu(

        apiResponse: Result<AccountsResponse>,
        username: String,
        userId: UInt,
        previousTransactionData: InsertTransactionResult,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ): InsertTransactionResult {

        var localInsertTransactionResult: InsertTransactionResult = previousTransactionData

        val getUserAccountsMapResult: IsOkModel<LinkedHashMap<UInt, AccountResponse>> =
            getUserAccountsMap(apiResponse = apiResponse)

        return CommonHandleResponses.isOkModelHandler(

            isOkModel = getUserAccountsMapResult,
            data = localInsertTransactionResult,
            successActions = fun(): InsertTransactionResult {

//                if (isDevelopmentMode) {
//
////                    println(AccountUtils.userAccountsToStringFromLinkedHashMap(userAccountsMap = getUserAccountsMapResult.data!!))
//                }

                do {
                    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                        listOf(
                            "\nUser : $username",
                            CommonConstants.dashedLineSeparator,
                            "Accounts",
                            CommonConstants.dashedLineSeparator,
                            AccountUtils.userAccountsToStringFromList(

                                accounts = getUserAccountsMapResult.data!!.values.toList().takeLast(n = 10)
//                                accounts = getUserAccountsMapResult.data!!.values.toList()
                            ),
                            "1 - Choose Account - By Index Number",
                            "2 - Choose Account - By Search",
                            "3 - Add Account",
                            "0 - Back",
                            "",
                            "Enter Your Choice : "
                        )
                    )

                    val processChildAccountScreenInputResult: ViewTransactionsOutput = processChildAccountScreenInput(

                        userAccountsMap = getUserAccountsMapResult.data!!,
                        userId = userId,
                        username = username,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                    localInsertTransactionResult = processChildAccountScreenInputResult.addTransactionResult

                } while (processChildAccountScreenInputResult.output != "0")

                return localInsertTransactionResult
            })
    }

    internal fun getUserAccountsMap(apiResponse: Result<AccountsResponse>): IsOkModel<LinkedHashMap<UInt, AccountResponse>> {

        if (apiResponse.isFailure) {

            println("Error : ${(apiResponse.exceptionOrNull() as Exception).localizedMessage}")
            return IsOkModel(isOK = false)

        } else {

            val localAccountsResponseWithStatus: AccountsResponse = apiResponse.getOrNull() as AccountsResponse
            return if (localAccountsResponseWithStatus.status == 1u) {

                println("No Accounts...")
                IsOkModel(isOK = false)

            } else {

                IsOkModel(
                    isOK = true,
                    data = AccountUtils.prepareUserAccountsMap(localAccountsResponseWithStatus.accounts)
                )
            }
        }
    }

    internal fun handleAccountsApiResponse(

        apiResponse: Result<AccountsResponse>,
        purpose: AccountTypeEnum,
        isDevelopmentMode: Boolean

    ): HandleAccountsApiResponseResult {

        if (apiResponse.isFailure) {

            println("Error : ${(apiResponse.exceptionOrNull() as Exception).localizedMessage}")
            return HandleAccountsApiResponseResult(isAccountIdSelected = false)

        } else {

            val accountsResponseResult: AccountsResponse = apiResponse.getOrNull() as AccountsResponse
            if (accountsResponseResult.status == 1u) {

                println("No Accounts...")

            } else {

                val userAccountsMap: LinkedHashMap<UInt, AccountResponse> =
                    AccountUtils.prepareUserAccountsMap(accountsResponseResult.accounts)
                do {
                    val purposeForPrint: String = EnumUtils.getEnumNameForPrint(localEnum = purpose)
                    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                        listOf(
                            "\nAccounts",
                            AccountUtils.userAccountsToStringFromList(

                                accounts = userAccountsMap.values.toList()
                            ),
                            "1 - Choose $purposeForPrint Account - By Index Number",
                            "2 - Search $purposeForPrint Account - By Part Of Name",
                            "0 - Back",
                            "",
                            "Enter Your Choice : "
                        )
                    )
                    when (readln()) {
                        "1" -> {
                            return getHandleAccountsResponseFromApiResult(

                                selectedAccountId = getValidIndexWithInputPrompt(

                                    map = userAccountsMap,
                                    itemSpecification = Constants.accountText,
                                    items = AccountUtils.userAccountsToStringFromList(

                                        accounts = userAccountsMap.values.toList()
                                    ),
                                    backValue = 0u
                                ),
                                userAccountsMap = userAccountsMap
                            )
                        }

                        "2" -> {
                            return getHandleAccountsResponseFromApiResult(

                                selectedAccountId = searchAccount(

                                    userAccountsMap = userAccountsMap,
                                    isDevelopmentMode = isDevelopmentMode
                                ),
                                userAccountsMap = userAccountsMap
                            )
                        }

                        "0" -> {
                            return HandleAccountsApiResponseResult(isAccountIdSelected = false)
                        }

                        else -> InteractiveUtils.invalidOptionMessage()
                    }
                } while (true)
            }
        }
        return HandleAccountsApiResponseResult(isAccountIdSelected = false)
    }

    private fun getHandleAccountsResponseFromApiResult(

        selectedAccountId: UInt,
        userAccountsMap: LinkedHashMap<UInt, AccountResponse>

    ): HandleAccountsApiResponseResult {

        if (selectedAccountId != 0u) {

            return HandleAccountsApiResponseResult(
                isAccountIdSelected = true,
                selectedAccount = userAccountsMap[selectedAccountId]!!
            )
        }
        return HandleAccountsApiResponseResult(isAccountIdSelected = false)
    }
}
