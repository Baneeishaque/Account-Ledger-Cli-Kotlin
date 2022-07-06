package accountLedgerCli.cli

import accountLedgerCli.api.response.TransactionsResponse
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.cli.App.Companion.userAccountsMap
import accountLedgerCli.to_utils.InputUtils
import accountLedgerCli.to_utils.ToDoUtils
import accountLedgerCli.utils.ApiUtils

internal fun viewTransactions(
    userId: Int,
    username: String,
    accountId: Int,
    accountFullName: String,
    functionCallSourceEnum: FunctionCallSourceEnum = FunctionCallSourceEnum.FROM_OTHERS
): String {

    val apiResponse = getUserTransactions(userId = userId, accountId = accountId)
    if (apiResponse.isError()) {

        println("Error : ${(apiResponse.getValue() as Exception).localizedMessage}")
        do {
            print("Retry (Y/N) ? : ")
            val input = readLine()
            when (input) {
                "Y", "" -> {
                    return viewTransactions(
                        userId = userId,
                        username = username,
                        accountId = accountId,
                        accountFullName = accountFullName,
                        functionCallSourceEnum = functionCallSourceEnum
                    )
                }

                "N" -> {}
                else -> invalidOptionMessage()
            }
        } while (input != "N")
        return "E"
    } else {

        val userTransactionsResponseResult = apiResponse.getValue() as TransactionsResponse
        if (userTransactionsResponseResult.status == 1) {

            println("Account - $accountFullName")
            println("No Transactions...")
            return "0"

        } else {

            var choice = ""
            do {
                var menuItems = listOf(
                    "\nUser : $username",
                    "$accountFullName - Transactions",
                    printAccountLedger(
                        transactions = userTransactionsResponseResult.transactions,
                        currentAccountId = accountId
                    )
                )
                if (functionCallSourceEnum == FunctionCallSourceEnum.FROM_CHECK_ACCOUNTS) {
                    menuItems = menuItems + listOf("0 to Back Enter to Continue : ")
                } else if (functionCallSourceEnum == FunctionCallSourceEnum.FROM_VIEW_TRANSACTIONS_OF_ACCOUNT) {
                    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(menuItems)
                    break
                } else {
                    menuItems = menuItems + listOf(
                        "1 - Delete Transaction - By Index Number",
                        "2 - Delete Transaction - By Search",
                        "3 - Edit Transaction - By Index Number",
                        "4 - Edit Transaction - By Search",
                        "5 - Add Transaction",
                        "0 - Back",
                        "",
                        "Enter Your Choice : "
                    )
                }
                commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(menuItems)

                choice = readLine()!!
                when (choice) {
                    "1", "2", "3", "4" -> {
                        if (functionCallSourceEnum == FunctionCallSourceEnum.FROM_CHECK_ACCOUNTS) {
                            invalidOptionMessage()
                        } else {
                            ToDoUtils.showTodo()
                        }
                    }

                    "5" -> {
                        if (functionCallSourceEnum == FunctionCallSourceEnum.FROM_CHECK_ACCOUNTS) {
                            invalidOptionMessage()
                        } else {
                            addTransaction(
                                userId = userId,
                                username = username,
                                transactionTypeEnum = TransactionTypeEnum.NORMAL
                            )
                        }
                    }

                    "0" -> {}
                    "" -> {
                        if (functionCallSourceEnum == FunctionCallSourceEnum.FROM_CHECK_ACCOUNTS) {
                            break
                        } else {
                            invalidOptionMessage()
                        }
                    }

                    else -> invalidOptionMessage()
                }
            } while (choice != "0")
            return choice
        }
    }
}

internal fun viewTransactionsOfSpecificAccount(userId: Int, username: String) {
    print("Enter Account Index or 0 to Back : A")
    val inputAccountIndex = readLine()!!
    if (inputAccountIndex != "0") {
        var accountIndex = InputUtils.getValidInt(inputAccountIndex, "Invalid Account Index")
        if (handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {
            accountIndex = getValidAccountIndex(userAccountsMap = userAccountsMap, accountId = accountIndex)
            if (accountIndex != 0) {
                viewTransactions(
                    userId = userId,
                    username = username,
                    accountId = accountIndex,
                    accountFullName = userAccountsMap[accountIndex]!!.fullName,
                    functionCallSourceEnum = FunctionCallSourceEnum.FROM_VIEW_TRANSACTIONS_OF_ACCOUNT
                )
            }
        }
    }
}