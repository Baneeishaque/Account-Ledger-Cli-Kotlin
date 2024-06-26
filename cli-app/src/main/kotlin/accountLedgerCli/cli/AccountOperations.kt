package accountLedgerCli.cli

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.api.response.AccountsResponse
import account.ledger.library.api.response.TransactionResponse
import account.ledger.library.api.response.MultipleTransactionResponse
import account.ledger.library.enums.FunctionCallSourceEnum
import account.ledger.library.models.InsertTransactionResult
import account.ledger.library.models.ViewTransactionsOutput
import account.ledger.library.operations.ServerOperations
import account.ledger.library.retrofit.data.MultipleTransactionDataSource
import account.ledger.library.utils.AccountUtils
import account.ledger.library.utils.ApiUtilsInteractive
import account.ledger.library.utils.TransactionUtils
import account.ledger.library.utils.TransactionUtilsInteractive
import common.utils.library.constants.ConstantsCommon
import common.utils.library.models.IsOkModel
import common.utils.library.utils.ErrorUtilsInteractive
import common.utils.library.utils.MysqlUtils
import io.github.cdimascio.dotenv.Dotenv
import kotlinx.coroutines.runBlocking

internal fun checkAffectedAccountsAfterSpecifiedDate(

    desiredDate: String,
    userId: UInt,
    username: String,
    previousTransactionData: InsertTransactionResult,
    isUpToTimeStamp: Boolean = false,
    upToTimeStamp: String = "",
    isConsoleMode: Boolean,
    isDevelopmentMode: Boolean,
    dotEnv: Dotenv

): InsertTransactionResult {

    var localInsertTransactionResult: InsertTransactionResult = previousTransactionData
    localInsertTransactionResult.isSuccess = false

    println("Contacting Server...")
    val apiResponse: Result<MultipleTransactionResponse>

    val specifiedDate: IsOkModel<String> = MysqlUtils.normalDateTextToMySqlDateText(normalDateText = desiredDate)
    if (specifiedDate.isOK) {

        runBlocking {

            apiResponse = MultipleTransactionDataSource().selectUserTransactionsAfterSpecifiedDate(

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
                            previousTransactionData = localInsertTransactionResult,
                            isUpToTimeStamp = isUpToTimeStamp,
                            upToTimeStamp = upToTimeStamp,
                            isConsoleMode = isConsoleMode,
                            isDevelopmentMode = isDevelopmentMode,
                            dotEnv = dotEnv
                        )
                    }

                    "N" -> {

                        break
                    }

                    else -> ErrorUtilsInteractive.printInvalidOptionMessage()
                }
            } while (true)

        } else {

            val selectUserTransactionsAfterSpecifiedDateResult: MultipleTransactionResponse = apiResponse.getOrNull()!!
            if (ApiUtilsInteractive.isTransactionResponseWithMessage(

                    responseStatus = selectUserTransactionsAfterSpecifiedDateResult.status
                )
            ) {

                val accounts: MutableMap<UInt, String> = mutableMapOf()

                val userTransactionsAfterSpecifiedDate: List<TransactionResponse> =
                    TransactionUtils.filterTransactionsForUpToDateTime(
                        isUpToTimeStamp = isUpToTimeStamp,
                        upToTimeStamp = upToTimeStamp,
                        transactions = selectUserTransactionsAfterSpecifiedDateResult.transactions
                    )

                userTransactionsAfterSpecifiedDate.forEach { transaction ->

                    accounts.putIfAbsent(transaction.fromAccountId, transaction.fromAccountFullName)
                    accounts.putIfAbsent(transaction.toAccountId, transaction.toAccountFullName)
                }
                println("Affected A/Cs${ConstantsCommon.dashedLineSeparator}\n${accountsMapToText(accountsMap = accounts)}")

                val getAccountsFullResult: Result<AccountsResponse> = ApiUtilsInteractive.getAccountsFull(

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
                            previousTransactionData = previousTransactionData,
                            fromAccount = selectedAccount,
                            isUpToTimeStamp = isUpToTimeStamp,
                            upToTimeStamp = upToTimeStamp,
                            isConsoleMode = isConsoleMode,
                            isDevelopmentMode = isDevelopmentMode,
                            dotEnv = dotEnv

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
                                    previousTransactionData = localInsertTransactionResult,
                                    fromAccount = selectedAccount,
                                    isUpToTimeStamp = isUpToTimeStamp,
                                    upToTimeStamp = upToTimeStamp,
                                    isConsoleMode = isConsoleMode,
                                    isDevelopmentMode = isDevelopmentMode,
                                    dotEnv = dotEnv

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

fun printUserTransactionAfterSpecifiedDate(

    userTransactionsAfterSpecifiedDate: List<TransactionResponse>,
    isDevelopmentMode: Boolean
) {
    println(
        "userTransactionsAfterSpecifiedDate = ${
            TransactionUtilsInteractive.userTransactionsToTextFromListForLedger(

                transactions = TransactionUtils.convertTransactionResponseListToTransactionListForLedger(transactions = userTransactionsAfterSpecifiedDate),
                currentAccountId = 0u,
                isDevelopmentMode = isDevelopmentMode
            )
        }"
    )
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
    isDevelopmentMode: Boolean,
    dotEnv: Dotenv

): InsertTransactionResult {

    val apiResponse: Result<AccountsResponse> = ServerOperations.getAccounts(

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
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
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

                else -> ErrorUtilsInteractive.printInvalidOptionMessage()
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
                App.commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
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
                    isDevelopmentMode = isDevelopmentMode,
                    dotEnv = dotEnv
                )

                val choice: String = processChildAccountScreenInputResult.output
                processChildAccountScreenInputInsertTransactionResult =
                    processChildAccountScreenInputResult.addTransactionResult

            } while (choice != "0")
        }
    }
    return processChildAccountScreenInputInsertTransactionResult
}
