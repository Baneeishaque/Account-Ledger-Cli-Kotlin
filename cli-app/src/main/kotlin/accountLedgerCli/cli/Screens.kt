package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.AccountsResponse
import accountLedgerCli.api.response.TransactionsResponse
import accountLedgerCli.retrofit.ResponseHolder
import accountLedgerCli.retrofit.data.TransactionsDataSource
import accountLedgerCli.to_utils.*
import accountLedgerCli.utils.AccountUtils
import accountLedgerCli.utils.ApiUtils
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime

internal fun userScreen(username: String, userId: Int) {

    do {
        commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
            listOf(
                "\nUser : $username",
                "1 - List Accounts : Top Levels",
                "2 - Insert Quick Transaction On : Wallet",
                "3 - Insert Quick Transaction On : Wallet To : $baneeFrequent1AccountName",
                "4 - Insert Quick Transaction On : Wallet To : $baneeFrequent2AccountName",
                "5 - Insert Quick Transaction On : Wallet To : $baneeFrequent3AccountName",
                "6 - Insert Quick Transaction On : Bank : $baneeBankAccountName",
                "7 - Insert Quick Transaction On : Bank : $baneeBankAccountName To : $baneeFrequent1AccountName",
                "8 - Insert Quick Transaction On : Bank : $baneeBankAccountName To : $baneeFrequent2AccountName",
                "9 - Insert Quick Transaction On : Bank : $baneeBankAccountName To : $baneeFrequent3AccountName",
                "10 - Insert Quick Transaction On : $baneeFrequent1AccountName",
                "11 - Insert Quick Transaction On : $baneeFrequent2AccountName",
                "12 - Insert Quick Transaction On : $baneeFrequent3AccountName",
                "13 - List Accounts : Full Names",
                "14 - Import Transactions To : Bank : $baneeBankAccountName From CSV",
                "15 - Import Transactions To : Bank : $baneeBankAccountName From XLX",
                "16 - Check A/Cs affected after a specified date",
                "17 - View Transactions of a specific A/C",
                "0 - Logout",
                "",
                "Enter Your Choice : "
            )
        )
        val choice = readLine()
        when (choice) {
            "1" -> listAccountsTop(username = username, userId = userId)
            "2" -> insertQuickTransactionWallet(userId = userId, username = username)
            "3" -> insertQuickTransactionWalletToFrequent1(userId = userId, username = username)
            "4" -> insertQuickTransactionWalletToFrequent2(userId = userId, username = username)
            "5" -> insertQuickTransactionWalletToFrequent3(userId = userId, username = username)
            "6" -> insertQuickTransactionBank(userId = userId, username = username)
            "7" -> insertQuickTransactionBankToFrequent1(userId = userId, username = username)
            "8" -> insertQuickTransactionBankToFrequent2(userId = userId, username = username)
            "9" -> insertQuickTransactionBankToFrequent3(userId = userId, username = username)
            "10" -> insertQuickTransactionFrequent1(userId = userId, username = username)
            "11" -> insertQuickTransactionFrequent2(userId = userId, username = username)
            "12" -> insertQuickTransactionFrequent3(userId = userId, username = username)
            "13" -> listAccountsFull(username = username, userId = userId)
            "14" -> importBankFromCsv()
            "15" -> importBankFromXlx()
            "16" -> checkAccountsAffectedAfterSpecifiedDate(userId = userId, username = username)
            "17" -> viewTransactionsOfSpecificAccount(userId = userId, username = username)
            "0" -> {}
            else -> invalidOptionMessage()
        }
    } while (choice != "0")
}

private fun checkAccountsAffectedAfterSpecifiedDate(userId: Int, username: String) {

    val inputDate = InputUtils.getValidDateInNormalPattern()
    val transactionsDataSource = TransactionsDataSource()
    println("Contacting Server...")
    val apiResponse: ResponseHolder<TransactionsResponse>
    val specifiedDate = MysqlUtils.normalDateStringToMysqlDateString(normalDateString = inputDate)
    if (specifiedDate.first) {
        runBlocking {
            apiResponse =
                transactionsDataSource.selectUserTransactionsAfterSpecifiedDate(
                    userId = userId,
                    specifiedDate = specifiedDate.second
                )
        }
        // println("Response : $apiResponse")
        if (apiResponse.isError()) {

            println("Error : ${(apiResponse.getValue() as Exception).localizedMessage}")
            do {
                print("Retry (Y/N) ? : ")
                val input = readLine()
                when (input) {
                    "Y", "" -> {
                        checkAccountsAffectedAfterSpecifiedDate(userId = userId, username = username)
                        return
                    }
                    "N" -> {
                    }
                    else -> invalidOptionMessage()
                }
            } while (input != "N")
        } else {

            val selectUserTransactionsAfterSpecifiedDateResult = apiResponse.getValue() as TransactionsResponse
            if (selectUserTransactionsAfterSpecifiedDateResult.status == 1) {

                println("No Transactions...")

            } else {

                val accounts = mutableMapOf<Int, String>()
                selectUserTransactionsAfterSpecifiedDateResult.transactions.forEach { transaction ->

                    accounts.putIfAbsent(transaction.from_account_id, transaction.from_account_full_name)
                    accounts.putIfAbsent(transaction.to_account_id, transaction.to_account_full_name)
                }
                println("Affected A/Cs : $accounts")
                for (account in accounts) {

                    when (viewTransactions(
                        userId = userId,
                        username = username,
                        accountId = account.key,
                        accountFullName = account.value,
                        functionCallSource = FunctionCallSource.FROM_CHECK_ACCOUNTS
                    )) {
                        "E", "0" -> {
                            break
                        }
                    }
                }
            }
        }
    }
}

private fun viewTransactionsOfSpecificAccount(userId: Int, username: String) {
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
                    functionCallSource = FunctionCallSource.FROM_VIEW_TRANSACTIONS_OF_ACCOUNT
                )
            }
        }
    }
}

internal fun accountHome(userId: Int, username: String) {

    do {
        commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
            listOf(
                "\nUser : $username",
                "Account - ${fromAccount.fullName}",
                "1 - View Transactions",
                "2 - Add Transaction",
                "3 - View Child Accounts",
                "4 - Add Via. Transaction",
                "5 - Add Two Way Transaction",
                "0 - Back",
                "",
                "Enter Your Choice : "
            )
        )
        val choiceInput = readLine()
        when (choiceInput) {
            "1" ->
                viewTransactions(
                    userId = userId,
                    username = username,
                    accountId = fromAccount.id,
                    accountFullName = fromAccount.fullName
                )
            "2" -> addTransaction(userId = userId, username = username, transactionType = TransactionType.NORMAL)
            "3" ->
                viewChildAccounts(
                    userId = userId,
                    username = username,
                )
            "4" -> addTransaction(userId = userId, username = username, transactionType = TransactionType.VIA)
            "5" -> addTransaction(userId = userId, username = username, transactionType = TransactionType.TWO_WAY)
            "0" -> {}
            else -> invalidOptionMessage()
        }
    } while (choiceInput != "0")
}

private fun viewTransactions(
    userId: Int,
    username: String,
    accountId: Int,
    accountFullName: String,
    functionCallSource: FunctionCallSource = FunctionCallSource.FROM_OTHERS
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
                        functionCallSource = functionCallSource
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
                if (functionCallSource == FunctionCallSource.FROM_CHECK_ACCOUNTS) {
                    menuItems = menuItems + listOf("0 to Back Enter to Continue : ")
                } else if (functionCallSource == FunctionCallSource.FROM_VIEW_TRANSACTIONS_OF_ACCOUNT) {
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
                        if (functionCallSource == FunctionCallSource.FROM_CHECK_ACCOUNTS) {
                            invalidOptionMessage()
                        } else {
                            ToDoUtils.showTodo()
                        }
                    }
                    "5" -> {
                        if (functionCallSource == FunctionCallSource.FROM_CHECK_ACCOUNTS) {
                            invalidOptionMessage()
                        } else {
                            addTransaction(
                                userId = userId,
                                username = username,
                                transactionType = TransactionType.NORMAL
                            )
                        }
                    }
                    "0" -> {}
                    "" -> {
                        if (functionCallSource == FunctionCallSource.FROM_CHECK_ACCOUNTS) {
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

private fun viewChildAccounts(username: String, userId: Int) {

    val apiResponse = getAccounts(userId = userId, parentAccountId = fromAccount.id)

    if (apiResponse.isError()) {

        println("Error : ${(apiResponse.getValue() as Exception).localizedMessage}")
        do {
            print("Retry (Y/N) ? : ")
            val input = readLine()
            when (input) {
                "Y", "" -> {
                    viewChildAccounts(username = username, userId = userId)
                    return
                }
                "N" -> {
                }
                else -> invalidOptionMessage()
            }
        } while (input != "N")
    } else {

        val accountsResponseResult = apiResponse.getValue() as AccountsResponse
        if (accountsResponseResult.status == 1) {

            println("No Child Accounts...")
        } else {

            val userAccountsMap = LinkedHashMap<Int, AccountResponse>()
            accountsResponseResult.accounts.forEach { currentAccount ->
                userAccountsMap[currentAccount.id] = currentAccount
            }
            do {
                commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                    listOf(
                        "\nUser : $username",
                        "${fromAccount.fullName} - Child Accounts",
                        userAccountsToStringFromList(
                            accounts = accountsResponseResult.accounts
                        ),
                        "1 - Choose Account - By Index Number",
                        "2 - Choose Account - By Search",
                        "3 - Add Child Account",
                        "0 - Back",
                        "",
                        "Enter Your Choice : "
                    )
                )

                val choice = processChildAccountScreenInput(userAccountsMap, userId, username)
            } while (choice != "0")
        }
    }
}
