package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.AccountsResponse
import accountLedgerCli.api.response.TransactionsResponse
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.enums.FunctionCallSourceEnum
import accountLedgerCli.models.InsertTransactionResult
import accountLedgerCli.models.ViewTransactionsOutput
import accountLedgerCli.retrofit.ResponseHolder
import accountLedgerCli.retrofit.data.TransactionsDataSource
import accountLedgerCli.to_models.IsOkModel
import accountLedgerCli.to_utils.InputUtils
import accountLedgerCli.to_utils.MysqlUtils
import accountLedgerCli.to_utils.invalidOptionMessage
import accountLedgerCli.utils.AccountUtils
import kotlinx.coroutines.runBlocking

internal fun checkAccountsAffectedAfterSpecifiedDate(

    userId: UInt,
    username: String,
    fromAccount: AccountResponse,
    viaAccount: AccountResponse,
    toAccount: AccountResponse,
    dateTimeInText: String,
    transactionParticulars: String,
    transactionAmount: Float
) {

    val inputDate: String = InputUtils.getValidDateInNormalPattern()
    val transactionsDataSource = TransactionsDataSource()
    println("Contacting Server...")
    val apiResponse: ResponseHolder<TransactionsResponse>
    val specifiedDate: IsOkModel<String> =
        MysqlUtils.normalDateTextToMysqlDateText(normalDateText = inputDate)
    if (specifiedDate.isOK) {
        runBlocking {
            apiResponse =
                transactionsDataSource.selectUserTransactionsAfterSpecifiedDate(
                    userId = userId,
                    specifiedDate = specifiedDate.data!!
                )
        }
        // println("Response : $apiResponse")
        if (apiResponse.isError()) {

            println("Error : ${(apiResponse.getValue() as Exception).localizedMessage}")
            do {
                print("Retry (Y/N) ? : ")
                when (readLine()!!) {
                    "Y", "" -> {
                        checkAccountsAffectedAfterSpecifiedDate(
                            userId = userId,
                            username = username,
                            fromAccount = fromAccount,
                            viaAccount = viaAccount,
                            toAccount = toAccount,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount
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

            val selectUserTransactionsAfterSpecifiedDateResult: TransactionsResponse =
                apiResponse.getValue() as TransactionsResponse
            if (selectUserTransactionsAfterSpecifiedDateResult.status == 1u) {

                println("No Transactions...")

            } else {

                val accounts: MutableMap<UInt, String> = mutableMapOf()
                selectUserTransactionsAfterSpecifiedDateResult.transactions.forEach { transaction ->

                    accounts.putIfAbsent(transaction.from_account_id, transaction.from_account_full_name)
                    accounts.putIfAbsent(transaction.to_account_id, transaction.to_account_full_name)
                }
                println("Affected A/Cs : $accounts")
                for (account: MutableMap.MutableEntry<UInt, String> in accounts) {

                    when (viewTransactions(

                        userId = userId,
                        username = username,
                        accountId = account.key,
                        accountFullName = account.value,
                        functionCallSource = FunctionCallSourceEnum.FROM_CHECK_ACCOUNTS,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = dateTimeInText,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount

                    ).output) {

                        "E", "0" -> {

                            break
                        }
                    }
                }
            }
        }
    }
}

internal fun viewChildAccounts(

    username: String,
    userId: UInt,
    fromAccount: AccountResponse,
    viaAccount: AccountResponse,
    toAccount: AccountResponse,
    dateTimeInText: String,
    transactionParticulars: String,
    transactionAmount: Float

): InsertTransactionResult {

    val apiResponse: Result<AccountsResponse> = getAccounts(
        userId = userId,
        parentAccountId = fromAccount.id
    )

    var processChildAccountScreenInputInsertTransactionResult = InsertTransactionResult(
        isSuccess = false,
        dateTimeInText = dateTimeInText,
        transactionParticulars = transactionParticulars,
        transactionAmount = transactionAmount
    )

    if (apiResponse.isFailure) {

        println("Error : ${(apiResponse.exceptionOrNull() as Exception).localizedMessage}")
        do {
            print("Retry (Y/N) ? : ")
            when (readLine()!!) {
                "Y", "" -> {
                    return viewChildAccounts(
                        username = username, userId = userId,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = dateTimeInText,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount
                    )
                }

                "N" -> {
                    return InsertTransactionResult(
                        isSuccess = false,
                        dateTimeInText = dateTimeInText,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount
                    )
                }

                else -> invalidOptionMessage()
            }
        } while (true)
    } else {

        val accountsResponseResult: AccountsResponse = apiResponse.getOrNull() as AccountsResponse
        if (accountsResponseResult.status == 1u) {

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
                        AccountUtils.userAccountsToStringFromList(
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

                val processChildAccountScreenInputResult: ViewTransactionsOutput = processChildAccountScreenInput(
                    userAccountsMap = userAccountsMap,
                    userId = userId,
                    username = username,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )

                val choice: String = processChildAccountScreenInputResult.output
                processChildAccountScreenInputInsertTransactionResult =
                    processChildAccountScreenInputResult.addTransactionResult

            } while (choice != "0")
        }
    }
    return processChildAccountScreenInputInsertTransactionResult
}