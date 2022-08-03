package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.TransactionManipulationResponse
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.constants.Constants
import accountLedgerCli.enums.AccountExchangeTypeEnum
import accountLedgerCli.enums.AccountTypeEnum
import accountLedgerCli.enums.HandleAccountsApiResponseResult
import accountLedgerCli.enums.TransactionTypeEnum
import accountLedgerCli.models.*
import accountLedgerCli.retrofit.data.TransactionDataSource
import accountLedgerCli.to_models.IsOkModel
import accountLedgerCli.to_utils.*
import accountLedgerCli.utils.ApiUtils
import accountLedgerCli.utils.ChooseAccountUtils
import kotlinx.coroutines.runBlocking
import accountLedgerCli.to_utils.ApiUtils as CommonApiUtils

object InsertOperations {

    internal val walletAccount: EnvironmentVariableForWholeNumber = getEnvironmentVariableValueForInsertOperation(
        environmentVariableName = EnvironmentalFileEntries.walletAccountId.entryName.name,
        environmentVariableFormalName = EnvironmentalFileEntries.walletAccountId.entryFormalName!!
    )

    internal val frequent1Account: EnvironmentVariableForWholeNumber = getEnvironmentVariableValueForInsertOperation(
        environmentVariableName = EnvironmentalFileEntries.frequent1AccountId.entryName.name,
        environmentVariableFormalName = EnvironmentalFileEntries.frequent1AccountId.entryFormalName!!
    )

    internal val frequent2Account: EnvironmentVariableForWholeNumber = getEnvironmentVariableValueForInsertOperation(
        environmentVariableName = EnvironmentalFileEntries.frequent2AccountId.entryName.name,
        environmentVariableFormalName = EnvironmentalFileEntries.frequent2AccountId.entryFormalName!!
    )

    internal val frequent3Account: EnvironmentVariableForWholeNumber = getEnvironmentVariableValueForInsertOperation(
        environmentVariableName = EnvironmentalFileEntries.frequent3AccountId.entryName.name,
        environmentVariableFormalName = EnvironmentalFileEntries.frequent3AccountId.entryFormalName!!
    )

    internal val bankAccount: EnvironmentVariableForWholeNumber = getEnvironmentVariableValueForInsertOperation(
        environmentVariableName = EnvironmentalFileEntries.bankAccountId.entryName.name,
        environmentVariableFormalName = EnvironmentalFileEntries.bankAccountId.entryFormalName!!
    )

    internal fun insertQuickTransactionFromAccount1toAccount2(

        account1: EnvironmentVariableForAny<*>,
        account2: EnvironmentVariableForAny<*>,
        userId: UInt,
        username: String,
        viaAccount: AccountResponse,
        dateTimeInText: String,
        transactionParticulars: String,
        transactionAmount: Float

    ): InsertTransactionResult {

        val insertTransactionResult = InsertTransactionResult(

            isSuccess = false,
            dateTimeInText = dateTimeInText,
            transactionParticulars = transactionParticulars,
            transactionAmount = transactionAmount
        )

        if (EnvironmentFileOperations.isEnvironmentVariablesAreAvailable(
                environmentVariables = listOf(
                    account1, account2
                )
            )
        ) {
            val getUserAccountsMapResult: IsOkModel<LinkedHashMap<UInt, AccountResponse>> =
                HandleResponses.getUserAccountsMap(apiResponse = ApiUtils.getAccountsFull(userId = userId))

            return HandleResponses.isOkModelHandler(

                isOkModel = getUserAccountsMapResult,
                data = insertTransactionResult,
                actionsAfterGetSuccess = fun(): InsertTransactionResult {

                    return transactionContinueCheck(
                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.NORMAL,
                        fromAccount = getUserAccountsMapResult.data!![account1.value]!!,
                        viaAccount = viaAccount,
                        toAccount = getUserAccountsMapResult.data[account2.value]!!,
                        dateTimeInText = dateTimeInText,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount
                    )
                }
            )
        }
        return insertTransactionResult
    }

    internal fun openSpecifiedAccountHome(

        account: EnvironmentVariableForWholeNumber,
        userId: UInt,
        username: String,
        viaAccount: AccountResponse,
        toAccount: AccountResponse,
        dateTimeInText: String,
        transactionParticulars: String,
        transactionAmount: Float

    ): InsertTransactionResult {

        val insertTransactionResult = InsertTransactionResult(

            isSuccess = false,
            dateTimeInText = dateTimeInText,
            transactionParticulars = transactionParticulars,
            transactionAmount = transactionAmount
        )

        if (account.isAvailable) {

            val getUserAccountsMapResult: IsOkModel<LinkedHashMap<UInt, AccountResponse>> =
                HandleResponses.getUserAccountsMap(apiResponse = ApiUtils.getAccountsFull(userId = userId))

            return HandleResponses.isOkModelHandler(

                isOkModel = getUserAccountsMapResult,
                data = insertTransactionResult,
                actionsAfterGetSuccess = fun(): InsertTransactionResult {

                    return Screens.accountHome(
                        userId = userId,
                        username = username,
                        fromAccount = getUserAccountsMapResult.data!![account.value]!!,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = dateTimeInText,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount
                    )
                })
        }
        return insertTransactionResult
    }

    internal fun addTransaction(

        userId: UInt,
        username: String,
        transactionType: TransactionTypeEnum,
        fromAccount: AccountResponse,
        viaAccount: AccountResponse,
        toAccount: AccountResponse,
        dateTimeInText: String,
        transactionParticulars: String,
        transactionAmount: Float

    ): InsertTransactionResult {
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
            when (readLine()!!) {
                "1" -> {
                    return processChooseAccountResult(
                        chooseAccountResult = chooseDepositTop(userId = userId),
                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        account1 = fromAccount,
                        account2 = viaAccount,
                        purpose = AccountTypeEnum.TO,
                        dateTimeInText = dateTimeInText,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount
                    )
                }

                "2" -> {
                    return processChooseAccountResult(
                        chooseAccountResult = chooseDepositFull(userId = userId),
                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        account1 = fromAccount,
                        account2 = viaAccount,
                        purpose = AccountTypeEnum.TO,
                        dateTimeInText = dateTimeInText,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount
                    )
                }

                "3" -> {

                    val chooseAccountResult: ChooseAccountResult = ChooseAccountUtils.chooseAccountById(
                        userId = userId,
                        accountType = AccountTypeEnum.TO
                    )
                    if (chooseAccountResult.chosenAccountId != 0u) {

                        return processSelectedAccount(
                            selectedAccount = chooseAccountResult.chosenAccount!!,
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            account1 = fromAccount,
                            account2 = viaAccount,
                            purpose = AccountTypeEnum.TO,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount
                        )
                    }
                }

                "4" -> {
                    return processChooseAccountResult(
                        chooseAccountResult = chooseWithdrawTop(userId = userId),
                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        account1 = viaAccount,
                        account2 = toAccount,
                        purpose = AccountTypeEnum.FROM,
                        dateTimeInText = dateTimeInText,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount
                    )
                }

                "5" -> {
                    return processChooseAccountResult(
                        chooseAccountResult = chooseWithdrawFull(userId = userId),
                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        account1 = viaAccount,
                        account2 = toAccount,
                        purpose = AccountTypeEnum.FROM,
                        dateTimeInText = dateTimeInText,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount
                    )
                }

                "6" -> {
                    val chooseAccountResult: ChooseAccountResult = ChooseAccountUtils.chooseAccountById(
                        userId = userId,
                        accountType = AccountTypeEnum.FROM
                    )
                    if (chooseAccountResult.chosenAccountId != 0u) {

                        return processSelectedAccount(
                            selectedAccount = chooseAccountResult.chosenAccount!!,
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            account1 = viaAccount,
                            account2 = toAccount,
                            purpose = AccountTypeEnum.FROM,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount
                        )
                    }
                }

                "7" -> {

                    return transactionContinueCheck(
                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = dateTimeInText,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount
                    )
                }

                "8" -> {

                    if (transactionType == TransactionTypeEnum.VIA) {

                        return addTransaction(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = viaAccount,
                            viaAccount = fromAccount,
                            toAccount = toAccount,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount
                        )

                    } else {

                        return addTransaction(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = toAccount,
                            viaAccount = viaAccount,
                            toAccount = fromAccount,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount
                        )
                    }
                }

                "9" -> {

                    if (transactionType == TransactionTypeEnum.VIA) {

                        return transactionContinueCheck(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = viaAccount,
                            viaAccount = fromAccount,
                            toAccount = toAccount,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount
                        )
                    } else {

                        return invokeContinueTransactionAfterExchangeOfFromAndToAccounts(

                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = fromAccount,
                            viaAccount = viaAccount,
                            toAccount = toAccount,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount
                        )
                    }
                }

                "10" -> {
                    if (transactionType == TransactionTypeEnum.VIA) {

                        return addTransaction(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = fromAccount,
                            viaAccount = toAccount,
                            toAccount = viaAccount,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount
                        )
                    } else {
                        invalidOptionMessage()
                    }
                }

                "11" -> {
                    if (transactionType == TransactionTypeEnum.VIA) {

                        return transactionContinueCheck(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = fromAccount,
                            viaAccount = toAccount,
                            toAccount = viaAccount,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount
                        )

                    } else {
                        invalidOptionMessage()
                    }
                }

                "12" -> {
                    if (transactionType == TransactionTypeEnum.VIA) {

                        return addTransaction(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = toAccount,
                            viaAccount = viaAccount,
                            toAccount = fromAccount,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount
                        )

                    } else {
                        invalidOptionMessage()
                    }
                }

                "13" -> {
                    if (transactionType == TransactionTypeEnum.VIA) {

                        return invokeContinueTransactionAfterExchangeOfFromAndToAccounts(

                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = fromAccount,
                            viaAccount = viaAccount,
                            toAccount = toAccount,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount
                        )
                    } else {
                        invalidOptionMessage()
                    }
                }

                "14" -> {

                    if (transactionType == TransactionTypeEnum.VIA) {

                        return processChooseAccountResult(
                            chooseAccountResult = chooseViaTop(userId = userId),
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            account1 = fromAccount,
                            account2 = toAccount,
                            purpose = AccountTypeEnum.VIA,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount
                        )
                    } else {
                        invalidOptionMessage()
                    }
                }

                "15" -> {

                    if (transactionType == TransactionTypeEnum.VIA) {

                        return processChooseAccountResult(
                            chooseAccountResult = chooseViaFull(userId = userId),
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            account1 = fromAccount,
                            account2 = toAccount,
                            purpose = AccountTypeEnum.VIA,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount
                        )
                    } else {
                        invalidOptionMessage()
                    }
                }

                "16" -> {

                    if (transactionType == TransactionTypeEnum.VIA) {

                        val chooseAccountResult: ChooseAccountResult =
                            ChooseAccountUtils.chooseAccountById(userId = userId, accountType = AccountTypeEnum.VIA)
                        if (chooseAccountResult.chosenAccountId != 0u) {

                            return processSelectedAccount(
                                selectedAccount = chooseAccountResult.chosenAccount!!,
                                userId = userId,
                                username = username,
                                transactionType = transactionType,
                                account1 = fromAccount,
                                account2 = toAccount,
                                purpose = AccountTypeEnum.VIA,
                                dateTimeInText = dateTimeInText,
                                transactionParticulars = transactionParticulars,
                                transactionAmount = transactionAmount
                            )
                        }
                    } else {
                        invalidOptionMessage()
                    }
                }

                "0" -> {
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
    }


    private fun processChooseAccountResult(

        chooseAccountResult: HandleAccountsApiResponseResult,
        userId: UInt,
        username: String,
        transactionType: TransactionTypeEnum,
        account1: AccountResponse,
        account2: AccountResponse,
        purpose: AccountTypeEnum,
        dateTimeInText: String,
        transactionParticulars: String,
        transactionAmount: Float

    ): InsertTransactionResult {

        if (chooseAccountResult.isAccountIdSelected) {

            return processSelectedAccount(

                selectedAccount = chooseAccountResult.selectedAccount!!,
                userId = userId,
                username = username,
                transactionType = transactionType,
                account1 = account1,
                account2 = account2,
                purpose = purpose,
                dateTimeInText = dateTimeInText,
                transactionParticulars = transactionParticulars,
                transactionAmount = transactionAmount
            )
        }
        return InsertTransactionResult(
            isSuccess = false,
            dateTimeInText = dateTimeInText,
            transactionParticulars = transactionParticulars,
            transactionAmount = transactionAmount
        )
    }

    private fun processSelectedAccount(

        selectedAccount: AccountResponse,
        userId: UInt,
        username: String,
        transactionType: TransactionTypeEnum,
        account1: AccountResponse,
        account2: AccountResponse,
        purpose: AccountTypeEnum,
        dateTimeInText: String,
        transactionParticulars: String,
        transactionAmount: Float

    ): InsertTransactionResult {

        when (purpose) {

            AccountTypeEnum.TO -> {

                return transactionContinueCheck(
                    userId = userId,
                    username = username,
                    transactionType = transactionType,
                    fromAccount = account1,
                    viaAccount = account2,
                    toAccount = selectedAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )
            }

            AccountTypeEnum.FROM -> {

                return transactionContinueCheck(
                    userId = userId,
                    username = username,
                    transactionType = transactionType,
                    fromAccount = selectedAccount,
                    viaAccount = account1,
                    toAccount = account2,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )
            }

            AccountTypeEnum.VIA -> {

                return transactionContinueCheck(
                    userId = userId,
                    username = username,
                    transactionType = transactionType,
                    fromAccount = account1,
                    viaAccount = selectedAccount,
                    toAccount = account2,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )
            }
        }
    }

    private fun invokeContinueTransactionAfterExchangeOfFromAndToAccounts(

        userId: UInt,
        username: String,
        transactionType: TransactionTypeEnum,
        fromAccount: AccountResponse,
        viaAccount: AccountResponse,
        toAccount: AccountResponse,
        dateTimeInText: String,
        transactionParticulars: String,
        transactionAmount: Float

    ): InsertTransactionResult {

        // TODO : Remove boilerplate code
        return transactionContinueCheck(

            userId = userId,
            username = username,
            transactionType = transactionType,
            fromAccount = toAccount,
            viaAccount = viaAccount,
            toAccount = fromAccount,
            dateTimeInText = dateTimeInText,
            transactionParticulars = transactionParticulars,
            transactionAmount = transactionAmount
        )
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
        transactionId: UInt = 0u,
        dateTimeInText: String,
        transactionParticulars: String,
        transactionAmount: Float,
        isEditStep: Boolean = false

    ): InsertTransactionResult {

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
        if (isViaStep) {

            return InsertTransactionResult(
                isSuccess = automatedInsertTransaction(
                    userId = userId,
                    eventDateTime = localDateTimeInText,
                    particulars = localTransactionParticulars,
                    amount = localTransactionAmount,
                    fromAccount = viaAccount,
                    toAccount = toAccount
                ),
                dateTimeInText = localDateTimeInText,
                transactionParticulars = localTransactionParticulars,
                transactionAmount = localTransactionAmount
            )

        } else if (isTwoWayStep) {

            return InsertTransactionResult(
                isSuccess = automatedInsertTransaction(
                    userId = userId,
                    eventDateTime = localDateTimeInText,
                    particulars = localTransactionParticulars,
                    amount = localTransactionAmount,
                    fromAccount = toAccount,
                    toAccount = fromAccount
                ),
                dateTimeInText = localDateTimeInText,
                transactionParticulars = localTransactionParticulars,
                transactionAmount = localTransactionAmount
            )

        } else {

            commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                listOfCommands = menuItems + listOf(
                    // TODO : Option for Complete Back
                    "Enter Time : "
                )
            )
            localDateTimeInText =
                enterDateWithTime(transactionType = transactionType, dateTimeInText = localDateTimeInText)
            when (localDateTimeInText) {

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
                        transactionAmount = localTransactionAmount,
                        isEditStep = isEditStep
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
                        transactionAmount = localTransactionAmount,
                        isEditStep = isEditStep
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
                        transactionAmount = localTransactionAmount,
                        isEditStep = isEditStep
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
                        transactionAmount = localTransactionAmount,
                        isEditStep = isEditStep
                    )
                }

                "Ex", "Ex13" -> {

                    return invokeAddTransactionStep2AfterExchangeOfAccounts(
                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = localDateTimeInText,
                        transactionParticulars = localTransactionParticulars,
                        transactionAmount = localTransactionAmount,
                        accountExchangeType = AccountExchangeTypeEnum.FROM_AND_TO,
                        isEditStep = isEditStep
                    )
                }

                "Ex12" -> {

                    return invokeAddTransactionStep2AfterExchangeOfAccounts(
                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = localDateTimeInText,
                        transactionParticulars = localTransactionParticulars,
                        transactionAmount = localTransactionAmount,
                        accountExchangeType = AccountExchangeTypeEnum.FROM_AND_VIA,
                        isEditStep = isEditStep
                    )
                }

                "Ex23" -> {

                    return invokeAddTransactionStep2AfterExchangeOfAccounts(
                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = localDateTimeInText,
                        transactionParticulars = localTransactionParticulars,
                        transactionAmount = localTransactionAmount,
                        accountExchangeType = AccountExchangeTypeEnum.VIA_AND_TO,
                        isEditStep = isEditStep
                    )
                }

                "B" -> {

                    return InsertTransactionResult(
                        isSuccess = false,
                        dateTimeInText = localDateTimeInText,
                        transactionParticulars = localTransactionParticulars,
                        transactionAmount = localTransactionAmount
                    )
                }

                else -> {

                    print("Enter Particulars (Current Value - $localTransactionParticulars): ")
                    // TODO : Back to fields, or complete back
                    val transactionParticularsInput: String = readLine()!!
                    if (transactionParticularsInput.isNotEmpty()) {

                        localTransactionParticulars = transactionParticularsInput
                    }

                    val inputPrompt = "Enter Amount (Current Value - $localTransactionAmount) : "
                    print(inputPrompt)
                    val transactionAmountInput: String = readLine()!!
                    if (transactionAmountInput.isNotEmpty()) {

                        localTransactionAmount =
                            InputUtils.getValidFloat(
                                inputString = transactionAmountInput,
                                invalidMessage = "Invalid Amount, $inputPrompt : "
                            )
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
                        when (readLine()!!) {

                            "Y", "" -> {

                                if (isEditStep) {

                                    when (transactionType) {

                                        TransactionTypeEnum.NORMAL -> {

                                            return InsertTransactionResult(
                                                isSuccess = updateTransaction(
                                                    transactionId = transactionId,
                                                    eventDateTime = localDateTimeInText,
                                                    particulars = localTransactionParticulars,
                                                    amount = localTransactionAmount,
                                                    fromAccount = fromAccount,
                                                    toAccount = toAccount
                                                ),
                                                dateTimeInText = localDateTimeInText,
                                                transactionParticulars = localTransactionParticulars,
                                                transactionAmount = localTransactionAmount
                                            )
                                        }

                                        TransactionTypeEnum.VIA -> {
                                            ToDoUtils.showTodo()
                                        }

                                        TransactionTypeEnum.TWO_WAY -> {
                                            ToDoUtils.showTodo()
                                        }
                                    }

                                } else {
                                    when (transactionType) {

                                        TransactionTypeEnum.NORMAL, TransactionTypeEnum.TWO_WAY -> {

                                            return InsertTransactionResult(
                                                isSuccess = insertTransaction(
                                                    userId = userId,
                                                    eventDateTime = localDateTimeInText,
                                                    particulars = localTransactionParticulars,
                                                    amount = localTransactionAmount,
                                                    fromAccount = fromAccount,
                                                    toAccount = toAccount
                                                ),
                                                dateTimeInText = localDateTimeInText,
                                                transactionParticulars = localTransactionParticulars,
                                                transactionAmount = localTransactionAmount
                                            )
                                        }

                                        TransactionTypeEnum.VIA -> {

                                            return InsertTransactionResult(
                                                isSuccess = insertTransaction(
                                                    userId = userId,
                                                    eventDateTime = localDateTimeInText,
                                                    particulars = localTransactionParticulars,
                                                    amount = localTransactionAmount,
                                                    fromAccount = fromAccount,
                                                    toAccount = viaAccount
                                                ),
                                                dateTimeInText = localDateTimeInText,
                                                transactionParticulars = localTransactionParticulars,
                                                transactionAmount = localTransactionAmount
                                            )
                                        }
                                    }
                                }
                            }
                            // TODO : Back to fields
                            "N" -> return addTransactionStep2(
                                userId = userId,
                                username = username,
                                transactionType = transactionType,
                                fromAccount = fromAccount,
                                viaAccount = toAccount,
                                toAccount = viaAccount,
                                dateTimeInText = localDateTimeInText,
                                transactionParticulars = localTransactionParticulars,
                                transactionAmount = localTransactionAmount,
                                isEditStep = isEditStep
                            )

                            "Ex" -> {

                                if (transactionType == TransactionTypeEnum.NORMAL) {

                                    return invokeAddTransactionStep2AfterExchangeOfAccounts(
                                        userId = userId,
                                        username = username,
                                        transactionType = transactionType,
                                        fromAccount = fromAccount,
                                        viaAccount = viaAccount,
                                        toAccount = toAccount,
                                        dateTimeInText = localDateTimeInText,
                                        transactionParticulars = localTransactionParticulars,
                                        transactionAmount = localTransactionAmount,
                                        accountExchangeType = AccountExchangeTypeEnum.FROM_AND_TO,
                                        isEditStep = isEditStep
                                    )

                                } else {

                                    invalidOptionMessage()
                                }
                            }

                            "Ex13" -> {
                                if (transactionType == TransactionTypeEnum.VIA) {

                                    return invokeAddTransactionStep2AfterExchangeOfAccounts(
                                        userId = userId,
                                        username = username,
                                        transactionType = transactionType,
                                        fromAccount = fromAccount,
                                        viaAccount = viaAccount,
                                        toAccount = toAccount,
                                        dateTimeInText = localDateTimeInText,
                                        transactionParticulars = localTransactionParticulars,
                                        transactionAmount = localTransactionAmount,
                                        accountExchangeType = AccountExchangeTypeEnum.FROM_AND_TO,
                                        isEditStep = isEditStep
                                    )

                                } else {

                                    invalidOptionMessage()
                                }
                            }

                            "Ex12" -> {

                                if (transactionType == TransactionTypeEnum.VIA) {

                                    return invokeAddTransactionStep2AfterExchangeOfAccounts(
                                        userId = userId,
                                        username = username,
                                        transactionType = transactionType,
                                        fromAccount = fromAccount,
                                        viaAccount = viaAccount,
                                        toAccount = toAccount,
                                        dateTimeInText = localDateTimeInText,
                                        transactionParticulars = localTransactionParticulars,
                                        transactionAmount = localTransactionAmount,
                                        accountExchangeType = AccountExchangeTypeEnum.FROM_AND_VIA,
                                        isEditStep = isEditStep
                                    )
                                } else {

                                    invalidOptionMessage()
                                }
                            }

                            "Ex23" -> {

                                if (transactionType == TransactionTypeEnum.VIA) {

                                    return invokeAddTransactionStep2AfterExchangeOfAccounts(
                                        userId = userId,
                                        username = username,
                                        transactionType = transactionType,
                                        fromAccount = fromAccount,
                                        viaAccount = viaAccount,
                                        toAccount = toAccount,
                                        dateTimeInText = localDateTimeInText,
                                        transactionParticulars = localTransactionParticulars,
                                        transactionAmount = localTransactionAmount,
                                        accountExchangeType = AccountExchangeTypeEnum.VIA_AND_TO,
                                        isEditStep = isEditStep
                                    )
                                } else {
                                    invalidOptionMessage()
                                }
                            }

                            "B" -> {
                                return InsertTransactionResult(
                                    isSuccess = false,
                                    dateTimeInText = localDateTimeInText,
                                    transactionParticulars = localTransactionParticulars,
                                    transactionAmount = localTransactionAmount
                                )
                            }

                            else -> invalidOptionMessage()
                        }
                    } while (true)
                }
            }
        }
    }

    private fun invokeAddTransactionStep2AfterExchangeOfAccounts(

        userId: UInt,
        username: String,
        transactionType: TransactionTypeEnum,
        fromAccount: AccountResponse,
        viaAccount: AccountResponse,
        toAccount: AccountResponse,
        dateTimeInText: String,
        transactionParticulars: String,
        transactionAmount: Float,
        accountExchangeType: AccountExchangeTypeEnum,
        isEditStep: Boolean

    ): InsertTransactionResult {

        when (accountExchangeType) {
            AccountExchangeTypeEnum.FROM_AND_TO -> {
                return addTransactionStep2(
                    userId = userId,
                    username = username,
                    transactionType = transactionType,
                    fromAccount = toAccount,
                    viaAccount = viaAccount,
                    toAccount = fromAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount,
                    isEditStep = isEditStep
                )
            }

            AccountExchangeTypeEnum.FROM_AND_VIA -> {
                return addTransactionStep2(
                    userId = userId,
                    username = username,
                    transactionType = transactionType,
                    fromAccount = viaAccount,
                    viaAccount = fromAccount,
                    toAccount = toAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount,
                    isEditStep = isEditStep
                )
            }

            AccountExchangeTypeEnum.VIA_AND_TO -> {
                return addTransactionStep2(
                    userId = userId,
                    username = username,
                    transactionType = transactionType,
                    fromAccount = fromAccount,
                    viaAccount = toAccount,
                    toAccount = viaAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount,
                    isEditStep = isEditStep
                )
            }
        }
    }

    private fun manipulateTransaction(

        transactionManipulationApiRequest: () -> Result<TransactionManipulationResponse>,
        transactionManipulationSuccessActions: () -> Unit

    ): Boolean {

        val transactionManipulationApiRequestResult: IsOkModel<TransactionManipulationResponse> =
            CommonApiUtils.makeApiRequestWithOptionalRetries(apiCallFunction = transactionManipulationApiRequest)

        if (transactionManipulationApiRequestResult.isOK) {

            val transactionManipulationResponseResult: TransactionManipulationResponse =
                transactionManipulationApiRequestResult.data!!
            if (transactionManipulationResponseResult.status == 0u) {

                println("OK...")
                transactionManipulationSuccessActions.invoke()
                return true

            } else {

                println("Server Execution Error : ${transactionManipulationResponseResult.error}")
            }
        }
        return false
    }

//    private fun manipulateTransactionWithEventDateTimeCheck(
//
//        eventDateTime: String,
//        transactionManipulationApiRequest: () -> Result<TransactionManipulationResponse>,
//        transactionManipulationSuccessActions: () -> Unit
//
//    ): Boolean {
//
//        val eventDateTimeConversionResult: IsOkModel<String> = MysqlUtils.dateTimeTextConversionWithMessage(
//            dateTimeTextConversionFunction = fun(): IsOkModel<String> {
//                return MysqlUtils.normalDateTextToMysqlDateText(
//                    normalDateText = eventDateTime
//                )
//            })
//
//        if (eventDateTimeConversionResult.isOK) {
//            return manipulateTransaction(
//                transactionManipulationApiRequest = transactionManipulationApiRequest,
//                transactionManipulationSuccessActions = transactionManipulationSuccessActions
//            )
//        }
//        return false
//    }

    private fun insertTransaction(

        userId: UInt,
        eventDateTime: String,
        particulars: String,
        amount: Float,
        fromAccount: AccountResponse,
        toAccount: AccountResponse,

        ): Boolean {

        val eventDateTimeConversionResult: IsOkModel<String> = MysqlUtils.dateTimeTextConversionWithMessage(
            dateTimeTextConversionFunction = fun(): IsOkModel<String> {
                return MysqlUtils.normalDateTimeTextToMySqlDateTimeText(
                    normalDateTimeText = eventDateTime
                )
            })

        if (eventDateTimeConversionResult.isOK) {

            return manipulateTransaction(transactionManipulationApiRequest = fun(): Result<TransactionManipulationResponse> {

                return runBlocking {

                    TransactionDataSource().insertTransaction(
                        userId = userId,
                        fromAccountId = fromAccount.id,
                        eventDateTimeString = eventDateTimeConversionResult.data!!,
                        particulars = particulars,
                        amount = amount,
                        toAccountId = toAccount.id
                    )
                }
            }, transactionManipulationSuccessActions = fun() {

                val readFrequencyOfAccountsFileResult: IsOkModel<FrequencyOfAccountsModel> =
                    JsonFileUtils.readJsonFile(Constants.frequencyOfAccountsFileName)

                if (App.isDevelopmentMode) {

                    println("readFrequencyOfAccountsFileResult : $readFrequencyOfAccountsFileResult")
                }

                if (readFrequencyOfAccountsFileResult.isOK) {

                    var frequencyOfAccounts: FrequencyOfAccountsModel = readFrequencyOfAccountsFileResult.data!!
                    val user: UserModel? = frequencyOfAccounts.users.find { user: UserModel -> user.id == userId }
                    if (user != null) {

                        frequencyOfAccounts = updateAccountFrequency(
                            user = user,
                            account = fromAccount,
                            frequencyOfAccounts = frequencyOfAccounts,
                            userId = userId
                        )
                        frequencyOfAccounts = updateAccountFrequency(
                            user = user,
                            account = toAccount,
                            frequencyOfAccounts = frequencyOfAccounts,
                            userId = userId
                        )

                    } else {
                        frequencyOfAccounts.users = frequencyOfAccounts.users.plusElement(
                            element = getInitialAccountFrequencyForUser(
                                userId = userId,
                                fromAccount = fromAccount,
                                toAccount = toAccount
                            )
                        )
                    }
                    JsonFileUtils.writeJsonFile(
                        fileName = Constants.frequencyOfAccountsFileName,
                        data = frequencyOfAccounts
                    )
                } else {

                    JsonFileUtils.writeJsonFile(
                        fileName = Constants.frequencyOfAccountsFileName, data = FrequencyOfAccountsModel(
                            users = listOf(
                                getInitialAccountFrequencyForUser(
                                    userId = userId,
                                    fromAccount = fromAccount,
                                    toAccount = toAccount
                                )
                            )
                        )
                    )
                }
            })
        }
        return false
    }

    internal fun updateTransaction(

        transactionId: UInt,
        eventDateTime: String,
        particulars: String,
        amount: Float,
        fromAccount: AccountResponse,
        toAccount: AccountResponse,
        isDateTimeUpdateOperation: Boolean = false

    ): Boolean {

        if (isDateTimeUpdateOperation) {

            return manipulateTransaction(transactionManipulationApiRequest = fun(): Result<TransactionManipulationResponse> {
                return runBlocking {

                    TransactionDataSource().updateTransaction(
                        transactionId = transactionId,
                        fromAccountId = fromAccount.id,
                        eventDateTimeString = eventDateTime,
                        particulars = particulars,
                        amount = amount,
                        toAccountId = toAccount.id
                    )
                }
            }, transactionManipulationSuccessActions = fun() {})

        } else {

            val eventDateTimeConversionResult: IsOkModel<String> = MysqlUtils.dateTimeTextConversionWithMessage(
                dateTimeTextConversionFunction = fun(): IsOkModel<String> {
                    return MysqlUtils.normalDateTimeTextToMySqlDateTimeText(
                        normalDateTimeText = eventDateTime
                    )
                })

            if (eventDateTimeConversionResult.isOK) {

                return manipulateTransaction(transactionManipulationApiRequest = fun(): Result<TransactionManipulationResponse> {
                    return runBlocking {

                        TransactionDataSource().updateTransaction(
                            transactionId = transactionId,
                            fromAccountId = fromAccount.id,
                            eventDateTimeString = eventDateTimeConversionResult.data!!,
                            particulars = particulars,
                            amount = amount,
                            toAccountId = toAccount.id
                        )
                    }
                }, transactionManipulationSuccessActions = fun() {})
            }
        }
        return false
    }

    internal fun deleteTransaction(

        transactionId: UInt

    ): Boolean {

        return manipulateTransaction(transactionManipulationApiRequest = fun(): Result<TransactionManipulationResponse> {
            return runBlocking {

                TransactionDataSource().deleteTransaction(transactionId = transactionId)
            }
        }, transactionManipulationSuccessActions = fun() {})
    }

    private fun updateAccountFrequency(

        user: UserModel,
        account: AccountResponse,
        frequencyOfAccounts: FrequencyOfAccountsModel,
        userId: UInt

    ): FrequencyOfAccountsModel {

//        println("frequencyOfAccounts : $frequencyOfAccounts")

        val accountFrequency: AccountFrequencyModel? =
            user.accountFrequencies.find { accountFrequency: AccountFrequencyModel ->
                accountFrequency.accountID == account.id
            }
//        println("accountFrequency : $accountFrequency")

        if (accountFrequency == null) {

            frequencyOfAccounts.users.find { localUser: UserModel -> localUser.id == userId }!!.accountFrequencies =

                frequencyOfAccounts.users.find { localUser: UserModel -> localUser.id == userId }!!.accountFrequencies.plusElement(
                    element = AccountFrequencyModel(
                        accountID = account.id,
                        accountName = account.fullName,
                        countOfRepetition = 1u
                    )
                )

        } else {

            frequencyOfAccounts.users.find { localUser: UserModel -> localUser.id == userId }!!.accountFrequencies.find { localAccountFrequency: AccountFrequencyModel -> localAccountFrequency.accountID == account.id }!!.countOfRepetition++
        }
//        println("frequencyOfAccounts : $frequencyOfAccounts")
        return frequencyOfAccounts
    }

    private fun getInitialAccountFrequencyForUser(

        userId: UInt,
        fromAccount: AccountResponse,
        toAccount: AccountResponse

    ) = UserModel(

        id = userId, accountFrequencies = listOf(
            AccountFrequencyModel(
                accountID = fromAccount.id,
                accountName = fromAccount.fullName,
                countOfRepetition = 1u
            ),
            AccountFrequencyModel
                (
                accountID = toAccount.id,
                accountName = toAccount.fullName,
                countOfRepetition = 1u
            )
        )
    )

    private fun getEnvironmentVariableValueForInsertOperation(

        environmentVariableName: String,
        environmentVariableFormalName: String

    ): EnvironmentVariableForWholeNumber = EnvironmentFileOperations.getEnvironmentVariableValueForWholeNumber(

        dotenv = App.dotenv,
        environmentVariableName = environmentVariableName,
        environmentVariableFormalName = environmentVariableFormalName
    )

    private fun automatedInsertTransaction(

        userId: UInt,
        eventDateTime: String,
        particulars: String,
        amount: Float,
        fromAccount: AccountResponse,
        toAccount: AccountResponse

    ): Boolean {

        commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
            listOfCommands = listOf(
                "\nTime - $eventDateTime",
                "Withdraw Account - ${fromAccount.id} : ${fromAccount.fullName}",
                "Deposit Account - ${toAccount.id} : ${toAccount.fullName}",
                "Particulars - $particulars",
                "Amount - $amount"
            )
        )
        return insertTransaction(
            userId = userId,
            eventDateTime = eventDateTime,
            particulars = particulars,
            amount = amount,
            fromAccount = fromAccount,
            toAccount = toAccount
        )
    }
}