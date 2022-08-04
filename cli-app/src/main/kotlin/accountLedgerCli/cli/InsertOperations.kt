package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.TransactionManipulationResponse
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.constants.Constants
import accountLedgerCli.enums.*
import accountLedgerCli.enums.AccountExchangeTypeEnum
import accountLedgerCli.enums.HandleAccountsApiResponseResult
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
        fromAccount: AccountResponse,
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
            transactionAmount = transactionAmount,
            fromAccount = fromAccount,
            viaAccount = viaAccount,
            toAccount = toAccount
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
                })
        }
        return insertTransactionResult
    }

    internal fun openSpecifiedAccountHome(

        account: EnvironmentVariableForWholeNumber,
        userId: UInt,
        username: String,
        fromAccount: AccountResponse,
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
            transactionAmount = transactionAmount,
            fromAccount = fromAccount,
            viaAccount = viaAccount,
            toAccount = toAccount
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

        var localInsertTransactionResult = InsertTransactionResult(

            isSuccess = false,
            dateTimeInText = dateTimeInText,
            transactionParticulars = transactionParticulars,
            transactionAmount = transactionAmount,
            fromAccount = fromAccount,
            viaAccount = viaAccount,
            toAccount = toAccount
        )

        do {
            commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                listOfCommands = Screens.getUserWithCurrentAccountSelectionsAsText(

                    username = username,
                    fromAccount = localInsertTransactionResult.fromAccount,
                    viaAccount = localInsertTransactionResult.viaAccount,
                    toAccount = localInsertTransactionResult.toAccount,
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

                        "8 - Exchange Accounts\n" + "9 - Exchange Accounts, Then Continue Transaction\n" + "10 - Input To Account ID Directly, Exchange Accounts, Then Continue Transaction\n" + "11 - Input From Account ID Directly, Exchange Accounts, Then Continue Transaction\n" + "12 - Choose To Account From List - Top Levels, Exchange Accounts, Then Continue Transaction\n" + "13 - Choose To Account From List - Full Names, Exchange Accounts, Then Continue Transaction\n" + "14 - Choose From Account From List - Top Levels, Exchange Accounts, Then Continue Transaction\n" + "15 - Choose From Account From List - Full Names, Exchange Accounts, Then Continue Transaction"
                    },
                    "17 - ${Screens.getQuickTransactionOnWalletText()}",
                    "18 - ${Screens.getQuickTransactionOnWalletToFrequent1Text()}",
                    "19 - ${Screens.getQuickTransactionOnWalletToFrequent2Text()}",
                    "20 - ${Screens.getQuickTransactionOnWalletToFrequent3Text()}",
                    "21 - ${Screens.getQuickTransactionOnBankText()}",
                    "22 - ${Screens.getQuickTransactionOnBankToFrequent1Text()}",
                    "23 - ${Screens.getQuickTransactionOnBankToFrequent2Text()}",
                    "24 - ${Screens.getQuickTransactionOnBankToFrequent3Text()}",
                    "25 - ${Screens.getQuickTransactionOnFrequent1Text()}",
                    "26 - ${Screens.getQuickTransactionOnFrequent2Text()}",
                    "27 - ${Screens.getQuickTransactionOnFrequent3Text()}",
                    "0 - Back",
                    "",
                    "Enter Your Choice : "
                )
            )
            when (readLine()!!) {

                "1" -> {

                    localInsertTransactionResult = processChooseAccountResult(

                        chooseAccountResult = chooseDepositTop(userId = userId),
                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        account1 = localInsertTransactionResult.fromAccount,
                        account2 = localInsertTransactionResult.viaAccount,
                        purpose = AccountTypeEnum.TO,
                        dateTimeInText = dateTimeInText,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount
                    )
                }

                "2" -> {

                    localInsertTransactionResult = processChooseAccountResult(

                        chooseAccountResult = chooseDepositFull(userId = userId),
                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        account1 = localInsertTransactionResult.fromAccount,
                        account2 = localInsertTransactionResult.viaAccount,
                        purpose = AccountTypeEnum.TO,
                        dateTimeInText = dateTimeInText,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount
                    )
                }

                "3" -> {

                    val chooseToAccountResult: ChooseAccountResult = chooseToAccount(userId = userId)
                    if (chooseToAccountResult.chosenAccountId != 0u) {

                        val processSelectedAccountResult: InsertTransactionResult = processSelectedAccount(

                            selectedAccount = chooseToAccountResult.chosenAccount!!,
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            account1 = localInsertTransactionResult.fromAccount,
                            account2 = localInsertTransactionResult.viaAccount,
                            purpose = AccountTypeEnum.TO,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount
                        )
                        if (processSelectedAccountResult.isSuccess) {

                            localInsertTransactionResult = processSelectedAccountResult

                        } else {

                            localInsertTransactionResult.fromAccount = processSelectedAccountResult.fromAccount
                            localInsertTransactionResult.viaAccount = processSelectedAccountResult.viaAccount
                            localInsertTransactionResult.toAccount = processSelectedAccountResult.toAccount
                        }
                    }
                }

                "4" -> {

                    localInsertTransactionResult = processChooseAccountResult(

                        chooseAccountResult = chooseWithdrawTop(userId = userId),
                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        account1 = localInsertTransactionResult.viaAccount,
                        account2 = localInsertTransactionResult.toAccount,
                        purpose = AccountTypeEnum.FROM,
                        dateTimeInText = dateTimeInText,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount
                    )
                }

                "5" -> {

                    localInsertTransactionResult = processChooseAccountResult(

                        chooseAccountResult = chooseWithdrawFull(userId = userId),
                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        account1 = localInsertTransactionResult.viaAccount,
                        account2 = localInsertTransactionResult.toAccount,
                        purpose = AccountTypeEnum.FROM,
                        dateTimeInText = dateTimeInText,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount
                    )
                }

                "6" -> {
                    val chooseFromAccountResult: ChooseAccountResult = chooseFromAccount(userId = userId)
                    if (chooseFromAccountResult.chosenAccountId != 0u) {

                        val processSelectedAccountResult: InsertTransactionResult = processSelectedAccount(

                            selectedAccount = chooseFromAccountResult.chosenAccount!!,
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            account1 = localInsertTransactionResult.viaAccount,
                            account2 = localInsertTransactionResult.toAccount,
                            purpose = AccountTypeEnum.FROM,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount
                        )
                        if (processSelectedAccountResult.isSuccess) {

                            localInsertTransactionResult = processSelectedAccountResult

                        } else {

                            localInsertTransactionResult.fromAccount = processSelectedAccountResult.fromAccount
                            localInsertTransactionResult.viaAccount = processSelectedAccountResult.viaAccount
                            localInsertTransactionResult.toAccount = processSelectedAccountResult.toAccount
                        }
                    }
                }

                "7" -> {

                    localInsertTransactionResult = transactionContinueCheck(
                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = dateTimeInText,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount
                    )
                }

                "8" -> {

                    if (transactionType == TransactionTypeEnum.VIA) {

                        localInsertTransactionResult = addTransaction(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = localInsertTransactionResult.viaAccount,
                            viaAccount = localInsertTransactionResult.fromAccount,
                            toAccount = localInsertTransactionResult.toAccount,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount
                        )

                    } else {

                        localInsertTransactionResult = addTransaction(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = localInsertTransactionResult.toAccount,
                            viaAccount = localInsertTransactionResult.viaAccount,
                            toAccount = localInsertTransactionResult.fromAccount,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount
                        )
                    }
                }

                "9" -> {

                    if (transactionType == TransactionTypeEnum.VIA) {

                        localInsertTransactionResult = transactionContinueCheck(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = localInsertTransactionResult.viaAccount,
                            viaAccount = localInsertTransactionResult.fromAccount,
                            toAccount = localInsertTransactionResult.toAccount,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount
                        )
                    } else {

                        localInsertTransactionResult = transactionContinueCheck(

                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = localInsertTransactionResult.toAccount,
                            viaAccount = localInsertTransactionResult.viaAccount,
                            toAccount = localInsertTransactionResult.fromAccount,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount
                        )
                    }
                }

                "10" -> {
                    if (transactionType == TransactionTypeEnum.VIA) {

                        localInsertTransactionResult = addTransaction(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = localInsertTransactionResult.fromAccount,
                            viaAccount = localInsertTransactionResult.toAccount,
                            toAccount = localInsertTransactionResult.viaAccount,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount
                        )
                    } else {

                        val chooseToAccountResult: ChooseAccountResult = chooseToAccount(userId = userId)
                        if (chooseToAccountResult.chosenAccountId != 0u) {

                            val processSelectedAccountResult: InsertTransactionResult = processSelectedAccount(

                                selectedAccount = chooseToAccountResult.chosenAccount!!,
                                userId = userId,
                                username = username,
                                transactionType = transactionType,
                                account1 = localInsertTransactionResult.fromAccount,
                                account2 = localInsertTransactionResult.viaAccount,
                                purpose = AccountTypeEnum.TO,
                                dateTimeInText = dateTimeInText,
                                transactionParticulars = transactionParticulars,
                                transactionAmount = transactionAmount,
                                wantToExchange = true
                            )
                            if (processSelectedAccountResult.isSuccess) {

                                localInsertTransactionResult = processSelectedAccountResult

                            } else {

                                localInsertTransactionResult.fromAccount = processSelectedAccountResult.fromAccount
                                localInsertTransactionResult.viaAccount = processSelectedAccountResult.viaAccount
                                localInsertTransactionResult.toAccount = processSelectedAccountResult.toAccount
                            }
                        }
                    }
                }

                "11" -> {
                    if (transactionType == TransactionTypeEnum.VIA) {

                        localInsertTransactionResult = transactionContinueCheck(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = localInsertTransactionResult.fromAccount,
                            viaAccount = localInsertTransactionResult.toAccount,
                            toAccount = localInsertTransactionResult.viaAccount,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount
                        )

                    } else {

                        val chooseFromAccountResult: ChooseAccountResult = chooseFromAccount(userId = userId)
                        if (chooseFromAccountResult.chosenAccountId != 0u) {

                            val processSelectedAccountResult: InsertTransactionResult = processSelectedAccount(

                                selectedAccount = chooseFromAccountResult.chosenAccount!!,
                                userId = userId,
                                username = username,
                                transactionType = transactionType,
                                account1 = localInsertTransactionResult.viaAccount,
                                account2 = localInsertTransactionResult.toAccount,
                                purpose = AccountTypeEnum.FROM,
                                dateTimeInText = dateTimeInText,
                                transactionParticulars = transactionParticulars,
                                transactionAmount = transactionAmount,
                                wantToExchange = true
                            )
                            if (processSelectedAccountResult.isSuccess) {

                                localInsertTransactionResult = processSelectedAccountResult

                            } else {

                                localInsertTransactionResult.fromAccount = processSelectedAccountResult.fromAccount
                                localInsertTransactionResult.viaAccount = processSelectedAccountResult.viaAccount
                                localInsertTransactionResult.toAccount = processSelectedAccountResult.toAccount
                            }
                        }
                    }
                }

                "12" -> {
                    if (transactionType == TransactionTypeEnum.VIA) {

                        localInsertTransactionResult = addTransaction(

                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = localInsertTransactionResult.toAccount,
                            viaAccount = localInsertTransactionResult.viaAccount,
                            toAccount = localInsertTransactionResult.fromAccount,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount
                        )

                    } else {

                        localInsertTransactionResult = processChooseAccountResult(

                            chooseAccountResult = chooseDepositTop(userId = userId),
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            account1 = localInsertTransactionResult.fromAccount,
                            account2 = localInsertTransactionResult.viaAccount,
                            purpose = AccountTypeEnum.TO,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount,
                            wantToExchange = true,
                            fromAccount = localInsertTransactionResult.fromAccount,
                            viaAccount = localInsertTransactionResult.viaAccount,
                            toAccount = localInsertTransactionResult.toAccount
                        )
                    }
                }

                "13" -> {
                    if (transactionType == TransactionTypeEnum.VIA) {

                        localInsertTransactionResult = transactionContinueCheck(

                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = localInsertTransactionResult.toAccount,
                            viaAccount = localInsertTransactionResult.viaAccount,
                            toAccount = localInsertTransactionResult.fromAccount,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount
                        )
                    } else {

                        localInsertTransactionResult = processChooseAccountResult(

                            chooseAccountResult = chooseDepositFull(userId = userId),
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            account1 = localInsertTransactionResult.fromAccount,
                            account2 = localInsertTransactionResult.viaAccount,
                            purpose = AccountTypeEnum.TO,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount,
                            wantToExchange = true,
                            fromAccount = localInsertTransactionResult.fromAccount,
                            viaAccount = localInsertTransactionResult.viaAccount,
                            toAccount = localInsertTransactionResult.toAccount
                        )
                    }
                }

                "14" -> {

                    if (transactionType == TransactionTypeEnum.VIA) {

                        localInsertTransactionResult = processChooseAccountResult(

                            chooseAccountResult = chooseViaTop(userId = userId),
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            account1 = localInsertTransactionResult.fromAccount,
                            account2 = localInsertTransactionResult.toAccount,
                            purpose = AccountTypeEnum.VIA,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount,
                            fromAccount = localInsertTransactionResult.fromAccount,
                            viaAccount = localInsertTransactionResult.viaAccount,
                            toAccount = localInsertTransactionResult.toAccount
                        )
                    } else {

                        localInsertTransactionResult = processChooseAccountResult(

                            chooseAccountResult = chooseWithdrawTop(userId = userId),
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            account1 = localInsertTransactionResult.viaAccount,
                            account2 = localInsertTransactionResult.toAccount,
                            purpose = AccountTypeEnum.FROM,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount,
                            wantToExchange = true,
                            fromAccount = localInsertTransactionResult.fromAccount,
                            viaAccount = localInsertTransactionResult.viaAccount,
                            toAccount = localInsertTransactionResult.toAccount
                        )
                    }
                }

                "15" -> {

                    if (transactionType == TransactionTypeEnum.VIA) {

                        localInsertTransactionResult = processChooseAccountResult(

                            chooseAccountResult = chooseViaFull(userId = userId),
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            account1 = localInsertTransactionResult.fromAccount,
                            account2 = localInsertTransactionResult.toAccount,
                            purpose = AccountTypeEnum.VIA,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount,
                            fromAccount = localInsertTransactionResult.fromAccount,
                            viaAccount = localInsertTransactionResult.viaAccount,
                            toAccount = localInsertTransactionResult.toAccount
                        )
                    } else {

                        localInsertTransactionResult = processChooseAccountResult(

                            chooseAccountResult = chooseWithdrawFull(userId = userId),
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            account1 = localInsertTransactionResult.viaAccount,
                            account2 = localInsertTransactionResult.toAccount,
                            purpose = AccountTypeEnum.FROM,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount,
                            wantToExchange = true,
                            fromAccount = localInsertTransactionResult.fromAccount,
                            viaAccount = localInsertTransactionResult.viaAccount,
                            toAccount = localInsertTransactionResult.toAccount
                        )
                    }
                }

                "16" -> {

                    if (transactionType == TransactionTypeEnum.VIA) {

                        val chooseAccountResult: ChooseAccountResult =
                            ChooseAccountUtils.chooseAccountById(userId = userId, accountType = AccountTypeEnum.VIA)
                        if (chooseAccountResult.chosenAccountId != 0u) {

                            val processSelectedAccountResult: InsertTransactionResult = processSelectedAccount(

                                selectedAccount = chooseAccountResult.chosenAccount!!,
                                userId = userId,
                                username = username,
                                transactionType = transactionType,
                                account1 = localInsertTransactionResult.fromAccount,
                                account2 = localInsertTransactionResult.toAccount,
                                purpose = AccountTypeEnum.VIA,
                                dateTimeInText = dateTimeInText,
                                transactionParticulars = transactionParticulars,
                                transactionAmount = transactionAmount
                            )
                            if (processSelectedAccountResult.isSuccess) {

                                localInsertTransactionResult = processSelectedAccountResult

                            } else {

                                localInsertTransactionResult.fromAccount = processSelectedAccountResult.fromAccount
                                localInsertTransactionResult.viaAccount = processSelectedAccountResult.viaAccount
                                localInsertTransactionResult.toAccount = processSelectedAccountResult.toAccount
                            }
                        }
                    } else {

                        invalidOptionMessage()
                    }
                }

                "17" -> {

                    localInsertTransactionResult = Screens.quickTransactionOnWallet(

                        insertTransactionResult = localInsertTransactionResult,
                        userId = userId,
                        username = username,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount
                    )
                }

                "18" -> {

                    localInsertTransactionResult = Screens.quickTransactionOnWalletToFrequent1(

                        insertTransactionResult = localInsertTransactionResult,
                        userId = userId,
                        username = username,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount
                    )
                }

                "19" -> {

                    localInsertTransactionResult = Screens.quickTransactionOnWalletToFrequent2(

                        insertTransactionResult = localInsertTransactionResult,
                        userId = userId,
                        username = username,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount
                    )
                }

                "20" -> {

                    localInsertTransactionResult = Screens.quickTransactionOnWalletToFrequent3(

                        insertTransactionResult = localInsertTransactionResult,
                        userId = userId,
                        username = username,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount
                    )
                }

                "21" -> {

                    localInsertTransactionResult = Screens.quickTransactionOnBank(

                        insertTransactionResult = localInsertTransactionResult,
                        userId = userId,
                        username = username,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount
                    )
                }

                "22" -> {

                    localInsertTransactionResult = Screens.quickTransactionOnBankToFrequent1(

                        insertTransactionResult = localInsertTransactionResult,
                        userId = userId,
                        username = username,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount
                    )
                }

                "23" -> {

                    localInsertTransactionResult = Screens.quickTransactionOnBankToFrequent2(

                        insertTransactionResult = localInsertTransactionResult,
                        userId = userId,
                        username = username,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount
                    )
                }

                "24" -> {

                    localInsertTransactionResult = Screens.quickTransactionOnBankToFrequent3(

                        insertTransactionResult = localInsertTransactionResult,
                        userId = userId,
                        username = username,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount
                    )
                }

                "25" -> {

                    localInsertTransactionResult = Screens.quickTransactionOnFrequent1(

                        insertTransactionResult = localInsertTransactionResult,
                        userId = userId,
                        username = username,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount
                    )
                }

                "26" -> {

                    localInsertTransactionResult = Screens.quickTransactionOnFrequent2(

                        insertTransactionResult = localInsertTransactionResult,
                        userId = userId,
                        username = username,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount
                    )
                }

                "27" -> {

                    localInsertTransactionResult = Screens.quickTransactionOnFrequent3(

                        insertTransactionResult = localInsertTransactionResult,
                        userId = userId,
                        username = username,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount
                    )
                }

                "0" -> {

                    return localInsertTransactionResult
                }

                else -> invalidOptionMessage()
            }
        } while (true)
    }

    private fun chooseFromAccount(userId: UInt): ChooseAccountResult {

        return ChooseAccountUtils.chooseAccountById(

            userId = userId, accountType = AccountTypeEnum.FROM
        )
    }

    private fun chooseToAccount(userId: UInt): ChooseAccountResult {

        return ChooseAccountUtils.chooseAccountById(

            userId = userId, accountType = AccountTypeEnum.TO
        )
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
        transactionAmount: Float,
        wantToExchange: Boolean = false,
        fromAccount: AccountResponse,
        viaAccount: AccountResponse,
        toAccount: AccountResponse

    ): InsertTransactionResult {

        var localFromAccount: AccountResponse = fromAccount
        var localViaAccount: AccountResponse = viaAccount
        var localToAccount: AccountResponse = toAccount

        if (chooseAccountResult.isAccountIdSelected) {

            val processSelectedAccountResult: InsertTransactionResult = processSelectedAccount(

                selectedAccount = chooseAccountResult.selectedAccount!!,
                userId = userId,
                username = username,
                transactionType = transactionType,
                account1 = account1,
                account2 = account2,
                purpose = purpose,
                dateTimeInText = dateTimeInText,
                transactionParticulars = transactionParticulars,
                transactionAmount = transactionAmount,
                wantToExchange = wantToExchange
            )
            if (processSelectedAccountResult.isSuccess) {

                return processSelectedAccountResult

            } else {

                localFromAccount = processSelectedAccountResult.fromAccount
                localViaAccount = processSelectedAccountResult.viaAccount
                localToAccount = processSelectedAccountResult.toAccount
            }
        }
        return InsertTransactionResult(

            isSuccess = false,
            dateTimeInText = dateTimeInText,
            transactionParticulars = transactionParticulars,
            transactionAmount = transactionAmount,
            fromAccount = localFromAccount,
            viaAccount = localViaAccount,
            toAccount = localToAccount
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
        transactionAmount: Float,
        wantToExchange: Boolean = false

    ): InsertTransactionResult {

        when (purpose) {

            AccountTypeEnum.TO -> {

                if (wantToExchange) {

                    return transactionContinueCheck(

                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        fromAccount = selectedAccount,
                        viaAccount = account2,
                        toAccount = account1,
                        dateTimeInText = dateTimeInText,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount
                    )

                } else {

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
            }

            AccountTypeEnum.FROM -> {

                if (wantToExchange) {

                    return transactionContinueCheck(

                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        fromAccount = account2,
                        viaAccount = account1,
                        toAccount = selectedAccount,
                        dateTimeInText = dateTimeInText,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount
                    )

                } else {

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
        isEditStep: Boolean = false,
        splitIndex: UInt = 0u

    ): InsertTransactionResult {

        var localDateTimeInText: String = dateTimeInText
        var localTransactionParticulars: String = transactionParticulars
        var localTransactionAmount: Float = transactionAmount

        var menuItems: List<String> = listOf(
            "\nUser : $username${getSplitIndicator(splitCount = splitIndex)}",
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

        commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
            listOfCommands = menuItems + listOf(
                // TODO : Option for Complete Back
                "Enter Time : "
            )
        )
        val localDateTimeInTextBackup: String = localDateTimeInText
        localDateTimeInText = enterDateWithTime(

            transactionType = transactionType,
            dateTimeInText = localDateTimeInText,
            isNotFromSplitTransaction = splitIndex == 0u
        )
        when (localDateTimeInText) {

            "D+Tr" -> {

                return addTransactionStep2(

                    userId = userId,
                    username = username,
                    transactionType = transactionType,
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    isViaStep = isViaStep,
                    isTwoWayStep = isTwoWayStep,
                    transactionId = transactionId,
                    dateTimeInText = DateTimeUtils.add1DayWith9ClockTimeToDateTimeInText(dateTimeInText = localDateTimeInTextBackup),
                    transactionParticulars = localTransactionParticulars,
                    transactionAmount = localTransactionAmount,
                    isEditStep = isEditStep,
                    splitIndex = splitIndex
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
                    isViaStep = isViaStep,
                    isTwoWayStep = isTwoWayStep,
                    transactionId = transactionId,
                    dateTimeInText = DateTimeUtils.add1DayToDateTimeInText(dateTimeInText = localDateTimeInTextBackup),
                    transactionParticulars = localTransactionParticulars,
                    transactionAmount = localTransactionAmount,
                    isEditStep = isEditStep,
                    splitIndex = splitIndex
                )
            }

            "D-" -> {

                return addTransactionStep2(

                    userId = userId,
                    username = username,
                    transactionType = transactionType,
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    isViaStep = isViaStep,
                    isTwoWayStep = isTwoWayStep,
                    transactionId = transactionId,
                    dateTimeInText = DateTimeUtils.subtract1DayToDateTimeInText(dateTimeInText = localDateTimeInTextBackup),
                    transactionParticulars = localTransactionParticulars,
                    transactionAmount = localTransactionAmount,
                    isEditStep = isEditStep,
                    splitIndex = splitIndex
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
                    isViaStep = isViaStep,
                    isTwoWayStep = isTwoWayStep,
                    transactionId = transactionId,
                    dateTimeInText = DateTimeUtils.add2DaysWith9ClockTimeToDateTimeInText(

                        dateTimeInText = localDateTimeInTextBackup
                    ),
                    transactionParticulars = localTransactionParticulars,
                    transactionAmount = localTransactionAmount,
                    isEditStep = isEditStep,
                    splitIndex = splitIndex
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
                    isViaStep = isViaStep,
                    isTwoWayStep = isTwoWayStep,
                    transactionId = transactionId,
                    dateTimeInText = DateTimeUtils.add2DaysToDateTimeInText(dateTimeInText = localDateTimeInTextBackup),
                    transactionParticulars = localTransactionParticulars,
                    transactionAmount = localTransactionAmount,
                    isEditStep = isEditStep,
                    splitIndex = splitIndex
                )
            }

            "D2-" -> {

                return addTransactionStep2(

                    userId = userId,
                    username = username,
                    transactionType = transactionType,
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    isViaStep = isViaStep,
                    isTwoWayStep = isTwoWayStep,
                    transactionId = transactionId,
                    dateTimeInText = DateTimeUtils.subtract2DaysToDateTimeInText(dateTimeInText = localDateTimeInTextBackup),
                    transactionParticulars = localTransactionParticulars,
                    transactionAmount = localTransactionAmount,
                    isEditStep = isEditStep,
                    splitIndex = splitIndex
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
                    dateTimeInText = localDateTimeInTextBackup,
                    transactionParticulars = localTransactionParticulars,
                    transactionAmount = localTransactionAmount,
                    accountExchangeType = AccountExchangeTypeEnum.FROM_AND_TO,
                    isEditStep = isEditStep,
                    isViaStep = isViaStep,
                    isTwoWayStep = isTwoWayStep,
                    transactionId = transactionId
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
                    dateTimeInText = localDateTimeInTextBackup,
                    transactionParticulars = localTransactionParticulars,
                    transactionAmount = localTransactionAmount,
                    accountExchangeType = AccountExchangeTypeEnum.FROM_AND_VIA,
                    isEditStep = isEditStep,
                    isViaStep = isViaStep,
                    isTwoWayStep = isTwoWayStep,
                    transactionId = transactionId
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
                    dateTimeInText = localDateTimeInTextBackup,
                    transactionParticulars = localTransactionParticulars,
                    transactionAmount = localTransactionAmount,
                    accountExchangeType = AccountExchangeTypeEnum.VIA_AND_TO,
                    isEditStep = isEditStep,
                    isViaStep = isViaStep,
                    isTwoWayStep = isTwoWayStep,
                    transactionId = transactionId
                )
            }

            "S" -> {

                print("Enter No. of Splits : ")
                val thresholdValue: UInt = 0u
                val noOfSplits: UInt = InputUtils.getGreaterUnsignedInt(

                    inputUInt = InputUtils.getValidUnsignedInt(

                        inputText = readLine()!!, invalidMessage = "Please Enter Valid Unsigned Integer"

                    ), thresholdValue = thresholdValue, constructInvalidMessage = fun(currentUInt: UInt): String {

                        return "Please Enter Unsigned Integer > $thresholdValue (Current Value is $currentUInt) : "
                    })

                var localInsertTransactionResult = InsertTransactionResult(

                    isSuccess = false,
                    dateTimeInText = localDateTimeInTextBackup,
                    transactionParticulars = localTransactionParticulars,
                    transactionAmount = localTransactionAmount,
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount
                )
                for (index: UInt in 1u..noOfSplits) {

                    localInsertTransactionResult = addTransactionStep2(

                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        isViaStep = isViaStep,
                        isTwoWayStep = isTwoWayStep,
                        transactionId = transactionId,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        splitIndex = index
                    )
                }
                return localInsertTransactionResult
            }

            "B" -> {

                return InsertTransactionResult(

                    isSuccess = false,
                    dateTimeInText = localDateTimeInTextBackup,
                    transactionParticulars = localTransactionParticulars,
                    transactionAmount = localTransactionAmount,
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount
                )
            }

            else -> {

                val reversedTransactionParticulars: String =
                    SentenceUtils.reverseOrderOfWords(sentence = localTransactionParticulars)
                print("Enter Particulars (Current Value - $localTransactionParticulars), R to Reverse (Reversed Value - $reversedTransactionParticulars), AS to Add Suffix, AP to Add Prefix : ")

                val transactionParticularsInput: String = readLine()!!
                if (transactionParticularsInput.isNotEmpty()) {

                    localTransactionParticulars = if (transactionParticularsInput == "R") {

                        reversedTransactionParticulars

                    } else if (transactionParticularsInput == "AS") {

                        print("Enter Suffix : ")
                        val transactionSuffixInput: String = readLine()!!
                        val suffixedTransactionParticulars = "$localTransactionParticulars$transactionSuffixInput"

                        do {
                            print("Particulars (Current Value - $localTransactionParticulars), (Suffixed Value - $suffixedTransactionParticulars), Do you want to Continue (Y/N) : ")

                            when (readLine()!!) {

                                "Y", "" -> {

                                    localTransactionParticulars = suffixedTransactionParticulars
                                    break
                                }

                                "N" -> {

                                    break
                                }

                                else -> {

                                    invalidOptionMessage()
                                }
                            }
                        } while (true)

                        localTransactionParticulars

                    } else if (transactionParticularsInput == "AP") {

                        print("Enter Prefix : ")
                        val transactionPrefixInput: String = readLine()!!
                        val prefixedTransactionParticulars = "$transactionPrefixInput$localTransactionParticulars"

                        do {
                            print("Particulars (Current Value - $localTransactionParticulars), (Preffixed Value - $prefixedTransactionParticulars), Do you want to Continue (Y/N) : ")

                            when (readLine()!!) {

                                "Y" -> {

                                    localTransactionParticulars = prefixedTransactionParticulars
                                }

                                "N" -> {

                                    break
                                }

                                else -> {

                                    invalidOptionMessage()
                                }
                            }
                        } while (true)

                        localTransactionParticulars

                    } else {

                        transactionParticularsInput
                    }
                }

                if (isTwoWayStep || isViaStep) {

                    // TODO : Prefix Particulars
                    // TODO : Suffix Particulars
                    // TODO : Other String Manipulations
                }

                print("Enter Amount (Current Value - $localTransactionAmount) : ")
                val transactionAmountInput

                        : String = readLine()!!
                if (transactionAmountInput.isNotEmpty()) {

                    localTransactionAmount = InputUtils.getValidFloat(
                        inputText = transactionAmountInput,
                        constructInvalidMessage = fun(inputText: String): String {
                            return "Invalid Amount, Enter Amount (Current Value - $inputText) : "
                        })
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
                    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                        listOfCommands = menuItems
                    )
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
                                            transactionAmount = localTransactionAmount,
                                            fromAccount = fromAccount,
                                            viaAccount = viaAccount,
                                            toAccount = toAccount
                                        )
                                    }

                                    TransactionTypeEnum.VIA -> {

                                        ToDoUtils.showTodo()
                                    }

                                    TransactionTypeEnum.TWO_WAY -> {

                                        ToDoUtils.showTodo()
                                    }
                                }

                            } else if (isTwoWayStep) {

                                return InsertTransactionResult(

                                    isSuccess = insertTransaction(

                                        userId = userId,
                                        eventDateTime = localDateTimeInText,
                                        particulars = localTransactionParticulars,
                                        amount = localTransactionAmount,
                                        fromAccount = toAccount,
                                        toAccount = fromAccount
                                    ),
                                    dateTimeInText = localDateTimeInText,
                                    transactionParticulars = localTransactionParticulars,
                                    transactionAmount = localTransactionAmount,
                                    fromAccount = fromAccount,
                                    viaAccount = viaAccount,
                                    toAccount = toAccount
                                )
                            } else if (isViaStep) {

                                return InsertTransactionResult(

                                    isSuccess = insertTransaction(

                                        userId = userId,
                                        eventDateTime = localDateTimeInText,
                                        particulars = localTransactionParticulars,
                                        amount = localTransactionAmount,
                                        fromAccount = viaAccount,
                                        toAccount = toAccount
                                    ),
                                    dateTimeInText = localDateTimeInText,
                                    transactionParticulars = localTransactionParticulars,
                                    transactionAmount = localTransactionAmount,
                                    fromAccount = fromAccount,
                                    viaAccount = viaAccount,
                                    toAccount = toAccount
                                )
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
                                            dateTimeInText = if (splitIndex > 0u) DateTimeUtils.add5MinutesToDateTimeInText(

                                                dateTimeInText = localDateTimeInText

                                            ) else localDateTimeInText,
                                            transactionParticulars = localTransactionParticulars,
                                            transactionAmount = localTransactionAmount,
                                            fromAccount = fromAccount,
                                            viaAccount = viaAccount,
                                            toAccount = toAccount
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
                                            dateTimeInText = if (splitIndex > 0u) DateTimeUtils.add5MinutesToDateTimeInText(

                                                dateTimeInText = localDateTimeInText

                                            ) else localDateTimeInText,
                                            transactionParticulars = localTransactionParticulars,
                                            transactionAmount = localTransactionAmount,
                                            fromAccount = fromAccount,
                                            viaAccount = viaAccount,
                                            toAccount = toAccount
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
                            isViaStep = isViaStep,
                            isTwoWayStep = isTwoWayStep,
                            transactionId = transactionId,
                            dateTimeInText = localDateTimeInText,
                            transactionParticulars = localTransactionParticulars,
                            transactionAmount = localTransactionAmount,
                            isEditStep = isEditStep,
                            splitIndex = splitIndex
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
                                    isEditStep = isEditStep,
                                    isViaStep = isViaStep,
                                    isTwoWayStep = isTwoWayStep,
                                    transactionId = transactionId
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
                                    isEditStep = isEditStep,
                                    isViaStep = isViaStep,
                                    isTwoWayStep = isTwoWayStep,
                                    transactionId = transactionId
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
                                    isEditStep = isEditStep,
                                    isViaStep = isViaStep,
                                    isTwoWayStep = isTwoWayStep,
                                    transactionId = transactionId
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
                                    isEditStep = isEditStep,
                                    isViaStep = isViaStep,
                                    isTwoWayStep = isTwoWayStep,
                                    transactionId = transactionId
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
                                transactionAmount = localTransactionAmount,
                                fromAccount = fromAccount,
                                viaAccount = viaAccount,
                                toAccount = toAccount
                            )
                        }

                        else -> invalidOptionMessage()
                    }
                } while (true)
            }
        }
    }

    private fun getSplitIndicator(splitCount: UInt): String {

        if (splitCount > 0u) {

            return " [S$splitCount]"
        }
        return ""
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
        isEditStep: Boolean,
        isViaStep: Boolean,
        isTwoWayStep: Boolean,
        transactionId: UInt

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
                    isViaStep = isViaStep,
                    isTwoWayStep = isTwoWayStep,
                    transactionId = transactionId,
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
                    isViaStep = isViaStep,
                    isTwoWayStep = isTwoWayStep,
                    transactionId = transactionId,
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
                    isViaStep = isViaStep,
                    isTwoWayStep = isTwoWayStep,
                    transactionId = transactionId,
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

        val eventDateTimeConversionResult: IsOkModel<String> =
            MysqlUtils.dateTimeTextConversionWithMessage(dateTimeTextConversionFunction = fun(): IsOkModel<String> {
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

                val readFrequencyOfAccountsFileResult: IsOkModel<FrequencyOfAccountsModel> = JsonFileUtils.readJsonFile(
                    fileName = Constants.frequencyOfAccountsFileName, isDevelopmentMode = App.isDevelopmentMode
                )

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
                            user = user, account = toAccount, frequencyOfAccounts = frequencyOfAccounts, userId = userId
                        )

                    } else {
                        frequencyOfAccounts.users = frequencyOfAccounts.users.plusElement(
                            element = getInitialAccountFrequencyForUser(
                                userId = userId, fromAccount = fromAccount, toAccount = toAccount
                            )
                        )
                    }
                    JsonFileUtils.writeJsonFile(
                        fileName = Constants.frequencyOfAccountsFileName, data = frequencyOfAccounts
                    )
                } else {

                    JsonFileUtils.writeJsonFile(
                        fileName = Constants.frequencyOfAccountsFileName, data = FrequencyOfAccountsModel(
                            users = listOf(
                                getInitialAccountFrequencyForUser(
                                    userId = userId, fromAccount = fromAccount, toAccount = toAccount
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

            val eventDateTimeConversionResult: IsOkModel<String> =
                MysqlUtils.dateTimeTextConversionWithMessage(dateTimeTextConversionFunction = fun(): IsOkModel<String> {
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

        user: UserModel, account: AccountResponse, frequencyOfAccounts: FrequencyOfAccountsModel, userId: UInt

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
                        accountID = account.id, accountName = account.fullName, countOfRepetition = 1u
                    )
                )

        } else {

            frequencyOfAccounts.users.find { localUser: UserModel -> localUser.id == userId }!!.accountFrequencies.find { localAccountFrequency: AccountFrequencyModel -> localAccountFrequency.accountID == account.id }!!.countOfRepetition++
        }
//        println("frequencyOfAccounts : $frequencyOfAccounts")
        return frequencyOfAccounts
    }

    private fun getInitialAccountFrequencyForUser(

        userId: UInt, fromAccount: AccountResponse, toAccount: AccountResponse

    ) = UserModel(

        id = userId, accountFrequencies = listOf(
            AccountFrequencyModel(
                accountID = fromAccount.id, accountName = fromAccount.fullName, countOfRepetition = 1u
            ), AccountFrequencyModel(
                accountID = toAccount.id, accountName = toAccount.fullName, countOfRepetition = 1u
            )
        )
    )

    private fun getEnvironmentVariableValueForInsertOperation(

        environmentVariableName: String, environmentVariableFormalName: String

    ): EnvironmentVariableForWholeNumber = EnvironmentFileOperations.getEnvironmentVariableValueForWholeNumber(

        dotenv = App.dotenv,
        environmentVariableName = environmentVariableName,
        environmentVariableFormalName = environmentVariableFormalName
    )

//    private fun automatedInsertTransaction(
//
//        userId: UInt,
//        eventDateTime: String,
//        particulars: String,
//        amount: Float,
//        fromAccount: AccountResponse,
//        toAccount: AccountResponse
//
//    ): Boolean {
//
//        commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
//            listOfCommands = listOf(
//                "\nTime - $eventDateTime",
//                "Withdraw Account - ${fromAccount.id} : ${fromAccount.fullName}",
//                "Deposit Account - ${toAccount.id} : ${toAccount.fullName}",
//                "Particulars - $particulars",
//                "Amount - $amount"
//            )
//        )
//        return insertTransaction(
//            userId = userId,
//            eventDateTime = eventDateTime,
//            particulars = particulars,
//            amount = amount,
//            fromAccount = fromAccount,
//            toAccount = toAccount
//        )
//    }
}