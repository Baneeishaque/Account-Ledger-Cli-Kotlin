package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.TransactionsResponse
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.constants.Constants
import accountLedgerCli.enums.FunctionCallSourceEnum
import accountLedgerCli.enums.TransactionTypeEnum
import accountLedgerCli.models.InsertTransactionResult
import accountLedgerCli.models.ViewTransactionsOutput
import accountLedgerCli.retrofit.ResponseHolder
import accountLedgerCli.to_models.IsOkModel
import accountLedgerCli.to_utils.ToDoUtils
import accountLedgerCli.utils.AccountUtils
import accountLedgerCli.utils.ApiUtils
import accountLedgerCli.utils.TransactionUtils

internal fun viewTransactions(

    userId: UInt,
    username: String,
    accountId: UInt,
    accountFullName: String,
    functionCallSourceEnum: FunctionCallSourceEnum = FunctionCallSourceEnum.FROM_OTHERS,
    fromAccount: AccountResponse,
    viaAccount: AccountResponse,
    toAccount: AccountResponse,
    dateTimeInText: String,
    transactionParticulars: String,
    transactionAmount: Float

): ViewTransactionsOutput {

    val apiResponse: ResponseHolder<TransactionsResponse> = getUserTransactions(userId = userId, accountId = accountId)
    if (apiResponse.isError()) {

        println("Error : ${(apiResponse.getValue() as Exception).localizedMessage}")
        do {
            print("Retry (Y/N) ? : ")
            when (readLine()!!) {
                "Y", "" -> {
                    return viewTransactions(
                        userId = userId,
                        username = username,
                        accountId = accountId,
                        accountFullName = accountFullName,
                        functionCallSourceEnum = functionCallSourceEnum,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = dateTimeInText,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount
                    )
                }

                "N" -> {
                    return ViewTransactionsOutput(
                        output = "E",
                        addTransactionResult = InsertTransactionResult(
                            isSuccess = false,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount
                        )
                    )
                }

                else -> invalidOptionMessage()
            }
        } while (true)

    } else {

        val userTransactionsResponse: TransactionsResponse = apiResponse.getValue() as TransactionsResponse
        if (userTransactionsResponse.status == 1u) {

            println("Account - $accountFullName")
            println("No Transactions...")
            return ViewTransactionsOutput(
                output = "0",
                addTransactionResult = InsertTransactionResult(
                    isSuccess = false,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )
            )


        } else {

            var choice: String
            do {
                var menuItems: List<String> = listOf(
                    "\nUser : $username",
                    "$accountFullName - Transactions",
                    TransactionUtils.userTransactionsToStringFromList(
                        transactions = userTransactionsResponse.transactions,
                        currentAccountId = accountId
                    )
                )
                when (functionCallSourceEnum) {
                    FunctionCallSourceEnum.FROM_CHECK_ACCOUNTS -> {
                        menuItems = menuItems + listOf("0 to Back Enter to Continue : ")
                    }

                    FunctionCallSourceEnum.FROM_VIEW_TRANSACTIONS_OF_ACCOUNT -> {

                        commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(menuItems)
                        return ViewTransactionsOutput(
                            output = "",
                            addTransactionResult = InsertTransactionResult(
                                isSuccess = false,
                                dateTimeInText = dateTimeInText,
                                transactionParticulars = transactionParticulars,
                                transactionAmount = transactionAmount
                            )
                        )
                    }

                    else -> {
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
                }
                commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(menuItems)

                choice = readLine()!!
                var addTransactionResult = InsertTransactionResult(
                    isSuccess = false,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )
                when (choice) {

                    "1", "2", "4" -> {

                        if (functionCallSourceEnum == FunctionCallSourceEnum.FROM_CHECK_ACCOUNTS) {

                            invalidOptionMessage()

                        } else {

                            ToDoUtils.showTodo()
                        }
                    }

                    "3" -> {
                        val transactionIndex: UInt = getValidIndex(
                            map = TransactionUtils.prepareUserTransactionsMap(transactions = userTransactionsResponse.transactions),
                            itemSpecification = Constants.transactionText,
                            items = TransactionUtils.userTransactionsToStringFromList(
                                transactions = userTransactionsResponse.transactions,
                                currentAccountId = fromAccount.id
                            )
                        )
                        val userAccountsMap: LinkedHashMap<UInt, AccountResponse> = AccountUtils.prepareUserAccountsMap(
                            accounts = ApiUtils.getAccountsFull(userId = userId).getOrNull()!!.accounts
                        )
                        addTransactionResult = InsertOperations.addTransactionStep2(
                            userId = userId,
                            username = username,
                            transactionType = TransactionTypeEnum.NORMAL,
                            fromAccount = userAccountsMap[userTransactionsResponse.transactions[transactionIndex.toInt()].from_account_id]!!,
                            viaAccount = viaAccount,
                            toAccount = toAccount,
                            transactionId = transactionIndex,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount,
                            isEditStep = true
                        )
                    }

                    "5" -> {

                        if (functionCallSourceEnum == FunctionCallSourceEnum.FROM_CHECK_ACCOUNTS) {

                            invalidOptionMessage()

                        } else {

                            addTransactionResult = InsertOperations.addTransaction(

                                userId = userId,
                                username = username,
                                transactionType = TransactionTypeEnum.NORMAL,
                                fromAccount = fromAccount,
                                viaAccount = viaAccount,
                                toAccount = toAccount,
                                dateTimeInText = addTransactionResult.dateTimeInText,
                                transactionParticulars = addTransactionResult.transactionParticulars,
                                transactionAmount = addTransactionResult.transactionAmount
                            )
                        }
                    }

                    "0" -> {
                        return ViewTransactionsOutput(
                            output = "0",
                            addTransactionResult = addTransactionResult
                        )

                    }

                    "" -> {
                        if (functionCallSourceEnum == FunctionCallSourceEnum.FROM_CHECK_ACCOUNTS) {

                            return ViewTransactionsOutput(
                                output = "",
                                addTransactionResult = addTransactionResult
                            )
                        } else {
                            invalidOptionMessage()
                        }
                    }

                    else -> invalidOptionMessage()
                }
            } while (true)
        }
    }
}

internal fun viewTransactionsOfSpecificAccount(

    userId: UInt,
    username: String,
    fromAccount: AccountResponse,
    viaAccount: AccountResponse,
    toAccount: AccountResponse,
    dateTimeInText: String,
    transactionParticulars: String,
    transactionAmount: Float

) {
    print("Enter Account Index or 0 to Back : A")
    val inputAccountIndex: String = readLine()!!
    if (inputAccountIndex != "0") {

        val getUserAccountsMapResult: IsOkModel<LinkedHashMap<UInt, AccountResponse>> =
            HandleResponses.getUserAccountsMap(apiResponse = ApiUtils.getAccountsFull(userId = userId))

        HandleResponses.isOkModelHandler(

            isOkModel = getUserAccountsMapResult,
            data = Unit,
            actionsAfterGetSuccess = fun() {

                val accountIndex: UInt = getValidIndex(

                    map = getUserAccountsMapResult.data!!,
                    itemSpecification = Constants.accountText,
                    items = AccountUtils.userAccountsToStringFromLinkedHashMap(userAccountsMap = getUserAccountsMapResult.data),
                )
                if (accountIndex != 0u) {
                    viewTransactions(
                        userId = userId,
                        username = username,
                        accountId = accountIndex,
                        accountFullName = getUserAccountsMapResult.data[accountIndex]!!.fullName,
                        functionCallSourceEnum = FunctionCallSourceEnum.FROM_VIEW_TRANSACTIONS_OF_ACCOUNT,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = dateTimeInText,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount
                    )
                }
            })
    }
}