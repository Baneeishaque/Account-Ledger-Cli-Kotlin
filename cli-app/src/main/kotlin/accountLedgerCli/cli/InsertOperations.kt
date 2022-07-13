package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.InsertionResponse
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.cli.App.Companion.dateTimeString
import accountLedgerCli.cli.App.Companion.fromAccount
import accountLedgerCli.cli.App.Companion.toAccount
import accountLedgerCli.cli.App.Companion.transactionAmount
import accountLedgerCli.cli.App.Companion.transactionParticulars
import accountLedgerCli.cli.App.Companion.userAccountsMap
import accountLedgerCli.cli.App.Companion.viaAccount
import accountLedgerCli.retrofit.data.TransactionDataSource
import accountLedgerCli.to_utils.DateTimeUtils
import accountLedgerCli.to_utils.InputUtils
import accountLedgerCli.to_utils.MysqlUtils
import accountLedgerCli.to_utils.ToDoUtils
import accountLedgerCli.utils.ApiUtils
import accountLedgerCli.utils.ChooseAccountUtils
import kotlinx.coroutines.runBlocking

internal val walletAccountId: EnvironmentVariableForWholeNumber =
    getEnvironmentVariableValueForInsertOperation(
        environmentVariableName = EnvironmentalFileEntries.walletAccountId.entryName.name,
        environmentVariableFormalName = EnvironmentalFileEntries.walletAccountId.entryFormalName
    )

internal val frequent1AccountId: EnvironmentVariableForWholeNumber =
    getEnvironmentVariableValueForInsertOperation(
        environmentVariableName = EnvironmentalFileEntries.frequent1AccountId.entryName.name,
        environmentVariableFormalName = EnvironmentalFileEntries.frequent1AccountId.entryFormalName
    )

internal val frequent2AccountId: EnvironmentVariableForWholeNumber =
    getEnvironmentVariableValueForInsertOperation(
        environmentVariableName = EnvironmentalFileEntries.frequent2AccountId.entryName.name,
        environmentVariableFormalName = EnvironmentalFileEntries.frequent2AccountId.entryFormalName
    )

internal val frequent3AccountId: EnvironmentVariableForWholeNumber =
    getEnvironmentVariableValueForInsertOperation(
        environmentVariableName = EnvironmentalFileEntries.frequent3AccountId.entryName.name,
        environmentVariableFormalName = EnvironmentalFileEntries.frequent3AccountId.entryFormalName
    )

internal val bankAccountId: EnvironmentVariableForWholeNumber =
    getEnvironmentVariableValueForInsertOperation(
        environmentVariableName = EnvironmentalFileEntries.bankAccountId.entryName.name,
        environmentVariableFormalName = EnvironmentalFileEntries.bankAccountId.entryFormalName
    )

internal fun insertQuickTransactionWallet(userId: UInt, username: String) {

    if (walletAccountId.isAvailable && handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[walletAccountId.value]!!
        accountHome(userId = userId, username = username)
    }
}

internal fun insertQuickTransactionWalletToFrequent1(userId: UInt, username: String) {

    if (EnvironmentFileOperations.isEnvironmentVariablesAreAvailable(
            environmentVariables = listOf(
                walletAccountId, frequent1AccountId
            )
        ) && handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))
    ) {
        fromAccount = userAccountsMap[walletAccountId.value]!!
        toAccount = userAccountsMap[frequent1AccountId.value]!!
        transactionContinueCheck(
            userId = userId,
            username = username,
            transactionTypeEnum = TransactionTypeEnum.NORMAL
        )
    }
}

internal fun insertQuickTransactionWalletToFrequent2(userId: UInt, username: String) {

    if (EnvironmentFileOperations.isEnvironmentVariablesAreAvailable(
            environmentVariables = listOf(
                walletAccountId, frequent2AccountId
            )
        ) && handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))
    ) {
        fromAccount = userAccountsMap[walletAccountId.value]!!
        toAccount = userAccountsMap[frequent2AccountId.value]!!
        transactionContinueCheck(
            userId = userId,
            username = username,
            transactionTypeEnum = TransactionTypeEnum.NORMAL
        )
    }

}

internal fun insertQuickTransactionWalletToFrequent3(userId: UInt, username: String) {

    if (EnvironmentFileOperations.isEnvironmentVariablesAreAvailable(
            environmentVariables = listOf(
                walletAccountId, frequent3AccountId
            )
        ) && handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))
    ) {
        fromAccount = userAccountsMap[walletAccountId.value]!!
        toAccount = userAccountsMap[frequent3AccountId.value]!!
        transactionContinueCheck(userId = userId, username = username, transactionTypeEnum = TransactionTypeEnum.NORMAL)
    }
}

internal fun insertQuickTransactionBank(userId: UInt, username: String) {

    if (bankAccountId.isAvailable && handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[bankAccountId.value]!!
        accountHome(userId = userId, username = username)
    }
}

internal fun insertQuickTransactionBankToFrequent1(userId: UInt, username: String) {

    if (EnvironmentFileOperations.isEnvironmentVariablesAreAvailable(
            environmentVariables = listOf(
                bankAccountId,
                frequent1AccountId
            )
        ) && handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))
    ) {
        fromAccount = userAccountsMap[bankAccountId.value]!!
        toAccount = userAccountsMap[frequent1AccountId.value]!!
        transactionContinueCheck(userId = userId, username = username, transactionTypeEnum = TransactionTypeEnum.NORMAL)
    }
}

internal fun insertQuickTransactionBankToFrequent2(userId: UInt, username: String) {

    if (EnvironmentFileOperations.isEnvironmentVariablesAreAvailable(
            environmentVariables = listOf(
                bankAccountId,
                frequent2AccountId
            )
        ) && handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))
    ) {
        fromAccount = userAccountsMap[bankAccountId.value]!!
        toAccount = userAccountsMap[frequent2AccountId.value]!!
        transactionContinueCheck(userId = userId, username = username, transactionTypeEnum = TransactionTypeEnum.NORMAL)
    }
}

internal fun insertQuickTransactionFrequent1(userId: UInt, username: String) {

    if (frequent1AccountId.isAvailable && handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[frequent1AccountId.value]!!
        accountHome(userId = userId, username = username)
    }
}

internal fun insertQuickTransactionBankToFrequent3(userId: UInt, username: String) {

    if (EnvironmentFileOperations.isEnvironmentVariablesAreAvailable(
            environmentVariables = listOf(
                bankAccountId,
                frequent3AccountId
            )
        ) && handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))
    ) {
        fromAccount = userAccountsMap[bankAccountId.value]!!
        toAccount = userAccountsMap[frequent3AccountId.value]!!
        transactionContinueCheck(userId = userId, username = username, transactionTypeEnum = TransactionTypeEnum.NORMAL)
    }
}

internal fun insertQuickTransactionFrequent2(userId: UInt, username: String) {

    if (frequent2AccountId.isAvailable && handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[frequent2AccountId.value]!!
        accountHome(userId = userId, username = username)
    }
}

internal fun insertQuickTransactionFrequent3(userId: UInt, username: String) {

    if (frequent3AccountId.isAvailable && handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[frequent3AccountId.value]!!
        accountHome(userId = userId, username = username)
    }
}

internal fun addAccount() {

    // Use all accounts for general account addition, or from account for child account addition
    ToDoUtils.showTodo()
}

internal fun addTransaction(userId: UInt, username: String, transactionTypeEnum: TransactionTypeEnum) {

    do {
        var menuItems = listOf(
            "\nUser : $username",
            "Transaction Type : $transactionTypeEnum",
            "From Account - ${fromAccount.id} : ${fromAccount.fullName}"
        )
        if (transactionTypeEnum == TransactionTypeEnum.VIA) {
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
        if (transactionTypeEnum == TransactionTypeEnum.VIA) {
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

                    transactionContinueCheck(userId, username, transactionTypeEnum)
                    return
                }
            }

            "2" -> {
                if (chooseDepositFull(userId)) {

                    transactionContinueCheck(userId, username, transactionTypeEnum)
                    return
                }
            }

            "3" -> {

                val chooseAccountResult = ChooseAccountUtils.chooseAccountById(userId)
                if (chooseAccountResult.chosenAccountId != 0u) {

                    toAccount = chooseAccountResult.chosenAccount
                    transactionContinueCheck(userId, username, transactionTypeEnum)
                    return
                }
            }

            "4" -> {
                if (chooseFromAccountTop(userId)) {

                    transactionContinueCheck(userId, username, transactionTypeEnum)
                    return
                }
            }

            "5" -> {
                if (chooseFromAccountFull(userId)) {

                    transactionContinueCheck(userId, username, transactionTypeEnum)
                    return
                }
            }

            "6" -> {
                val chooseAccountResult = ChooseAccountUtils.chooseAccountById(userId)
                if (chooseAccountResult.chosenAccountId != 0u) {

                    fromAccount = chooseAccountResult.chosenAccount
                    transactionContinueCheck(userId, username, transactionTypeEnum)
                    return
                }
            }

            "7" -> {

                transactionContinueCheck(userId, username, transactionTypeEnum)
                return
            }

            "8" -> {

                if (transactionTypeEnum == TransactionTypeEnum.VIA) {

                    exchangeFromAndViaAccounts()

                } else {

                    exchangeFromAndToAccounts()
                }
                addTransaction(userId = userId, username = username, transactionTypeEnum = transactionTypeEnum)
                return
            }

            "9" -> {

                if (transactionTypeEnum == TransactionTypeEnum.VIA) {

                    exchangeFromAndViaAccounts()

                } else {

                    exchangeFromAndToAccounts()
                }
                transactionContinueCheck(userId, username, transactionTypeEnum)
                return
            }

            "10" -> {
                if (transactionTypeEnum == TransactionTypeEnum.VIA) {

                    exchangeToAndViaAccounts()
                    addTransaction(userId = userId, username = username, transactionTypeEnum = transactionTypeEnum)
                    return

                } else {
                    invalidOptionMessage()
                }
            }

            "11" -> {
                if (transactionTypeEnum == TransactionTypeEnum.VIA) {

                    exchangeToAndViaAccounts()
                    transactionContinueCheck(userId, username, transactionTypeEnum)
                    return

                } else {
                    invalidOptionMessage()
                }
            }

            "12" -> {
                if (transactionTypeEnum == TransactionTypeEnum.VIA) {

                    exchangeFromAndToAccounts()
                    addTransaction(userId = userId, username = username, transactionTypeEnum = transactionTypeEnum)
                    return

                } else {
                    invalidOptionMessage()
                }
            }

            "13" -> {
                if (transactionTypeEnum == TransactionTypeEnum.VIA) {

                    exchangeFromAndToAccounts()
                    transactionContinueCheck(userId, username, transactionTypeEnum)
                    return

                } else {
                    invalidOptionMessage()
                }
            }

            "14" -> {
                if (transactionTypeEnum == TransactionTypeEnum.VIA) {

                    ToDoUtils.showTodo()
                    return

                } else {
                    invalidOptionMessage()
                }
            }

            "15" -> {
                if (transactionTypeEnum == TransactionTypeEnum.VIA) {

                    if (chooseViaAccountFull(userId)) {

                        transactionContinueCheck(userId, username, TransactionTypeEnum.VIA)
                        return
                    }
                } else {
                    invalidOptionMessage()
                }
            }

            "16" -> {
                if (transactionTypeEnum == TransactionTypeEnum.VIA) {

                    val chooseAccountResult = ChooseAccountUtils.chooseAccountById(userId)
                    if (chooseAccountResult.chosenAccountId != 0u) {

                        viaAccount = chooseAccountResult.chosenAccount
                        transactionContinueCheck(userId, username, transactionTypeEnum)
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
    userId: UInt,
    username: String,
    localFromAccount: AccountResponse,
    localToAccount: AccountResponse,
    transactionTypeEnum: TransactionTypeEnum,
    localViaAccount: AccountResponse,
    isViaStep: Boolean = false,
    isTwoWayStep: Boolean = false
): Boolean {

    var menuItems = listOf(
        "\nUser : $username",
        "Withdraw Account - ${fromAccount.id} : ${fromAccount.fullName}",
    )
    if (transactionTypeEnum == TransactionTypeEnum.VIA) {
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
        when (val inputDateTimeString = enterDateWithTime(transactionTypeEnum = transactionTypeEnum)) {
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
                    transactionTypeEnum = transactionTypeEnum,
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
                    transactionTypeEnum = transactionTypeEnum,
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
                    transactionTypeEnum = transactionTypeEnum,
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
                    transactionTypeEnum = transactionTypeEnum,
                    localViaAccount = localViaAccount
                )
            }

            "Ex" -> {

                return ex13(userId, username, localFromAccount, localToAccount, transactionTypeEnum, localViaAccount)
            }

            "Ex13" -> {

                return ex13(userId, username, localFromAccount, localToAccount, transactionTypeEnum, localViaAccount)
            }

            "Ex12" -> {

                return ex12(userId, username, localFromAccount, localToAccount, transactionTypeEnum, localViaAccount)
            }

            "Ex23" -> {

                return ex23(userId, username, localFromAccount, localToAccount, transactionTypeEnum, localViaAccount)
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
                            "\nCorrect ? (Y/N),${if (transactionTypeEnum == TransactionTypeEnum.VIA) " Ex12 to exchange From & Via A/Cs, Ex23 to exchange Via & To A/Cs, Ex13 to exchange From & To A/Cs" else " Ex to exchange From & To A/Cs"} or B to back : "
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
                                transactionTypeEnum = transactionTypeEnum,
                                localViaAccount = localViaAccount
                            )

                        "Ex" -> {

                            if (transactionTypeEnum == TransactionTypeEnum.NORMAL) {
                                return ex13(
                                    userId,
                                    username,
                                    localFromAccount,
                                    localToAccount,
                                    transactionTypeEnum,
                                    localViaAccount
                                )
                            } else {
                                invalidOptionMessage()
                            }
                        }

                        "Ex13" -> {
                            if (transactionTypeEnum == TransactionTypeEnum.VIA) {
                                return ex13(
                                    userId,
                                    username,
                                    localFromAccount,
                                    localToAccount,
                                    transactionTypeEnum,
                                    localViaAccount
                                )
                            } else {
                                invalidOptionMessage()
                            }
                        }

                        "Ex12" -> {

                            if (transactionTypeEnum == TransactionTypeEnum.VIA) {
                                return ex12(
                                    userId,
                                    username,
                                    localFromAccount,
                                    localToAccount,
                                    transactionTypeEnum,
                                    localViaAccount
                                )
                            } else {
                                invalidOptionMessage()
                            }
                        }

                        "Ex23" -> {

                            if (transactionTypeEnum == TransactionTypeEnum.VIA) {
                                return ex23(
                                    userId,
                                    username,
                                    localFromAccount,
                                    localToAccount,
                                    transactionTypeEnum,
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
    userid: UInt,
    eventDateTime: String,
    particulars: String,
    amount: Float,
    localFromAccount: AccountResponse,
    localToAccount: AccountResponse
): Boolean {

    val apiResponse: Result<InsertionResponse>
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
        if (apiResponse.isFailure) {

            println("Error : ${(apiResponse.exceptionOrNull() as Exception).localizedMessage}")
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

            val insertionResponseResult = apiResponse.getOrNull() as InsertionResponse
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

private fun getEnvironmentVariableValueForInsertOperation(

    environmentVariableName: String,
    environmentVariableFormalName: String

): EnvironmentVariableForWholeNumber = EnvironmentFileOperations.getEnvironmentVariableValueForWholeNumber(

    dotenv = App.dotenv,
    environmentVariableName = environmentVariableName,
    environmentVariableFormalName = environmentVariableFormalName
)
