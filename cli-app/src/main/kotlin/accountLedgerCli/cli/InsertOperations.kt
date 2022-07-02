package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.InsertionResponse
import accountLedgerCli.retrofit.ResponseHolder
import accountLedgerCli.retrofit.data.TransactionDataSource
import accountLedgerCli.to_utils.DateTimeUtils
import accountLedgerCli.to_utils.InputUtils
import accountLedgerCli.to_utils.MysqlUtils
import accountLedgerCli.to_utils.ToDoUtils
import accountLedgerCli.utils.ApiUtils
import accountLedgerCli.utils.ChooseAccountUtils
import kotlinx.coroutines.runBlocking

internal fun insertQuickTransactionWallet(userId: Int, username: String) {

    if (handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeWalletAccountId]!!
        accountHome(userId = userId, username = username)
    }
}

internal fun insertQuickTransactionWalletToFrequent1(userId: Int, username: String) {

    if (handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeWalletAccountId]!!
        toAccount = userAccountsMap[baneeFrequent1AccountId]!!
        transactionContinueCheck(userId = userId, username = username, transactionType = TransactionType.NORMAL)
    }
}

internal fun insertQuickTransactionWalletToFrequent2(userId: Int, username: String) {

    if (handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeWalletAccountId]!!
        toAccount = userAccountsMap[baneeFrequent2AccountId]!!
        transactionContinueCheck(userId = userId, username = username, transactionType = TransactionType.NORMAL)
    }
}

internal fun insertQuickTransactionWalletToFrequent3(userId: Int, username: String) {

    if (handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeWalletAccountId]!!
        toAccount = userAccountsMap[baneeFrequent3AccountId]!!
        transactionContinueCheck(userId = userId, username = username, transactionType = TransactionType.NORMAL)
    }
}

internal fun insertQuickTransactionBank(userId: Int, username: String) {

    if (handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeBankAccountId]!!
        accountHome(userId = userId, username = username)
    }
}

internal fun insertQuickTransactionBankToFrequent1(userId: Int, username: String) {

    if (handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeBankAccountId]!!
        toAccount = userAccountsMap[baneeFrequent1AccountId]!!
        transactionContinueCheck(userId = userId, username = username, transactionType = TransactionType.NORMAL)
    }
}

internal fun insertQuickTransactionBankToFrequent2(userId: Int, username: String) {

    if (handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeBankAccountId]!!
        toAccount = userAccountsMap[baneeFrequent2AccountId]!!
        transactionContinueCheck(userId = userId, username = username, transactionType = TransactionType.NORMAL)
    }
}

internal fun insertQuickTransactionFrequent1(userId: Int, username: String) {

    if (handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeFrequent1AccountId]!!
        accountHome(userId = userId, username = username)
    }
}

internal fun insertQuickTransactionBankToFrequent3(userId: Int, username: String) {

    if (handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeBankAccountId]!!
        toAccount = userAccountsMap[baneeFrequent3AccountId]!!
        transactionContinueCheck(userId = userId, username = username, transactionType = TransactionType.NORMAL)
    }
}

internal fun insertQuickTransactionFrequent2(userId: Int, username: String) {

    if (handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeFrequent2AccountId]!!
        accountHome(userId = userId, username = username)
    }
}

internal fun insertQuickTransactionFrequent3(userId: Int, username: String) {

    if (handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeFrequent3AccountId]!!
        accountHome(userId = userId, username = username)
    }
}

internal fun addAccount() {

    // Use all accounts for general account addition, or from account for child account addition
    ToDoUtils.showTodo()
}

internal fun addTransaction(userId: Int, username: String, transactionType: TransactionType) {

    do {
        var menuItems = listOf(
            "\nUser : $username",
            "Transaction Type : $transactionType",
            "From Account - ${fromAccount.id} : ${fromAccount.fullName}"
        )
        if (transactionType == TransactionType.VIA) {
            menuItems = menuItems + listOf(
                "Via. Account - ${viaAccount.id} : ${viaAccount.fullName}"
            )
        }
        menuItems = menuItems + listOf(
            "To Account - ${toAccount.id} : ${toAccount.fullName}",
            "1 - Choose To Account From List - Top Levels",
            "2 - Choose To Account From List - Full Names",
            "3 - Input To Account ID Directly",
            "4 - Choose From Account From List - Top Levels",
            "5 - Choose From Account From List - Full Names",
            "6 - Input From Account ID Directly",
            "7 - Continue Transaction"
        )
        if (transactionType == TransactionType.VIA) {
            menuItems = menuItems + listOf(
                "8 - Exchange From & Via. A/Cs",
                "9 - Exchange From & Via. A/Cs, Then Continue Transaction",
                "10 - Exchange Via. & To A/Cs",
                "11 - Exchange Via & To A/Cs, Then Continue Transaction",
                "12 - Exchange From & To. A/Cs",
                "13 - Exchange From & To A/Cs, Then Continue Transaction",
                "14 - Choose Via. Account From List - Top Levels",
                "15 - Choose Via. Account From List - Full Names",
                "16 - Input Via. Account ID Directly"
            )
        } else {
            menuItems = menuItems + listOf(
                "8 - Exchange Accounts",
                "9 - Exchange Accounts, Then Continue Transaction",
            )
        }
        menuItems = menuItems + listOf(
            "0 - Back",
            "",
            "Enter Your Choice : "
        )
        commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
            menuItems
        )
        val choice = readLine()
        when (choice) {
            "1" -> {
                if (chooseDepositTop(userId)) {

                    transactionContinueCheck(userId, username, transactionType)
                    return
                }
            }
            "2" -> {
                if (chooseDepositFull(userId)) {

                    transactionContinueCheck(userId, username, transactionType)
                    return
                }
            }
            "3" -> {

                val chooseAccountResult = ChooseAccountUtils.chooseAccountById(userId)
                if (chooseAccountResult.choosedAccountId != 0) {

                    toAccount = chooseAccountResult.choosedAccount
                    transactionContinueCheck(userId, username, transactionType)
                    return
                }
            }
            "4" -> {
                if (chooseFromAccountTop(userId)) {

                    transactionContinueCheck(userId, username, transactionType)
                    return
                }
            }
            "5" -> {
                if (chooseFromAccountFull(userId)) {

                    transactionContinueCheck(userId, username, transactionType)
                    return
                }
            }
            "6" -> {
                val chooseAccountResult = ChooseAccountUtils.chooseAccountById(userId)
                if (chooseAccountResult.choosedAccountId != 0) {

                    fromAccount = chooseAccountResult.choosedAccount
                    transactionContinueCheck(userId, username, transactionType)
                    return
                }
            }
            "7" -> {

                transactionContinueCheck(userId, username, transactionType)
                return
            }
            "8" -> {

                if (transactionType == TransactionType.VIA) {

                    exchangeFromAndViaAccounts()

                } else {

                    exchangeFromAndToAccounts()
                }
                addTransaction(userId = userId, username = username, transactionType = transactionType)
                return
            }
            "9" -> {

                if (transactionType == TransactionType.VIA) {

                    exchangeFromAndViaAccounts()

                } else {

                    exchangeFromAndToAccounts()
                }
                transactionContinueCheck(userId, username, transactionType)
                return
            }
            "10" -> {
                if (transactionType == TransactionType.VIA) {

                    exchangeToAndViaAccounts()
                    addTransaction(userId = userId, username = username, transactionType = transactionType)
                    return

                } else {
                    invalidOptionMessage()
                }
            }
            "11" -> {
                if (transactionType == TransactionType.VIA) {

                    exchangeToAndViaAccounts()
                    transactionContinueCheck(userId, username, transactionType)
                    return

                } else {
                    invalidOptionMessage()
                }
            }
            "12" -> {
                if (transactionType == TransactionType.VIA) {

                    exchangeFromAndToAccounts()
                    addTransaction(userId = userId, username = username, transactionType = transactionType)
                    return

                } else {
                    invalidOptionMessage()
                }
            }
            "13" -> {
                if (transactionType == TransactionType.VIA) {

                    exchangeFromAndToAccounts()
                    transactionContinueCheck(userId, username, transactionType)
                    return

                } else {
                    invalidOptionMessage()
                }
            }
            "14" -> {
                if (transactionType == TransactionType.VIA) {

                    ToDoUtils.showTodo()
                    return

                } else {
                    invalidOptionMessage()
                }
            }
            "15" -> {
                if (transactionType == TransactionType.VIA) {

                    if (chooseViaAccountFull(userId)) {

                        transactionContinueCheck(userId, username, TransactionType.VIA)
                        return
                    }
                } else {
                    invalidOptionMessage()
                }
            }
            "16" -> {
                if (transactionType == TransactionType.VIA) {

                    val chooseAccountResult = ChooseAccountUtils.chooseAccountById(userId)
                    if (chooseAccountResult.choosedAccountId != 0) {

                        viaAccount = chooseAccountResult.choosedAccount
                        transactionContinueCheck(userId, username, transactionType)
                        return
                    }
                } else {
                    invalidOptionMessage()
                }
            }
            "0" -> {}
            else -> invalidOptionMessage()
        }
    } while (choice != "0")
}

internal fun addTransactionStep2(
    userId: Int,
    username: String,
    localFromAccount: AccountResponse,
    localToAccount: AccountResponse,
    transactionType: TransactionType,
    localViaAccount: AccountResponse,
    isViaStep: Boolean = false,
    isTwoWayStep: Boolean = false
): Boolean {

    var menuItems = listOf(
        "\nUser : $username",
        "Withdraw Account - ${fromAccount.id} : ${fromAccount.fullName}",
    )
    if (transactionType == TransactionType.VIA) {
        menuItems = menuItems + listOf(
            "Intermediate Account - ${viaAccount.id} : ${viaAccount.fullName}",
        )
    }
    menuItems = menuItems + listOf(
        "Deposit Account - ${toAccount.id} : ${toAccount.fullName}",
    )
    if (isViaStep || isTwoWayStep) {

        return invokeAutomatedInsertTransaction(
            userId = userId,
            eventDateTime = dateTimeString,
            particulars = transactionParticulars,
            amount = transactionAmount,
            localFromAccount = localFromAccount,
            localToAccount = localToAccount
        )

    } else {

        menuItems = menuItems + listOf(
            // TODO : Complete back
            "Enter Time : "
        )
        commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(menuItems)
        when (val inputDateTimeString = enterDateWithTime(transactionType = transactionType)) {
            "D+Tr" -> {

                dateTimeString =
                    DateTimeUtils.add1DayWith9ClockTimeToDateTimeString(
                        dateTimeString = dateTimeString
                    )
                return invokeAddTransactionStep2(
                    userId = userId,
                    username = username,
                    localFromAccount = localFromAccount,
                    localToAccount = localToAccount,
                    transactionType = transactionType,
                    localViaAccount = localViaAccount
                )
            }
            "D+" -> {

                dateTimeString = DateTimeUtils.add1DayToDateTimeString(dateTimeString = dateTimeString)
                return invokeAddTransactionStep2(
                    userId = userId,
                    username = username,
                    localFromAccount = localFromAccount,
                    localToAccount = localToAccount,
                    transactionType = transactionType,
                    localViaAccount = localViaAccount
                )
            }
            "D2+Tr" -> {

                dateTimeString =
                    DateTimeUtils.add2DaysWith9ClockTimeToDateTimeString(
                        dateTimeString = dateTimeString
                    )
                return invokeAddTransactionStep2(
                    userId = userId,
                    username = username,
                    localFromAccount = localFromAccount,
                    localToAccount = localToAccount,
                    transactionType = transactionType,
                    localViaAccount = localViaAccount
                )
            }
            "D2+" -> {

                dateTimeString = DateTimeUtils.add2DaysToDateTimeString(dateTimeString = dateTimeString)
                return invokeAddTransactionStep2(
                    userId = userId,
                    username = username,
                    localFromAccount = localFromAccount,
                    localToAccount = localToAccount,
                    transactionType = transactionType,
                    localViaAccount = localViaAccount
                )
            }
            "Ex" -> {

                return ex13(userId, username, localFromAccount, localToAccount, transactionType, localViaAccount)
            }
            "Ex13" -> {

                return ex13(userId, username, localFromAccount, localToAccount, transactionType, localViaAccount)
            }
            "Ex12" -> {

                return ex12(userId, username, localFromAccount, localToAccount, transactionType, localViaAccount)
            }
            "Ex23" -> {

                return ex23(userId, username, localFromAccount, localToAccount, transactionType, localViaAccount)
            }
            "B" -> {

                return false
            }
            else -> {

                dateTimeString = inputDateTimeString

                print("Enter Particulars (Current Value - $transactionParticulars): ")
                // TODO : Back to fields, or complete back
                val transactionParticularsInput = readLine()!!
                if (transactionParticularsInput.isNotEmpty()) {

                    transactionParticulars = transactionParticularsInput
                }

                print("Enter Amount (Current Value - $transactionAmount) : ")
                val transactionAmountInput = readLine()!!
                if (transactionAmountInput.isNotEmpty()) {

                    transactionAmount =
                        InputUtils.getValidFloat(transactionAmountInput, "Invalid Amount : Try Again")
                }

                do {
                    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                        listOf(
                            "\nTime - $dateTimeString",
                            "Account - ${localFromAccount.id} : ${localFromAccount.fullName}",
                            "Deposit Account - ${localToAccount.id} : ${localToAccount.fullName}",
                            "Particulars - $transactionParticulars",
                            "Amount - $transactionAmount",
                            "\nCorrect ? (Y/N),${if (transactionType == TransactionType.VIA) " Ex12 to exchange From & Via A/Cs, Ex23 to exchange Via & To A/Cs, Ex13 to exchange From & To A/Cs" else " Ex to exchange From & To A/Cs"} or B to back : "
                        )
                    )
                    val isCorrect = readLine()
                    when (isCorrect) {
                        "Y", "" -> {
                            return invokeInsertTransaction(
                                userId = userId,
                                eventDateTime = dateTimeString,
                                particulars = transactionParticulars,
                                amount = transactionAmount,
                                localFromAccount = localFromAccount,
                                localToAccount = localToAccount
                            )
                        }
                        // TODO : Back to fields
                        "N" ->
                            return invokeAddTransactionStep2(
                                userId = userId,
                                username = username,
                                localFromAccount = localFromAccount,
                                localToAccount = localToAccount,
                                transactionType = transactionType,
                                localViaAccount = localViaAccount
                            )
                        "Ex" -> {

                            if (transactionType == TransactionType.NORMAL) {
                                return ex13(
                                    userId,
                                    username,
                                    localFromAccount,
                                    localToAccount,
                                    transactionType,
                                    localViaAccount
                                )
                            } else {
                                invalidOptionMessage()
                            }
                        }
                        "Ex13" -> {
                            if (transactionType == TransactionType.VIA) {
                                return ex13(
                                    userId,
                                    username,
                                    localFromAccount,
                                    localToAccount,
                                    transactionType,
                                    localViaAccount
                                )
                            } else {
                                invalidOptionMessage()
                            }
                        }
                        "Ex12" -> {

                            if (transactionType == TransactionType.VIA) {
                                return ex12(
                                    userId,
                                    username,
                                    localFromAccount,
                                    localToAccount,
                                    transactionType,
                                    localViaAccount
                                )
                            } else {
                                invalidOptionMessage()
                            }
                        }
                        "Ex23" -> {

                            if (transactionType == TransactionType.VIA) {
                                return ex23(
                                    userId,
                                    username,
                                    localFromAccount,
                                    localToAccount,
                                    transactionType,
                                    localViaAccount
                                )
                            } else {
                                invalidOptionMessage()
                            }
                        }
                        else -> invalidOptionMessage()
                    }
                } while (isCorrect != "B")
            }
        }
        return false
    }
}

internal fun insertTransaction(
    userid: Int,
    eventDateTime: String,
    particulars: String,
    amount: Float,
    localFromAccount: AccountResponse,
    localToAccount: AccountResponse
): Boolean {

    val apiResponse: ResponseHolder<InsertionResponse>
    val userTransactionDataSource = TransactionDataSource()
    println("Contacting Server...")
    val eventDateTimeConversionResult =
        MysqlUtils.normalDateTimeStringToMysqlDateTimeString(normalDateTimeString = eventDateTime)
    if (eventDateTimeConversionResult.first) {
        runBlocking {
            apiResponse =
                userTransactionDataSource.insertTransaction(
                    userId = userid,
                    fromAccountId = localFromAccount.id,
                    eventDateTimeString = eventDateTimeConversionResult.second,
                    particulars = particulars,
                    amount = amount,
                    toAccountId = localToAccount.id
                )
        }
        //    println("Response : $apiResponse")
        if (apiResponse.isError()) {

            println("Error : ${(apiResponse.getValue() as Exception).localizedMessage}")
            //        do {
            //            print("Retry (Y/N) ? : ")
            //            val input = readLine()
            //            when (input) {
            //                "Y", "" -> {
            //                    login()
            //                    return
            //                }
            //                "N" -> {
            //                }
            //                else -> println("Invalid option, try again...")
            //            }
            //        } while (input != "N")
        } else {

            val insertionResponseResult = apiResponse.getValue() as InsertionResponse
            if (insertionResponseResult.status == 0) {

                println("OK...")
                return true
            } else {

                println("Server Execution Error : ${insertionResponseResult.error}")
            }
        }
    }
    return false
}
