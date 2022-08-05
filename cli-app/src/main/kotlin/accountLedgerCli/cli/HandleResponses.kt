package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.AccountsResponse
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.constants.Constants
import accountLedgerCli.enums.AccountTypeEnum
import accountLedgerCli.enums.HandleAccountsApiResponseResult
import accountLedgerCli.models.InsertTransactionResult
import accountLedgerCli.models.ViewTransactionsOutput
import accountLedgerCli.to_models.IsOkModel
import accountLedgerCli.to_utils.EnumUtils
import accountLedgerCli.to_utils.invalidOptionMessage
import accountLedgerCli.utils.AccountUtils
import accountLedgerCli.to_constants.Constants as CommonConstants
import accountLedgerCli.to_utils.HandleResponses as CommonHandleResponses

object HandleResponses {

    internal fun handleAccountsResponseAndPrintMenu(

        apiResponse: Result<AccountsResponse>,
        username: String,
        userId: UInt,
        insertTransactionResult: InsertTransactionResult

    ): InsertTransactionResult {

        var localInsertTransactionResult: InsertTransactionResult = insertTransactionResult

        val getUserAccountsMapResult: IsOkModel<LinkedHashMap<UInt, AccountResponse>> =
            getUserAccountsMap(apiResponse = apiResponse)

        return CommonHandleResponses.isOkModelHandler(

            isOkModel = getUserAccountsMapResult,
            data = localInsertTransactionResult,
            successActions = fun(): InsertTransactionResult {

                do {
                    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                        listOf(
                            "\nUser : $username",
                            CommonConstants.dashedLineSeparator,
                            "Accounts",
                            CommonConstants.dashedLineSeparator,
                            AccountUtils.userAccountsToStringFromListPair(
                                userAccountsList = getUserAccountsMapResult.data!!.toList().takeLast(n = 10)
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

                        userAccountsMap = getUserAccountsMapResult.data,
                        userId = userId,
                        username = username,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount
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
        purpose: AccountTypeEnum

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
                            AccountUtils.userAccountsToStringFromLinkedHashMap(
                                userAccountsMap = userAccountsMap
                            ),
                            "1 - Choose $purposeForPrint Account - By Index Number",
                            "2 - Search $purposeForPrint Account - By Part Of Name",
                            "0 - Back",
                            "",
                            "Enter Your Choice : "
                        )
                    )
                    when (readLine()!!) {
                        "1" -> {
                            return getHandleAccountsResponseFromApiResult(

                                selectedAccountId = getValidIndexWithInputPrompt(

                                    map = userAccountsMap,
                                    itemSpecification = Constants.accountText,
                                    items = AccountUtils.userAccountsToStringFromLinkedHashMap(userAccountsMap = userAccountsMap),
                                    backValue = 0u
                                ),
                                userAccountsMap = userAccountsMap
                            )
                        }

                        "2" -> {
                            return getHandleAccountsResponseFromApiResult(

                                selectedAccountId = searchAccount(userAccountsMap = userAccountsMap),
                                userAccountsMap = userAccountsMap
                            )
                        }

                        "0" -> {
                            return HandleAccountsApiResponseResult(isAccountIdSelected = false)
                        }

                        else -> invalidOptionMessage()
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