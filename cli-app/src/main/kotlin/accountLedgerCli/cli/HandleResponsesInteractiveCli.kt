package accountLedgerCli.cli

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.api.response.AccountsResponse
import account.ledger.library.enums.AccountTypeEnum
import account.ledger.library.enums.AccountsListSortMode
import account.ledger.library.enums.HandleAccountsApiResponseResult
import account.ledger.library.models.InsertTransactionResult
import account.ledger.library.models.ViewTransactionsOutput
import account.ledger.library.utils.AccountUtils
import account.ledger.library.utils.HandleResponsesInteractiveLibrary
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import account_ledger_library.constants.ConstantsNative
import common.utils.library.constants.ConstantsCommon
import common.utils.library.models.IsOkModel
import common.utils.library.utils.*
import io.github.cdimascio.dotenv.Dotenv

object HandleResponsesInteractiveCli {

    internal fun handleAccountsResponseAndPrintMenu(

        apiResponse: Result<AccountsResponse>,
        username: String,
        userId: UInt,
        previousTransactionData: InsertTransactionResult,
        isDevelopmentMode: Boolean,
        dotEnv: Dotenv

    ): InsertTransactionResult {

        var localInsertTransactionResult: InsertTransactionResult = previousTransactionData

        val getUserAccountsMapResult: IsOkModel<LinkedHashMap<UInt, AccountResponse>> =
            HandleResponsesInteractiveLibrary.getUserAccountsMap(apiResponse = apiResponse)

        return IsOkUtils.isOkHandler(

            isOkModel = getUserAccountsMapResult,
            dataOnFailure = localInsertTransactionResult,
            successActions = fun(): InsertTransactionResult {

                do {
                    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                        listOf(
                            "\nUser : $username",
                            ConstantsCommon.dashedLineSeparator,
                            "Accounts",
                            ConstantsCommon.dashedLineSeparator,
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
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                    localInsertTransactionResult = processChildAccountScreenInputResult.addTransactionResult

                } while (processChildAccountScreenInputResult.output != "0")

                return localInsertTransactionResult
            })!!
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
                            "2 - Search $purposeForPrint Account - By Part Of Name (Sort by A/C ID)",
                            "3 - Search $purposeForPrint Account - By Part Of Name (Sort by A/C Full Name)",
                            "4 - Search $purposeForPrint Account - By Part Of Name (Sort by A/C Name)",
                            "5 - Search $purposeForPrint Account - By Part Of Name (Sort by A/C Parent ID)",
                            "0 - Back",
                            "",
                            "Enter Your Choice : "
                        )
                    )
                    when (readln()) {
                        "1" -> {
                            return getHandleAccountsResponseFromApiResult(

                                selectedAccountId = ListUtilsInteractive.getValidIndexFromCollectionWithSelectionPromptAndZeroAsBack(

                                    map = userAccountsMap,
                                    itemSpecification = ConstantsNative.ACCOUNT_TEXT,
                                    items = AccountUtils.userAccountsToStringFromList(

                                        accounts = userAccountsMap.values.toList()
                                    )
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

                        "3" -> {
                            return getHandleAccountsResponseFromApiResult(

                                selectedAccountId = searchAccount(

                                    userAccountsMap = userAccountsMap,
                                    isDevelopmentMode = isDevelopmentMode,
                                    searchMode = AccountsListSortMode.BASED_ON_FULL_NAME
                                ),
                                userAccountsMap = userAccountsMap
                            )
                        }

                        "4" -> {
                            return getHandleAccountsResponseFromApiResult(

                                selectedAccountId = searchAccount(

                                    userAccountsMap = userAccountsMap,
                                    isDevelopmentMode = isDevelopmentMode,
                                    searchMode = AccountsListSortMode.BASED_ON_NAME
                                ),
                                userAccountsMap = userAccountsMap
                            )
                        }

                        "5" -> {
                            return getHandleAccountsResponseFromApiResult(

                                selectedAccountId = searchAccount(

                                    userAccountsMap = userAccountsMap,
                                    isDevelopmentMode = isDevelopmentMode,
                                    searchMode = AccountsListSortMode.BASED_ON_PARENT_ID
                                ),
                                userAccountsMap = userAccountsMap
                            )
                        }

                        "0" -> {
                            return HandleAccountsApiResponseResult(isAccountIdSelected = false)
                        }

                        else -> ErrorUtilsInteractive.printInvalidOptionMessage()
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
