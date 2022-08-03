package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.TransactionResponse
import accountLedgerCli.api.response.TransactionsResponse
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.constants.Constants
import accountLedgerCli.enums.FunctionCallSourceEnum
import accountLedgerCli.enums.TransactionTypeEnum
import accountLedgerCli.models.InsertTransactionResult
import accountLedgerCli.models.ViewTransactionsOutput
import accountLedgerCli.to_models.IsOkModel
import accountLedgerCli.to_utils.MysqlUtils
import accountLedgerCli.to_utils.ToDoUtils
import accountLedgerCli.to_utils.invalidOptionMessage
import accountLedgerCli.utils.AccountUtils
import accountLedgerCli.utils.ApiUtils
import accountLedgerCli.utils.TransactionUtils

internal fun viewTransactions(

    userId: UInt,
    username: String,
    accountId: UInt,
    accountFullName: String,
    functionCallSource: FunctionCallSourceEnum = FunctionCallSourceEnum.FROM_OTHERS,
    fromAccount: AccountResponse,
    viaAccount: AccountResponse,
    toAccount: AccountResponse,
    dateTimeInText: String,
    transactionParticulars: String,
    transactionAmount: Float,

    ): ViewTransactionsOutput {

    val apiResponse: Result<TransactionsResponse> = getUserTransactions(

        userId = userId,
        accountId = accountId
    )
    if (apiResponse.isFailure) {

        return ViewTransactionsOutput(

            output = "E",
            addTransactionResult = InsertTransactionResult(
                isSuccess = false,
                dateTimeInText = dateTimeInText,
                transactionParticulars = transactionParticulars,
                transactionAmount = transactionAmount
            )
        )
    } else {

        val userTransactionsResponse: TransactionsResponse = apiResponse.getOrNull()!!
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

            val userTransactionsMap: LinkedHashMap<UInt, TransactionResponse> =
                TransactionUtils.prepareUserTransactionsMap(transactions = userTransactionsResponse.transactions)

            do {
                val userTransactionsText: String = TransactionUtils.userTransactionsToTextFromMap(

                    transactionsMap = userTransactionsMap,
                    currentAccountId = fromAccount.id
                )

                var menuItems: List<String> = listOf(

                    "\nUser : $username",
                    "$accountFullName - Transactions",
                    userTransactionsText
                )
                when (functionCallSource) {

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
                            "2 - Delete Transactions - From Index to Index",
                            "3 - Delete Transaction - By Search",
                            "4 - Edit Transaction - By Index Number",
                            "5 - Edit Transaction - By Search",
                            "6 - Add Transaction",
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

                    "1" -> {

                        if (isCallNotFromCheckAccounts(

                                functionCallSource = functionCallSource,
                                furtherActionsOnFalse = { invalidOptionMessage() })
                        ) {

                            val transactionIndex: UInt = getValidIndex(

                                map = userTransactionsMap,
                                itemSpecification = Constants.transactionText,
                                items = userTransactionsText
                            )

                            // TODO : Take Confirmation from the user
                            if (InsertOperations.deleteTransaction(transactionId = transactionIndex)) {

                                userTransactionsMap.remove(key = transactionIndex)
                            }
                        }
                    }

                    "2" -> {

                        if (isCallNotFromCheckAccounts(

                                functionCallSource = functionCallSource,
                                furtherActionsOnFalse = { invalidOptionMessage() })
                        ) {

                            val transactionStartIndex: UInt = getValidIndex(

                                map = userTransactionsMap,
                                itemSpecification = Constants.transactionText,
                                items = userTransactionsText,
                                itemSpecificationPrefix = "Start "
                            )

                            if (transactionStartIndex != 0u) {

                                val reducedUserTransactionsMap: Map<UInt, TransactionResponse> =
                                    userTransactionsMap.filterKeys { transactionId: UInt -> transactionId > transactionStartIndex }

                                val transactionEndIndex: UInt = getValidIndex(

                                    map = reducedUserTransactionsMap,
                                    itemSpecification = Constants.transactionText,
                                    items = TransactionUtils.userTransactionsToTextFromMap(

                                        transactionsMap = reducedUserTransactionsMap,
                                        currentAccountId = fromAccount.id
                                    ),
                                    itemSpecificationPrefix = "End "
                                )

                                if (transactionEndIndex != 0u) {

                                    userTransactionsMap.filterKeys { transactionId: UInt ->
                                        transactionId in transactionStartIndex..transactionEndIndex
                                    }
                                        .forEach { transactionMapEntryForDelete: Map.Entry<UInt, TransactionResponse> ->

                                            if (InsertOperations.deleteTransaction(transactionId = transactionMapEntryForDelete.key)) {

                                                userTransactionsMap.remove(key = transactionMapEntryForDelete.key)

                                            } else {

                                                // TODO : Continue with confirmation
                                                return@forEach
                                            }
                                        }
                                }
                            }
                        }
                    }

                    "3", "5" -> {

                        if (isCallNotFromCheckAccounts(

                                functionCallSource = functionCallSource,
                                furtherActionsOnFalse = { invalidOptionMessage() })
                        ) {

                            ToDoUtils.showTodo()
                        }
                    }

                    "4" -> {

                        if (isCallNotFromCheckAccounts(

                                functionCallSource = functionCallSource,
                                furtherActionsOnFalse = { invalidOptionMessage() })
                        ) {

                            val transactionIndex: UInt = getValidIndex(

                                map = userTransactionsMap,
                                itemSpecification = Constants.transactionText,
                                items = userTransactionsText
                            )
                            val userAccountsMap: LinkedHashMap<UInt, AccountResponse> =
                                AccountUtils.prepareUserAccountsMap(

                                    accounts = ApiUtils.getAccountsFull(userId = userId).getOrNull()!!.accounts
                                )

                            val selectedTransaction: TransactionResponse = userTransactionsMap[transactionIndex]!!
                            val selectedTransactionDateTimeConversionResult: IsOkModel<String> =

                                MysqlUtils.dateTimeTextConversionWithMessage(dateTimeTextConversionFunction = fun(): IsOkModel<String> {

                                    return MysqlUtils.mySqlDateTimeTextToNormalDateTimeText(mySqlDateTimeText = selectedTransaction.event_date_time)
                                })
                            if (selectedTransactionDateTimeConversionResult.isOK) {

                                val updateTransactionResult: InsertTransactionResult =
                                    InsertOperations.addTransactionStep2(

                                        userId = userId,
                                        username = username,
                                        transactionType = TransactionTypeEnum.NORMAL,
                                        fromAccount = userAccountsMap[selectedTransaction.from_account_id]!!,
                                        viaAccount = viaAccount,
                                        toAccount = userAccountsMap[selectedTransaction.to_account_id]!!,
                                        transactionId = transactionIndex,
                                        dateTimeInText = selectedTransactionDateTimeConversionResult.data!!,
                                        transactionParticulars = selectedTransaction.particulars,
                                        transactionAmount = selectedTransaction.amount,
                                        isEditStep = true
                                    )

                                userTransactionsMap[transactionIndex]!!.event_date_time =
                                    updateTransactionResult.dateTimeInText
                                userTransactionsMap[transactionIndex]!!.particulars =
                                    updateTransactionResult.transactionParticulars
                                userTransactionsMap[transactionIndex]!!.amount =
                                    updateTransactionResult.transactionAmount
                            }
                        }
                    }

                    "6" -> {

                        if (isCallNotFromCheckAccounts(

                                functionCallSource = functionCallSource,
                                furtherActionsOnFalse = { invalidOptionMessage() })
                        ) {

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

                        if (isCallFromCheckAccounts(

                                functionCallSource = functionCallSource,
                                furtherActionsOnFalse = { invalidOptionMessage() }
                            )
                        ) {

                            return ViewTransactionsOutput(

                                output = "",
                                addTransactionResult = addTransactionResult
                            )
                        }
                    }

                    else -> invalidOptionMessage()
                }
            } while (true)
        }
    }
}

private fun isCallNotFromCheckAccounts(

    functionCallSource: FunctionCallSourceEnum,
    furtherActionsOnTrue: () -> Unit = fun() {},
    furtherActionsOnFalse: () -> Unit = fun() {}

): Boolean {

    return !isCallFromCheckAccounts(
        functionCallSource = functionCallSource,
        furtherActionsOnTrue = furtherActionsOnFalse,
        furtherActionsOnFalse = furtherActionsOnTrue
    )
}

private fun isCallFromCheckAccounts(

    functionCallSource: FunctionCallSourceEnum,
    furtherActionsOnTrue: () -> Unit = fun() {},
    furtherActionsOnFalse: () -> Unit = fun() {}

): Boolean {

    if (functionCallSource == FunctionCallSourceEnum.FROM_CHECK_ACCOUNTS) {

        furtherActionsOnTrue.invoke()
        return true
    }
    furtherActionsOnFalse.invoke()
    return false
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
                        functionCallSource = FunctionCallSourceEnum.FROM_VIEW_TRANSACTIONS_OF_ACCOUNT,
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