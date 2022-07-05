package accountLedgerCli.cli

import accountLedgerCli.api.response.TransactionResponse
import accountLedgerCli.api.response.TransactionsResponse
import accountLedgerCli.api.response.UserResponse
import accountLedgerCli.retrofit.ResponseHolder
import accountLedgerCli.retrofit.data.TransactionsDataSource
import accountLedgerCli.to_utils.DateTimeUtils
import accountLedgerCli.to_utils.MysqlUtils
import kotlinx.coroutines.runBlocking

internal fun balanceSheetOfUser(usersMap: LinkedHashMap<Int, UserResponse>) {
    if (handleUserSelection(
            chosenUserId = chooseUserByIndex(usersMap = usersMap), usersMap = usersMap
        )
    ) {
        printBalanceSheetOfUser(currentUserName = chosenUser.username, currentUserId = chosenUser.id)
    }
}

internal fun printBalanceSheetOfUser(
    currentUserName: String,
    currentUserId: Int,
    mode: BalanceSheetRefineLevel = BalanceSheetRefineLevel.WITHOUT_EXPENSE_ACCOUNTS
) {

//    print("currentUser : $currentUserName")
    val transactionsDataSource = TransactionsDataSource()
    println("Contacting Server...")
    val apiResponse2: ResponseHolder<TransactionsResponse>
    val specifiedDate: Pair<Boolean, String> = MysqlUtils.normalDateStringToMysqlDateString(
        normalDateString = getUserInitialTransactionDateFromUsername(username = currentUserName).minusDays(
            1
        ).format(DateTimeUtils.normalDatePattern)
    )
    if (specifiedDate.first) {
        runBlocking {
            apiResponse2 = transactionsDataSource.selectUserTransactionsAfterSpecifiedDate(
                userId = currentUserId, specifiedDate = specifiedDate.second
            )
        }
        // println("Response : $apiResponse2")
        if (apiResponse2.isError()) {

            println("Error : ${(apiResponse2.getValue() as Exception).localizedMessage}")
            do {
                print("Retry (Y/N) ? : ")
                val input: String? = readLine()
                when (input) {
                    "Y", "" -> {
                        return
                    }

                    "N" -> {
                    }

                    else -> invalidOptionMessage()
                }
            } while (input != "N")
        } else {

            val selectUserTransactionsAfterSpecifiedDateResult: TransactionsResponse =
                apiResponse2.getValue() as TransactionsResponse
            if (selectUserTransactionsAfterSpecifiedDateResult.status == 1) {

                println("No Transactions...")

            } else {

                var accountsToExclude: List<String> = emptyList()
                if (mode != BalanceSheetRefineLevel.ALL) {

                    when (mode) {

                        BalanceSheetRefineLevel.WITHOUT_OPEN_BALANCES -> {
                            accountsToExclude = (UserOperations.dotenv["OPEN_BALANCE_ACCOUNT_IDS"] ?: "0").split(',')
                        }

                        BalanceSheetRefineLevel.WITHOUT_MISC_INCOMES -> {
                            accountsToExclude = (UserOperations.dotenv["OPEN_BALANCE_ACCOUNT_IDS"]
                                ?: "0").split(',') + (UserOperations.dotenv["MISC_INCOME_ACCOUNT_IDS"]
                                ?: "0").split(',')
                        }

                        BalanceSheetRefineLevel.WITHOUT_INVESTMENT_RETURNS -> {
                            accountsToExclude =
                                (UserOperations.dotenv["OPEN_BALANCE_ACCOUNT_IDS"]
                                    ?: "0").split(',') + (UserOperations.dotenv["MISC_INCOME_ACCOUNT_IDS"]
                                    ?: "0").split(',') + (UserOperations.dotenv["INVESTMENT_RETURNS_ACCOUNT_IDS"]
                                    ?: "0").split(',')
                        }

                        BalanceSheetRefineLevel.WITHOUT_FAMILY_ACCOUNTS -> {
                            accountsToExclude =
                                (UserOperations.dotenv["OPEN_BALANCE_ACCOUNT_IDS"]
                                    ?: "0").split(',') + (UserOperations.dotenv["MISC_INCOME_ACCOUNT_IDS"]
                                    ?: "0").split(',') + (UserOperations.dotenv["INVESTMENT_RETURNS_ACCOUNT_IDS"]
                                    ?: "0").split(',') + (UserOperations.dotenv["FAMILY_ACCOUNT_IDS"]
                                    ?: "0").split(',')
                        }

                        BalanceSheetRefineLevel.WITHOUT_EXPENSE_ACCOUNTS -> {
                            accountsToExclude =
                                (UserOperations.dotenv["OPEN_BALANCE_ACCOUNT_IDS"]
                                    ?: "0").split(',') + (UserOperations.dotenv["MISC_INCOME_ACCOUNT_IDS"]
                                    ?: "0").split(',') + (UserOperations.dotenv["INVESTMENT_RETURNS_ACCOUNT_IDS"]
                                    ?: "0").split(',') + (UserOperations.dotenv["FAMILY_ACCOUNT_IDS"]
                                    ?: "0").split(',') + (UserOperations.dotenv["EXPENSE_ACCOUNT_IDS"]
                                    ?: "0").split(',')
                        }
                        //TODO : Report this
                        else -> {}
                    }
                }
                val accounts: MutableMap<Int, String> = mutableMapOf()
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
                var menuItems: List<String> = listOf("\nUser : $currentUserName Balance Sheet Ledger")
                for (account in accounts) {

                    val apiResponse3: ResponseHolder<TransactionsResponse> =
                        getUserTransactions(
                            userId = currentUserId,
                            accountId = account.key,
                            isNotFromBalanceSheet = false
                        )
                    if (apiResponse3.isError()) {

                        println("Error : ${(apiResponse3.getValue() as Exception).localizedMessage}")
                        do {
                            print("Retry (Y/N) ? : ")
                            val input: String? = readLine()
                            when (input) {
                                "Y", "" -> {
                                }

                                "N" -> {}
                                else -> invalidOptionMessage()
                            }
                        } while (input != "N")
                    } else {

                        val userTransactionsResponseResult: TransactionsResponse =
                            apiResponse3.getValue() as TransactionsResponse
                        if (userTransactionsResponseResult.status == 0) {

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
                                menuItems = menuItems + listOf("\n${account.key} : ${account.value} : $currentBalance")
                            }
                        }
                    }
                }
                //TODO : print Balance Sheet on Console
                println(menuItems)
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
    }
}