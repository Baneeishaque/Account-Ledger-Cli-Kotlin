package accountLedgerCli.cli

import accountLedgerCli.api.response.TransactionResponse
import accountLedgerCli.api.response.TransactionsResponse
import accountLedgerCli.api.response.UserResponse
import accountLedgerCli.cli.App.Companion.dotenv
import accountLedgerCli.constants.Constants
import accountLedgerCli.enums.BalanceSheetRefineLevelEnum
import accountLedgerCli.models.BalanceSheetDataModel
import accountLedgerCli.models.BalanceSheetDataRowModel
import accountLedgerCli.models.ChooseUserResult
import accountLedgerCli.retrofit.data.TransactionsDataSource
import accountLedgerCli.to_models.IsOkModel
import accountLedgerCli.to_utils.DateTimeUtils
import accountLedgerCli.to_utils.MysqlUtils
import accountLedgerCli.to_utils.invalidOptionMessage
import accountLedgerCli.utils.UserUtils
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

internal fun balanceSheetOfUser(usersMap: LinkedHashMap<UInt, UserResponse>, isConsoleMode: Boolean, isDevelopmentMode: Boolean) {

    val chooseUserResult: ChooseUserResult = handleUserSelection(
        chosenUserId = getValidIndexWithInputPrompt(

            map = usersMap,
            itemSpecification = Constants.userText,
            items = UserUtils.usersToStringFromLinkedHashMap(usersMap = usersMap),
            backValue = 0u
        ), usersMap = usersMap
    )
    if (chooseUserResult.isChosen) {

        printBalanceSheetOfUser(

            currentUserName = chooseUserResult.chosenUser!!.username,
            currentUserId = chooseUserResult.chosenUser.id,
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

    if(isConsoleMode) {

        print("currentUser : $currentUserName")
    }
    val transactionsDataSource = TransactionsDataSource()
    if (isNotApiCall) {

        println("Contacting Server...")
    }
    val apiResponse: Result<TransactionsResponse>
    val specifiedDate: IsOkModel<String> = MysqlUtils.normalDateTextToMySqlDateText(
        normalDateText = getUserInitialTransactionDateFromUsername(username = currentUserName).minusDays(
            1
        ).format(DateTimeUtils.normalDatePattern)
    )
    if (specifiedDate.isOK) {

        runBlocking {

            apiResponse = transactionsDataSource.selectUserTransactionsAfterSpecifiedDate(

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
                    when (readLine()!!) {
                        "Y", "" -> {
                            printBalanceSheetOfUser(
                                currentUserName = currentUserName,
                                currentUserId = currentUserId,
                                refineLevel = refineLevel,
                                isNotApiCall = isNotApiCall,
                                isConsoleMode = isConsoleMode,
                                isDevelopmentMode = isDevelopmentMode
                            )
                            return
                        }

                        "N" -> {
                            return
                        }

                        else -> invalidOptionMessage()
                    }
                } while (true)

            } else {

                print(
                    Json.encodeToString(
                        serializer = BalanceSheetDataModel.serializer(),
                        value = BalanceSheetDataModel(
                            status = 1,
                            error = "Error : ${(apiResponse.exceptionOrNull() as Exception).localizedMessage}"
                        )
                    )
                )
            }
        } else {

            val selectUserTransactionsAfterSpecifiedDateResult: TransactionsResponse = apiResponse.getOrNull()!!
            if (selectUserTransactionsAfterSpecifiedDateResult.status == 1u) {

                if (isNotApiCall) {

                    println("No Transactions...")

                } else {

                    print(
                        Json.encodeToString(

                            serializer = BalanceSheetDataModel.serializer(),
                            value = BalanceSheetDataModel(

                                status = 2,
                                error = "No Transactions"
                            )
                        )
                    )
                }

            } else {

                var accountsToExclude: List<String> = emptyList()
                if (refineLevel != BalanceSheetRefineLevelEnum.ALL) {

                    when (refineLevel) {

                        BalanceSheetRefineLevelEnum.WITHOUT_OPEN_BALANCES -> {
                            // TODO : Change to new api methods
                            accountsToExclude = (dotenv["OPEN_BALANCE_ACCOUNT_IDS"] ?: "0").split(',')
                        }

                        BalanceSheetRefineLevelEnum.WITHOUT_MISC_INCOMES -> {
                            accountsToExclude = (dotenv["OPEN_BALANCE_ACCOUNT_IDS"]
                                ?: "0").split(',') + (dotenv["MISC_INCOME_ACCOUNT_IDS"]
                                ?: "0").split(',')
                        }

                        BalanceSheetRefineLevelEnum.WITHOUT_INVESTMENT_RETURNS -> {
                            accountsToExclude =
                                (dotenv["OPEN_BALANCE_ACCOUNT_IDS"]
                                    ?: "0").split(',') + (dotenv["MISC_INCOME_ACCOUNT_IDS"]
                                    ?: "0").split(',') + (dotenv["INVESTMENT_RETURNS_ACCOUNT_IDS"]
                                    ?: "0").split(',')
                        }

                        BalanceSheetRefineLevelEnum.WITHOUT_FAMILY_ACCOUNTS -> {
                            accountsToExclude =
                                (dotenv["OPEN_BALANCE_ACCOUNT_IDS"]
                                    ?: "0").split(',') + (dotenv["MISC_INCOME_ACCOUNT_IDS"]
                                    ?: "0").split(',') + (dotenv["INVESTMENT_RETURNS_ACCOUNT_IDS"]
                                    ?: "0").split(',') + (dotenv["FAMILY_ACCOUNT_IDS"]
                                    ?: "0").split(',')
                        }

                        BalanceSheetRefineLevelEnum.WITHOUT_EXPENSE_ACCOUNTS -> {
                            accountsToExclude =
                                (dotenv["OPEN_BALANCE_ACCOUNT_IDS"]
                                    ?: "0").split(',') + (dotenv["MISC_INCOME_ACCOUNT_IDS"]
                                    ?: "0").split(',') + (dotenv["INVESTMENT_RETURNS_ACCOUNT_IDS"]
                                    ?: "0").split(',') + (dotenv["FAMILY_ACCOUNT_IDS"]
                                    ?: "0").split(',') + (dotenv["EXPENSE_ACCOUNT_IDS"]
                                    ?: "0").split(',')
                        }
                        //TODO : Report this
                        else -> {}
                    }
                }
                val accounts: MutableMap<UInt, String> = mutableMapOf()
                selectUserTransactionsAfterSpecifiedDateResult.transactions.filterNot { transactionResponse: TransactionResponse ->
                    (accountsToExclude.contains(transactionResponse.from_account_id.toString())) || (accountsToExclude.contains(
                        transactionResponse.to_account_id.toString()
                    ))
                }.forEach { transaction: TransactionResponse ->

                    accounts.putIfAbsent(
                        transaction.from_account_id, transaction.from_account_full_name
                    )
                    accounts.putIfAbsent(
                        transaction.to_account_id, transaction.to_account_full_name
                    )
                }
//                println("Affected A/Cs : $accounts")
                val menuItems: MutableList<String> = mutableListOf("\nUser : $currentUserName Balance Sheet Ledger")
                val balanceSheetDataRows: MutableList<BalanceSheetDataRowModel> = mutableListOf()
                for (account: MutableMap.MutableEntry<UInt, String> in accounts) {

                    val apiResponse2: Result<TransactionsResponse> =
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
                                    serializer = BalanceSheetDataModel.serializer(),
                                    value = BalanceSheetDataModel(
                                        status = 1,
                                        error = "Error : ${(apiResponse2.exceptionOrNull() as Exception).localizedMessage}"
                                    )
                                )
                            )
                        }
                    } else {

                        val userTransactionsResponseResult: TransactionsResponse = apiResponse2.getOrNull()!!
                        if (userTransactionsResponseResult.status == 0u) {

                            var currentBalance = 0.0F
                            userTransactionsResponseResult.transactions.forEach { currentTransaction: TransactionResponse ->
                                if (currentTransaction.from_account_id == account.key) {

                                    currentBalance -= currentTransaction.amount

                                } else {

                                    currentBalance += currentTransaction.amount
                                }
                            }
                            if (currentBalance != 0.0F) {
                                //TODO : Print Ledger
                                menuItems.add(element = "\n${account.key} : ${account.value} : $currentBalance")
                                balanceSheetDataRows.add(
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
                                        serializer = BalanceSheetDataModel.serializer(),
                                        value = BalanceSheetDataModel(
                                            status = 1,
                                            error = "Server Execution Error, Execution Status is ${userTransactionsResponseResult.status}"
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
                            serializer = BalanceSheetDataModel.serializer(),
                            value = BalanceSheetDataModel(
                                status = 0,
                                data = balanceSheetDataRows
                            )
                        )
                    )
                }
//                menuItems = menuItems + listOf("0 to Back Enter to Continue : ")
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

                    serializer = BalanceSheetDataModel.serializer(),
                    value = BalanceSheetDataModel(
                        status = 1,
                        error = "Error : ${specifiedDate.data!!}"
                    )
                )
            )
        }
    }
}