package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.InsertionResponse
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.retrofit.data.TransactionDataSource
import accountLedgerCli.to_utils.DateTimeUtils
import accountLedgerCli.to_utils.InputUtils
import accountLedgerCli.to_utils.MysqlUtils
import accountLedgerCli.utils.*
import kotlinx.coroutines.runBlocking

object InsertOperations {

    internal val walletAccount: EnvironmentVariableForWholeNumber = getEnvironmentVariableValueForInsertOperation(
        environmentVariableName = EnvironmentalFileEntries.walletAccountId.entryName.name,
        environmentVariableFormalName = EnvironmentalFileEntries.walletAccountId.entryFormalName
    )

    internal val frequent1Account: EnvironmentVariableForWholeNumber = getEnvironmentVariableValueForInsertOperation(
        environmentVariableName = EnvironmentalFileEntries.frequent1AccountId.entryName.name,
        environmentVariableFormalName = EnvironmentalFileEntries.frequent1AccountId.entryFormalName
    )

    internal val frequent2Account: EnvironmentVariableForWholeNumber = getEnvironmentVariableValueForInsertOperation(
        environmentVariableName = EnvironmentalFileEntries.frequent2AccountId.entryName.name,
        environmentVariableFormalName = EnvironmentalFileEntries.frequent2AccountId.entryFormalName
    )

    internal val frequent3Account: EnvironmentVariableForWholeNumber = getEnvironmentVariableValueForInsertOperation(
        environmentVariableName = EnvironmentalFileEntries.frequent3AccountId.entryName.name,
        environmentVariableFormalName = EnvironmentalFileEntries.frequent3AccountId.entryFormalName
    )

    internal val bankAccount: EnvironmentVariableForWholeNumber = getEnvironmentVariableValueForInsertOperation(
        environmentVariableName = EnvironmentalFileEntries.bankAccountId.entryName.name,
        environmentVariableFormalName = EnvironmentalFileEntries.bankAccountId.entryFormalName
    )

    internal fun insertQuickTransactionFromAccount1toAccount2(
        account1: EnvironmentVariableForAny<*>,
        account2: EnvironmentVariableForAny<*>,
        userId: UInt,
        userAccountsMapLocal: LinkedHashMap<UInt, AccountResponse>,
        username: String,
        viaAccount: AccountResponse
    ) {
        if (EnvironmentFileOperations.isEnvironmentVariablesAreAvailable(
                environmentVariables = listOf(
                    account1, account2
                )
            ) && handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))
        ) {
            transactionContinueCheck(
                userId = userId,
                username = username,
                transactionType = TransactionTypeEnum.NORMAL,
                fromAccount = userAccountsMapLocal[account1.value]!!,
                viaAccount = viaAccount,
                toAccount = userAccountsMapLocal[account2.value]!!
            )
        }
    }

    internal fun insertQuickTransactionOnAccount(
        account: EnvironmentVariableForWholeNumber,
        userId: UInt,
        userAccountsMapLocal: LinkedHashMap<UInt, AccountResponse>,
        username: String,
        viaAccount: AccountResponse,
        toAccount: AccountResponse,
    ) {

        if (account.isAvailable && handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

            Screens.accountHome(
                userId = userId,
                username = username,
                fromAccount = userAccountsMapLocal[account.value]!!,
                viaAccount = viaAccount,
                toAccount = toAccount
            )
        }
    }

    internal fun addTransaction(

        userId: UInt,
        username: String,
        transactionType: TransactionTypeEnum,
        fromAccount: AccountResponse,
        viaAccount: AccountResponse,
        toAccount: AccountResponse

    ) {
        do {
            commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                listOfCommands = Screens.getUserWithCurrentAccountSelectionsAsText(

                    username = username,
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    transactionType = transactionType

                ) + listOf(

                    "1 - Choose To Account From List - Top Levels",
                    "2 - Choose To Account From List - Full Names",
                    "3 - Input To Account ID Directly",
                    "4 - Choose From Account From List - Top Levels",
                    "5 - Choose From Account From List - Full Names",
                    "6 - Input From Account ID Directly",
                    "7 - Continue Transaction",

                    if (transactionType == TransactionTypeEnum.VIA) {

                        "8 - Exchange From & Via. A/Cs\n" + "9 - Exchange From & Via. A/Cs, Then Continue Transaction\n" + "10 - Exchange Via. & To A/Cs\n" + "11 - Exchange Via & To A/Cs, Then Continue Transaction\n" + "12 - Exchange From & To. A/Cs\n" + "13 - Exchange From & To A/Cs, Then Continue Transaction\n" + "14 - Choose Via. Account From List - Top Levels\n" + "15 - Choose Via. Account From List - Full Names\n" + "16 - Input Via. Account ID Directly"

                    } else {

                        "8 - Exchange Accounts\n" + "9 - Exchange Accounts, Then Continue Transaction"
                    },
                    "0 - Back",
                    "",
                    "Enter Your Choice : "
                )
            )
            val choice: String? = readLine()
            when (choice) {
                "1" -> {
                    if (processChooseAccountResult(
                            chooseAccountResult = chooseDepositTop(userId = userId),
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            account1 = fromAccount,
                            account2 = viaAccount,
                            purpose = AccountsApiCallPurposeEnum.TO
                        )
                    ) {
                        return
                    }
                }

                "2" -> {
                    if (processChooseAccountResult(
                            chooseAccountResult = chooseDepositFull(userId = userId),
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            account1 = fromAccount,
                            account2 = viaAccount,
                            purpose = AccountsApiCallPurposeEnum.TO
                        )
                    ) {
                        return
                    }
                }

                "3" -> {

                    val chooseAccountResult: ChooseAccountResult = ChooseAccountUtils.chooseAccountById(userId = userId)
                    if (chooseAccountResult.chosenAccountId != 0u) {

                        processSelectedAccount(
                            selectedAccount = chooseAccountResult.chosenAccount!!,
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            account1 = fromAccount,
                            account2 = viaAccount,
                            purpose = AccountsApiCallPurposeEnum.TO
                        )
                        return
                    }
                }

                "4" -> {
                    if (processChooseAccountResult(
                            chooseAccountResult = chooseWithdrawTop(userId = userId),
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            account1 = viaAccount,
                            account2 = toAccount,
                            purpose = AccountsApiCallPurposeEnum.FROM
                        )
                    ) {
                        return
                    }
                }

                "5" -> {
                    if (processChooseAccountResult(
                            chooseAccountResult = chooseWithdrawFull(userId = userId),
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            account1 = viaAccount,
                            account2 = toAccount,
                            purpose = AccountsApiCallPurposeEnum.FROM
                        )
                    ) {
                        return
                    }
                }

                "6" -> {
                    val chooseAccountResult: ChooseAccountResult = ChooseAccountUtils.chooseAccountById(userId = userId)
                    if (chooseAccountResult.chosenAccountId != 0u) {

                        processSelectedAccount(
                            selectedAccount = chooseAccountResult.chosenAccount!!,
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            account1 = viaAccount,
                            account2 = toAccount,
                            purpose = AccountsApiCallPurposeEnum.FROM
                        )
                        return
                    }
                }

                "7" -> {

                    transactionContinueCheck(
                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount
                    )
                    return
                }

                "8" -> {

                    if (transactionType == TransactionTypeEnum.VIA) {

                        addTransaction(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = viaAccount,
                            viaAccount = fromAccount,
                            toAccount = toAccount
                        )

                    } else {

                        addTransaction(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = toAccount,
                            viaAccount = viaAccount,
                            toAccount = fromAccount
                        )
                    }
                    return
                }

                "9" -> {

                    if (transactionType == TransactionTypeEnum.VIA) {

                        transactionContinueCheck(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = viaAccount,
                            viaAccount = fromAccount,
                            toAccount = toAccount
                        )
                    } else {

                        transactionContinueCheck(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = toAccount,
                            viaAccount = viaAccount,
                            toAccount = fromAccount
                        )
                    }
                    return
                }

                "10" -> {
                    if (transactionType == TransactionTypeEnum.VIA) {

                        addTransaction(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = fromAccount,
                            viaAccount = toAccount,
                            toAccount = viaAccount
                        )
                        return

                    } else {
                        invalidOptionMessage()
                    }
                }

                "11" -> {
                    if (transactionType == TransactionTypeEnum.VIA) {

                        transactionContinueCheck(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = fromAccount,
                            viaAccount = toAccount,
                            toAccount = viaAccount
                        )
                        return

                    } else {
                        invalidOptionMessage()
                    }
                }

                "12" -> {
                    if (transactionType == TransactionTypeEnum.VIA) {

                        addTransaction(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = toAccount,
                            viaAccount = viaAccount,
                            toAccount = fromAccount
                        )
                        return

                    } else {
                        invalidOptionMessage()
                    }
                }

                "13" -> {
                    if (transactionType == TransactionTypeEnum.VIA) {

                        transactionContinueCheck(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = toAccount,
                            viaAccount = viaAccount,
                            toAccount = fromAccount
                        )
                        return

                    } else {
                        invalidOptionMessage()
                    }
                }

                "14" -> {

                    if (transactionType == TransactionTypeEnum.VIA) {

                        if (processChooseAccountResult(
                                chooseAccountResult = chooseViaTop(userId = userId),
                                userId = userId,
                                username = username,
                                transactionType = transactionType,
                                account1 = fromAccount,
                                account2 = toAccount,
                                purpose = AccountsApiCallPurposeEnum.VIA
                            )
                        ) {
                            return
                        }
                    } else {
                        invalidOptionMessage()
                    }
                }

                "15" -> {

                    if (transactionType == TransactionTypeEnum.VIA) {

                        if (processChooseAccountResult(
                                chooseAccountResult = chooseViaFull(userId = userId),
                                userId = userId,
                                username = username,
                                transactionType = transactionType,
                                account1 = fromAccount,
                                account2 = toAccount,
                                purpose = AccountsApiCallPurposeEnum.VIA
                            )
                        ) {
                            return
                        }
                    } else {
                        invalidOptionMessage()
                    }
                }

                "16" -> {

                    if (transactionType == TransactionTypeEnum.VIA) {

                        val chooseAccountResult: ChooseAccountResult =
                            ChooseAccountUtils.chooseAccountById(userId = userId)
                        if (chooseAccountResult.chosenAccountId != 0u) {

                            processSelectedAccount(
                                selectedAccount = chooseAccountResult.chosenAccount!!,
                                userId = userId,
                                username = username,
                                transactionType = transactionType,
                                account1 = fromAccount,
                                account2 = toAccount,
                                purpose = AccountsApiCallPurposeEnum.VIA
                            )
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


    private fun processChooseAccountResult(
        chooseAccountResult: HandleAccountsApiResponseResult,
        userId: UInt,
        username: String,
        transactionType: TransactionTypeEnum,
        account1: AccountResponse,
        account2: AccountResponse,
        purpose: AccountsApiCallPurposeEnum
    ): Boolean {
        if (chooseAccountResult.isAccountIdSelected) {
            processSelectedAccount(
                selectedAccount = chooseAccountResult.selectedAccount!!,
                userId = userId,
                username = username,
                transactionType = transactionType,
                account1 = account1,
                account2 = account2,
                purpose = purpose
            )
            return true
        }
        return false
    }

    private fun processSelectedAccount(
        selectedAccount: AccountResponse,
        userId: UInt,
        username: String,
        transactionType: TransactionTypeEnum,
        account1: AccountResponse,
        account2: AccountResponse,
        purpose: AccountsApiCallPurposeEnum
    ) {
        when (purpose) {
            AccountsApiCallPurposeEnum.TO -> {
                transactionContinueCheck(
                    userId = userId,
                    username = username,
                    transactionType = transactionType,
                    fromAccount = account1,
                    viaAccount = account2,
                    toAccount = selectedAccount
                )
            }

            AccountsApiCallPurposeEnum.FROM -> {
                transactionContinueCheck(
                    userId = userId,
                    username = username,
                    transactionType = transactionType,
                    fromAccount = selectedAccount,
                    viaAccount = account1,
                    toAccount = account2
                )
            }

            AccountsApiCallPurposeEnum.VIA -> {
                transactionContinueCheck(
                    userId = userId,
                    username = username,
                    transactionType = transactionType,
                    fromAccount = account1,
                    viaAccount = selectedAccount,
                    toAccount = account2
                )
            }
        }
    }

    internal fun addTransactionStep2(

        userId: UInt,
        username: String,
        transactionType: TransactionTypeEnum,
        fromAccount: AccountResponse,
        viaAccount: AccountResponse,
        toAccount: AccountResponse,
        isViaStep: Boolean = false,
        isTwoWayStep: Boolean = false,
        dateTimeInText: String = DateUtils.getCurrentDateTimeText(),
        transactionParticulars: String = "",
        transactionAmount: Float = 0F

    ): Boolean {

        var localDateTimeInText: String = dateTimeInText
        var localTransactionParticulars: String = transactionParticulars
        var localTransactionAmount: Float = transactionAmount

        var menuItems: List<String> = listOf(
            "\nUser : $username",
            "Withdraw Account - ${fromAccount.id} : ${fromAccount.fullName}",
        )
        if (transactionType == TransactionTypeEnum.VIA) {
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
                eventDateTime = localDateTimeInText,
                particulars = localTransactionParticulars,
                amount = localTransactionAmount,
                fromAccount = fromAccount,
                toAccount = toAccount
            )

        } else {

            commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                listOfCommands = menuItems + listOf(
                    // TODO : Option for Complete Back
                    "Enter Time : "
                )
            )
            when (val inputDateTimeInText: String =
                enterDateWithTime(transactionType = transactionType, dateTimeInText = localDateTimeInText)) {
                "D+Tr" -> {

                    return addTransactionStep2(
                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = DateTimeUtils.add1DayWith9ClockTimeToDateTimeInText(dateTimeInText = localDateTimeInText),
                        transactionParticulars = localTransactionParticulars,
                        transactionAmount = localTransactionAmount
                    )
                }

                "D+" -> {

                    return addTransactionStep2(
                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = DateTimeUtils.add1DayToDateTimeInText(dateTimeInText = localDateTimeInText),
                        transactionParticulars = localTransactionParticulars,
                        transactionAmount = localTransactionAmount
                    )
                }

                "D2+Tr" -> {

                    return addTransactionStep2(
                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = DateTimeUtils.add2DaysWith9ClockTimeToDateTimeInText(
                            dateTimeInText = localDateTimeInText
                        ),
                        transactionParticulars = localTransactionParticulars,
                        transactionAmount = localTransactionAmount
                    )
                }

                "D2+" -> {

                    return addTransactionStep2(
                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = DateTimeUtils.add2DaysToDateTimeString(dateTimeInText = localDateTimeInText),
                        transactionParticulars = localTransactionParticulars,
                        transactionAmount = localTransactionAmount
                    )
                }

                "Ex", "Ex13" -> {

                    return addTransactionStep2(
                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        fromAccount = toAccount,
                        viaAccount = viaAccount,
                        toAccount = fromAccount,
                        dateTimeInText = DateTimeUtils.add2DaysToDateTimeString(dateTimeInText = localDateTimeInText),
                        transactionParticulars = localTransactionParticulars,
                        transactionAmount = localTransactionAmount
                    )
                }

                "Ex12" -> {

                    return addTransactionStep2(
                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        fromAccount = viaAccount,
                        viaAccount = fromAccount,
                        toAccount = toAccount,
                        dateTimeInText = DateTimeUtils.add2DaysToDateTimeString(dateTimeInText = localDateTimeInText),
                        transactionParticulars = localTransactionParticulars,
                        transactionAmount = localTransactionAmount
                    )
                }

                "Ex23" -> {

                    return addTransactionStep2(
                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        fromAccount = fromAccount,
                        viaAccount = toAccount,
                        toAccount = viaAccount,
                        dateTimeInText = DateTimeUtils.add2DaysToDateTimeString(dateTimeInText = localDateTimeInText),
                        transactionParticulars = localTransactionParticulars,
                        transactionAmount = localTransactionAmount
                    )
                }

                "B" -> {

                    return false
                }

                else -> {

                    localDateTimeInText = inputDateTimeInText

                    print("Enter Particulars (Current Value - $localTransactionParticulars): ")
                    // TODO : Back to fields, or complete back
                    val transactionParticularsInput: String = readLine()!!
                    if (transactionParticularsInput.isNotEmpty()) {

                        localTransactionParticulars = transactionParticularsInput
                    }

                    print("Enter Amount (Current Value - $localTransactionAmount) : ")
                    val transactionAmountInput: String = readLine()!!
                    if (transactionAmountInput.isNotEmpty()) {

                        localTransactionAmount =
                            InputUtils.getValidFloat(transactionAmountInput, "Invalid Amount, Try Again : ")
                    }

                    do {
                        menuItems = listOf(
                            "\nTime - $localDateTimeInText",
                            "Withdraw Account - ${fromAccount.id} : ${fromAccount.fullName}"
                        )
                        if (transactionType == TransactionTypeEnum.VIA) {
                            menuItems =
                                menuItems + listOf("Intermediate Account - ${viaAccount.id} : ${viaAccount.fullName}")
                        }
                        menuItems = menuItems + listOf(
                            "Deposit Account - ${toAccount.id} : ${toAccount.fullName}",
                            "Particulars - $localTransactionParticulars",
                            "Amount - $localTransactionAmount",
                            "\nCorrect ? (Y/N), Enter ${if (transactionType == TransactionTypeEnum.VIA) "Ex12 to exchange From & Via A/Cs, Ex23 to exchange Via & To A/Cs, Ex13 to exchange From & To A/Cs" else "Ex to exchange From & To A/Cs"} or B to back : "
                        )
                        commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(listOfCommands = menuItems)
                        val isCorrect: String? = readLine()
                        when (isCorrect) {
                            "Y", "" -> {
                                return insertTransaction(
                                    userid = userId,
                                    eventDateTime = localDateTimeInText,
                                    particulars = localTransactionParticulars,
                                    amount = localTransactionAmount,
                                    fromAccount = fromAccount,
                                    toAccount = toAccount
                                )
                            }
                            // TODO : Back to fields
                            "N" -> return addTransactionStep2(
                                userId = userId,
                                username = username,
                                transactionType = transactionType,
                                fromAccount = fromAccount,
                                viaAccount = toAccount,
                                toAccount = viaAccount,
                                dateTimeInText = DateTimeUtils.add2DaysToDateTimeString(dateTimeInText = localDateTimeInText),
                                transactionParticulars = localTransactionParticulars,
                                transactionAmount = localTransactionAmount
                            )

                            "Ex" -> {

                                if (transactionType == TransactionTypeEnum.NORMAL) {

                                    return addTransactionStep2(
                                        userId = userId,
                                        username = username,
                                        transactionType = transactionType,
                                        fromAccount = toAccount,
                                        viaAccount = viaAccount,
                                        toAccount = fromAccount,
                                        dateTimeInText = DateTimeUtils.add2DaysToDateTimeString(dateTimeInText = localDateTimeInText),
                                        transactionParticulars = localTransactionParticulars,
                                        transactionAmount = localTransactionAmount
                                    )
                                } else {

                                    invalidOptionMessage()
                                }
                            }

                            "Ex13" -> {
                                if (transactionType == TransactionTypeEnum.VIA) {

                                    return addTransactionStep2(
                                        userId = userId,
                                        username = username,
                                        transactionType = transactionType,
                                        fromAccount = toAccount,
                                        viaAccount = viaAccount,
                                        toAccount = fromAccount,
                                        dateTimeInText = DateTimeUtils.add2DaysToDateTimeString(dateTimeInText = localDateTimeInText),
                                        transactionParticulars = localTransactionParticulars,
                                        transactionAmount = localTransactionAmount
                                    )
                                } else {

                                    invalidOptionMessage()
                                }
                            }

                            "Ex12" -> {

                                if (transactionType == TransactionTypeEnum.VIA) {

                                    return addTransactionStep2(
                                        userId = userId,
                                        username = username,
                                        transactionType = transactionType,
                                        fromAccount = viaAccount,
                                        viaAccount = fromAccount,
                                        toAccount = toAccount,
                                        dateTimeInText = DateTimeUtils.add2DaysToDateTimeString(dateTimeInText = localDateTimeInText),
                                        transactionParticulars = localTransactionParticulars,
                                        transactionAmount = localTransactionAmount
                                    )
                                } else {

                                    invalidOptionMessage()
                                }
                            }

                            "Ex23" -> {

                                if (transactionType == TransactionTypeEnum.VIA) {

                                    return addTransactionStep2(
                                        userId = userId,
                                        username = username,
                                        transactionType = transactionType,
                                        fromAccount = fromAccount,
                                        viaAccount = toAccount,
                                        toAccount = viaAccount,
                                        dateTimeInText = DateTimeUtils.add2DaysToDateTimeString(dateTimeInText = localDateTimeInText),
                                        transactionParticulars = localTransactionParticulars,
                                        transactionAmount = localTransactionAmount
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
        fromAccount: AccountResponse,
        toAccount: AccountResponse

    ): Boolean {

        val apiResponse: Result<InsertionResponse>
        val userTransactionDataSource = TransactionDataSource()

        val eventDateTimeConversionResult: Pair<Boolean, String> =
            MysqlUtils.normalDateTimeStringToMysqlDateTimeString(normalDateTimeString = eventDateTime)

        if (eventDateTimeConversionResult.first) {

            println("Contacting Server...")
            runBlocking {
                apiResponse = userTransactionDataSource.insertTransaction(
                    userId = userid,
                    fromAccountId = fromAccount.id,
                    eventDateTimeString = eventDateTimeConversionResult.second,
                    particulars = particulars,
                    amount = amount,
                    toAccountId = toAccount.id
                )
            }
            //    println("Response : $apiResponse")
            if (apiResponse.isFailure) {

                println("Error : ${(apiResponse.exceptionOrNull() as Exception).localizedMessage}")
                do {
                    print("Retry (Y/N) ? : ")
                    val input: String? = readLine()
                    when (input) {
                        "Y", "" -> {
                            return insertTransaction(
                                userid = userid,
                                eventDateTime = eventDateTime,
                                particulars = particulars,
                                amount = amount,
                                fromAccount = fromAccount,
                                toAccount = toAccount
                            )
                        }

                        "N" -> {
                        }

                        else -> println("Invalid option, try again...")
                    }
                } while (input != "N")

            } else {

                val insertionResponseResult: InsertionResponse = apiResponse.getOrNull()!!
                if (insertionResponseResult.status == 0) {

                    println("OK...")
                    return true

                } else {

                    println("Server Execution Error : ${insertionResponseResult.error}")
                }
            }
        } else {

            println("Date Error : ${eventDateTimeConversionResult.second}")
        }
        return false
    }

    private fun getEnvironmentVariableValueForInsertOperation(

        environmentVariableName: String, environmentVariableFormalName: String

    ): EnvironmentVariableForWholeNumber = EnvironmentFileOperations.getEnvironmentVariableValueForWholeNumber(

        dotenv = App.dotenv,
        environmentVariableName = environmentVariableName,
        environmentVariableFormalName = environmentVariableFormalName
    )
}