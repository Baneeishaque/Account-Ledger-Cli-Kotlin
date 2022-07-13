package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.AccountsResponse
import accountLedgerCli.api.response.TransactionsResponse
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.cli.App.Companion.fromAccount
import accountLedgerCli.retrofit.ResponseHolder
import accountLedgerCli.retrofit.data.TransactionsDataSource
import accountLedgerCli.to_utils.InputUtils
import accountLedgerCli.to_utils.MysqlUtils
import kotlinx.coroutines.runBlocking

internal fun checkAccountsAffectedAfterSpecifiedDate(userId: UInt, username: String) {

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
            if (selectUserTransactionsAfterSpecifiedDateResult.status == 1u) {

                println("No Transactions...")

            } else {

                val accounts = mutableMapOf<UInt, String>()
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
                        functionCallSourceEnum = FunctionCallSourceEnum.FROM_CHECK_ACCOUNTS
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

internal fun viewChildAccounts(username: String, userId: UInt) {

    val apiResponse:Result<AccountsResponse> = getAccounts(userId = userId, parentAccountId = fromAccount.id)

    if (apiResponse.isFailure) {

        println("Error : ${(apiResponse.exceptionOrNull() as Exception).localizedMessage}")
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

        val accountsResponseResult = apiResponse.getOrNull() as AccountsResponse
        if (accountsResponseResult.status == 1) {

            println("No Child Accounts...")
        } else {

            val userAccountsMap = LinkedHashMap<UInt, AccountResponse>()
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