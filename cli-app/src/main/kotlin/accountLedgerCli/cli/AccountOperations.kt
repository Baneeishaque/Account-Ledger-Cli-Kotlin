package accountLedgerCli.cli

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.api.response.AccountsResponse
import account.ledger.library.api.response.TransactionResponse
import account.ledger.library.api.response.TransactionsResponse
import account.ledger.library.operations.getAccounts
import account.ledger.library.enums.FunctionCallSourceEnum
import account.ledger.library.models.InsertTransactionResult
import account.ledger.library.models.ViewTransactionsOutput
import account.ledger.library.retrofit.data.TransactionsDataSource
import account.ledger.library.utils.ApiUtils
import account.ledger.library.utils.TransactionUtils
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.utils.AccountUtils
import common.utils.library.models.IsOkModel
import common.utils.library.utils.MysqlUtils
import common.utils.library.utils.invalidOptionMessage
import common.utils.library.constants.Constants as CommonConstants
import kotlinx.coroutines.runBlocking

internal fun checkAffectedAccountsAfterSpecifiedDate(

    desiredDate: String,
    userId: UInt,
    username: String,
    insertTransactionResult: InsertTransactionResult,
    isUpToTimeStamp: Boolean = false,
    upToTimeStamp: String = "",
    isConsoleMode: Boolean,
    isDevelopmentMode: Boolean

): InsertTransactionResult {

    var localInsertTransactionResult: InsertTransactionResult = insertTransactionResult
    localInsertTransactionResult.isSuccess = false

    println("Contacting Server...")
    val apiResponse: Result<TransactionsResponse>

    val specifiedDate: IsOkModel<String> = MysqlUtils.normalDateTextToMySqlDateText(normalDateText = desiredDate)
    if (specifiedDate.isOK) {

        runBlocking {

            apiResponse = TransactionsDataSource().selectUserTransactionsAfterSpecifiedDate(

                userId = userId,
                specifiedDate = specifiedDate.data!!
            )
        }
        if (isDevelopmentMode) {

            println("Response : $apiResponse")
        }

        // TODO : Update to new API Methods
        if (apiResponse.isFailure) {

            println("Error : ${(apiResponse.exceptionOrNull() as Exception).localizedMessage}")
            do {
                print("Retry (Y/N) ? : ")
                when (readln()) {

                    "Y", "" -> {

                        return checkAffectedAccountsAfterSpecifiedDate(

                            desiredDate = desiredDate,
                            userId = userId,
                            username = username,
                            insertTransactionResult = localInsertTransactionResult,
                            isUpToTimeStamp = isUpToTimeStamp,
                            upToTimeStamp = upToTimeStamp,
                            isConsoleMode = isConsoleMode,
                            isDevelopmentMode = isDevelopmentMode
                        )
                    }

                    "N" -> {

                        break
                    }

                    else -> invalidOptionMessage()
                }
            } while (true)

        } else {

            val selectUserTransactionsAfterSpecifiedDateResult: TransactionsResponse = apiResponse.getOrNull()!!
            if (ApiUtils.isNotNoTransactionsResponseWithMessage(

                    responseStatus = selectUserTransactionsAfterSpecifiedDateResult.status
                )
            ) {

                val accounts: MutableMap<UInt, String> = mutableMapOf()

                val userTransactionsAfterSpecifiedDate: List<TransactionResponse> =
                    TransactionUtils.filterTransactionsForUptoDateTime(
                        isUpToTimeStamp = isUpToTimeStamp,
                        upToTimeStamp = upToTimeStamp,
                        transactions = selectUserTransactionsAfterSpecifiedDateResult.transactions
                    )

                userTransactionsAfterSpecifiedDate.forEach { transaction ->

                    accounts.putIfAbsent(transaction.from_account_id, transaction.from_account_full_name)
                    accounts.putIfAbsent(transaction.to_account_id, transaction.to_account_full_name)
                }
                println("Affected A/Cs${CommonConstants.dashedLineSeparator}\n${accountsMapToText(accountsMap = accounts)}")

                val getAccountsFullResult: Result<AccountsResponse> = ApiUtils.getAccountsFull(

                    userId = userId,
                    isConsoleMode = isConsoleMode,
                    isDevelopmentMode = isDevelopmentMode
                )
                if (getAccountsFullResult.isSuccess) {

                    val userAccountsMap: LinkedHashMap<UInt, AccountResponse> =
                        AccountUtils.prepareUserAccountsMap(accounts = getAccountsFullResult.getOrNull()!!.accounts)

                    for (account: MutableMap.MutableEntry<UInt, String> in accounts) {

                        val selectedAccount: AccountResponse = userAccountsMap[account.key]!!
                        when (TransactionViews.viewTransactionsForAnAccount(

                            userId = userId,
                            username = username,
                            accountId = account.key,
                            accountFullName = account.value,
                            functionCallSource = FunctionCallSourceEnum.FROM_CHECK_ACCOUNTS,
                            insertTransactionResult = insertTransactionResult,
                            fromAccount = selectedAccount,
                            isUpToTimeStamp = isUpToTimeStamp,
                            upToTimeStamp = upToTimeStamp,
                            isConsoleMode = isConsoleMode,
                            isDevelopmentMode = isDevelopmentMode

                        ).output) {

                            "E", "0" -> {

                                break
                            }

                            "V" -> {

                                localInsertTransactionResult = TransactionViews.viewTransactionsForAnAccount(

                                    userId = userId,
                                    username = username,
                                    accountId = account.key,
                                    accountFullName = account.value,
                                    insertTransactionResult = localInsertTransactionResult,
                                    fromAccount = selectedAccount,
                                    isUpToTimeStamp = isUpToTimeStamp,
                                    upToTimeStamp = upToTimeStamp,
                                    isConsoleMode = isConsoleMode,
                                    isDevelopmentMode = isDevelopmentMode

                                ).addTransactionResult
                            }
                        }
                    }
                }
            }
        }
    }
    return localInsertTransactionResult
}

fun accountsMapToText(accountsMap: MutableMap<UInt, String>): String {

    var result = ""
    accountsMap.forEach { account: Map.Entry<UInt, String> -> result += "${account.key} - ${account.value}\n" }
    return result
}

private fun printUserTransactionAfterSpecifiedDate(

    userTransactionsAfterSpecifiedDate: List<TransactionResponse>,
    isDevelopmentMode: Boolean
) {
    if (isDevelopmentMode) {

        println(
            "userTransactionsAfterSpecifiedDate = ${
                TransactionUtils.userTransactionsToTextFromList(

                    transactions = userTransactionsAfterSpecifiedDate,
                    currentAccountId = 0u,
                    isDevelopmentMode = isDevelopmentMode
                )
            }"
        )
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
    transactionAmount: Float,
    isConsoleMode: Boolean,
    isDevelopmentMode: Boolean

): InsertTransactionResult {

    val apiResponse: Result<AccountsResponse> = getAccounts(

        userId = userId,
        parentAccountId = fromAccount.id,
        isConsoleMode = isConsoleMode,
        isDevelopmentMode = isDevelopmentMode
    )

    var processChildAccountScreenInputInsertTransactionResult = InsertTransactionResult(
        isSuccess = false,
        dateTimeInText = dateTimeInText,
        transactionParticulars = transactionParticulars,
        transactionAmount = transactionAmount,
        fromAccount = fromAccount,
        viaAccount = viaAccount,
        toAccount = toAccount
    )

    if (apiResponse.isFailure) {

        println("Error : ${(apiResponse.exceptionOrNull() as Exception).localizedMessage}")
        do {
            print("Retry (Y/N) ? : ")
            when (readln()) {
                "Y", "" -> {
                    return viewChildAccounts(
                        username = username, userId = userId,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = dateTimeInText,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "N" -> {
                    return InsertTransactionResult(
                        isSuccess = false,
                        dateTimeInText = dateTimeInText,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount
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
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount,
                    isConsoleMode = isConsoleMode,
                    isDevelopmentMode = isDevelopmentMode
                )

                val choice: String = processChildAccountScreenInputResult.output
                processChildAccountScreenInputInsertTransactionResult =
                    processChildAccountScreenInputResult.addTransactionResult

            } while (choice != "0")
        }
    }
    return processChildAccountScreenInputInsertTransactionResult
}