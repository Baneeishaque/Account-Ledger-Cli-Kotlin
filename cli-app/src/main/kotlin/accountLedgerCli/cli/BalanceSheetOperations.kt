package accountLedgerCli.cli

import account.ledger.library.api.response.MultipleTransactionResponse
import account.ledger.library.api.response.TransactionResponse
import account.ledger.library.api.response.UserResponse
import account.ledger.library.enums.BalanceSheetRefineLevelEnum
import account.ledger.library.models.BalanceSheetDataRowModel
import account.ledger.library.models.ChooseUserResult
import account.ledger.library.operations.getUserInitialTransactionDateFromUsername
import account.ledger.library.operations.getUserTransactionsForAnAccount
import account.ledger.library.retrofit.data.MultipleTransactionDataSource
import account.ledger.library.utils.UserUtils
import account_ledger_library.constants.ConstantsNative
import common.utils.library.models.CommonDataModel
import common.utils.library.models.IsOkModel
import common.utils.library.utils.DateTimeUtils
import common.utils.library.utils.InteractiveUtils
import common.utils.library.utils.MysqlUtils
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

internal fun balanceSheetOfUser(
    usersMap: LinkedHashMap<UInt, UserResponse>,
    isConsoleMode: Boolean,
    isDevelopmentMode: Boolean
) {

    val chooseUserResult: ChooseUserResult = handleUserSelection(
        chosenUserId = getValidIndexWithInputPrompt(

            map = usersMap,
            itemSpecification = ConstantsNative.userText,
            items = UserUtils.usersToStringFromLinkedHashMap(usersMap = usersMap),
            backValue = 0u
        ), usersMap = usersMap
    )
    if (chooseUserResult.isChosen) {

        printBalanceSheetOfUser(

            currentUserName = chooseUserResult.chosenUser!!.username,
            currentUserId = chooseUserResult.chosenUser!!.id,
            isConsoleMode = isConsoleMode,
            isDevelopmentMode = isDevelopmentMode
        )
    }
}

internal fun printBalanceSheetOfUser(

    currentUserName: String,
    currentUserId: UInt,
    refineLevel: BalanceSheetRefineLevelEnum = BalanceSheetRefineLevelEnum.WITHOUT_EXPENSE_ACCOUNTS,
    isNotApiCall: Boolean = true,
    isConsoleMode: Boolean,
    isDevelopmentMode: Boolean

) {

    printSheetOfUser(
        currentUserName = currentUserName,
        currentUserId = currentUserId,
        sheetTitle = "Balance",
        getDesiredAccountIdsForSheetOfUser = fun(selectUserTransactionsAfterSpecifiedDateResult: MultipleTransactionResponse): MutableMap<UInt, String> {
            return getDesiredAccountIdsForBalanceSheetOfUser(
                refineLevel = refineLevel,
                selectUserTransactionsAfterSpecifiedDateResult = selectUserTransactionsAfterSpecifiedDateResult
            )
        },
        isNotApiCall = isNotApiCall,
        isConsoleMode = isConsoleMode,
        isDevelopmentMode = isDevelopmentMode
    )
}


internal fun printSheetOfUser(

    currentUserName: String,
    currentUserId: UInt,
    sheetTitle: String,
    getDesiredAccountIdsForSheetOfUser: (MultipleTransactionResponse) -> MutableMap<UInt, String>,
    isNotApiCall: Boolean = true,
    isConsoleMode: Boolean,
    isDevelopmentMode: Boolean

) {

    if (isConsoleMode) {

        print("currentUser : $currentUserName")
    }
    val multipleTransactionDataSource = MultipleTransactionDataSource()
    if (isNotApiCall) {

        println("Contacting Server...")
    }
    val apiResponse: Result<MultipleTransactionResponse>
    //TODO : Only applicable for user_first_entry_date usernames
    val specifiedDate: IsOkModel<String> = MysqlUtils.normalDateTextToMySqlDateText(
        normalDateText = getUserInitialTransactionDateFromUsername(username = currentUserName).minusDays(
            1
        ).format(DateTimeUtils.normalDatePattern)
    )
    if (specifiedDate.isOK) {

        runBlocking {

            apiResponse = multipleTransactionDataSource.selectUserTransactionsAfterSpecifiedDate(

                userId = currentUserId,
                specifiedDate = specifiedDate.data!!
            )
        }

        // println("Response : $apiResponse2")
        if (apiResponse.isFailure) {

            if (isNotApiCall) {

                println("Error : ${(apiResponse.exceptionOrNull() as Exception).localizedMessage}")
                do {
                    print("Retry (Y/N) ? : ")
                    when (readln()) {
                        "Y", "" -> {
                            printSheetOfUser(
                                currentUserName = currentUserName,
                                currentUserId = currentUserId,
                                sheetTitle = sheetTitle,
                                getDesiredAccountIdsForSheetOfUser = getDesiredAccountIdsForSheetOfUser,
                                isConsoleMode = isConsoleMode,
                                isDevelopmentMode = isDevelopmentMode
                            )
                            return
                        }

                        "N" -> {
                            return
                        }

                        else -> InteractiveUtils.invalidOptionMessage()
                    }
                } while (true)

            } else {

                print(
                    Json.encodeToString(
                        serializer = CommonDataModel.serializer(Unit.serializer()),
                        value = CommonDataModel(
                            status = 1,
                            error = "Error : ${(apiResponse.exceptionOrNull() as Exception).localizedMessage}"
                        )
                    )
                )
            }
        } else {

            val selectUserTransactionsAfterSpecifiedDateResult: MultipleTransactionResponse = apiResponse.getOrNull()!!
            if (selectUserTransactionsAfterSpecifiedDateResult.status == 1u) {

                if (isNotApiCall) {

                    println("No Transactions...")

                } else {

                    print(
                        Json.encodeToString(

                            serializer = CommonDataModel.serializer(Unit.serializer()),
                            value = CommonDataModel(

                                status = 2,
                                error = "No Transactions"
                            )
                        )
                    )
                }

            } else {

                val accounts: MutableMap<UInt, String> =
                    getDesiredAccountIdsForSheetOfUser(
                        selectUserTransactionsAfterSpecifiedDateResult
                    )
                val menuItems: MutableList<String> = mutableListOf("\nUser : $currentUserName $sheetTitle Sheet Ledger")
                val sheetDataRows: MutableList<BalanceSheetDataRowModel> = mutableListOf()
                for (account: MutableMap.MutableEntry<UInt, String> in accounts) {

                    val apiResponse2: Result<MultipleTransactionResponse> =
                        getUserTransactionsForAnAccount(

                            userId = currentUserId,
                            accountId = account.key,
                            isNotFromBalanceSheet = false,
                            isDevelopmentMode = isDevelopmentMode
                        )
                    if (apiResponse2.isFailure) {

                        if (isNotApiCall) {
                            println("Error : ${(apiResponse2.exceptionOrNull() as Exception).localizedMessage}")
//                            do {
//                                print("Retry (Y/N) ? : ")
//                                val input: String = readLine()!!
//                                when (input) {
//                                    "Y", "" -> {
//                                    }
//
//                                    "N" -> {}
//                                    else -> invalidOptionMessage()
//                                }
//                            } while (input != "N")
                        } else {

                            print(
                                Json.encodeToString(
                                    serializer = CommonDataModel.serializer(Unit.serializer()),
                                    value = CommonDataModel(
                                        status = 1,
                                        error = "Error : ${(apiResponse2.exceptionOrNull() as Exception).localizedMessage}"
                                    )
                                )
                            )
                        }
                    } else {

                        val userMultipleTransactionResponseResult: MultipleTransactionResponse =
                            apiResponse2.getOrNull()!!
                        if (userMultipleTransactionResponseResult.status == 0u) {

                            var currentBalance = 0.0F
                            userMultipleTransactionResponseResult.transactions.forEach { currentTransaction: TransactionResponse ->
                                if (currentTransaction.fromAccountId == account.key) {

                                    currentBalance -= currentTransaction.amount

                                } else {

                                    currentBalance += currentTransaction.amount
                                }
                            }
                            if (currentBalance != 0.0F) {

                                //TODO : Print Ledger
                                menuItems.add(element = "\n${account.key} : ${account.value} : $currentBalance")
                                sheetDataRows.add(
                                    element =
                                    BalanceSheetDataRowModel(
                                        accountId = account.key,
                                        accountName = account.value,
                                        accountBalance = currentBalance
                                    )
                                )
                            }
                        } else {

                            if (isNotApiCall) {

                                println("Server Execution Error...")

                            } else {
                                print(
                                    Json.encodeToString(
                                        serializer = CommonDataModel.serializer(Unit.serializer()),
                                        value = CommonDataModel(
                                            status = 1,
                                            error = "Server Execution Error, Execution Status is ${userMultipleTransactionResponseResult.status}"
                                        )
                                    )
                                )
                            }
                        }
                    }
                }

                //TODO : print Balance Sheet on Console
                if (isNotApiCall) {
                    println(menuItems)
                } else {
                    print(
                        Json.encodeToString(
                            serializer = CommonDataModel.serializer(BalanceSheetDataRowModel.serializer()),
                            value = CommonDataModel(
                                status = 0,
                                data = sheetDataRows
                            )
                        )
                    )
                }
//                menuItems = menuItems + listOf("0 to Back Enter to Continue: ")
//                var choice2: String
//                do {
//                    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
//                        menuItems
//                    )
//                    choice2 = readLine()!!
//                    when (choice2) {
//                        "0" -> {}
//                        "" -> {
//                            break
//                        }
//
//                        else -> invalidOptionMessage()
//                    }
//                } while (choice2 != "0")
            }
        }
    } else {

        if (isNotApiCall) {

            println("Error : ${specifiedDate.data!!}")

        } else {

            print(
                Json.encodeToString(

                    serializer = CommonDataModel.serializer(Unit.serializer()),
                    value = CommonDataModel(
                        status = 1,
                        error = "Error : ${specifiedDate.data!!}"
                    )
                )
            )
        }
    }
}

private fun getDesiredAccountIdsForBalanceSheetOfUser(

    refineLevel: BalanceSheetRefineLevelEnum,
    selectUserTransactionsAfterSpecifiedDateResult: MultipleTransactionResponse

): MutableMap<UInt, String> {

    var accountsToExclude: List<String> = emptyList()
    App.dotEnv = App.reloadDotEnv()
    if (refineLevel != BalanceSheetRefineLevelEnum.ALL) {

        when (refineLevel) {

            BalanceSheetRefineLevelEnum.WITHOUT_OPEN_BALANCES -> {

                // TODO : Change to new api methods
                accountsToExclude = (App.dotEnv["OPEN_BALANCE_ACCOUNT_IDS"] ?: "0").split(',')
            }

            BalanceSheetRefineLevelEnum.WITHOUT_MISC_INCOMES -> {

                accountsToExclude = (App.dotEnv["OPEN_BALANCE_ACCOUNT_IDS"]
                    ?: "0").split(',') + (App.dotEnv["MISC_INCOME_ACCOUNT_IDS"]
                    ?: "0").split(',')
            }

            BalanceSheetRefineLevelEnum.WITHOUT_INVESTMENT_RETURNS -> {

                accountsToExclude =
                    (App.dotEnv["OPEN_BALANCE_ACCOUNT_IDS"]
                        ?: "0").split(',') + (App.dotEnv["MISC_INCOME_ACCOUNT_IDS"]
                        ?: "0").split(',') + (App.dotEnv["INVESTMENT_RETURNS_ACCOUNT_IDS"]
                        ?: "0").split(',')
            }

            BalanceSheetRefineLevelEnum.WITHOUT_FAMILY_ACCOUNTS -> {

                accountsToExclude =
                    (App.dotEnv["OPEN_BALANCE_ACCOUNT_IDS"]
                        ?: "0").split(',') + (App.dotEnv["MISC_INCOME_ACCOUNT_IDS"]
                        ?: "0").split(',') + (App.dotEnv["INVESTMENT_RETURNS_ACCOUNT_IDS"]
                        ?: "0").split(',') + (App.dotEnv["FAMILY_ACCOUNT_IDS"]
                        ?: "0").split(',')
            }

            BalanceSheetRefineLevelEnum.WITHOUT_EXPENSE_ACCOUNTS -> {

                accountsToExclude =
                    (App.dotEnv["OPEN_BALANCE_ACCOUNT_IDS"]
                        ?: "0").split(',') + (App.dotEnv["MISC_INCOME_ACCOUNT_IDS"]
                        ?: "0").split(',') + (App.dotEnv["INVESTMENT_RETURNS_ACCOUNT_IDS"]
                        ?: "0").split(',') + (App.dotEnv["FAMILY_ACCOUNT_IDS"]
                        ?: "0").split(',') + (App.dotEnv["EXPENSE_ACCOUNT_IDS"]
                        ?: "0").split(',')
            }
            //TODO : Report this
            else -> {}
        }
    }
    val accounts: MutableMap<UInt, String> = mutableMapOf()
    selectUserTransactionsAfterSpecifiedDateResult.transactions.forEach { transaction: TransactionResponse ->

        if (!accountsToExclude.contains(transaction.fromAccountId.toString())) {

            accounts.putIfAbsent(transaction.fromAccountId, transaction.fromAccountFullName)
        }

        if (!accountsToExclude.contains(transaction.toAccountId.toString())) {

            accounts.putIfAbsent(transaction.toAccountId, transaction.toAccountFullName)
        }
    }

//    println("Affected A/Cs : $accounts")
    return accounts
}

