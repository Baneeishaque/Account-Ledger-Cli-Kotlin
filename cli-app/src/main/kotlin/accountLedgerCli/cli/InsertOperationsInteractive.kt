package accountLedgerCli.cli

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.api.response.TransactionManipulationResponse
import account.ledger.library.constants.EnvironmentalFileEntries
import account.ledger.library.enums.AccountExchangeTypeEnum
import account.ledger.library.enums.AccountTypeEnum
import account.ledger.library.enums.HandleAccountsApiResponseResult
import account.ledger.library.enums.TransactionTypeEnum
import account.ledger.library.models.ChooseAccountResult
import account.ledger.library.models.FrequencyOfAccountsModel
import account.ledger.library.models.InsertTransactionResult
import account.ledger.library.models.UserModel
import account.ledger.library.operations.InsertOperations
import account.ledger.library.retrofit.data.TransactionDataSource
import account.ledger.library.utils.ApiUtils
import account.ledger.library.utils.HandleResponses
import account.ledger.library.utils.TransactionUtils
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.cli.Screens.quickTransactionOnWallet
import accountLedgerCli.utils.ChooseAccountUtils
import account_ledger_library.constants.ConstantsNative
import common.utils.library.models.IsOkModel
import common.utils.library.utils.*
import io.github.cdimascio.dotenv.Dotenv
import kotlinx.coroutines.runBlocking

object InsertOperationsInteractive {

    internal val walletAccount: EnvironmentVariableForWholeNumber = getEnvironmentVariableValueForInsertOperation(

        environmentVariableName = EnvironmentalFileEntries.walletAccountId.entryName.name,
        environmentVariableFormalName = EnvironmentalFileEntries.walletAccountId.entryFormalName!!,
        dotenv = App.dotEnv
    )

    internal val frequent1Account: EnvironmentVariableForWholeNumber = getEnvironmentVariableValueForInsertOperation(
        environmentVariableName = EnvironmentalFileEntries.frequent1AccountId.entryName.name,
        environmentVariableFormalName = EnvironmentalFileEntries.frequent1AccountId.entryFormalName!!,
        dotenv = App.dotEnv
    )

    internal val frequent2Account: EnvironmentVariableForWholeNumber = getEnvironmentVariableValueForInsertOperation(
        environmentVariableName = EnvironmentalFileEntries.frequent2AccountId.entryName.name,
        environmentVariableFormalName = EnvironmentalFileEntries.frequent2AccountId.entryFormalName!!,
        dotenv = App.dotEnv
    )

    internal val frequent3Account: EnvironmentVariableForWholeNumber = getEnvironmentVariableValueForInsertOperation(
        environmentVariableName = EnvironmentalFileEntries.frequent3AccountId.entryName.name,
        environmentVariableFormalName = EnvironmentalFileEntries.frequent3AccountId.entryFormalName!!,
        dotenv = App.dotEnv
    )

    internal val bankAccount: EnvironmentVariableForWholeNumber = getEnvironmentVariableValueForInsertOperation(
        environmentVariableName = EnvironmentalFileEntries.bankAccountId.entryName.name,
        environmentVariableFormalName = EnvironmentalFileEntries.bankAccountId.entryFormalName!!,
        dotenv = App.dotEnv
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
        transactionAmount: Float,
        isDevelopmentMode: Boolean

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
                HandleResponses.getUserAccountsMap(
                    apiResponse = ApiUtils.getAccountsFull(

                        userId = userId,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode
                    )
                )

            return HandleResponsesCommon.isOkModelHandler(

                isOkModel = getUserAccountsMapResult,
                data = insertTransactionResult,
                successActions = fun(): InsertTransactionResult {

                    return transactionContinueCheck(
                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.NORMAL,
                        fromAccount = getUserAccountsMapResult.data!![account1.value]!!,
                        viaAccount = viaAccount,
                        toAccount = getUserAccountsMapResult.data!![account2.value]!!,
                        dateTimeInText = dateTimeInText,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode
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
        transactionAmount: Float,
        isDevelopmentMode: Boolean

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
                HandleResponses.getUserAccountsMap(
                    apiResponse = ApiUtils.getAccountsFull(

                        userId = userId,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode
                    )
                )

            return HandleResponsesCommon.isOkModelHandler(

                isOkModel = getUserAccountsMapResult,
                data = insertTransactionResult,
                successActions = fun(): InsertTransactionResult {

                    return Screens.accountHome(

                        userId = userId,
                        username = username,
                        fromAccount = getUserAccountsMapResult.data!![account.value]!!,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = dateTimeInText,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount,
                        isDevelopmentMode = isDevelopmentMode
                    )
                })
        }
        return insertTransactionResult
    }

    @JvmStatic
    internal fun addTransaction(

        userId: UInt,
        username: String,
        transactionType: TransactionTypeEnum,
        fromAccount: AccountResponse,
        viaAccount: AccountResponse,
        toAccount: AccountResponse,
        dateTimeInText: String,
        transactionParticulars: String,
        transactionAmount: Float,
        isDevelopmentMode: Boolean

    ): InsertTransactionResult {

        var localInsertTransactionResult = TransactionUtils.getFailedInsertTransactionResult(

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
                    transactionType = transactionType,
                    userId = userId,
                    isDevelopmentMode = isDevelopmentMode

                ) + listOf(

                    "1 - Choose To Account From List - Top Levels",
                    "2 - Choose To Account From List - Full Names",
                    "3 - Input To Account ID Directly",
                    "4 - Choose From Account From List - Top Levels",
                    "5 - Choose From Account From List - Full Names",
                    "6 - Input From Account ID Directly",
                    "7 - Continue Transaction",

                    if ((transactionType == TransactionTypeEnum.VIA) || (transactionType == TransactionTypeEnum.CYCLIC_VIA)) {

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
            when (readln()) {

                "1" -> {

                    localInsertTransactionResult = processChooseAccountResult(

                        chooseAccountResult = chooseDepositTop(

                            userId = userId,
                            isConsoleMode = true,
                            isDevelopmentMode = isDevelopmentMode
                        ),
                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        account1 = localInsertTransactionResult.fromAccount,
                        account2 = localInsertTransactionResult.viaAccount,
                        purpose = AccountTypeEnum.TO,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "2" -> {

                    localInsertTransactionResult = processChooseAccountResult(

                        chooseAccountResult = chooseDepositFull(

                            userId = userId,
                            isConsoleMode = true,
                            isDevelopmentMode = isDevelopmentMode
                        ),
                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        account1 = localInsertTransactionResult.fromAccount,
                        account2 = localInsertTransactionResult.viaAccount,
                        purpose = AccountTypeEnum.TO,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "3" -> {

                    val chooseToAccountResult: ChooseAccountResult = chooseToAccount(

                        userId = userId,
                        isDevelopmentMode = isDevelopmentMode
                    )
                    if (chooseToAccountResult.chosenAccountId != 0u) {

                        val processSelectedAccountResult: InsertTransactionResult = processSelectedAccount(

                            selectedAccount = chooseToAccountResult.chosenAccount!!,
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            account1 = localInsertTransactionResult.fromAccount,
                            account2 = localInsertTransactionResult.viaAccount,
                            purpose = AccountTypeEnum.TO,
                            dateTimeInText = localInsertTransactionResult.dateTimeInText,
                            transactionParticulars = localInsertTransactionResult.transactionParticulars,
                            transactionAmount = localInsertTransactionResult.transactionAmount,
                            isDevelopmentMode = isDevelopmentMode
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

                        chooseAccountResult = chooseWithdrawTop(

                            userId = userId,
                            isConsoleMode = true,
                            isDevelopmentMode = isDevelopmentMode
                        ),
                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        account1 = localInsertTransactionResult.viaAccount,
                        account2 = localInsertTransactionResult.toAccount,
                        purpose = AccountTypeEnum.FROM,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "5" -> {

                    localInsertTransactionResult = processChooseAccountResult(

                        chooseAccountResult = chooseWithdrawFull(

                            userId = userId,
                            isConsoleMode = true,
                            isDevelopmentMode = isDevelopmentMode
                        ),
                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        account1 = localInsertTransactionResult.viaAccount,
                        account2 = localInsertTransactionResult.toAccount,
                        purpose = AccountTypeEnum.FROM,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "6" -> {
                    val chooseFromAccountResult: ChooseAccountResult = chooseFromAccount(

                        userId = userId,
                        isDevelopmentMode = isDevelopmentMode
                    )
                    if (chooseFromAccountResult.chosenAccountId != 0u) {

                        val processSelectedAccountResult: InsertTransactionResult = processSelectedAccount(

                            selectedAccount = chooseFromAccountResult.chosenAccount!!,
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            account1 = localInsertTransactionResult.viaAccount,
                            account2 = localInsertTransactionResult.toAccount,
                            purpose = AccountTypeEnum.FROM,
                            dateTimeInText = localInsertTransactionResult.dateTimeInText,
                            transactionParticulars = localInsertTransactionResult.transactionParticulars,
                            transactionAmount = localInsertTransactionResult.transactionAmount,
                            isDevelopmentMode = isDevelopmentMode
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
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "8" -> {

                    if ((transactionType == TransactionTypeEnum.VIA) || (transactionType == TransactionTypeEnum.CYCLIC_VIA)) {

                        localInsertTransactionResult = addTransaction(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = localInsertTransactionResult.viaAccount,
                            viaAccount = localInsertTransactionResult.fromAccount,
                            toAccount = localInsertTransactionResult.toAccount,
                            dateTimeInText = localInsertTransactionResult.dateTimeInText,
                            transactionParticulars = localInsertTransactionResult.transactionParticulars,
                            transactionAmount = localInsertTransactionResult.transactionAmount,
                            isDevelopmentMode = isDevelopmentMode
                        )

                    } else {

                        localInsertTransactionResult = addTransaction(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = localInsertTransactionResult.toAccount,
                            viaAccount = localInsertTransactionResult.viaAccount,
                            toAccount = localInsertTransactionResult.fromAccount,
                            dateTimeInText = localInsertTransactionResult.dateTimeInText,
                            transactionParticulars = localInsertTransactionResult.transactionParticulars,
                            transactionAmount = localInsertTransactionResult.transactionAmount,
                            isDevelopmentMode = isDevelopmentMode
                        )
                    }
                }

                "9" -> {

                    if ((transactionType == TransactionTypeEnum.VIA) || (transactionType == TransactionTypeEnum.CYCLIC_VIA)) {

                        localInsertTransactionResult = transactionContinueCheck(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = localInsertTransactionResult.viaAccount,
                            viaAccount = localInsertTransactionResult.fromAccount,
                            toAccount = localInsertTransactionResult.toAccount,
                            dateTimeInText = localInsertTransactionResult.dateTimeInText,
                            transactionParticulars = localInsertTransactionResult.transactionParticulars,
                            transactionAmount = localInsertTransactionResult.transactionAmount,
                            isConsoleMode = true,
                            isDevelopmentMode = isDevelopmentMode
                        )
                    } else {

                        localInsertTransactionResult = transactionContinueCheck(

                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = localInsertTransactionResult.toAccount,
                            viaAccount = localInsertTransactionResult.viaAccount,
                            toAccount = localInsertTransactionResult.fromAccount,
                            dateTimeInText = localInsertTransactionResult.dateTimeInText,
                            transactionParticulars = localInsertTransactionResult.transactionParticulars,
                            transactionAmount = localInsertTransactionResult.transactionAmount,
                            isConsoleMode = true,
                            isDevelopmentMode = isDevelopmentMode
                        )
                    }
                }

                "10" -> {
                    if ((transactionType == TransactionTypeEnum.VIA) || (transactionType == TransactionTypeEnum.CYCLIC_VIA)) {

                        localInsertTransactionResult = addTransaction(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = localInsertTransactionResult.fromAccount,
                            viaAccount = localInsertTransactionResult.toAccount,
                            toAccount = localInsertTransactionResult.viaAccount,
                            dateTimeInText = localInsertTransactionResult.dateTimeInText,
                            transactionParticulars = localInsertTransactionResult.transactionParticulars,
                            transactionAmount = localInsertTransactionResult.transactionAmount,
                            isDevelopmentMode = isDevelopmentMode
                        )
                    } else {

                        val chooseToAccountResult: ChooseAccountResult = chooseToAccount(

                            userId = userId,
                            isDevelopmentMode = isDevelopmentMode
                        )
                        if (chooseToAccountResult.chosenAccountId != 0u) {

                            val processSelectedAccountResult: InsertTransactionResult = processSelectedAccount(

                                selectedAccount = chooseToAccountResult.chosenAccount!!,
                                userId = userId,
                                username = username,
                                transactionType = transactionType,
                                account1 = localInsertTransactionResult.fromAccount,
                                account2 = localInsertTransactionResult.viaAccount,
                                purpose = AccountTypeEnum.TO,
                                dateTimeInText = localInsertTransactionResult.dateTimeInText,
                                transactionParticulars = localInsertTransactionResult.transactionParticulars,
                                transactionAmount = localInsertTransactionResult.transactionAmount,
                                wantToExchange = true,
                                isDevelopmentMode = isDevelopmentMode
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
                    if ((transactionType == TransactionTypeEnum.VIA) || (transactionType == TransactionTypeEnum.CYCLIC_VIA)) {

                        localInsertTransactionResult = transactionContinueCheck(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = localInsertTransactionResult.fromAccount,
                            viaAccount = localInsertTransactionResult.toAccount,
                            toAccount = localInsertTransactionResult.viaAccount,
                            dateTimeInText = localInsertTransactionResult.dateTimeInText,
                            transactionParticulars = localInsertTransactionResult.transactionParticulars,
                            transactionAmount = localInsertTransactionResult.transactionAmount,
                            isConsoleMode = true,
                            isDevelopmentMode = isDevelopmentMode
                        )
                    } else {

                        val chooseFromAccountResult: ChooseAccountResult = chooseFromAccount(

                            userId = userId,
                            isDevelopmentMode = isDevelopmentMode
                        )
                        if (chooseFromAccountResult.chosenAccountId != 0u) {

                            val processSelectedAccountResult: InsertTransactionResult = processSelectedAccount(

                                selectedAccount = chooseFromAccountResult.chosenAccount!!,
                                userId = userId,
                                username = username,
                                transactionType = transactionType,
                                account1 = localInsertTransactionResult.viaAccount,
                                account2 = localInsertTransactionResult.toAccount,
                                purpose = AccountTypeEnum.FROM,
                                dateTimeInText = localInsertTransactionResult.dateTimeInText,
                                transactionParticulars = localInsertTransactionResult.transactionParticulars,
                                transactionAmount = localInsertTransactionResult.transactionAmount,
                                wantToExchange = true,
                                isDevelopmentMode = isDevelopmentMode
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
                    if ((transactionType == TransactionTypeEnum.VIA) || (transactionType == TransactionTypeEnum.CYCLIC_VIA)) {

                        localInsertTransactionResult = addTransaction(

                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = localInsertTransactionResult.toAccount,
                            viaAccount = localInsertTransactionResult.viaAccount,
                            toAccount = localInsertTransactionResult.fromAccount,
                            dateTimeInText = localInsertTransactionResult.dateTimeInText,
                            transactionParticulars = localInsertTransactionResult.transactionParticulars,
                            transactionAmount = localInsertTransactionResult.transactionAmount,
                            isDevelopmentMode = isDevelopmentMode
                        )

                    } else {

                        localInsertTransactionResult = processChooseAccountResult(

                            chooseAccountResult = chooseDepositTop(

                                userId = userId,
                                isConsoleMode = true,
                                isDevelopmentMode = isDevelopmentMode
                            ),
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            account1 = localInsertTransactionResult.fromAccount,
                            account2 = localInsertTransactionResult.viaAccount,
                            purpose = AccountTypeEnum.TO,
                            dateTimeInText = localInsertTransactionResult.dateTimeInText,
                            transactionParticulars = localInsertTransactionResult.transactionParticulars,
                            transactionAmount = localInsertTransactionResult.transactionAmount,
                            wantToExchange = true,
                            fromAccount = localInsertTransactionResult.fromAccount,
                            viaAccount = localInsertTransactionResult.viaAccount,
                            toAccount = localInsertTransactionResult.toAccount,
                            isDevelopmentMode = isDevelopmentMode
                        )
                    }
                }

                "13" -> {
                    if ((transactionType == TransactionTypeEnum.VIA) || (transactionType == TransactionTypeEnum.CYCLIC_VIA)) {

                        localInsertTransactionResult = transactionContinueCheck(

                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = localInsertTransactionResult.toAccount,
                            viaAccount = localInsertTransactionResult.viaAccount,
                            toAccount = localInsertTransactionResult.fromAccount,
                            dateTimeInText = localInsertTransactionResult.dateTimeInText,
                            transactionParticulars = localInsertTransactionResult.transactionParticulars,
                            transactionAmount = localInsertTransactionResult.transactionAmount,
                            isConsoleMode = true,
                            isDevelopmentMode = isDevelopmentMode
                        )
                    } else {

                        localInsertTransactionResult = processChooseAccountResult(

                            chooseAccountResult = chooseDepositFull(

                                userId = userId,
                                isConsoleMode = true,
                                isDevelopmentMode = isDevelopmentMode
                            ),
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            account1 = localInsertTransactionResult.fromAccount,
                            account2 = localInsertTransactionResult.viaAccount,
                            purpose = AccountTypeEnum.TO,
                            dateTimeInText = localInsertTransactionResult.dateTimeInText,
                            transactionParticulars = localInsertTransactionResult.transactionParticulars,
                            transactionAmount = localInsertTransactionResult.transactionAmount,
                            wantToExchange = true,
                            fromAccount = localInsertTransactionResult.fromAccount,
                            viaAccount = localInsertTransactionResult.viaAccount,
                            toAccount = localInsertTransactionResult.toAccount,
                            isDevelopmentMode = isDevelopmentMode
                        )
                    }
                }

                "14" -> {

                    if ((transactionType == TransactionTypeEnum.VIA) || (transactionType == TransactionTypeEnum.CYCLIC_VIA)) {

                        localInsertTransactionResult = processChooseAccountResult(

                            chooseAccountResult = chooseViaTop(

                                userId = userId,
                                isConsoleMode = true,
                                isDevelopmentMode = isDevelopmentMode
                            ),
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            account1 = localInsertTransactionResult.fromAccount,
                            account2 = localInsertTransactionResult.toAccount,
                            purpose = AccountTypeEnum.VIA,
                            dateTimeInText = localInsertTransactionResult.dateTimeInText,
                            transactionParticulars = localInsertTransactionResult.transactionParticulars,
                            transactionAmount = localInsertTransactionResult.transactionAmount,
                            fromAccount = localInsertTransactionResult.fromAccount,
                            viaAccount = localInsertTransactionResult.viaAccount,
                            toAccount = localInsertTransactionResult.toAccount,
                            isDevelopmentMode = isDevelopmentMode
                        )
                    } else {

                        localInsertTransactionResult = processChooseAccountResult(

                            chooseAccountResult = chooseWithdrawTop(

                                userId = userId,
                                isConsoleMode = true,
                                isDevelopmentMode = isDevelopmentMode
                            ),
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            account1 = localInsertTransactionResult.viaAccount,
                            account2 = localInsertTransactionResult.toAccount,
                            purpose = AccountTypeEnum.FROM,
                            dateTimeInText = localInsertTransactionResult.dateTimeInText,
                            transactionParticulars = localInsertTransactionResult.transactionParticulars,
                            transactionAmount = localInsertTransactionResult.transactionAmount,
                            wantToExchange = true,
                            fromAccount = localInsertTransactionResult.fromAccount,
                            viaAccount = localInsertTransactionResult.viaAccount,
                            toAccount = localInsertTransactionResult.toAccount,
                            isDevelopmentMode = isDevelopmentMode
                        )
                    }
                }

                "15" -> {

                    if ((transactionType == TransactionTypeEnum.VIA) || (transactionType == TransactionTypeEnum.CYCLIC_VIA)) {

                        localInsertTransactionResult = processChooseAccountResult(

                            chooseAccountResult = chooseViaFull(

                                userId = userId,
                                isConsoleMode = true,
                                isDevelopmentMode = isDevelopmentMode
                            ),
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            account1 = localInsertTransactionResult.fromAccount,
                            account2 = localInsertTransactionResult.toAccount,
                            purpose = AccountTypeEnum.VIA,
                            dateTimeInText = localInsertTransactionResult.dateTimeInText,
                            transactionParticulars = localInsertTransactionResult.transactionParticulars,
                            transactionAmount = localInsertTransactionResult.transactionAmount,
                            fromAccount = localInsertTransactionResult.fromAccount,
                            viaAccount = localInsertTransactionResult.viaAccount,
                            toAccount = localInsertTransactionResult.toAccount,
                            isDevelopmentMode = isDevelopmentMode
                        )
                    } else {

                        localInsertTransactionResult = processChooseAccountResult(

                            chooseAccountResult = chooseWithdrawFull(

                                userId = userId,
                                isConsoleMode = true,
                                isDevelopmentMode = isDevelopmentMode
                            ),
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            account1 = localInsertTransactionResult.viaAccount,
                            account2 = localInsertTransactionResult.toAccount,
                            purpose = AccountTypeEnum.FROM,
                            dateTimeInText = localInsertTransactionResult.dateTimeInText,
                            transactionParticulars = localInsertTransactionResult.transactionParticulars,
                            transactionAmount = localInsertTransactionResult.transactionAmount,
                            wantToExchange = true,
                            fromAccount = localInsertTransactionResult.fromAccount,
                            viaAccount = localInsertTransactionResult.viaAccount,
                            toAccount = localInsertTransactionResult.toAccount,
                            isDevelopmentMode = isDevelopmentMode
                        )
                    }
                }

                "16" -> {

                    if ((transactionType == TransactionTypeEnum.VIA) || (transactionType == TransactionTypeEnum.CYCLIC_VIA)) {

                        val chooseAccountResult: ChooseAccountResult =
                            ChooseAccountUtils.chooseAccountById(

                                userId = userId,
                                accountType = AccountTypeEnum.VIA,
                                isDevelopmentMode = isDevelopmentMode
                            )
                        if (chooseAccountResult.chosenAccountId != 0u) {

                            val processSelectedAccountResult: InsertTransactionResult = processSelectedAccount(

                                selectedAccount = chooseAccountResult.chosenAccount!!,
                                userId = userId,
                                username = username,
                                transactionType = transactionType,
                                account1 = localInsertTransactionResult.fromAccount,
                                account2 = localInsertTransactionResult.toAccount,
                                purpose = AccountTypeEnum.VIA,
                                dateTimeInText = localInsertTransactionResult.dateTimeInText,
                                transactionParticulars = localInsertTransactionResult.transactionParticulars,
                                transactionAmount = localInsertTransactionResult.transactionAmount,
                                isDevelopmentMode = isDevelopmentMode
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

                        InteractiveUtils.invalidOptionMessage()
                    }
                }

                "17" -> {

                    localInsertTransactionResult = quickTransactionOnWallet(

                        previousTransactionData = localInsertTransactionResult,
                        userId = userId,
                        username = username,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "18" -> {

                    localInsertTransactionResult = Screens.quickTransactionOnWalletToFrequent1(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "19" -> {

                    localInsertTransactionResult = Screens.quickTransactionOnWalletToFrequent2(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "20" -> {

                    localInsertTransactionResult = Screens.quickTransactionOnWalletToFrequent3(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "21" -> {

                    localInsertTransactionResult = Screens.quickTransactionOnBank(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "22" -> {

                    localInsertTransactionResult = Screens.quickTransactionOnBankToFrequent1(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "23" -> {

                    localInsertTransactionResult = Screens.quickTransactionOnBankToFrequent2(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "24" -> {

                    localInsertTransactionResult = Screens.quickTransactionOnBankToFrequent3(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "25" -> {

                    localInsertTransactionResult = Screens.quickTransactionOnFrequent1(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "26" -> {

                    localInsertTransactionResult = Screens.quickTransactionOnFrequent2(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "27" -> {

                    localInsertTransactionResult = Screens.quickTransactionOnFrequent3(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "0" -> {

                    return localInsertTransactionResult
                }

                else -> InteractiveUtils.invalidOptionMessage()
            }
        } while (true)
    }

    private fun chooseFromAccount(

        userId: UInt,
        isDevelopmentMode: Boolean

    ): ChooseAccountResult {

        return ChooseAccountUtils.chooseAccountById(

            userId = userId,
            accountType = AccountTypeEnum.FROM,
            isDevelopmentMode = isDevelopmentMode
        )
    }

    private fun chooseToAccount(userId: UInt, isDevelopmentMode: Boolean): ChooseAccountResult {

        return ChooseAccountUtils.chooseAccountById(

            userId = userId,
            accountType = AccountTypeEnum.TO,
            isDevelopmentMode = isDevelopmentMode
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
        toAccount: AccountResponse,
        isDevelopmentMode: Boolean

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
                wantToExchange = wantToExchange,
                isDevelopmentMode = isDevelopmentMode
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
        wantToExchange: Boolean = false,
        isDevelopmentMode: Boolean

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
                        transactionAmount = transactionAmount,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode
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
                        transactionAmount = transactionAmount,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode
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
                        transactionAmount = transactionAmount,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode
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
                        transactionAmount = transactionAmount,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode
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
                    transactionAmount = transactionAmount,
                    isConsoleMode = true,
                    isDevelopmentMode = isDevelopmentMode
                )
            }
        }
    }

    fun insertTransactionVariantsInteractive(

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
        splitIndex: UInt = 0u,
        isConsoleMode: Boolean = true,
        isDevelopmentMode: Boolean,
        isCyclicViaStep: Boolean = false

    ): InsertTransactionResult {

        var localDateTimeInText: String = dateTimeInText

        var menuItems: List<String> = listOf(
            "User : $username${getSplitIndicator(splitCount = splitIndex)}",
            "Withdraw Account - ${fromAccount.id} : ${fromAccount.fullName}",
        )
        if ((transactionType == TransactionTypeEnum.VIA) || (transactionType == TransactionTypeEnum.CYCLIC_VIA)) {
            menuItems = menuItems + listOf(
                "Intermediate Account - ${viaAccount.id} : ${viaAccount.fullName}",
            )
        }
        menuItems = menuItems + listOf(
            "Deposit Account - ${toAccount.id} : ${toAccount.fullName}",
        )

        localDateTimeInText = enterDateWithTime(

            promptCommands = menuItems,
            transactionType = transactionType,
            dateTimeInText = localDateTimeInText,
            isNotFromSplitTransaction = splitIndex == 0u
        )
        when (localDateTimeInText) {

            "Ex", "Ex13" -> {

                return invokeInsertTransactionVariantsInteractiveAfterExchangeOfAccounts(

                    userId = userId,
                    username = username,
                    transactionType = transactionType,
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount,
                    accountExchangeType = AccountExchangeTypeEnum.FROM_AND_TO,
                    isEditStep = isEditStep,
                    isViaStep = isViaStep,
                    isTwoWayStep = isTwoWayStep,
                    transactionId = transactionId,
                    isDevelopmentMode = isDevelopmentMode,
                    isCyclicViaStep = isCyclicViaStep
                )
            }

            "Ex12" -> {

                return invokeInsertTransactionVariantsInteractiveAfterExchangeOfAccounts(

                    userId = userId,
                    username = username,
                    transactionType = transactionType,
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount,
                    accountExchangeType = AccountExchangeTypeEnum.FROM_AND_VIA,
                    isEditStep = isEditStep,
                    isViaStep = isViaStep,
                    isTwoWayStep = isTwoWayStep,
                    transactionId = transactionId,
                    isDevelopmentMode = isDevelopmentMode,
                    isCyclicViaStep = isCyclicViaStep
                )
            }

            "Ex23" -> {

                return invokeInsertTransactionVariantsInteractiveAfterExchangeOfAccounts(

                    userId = userId,
                    username = username,
                    transactionType = transactionType,
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount,
                    accountExchangeType = AccountExchangeTypeEnum.VIA_AND_TO,
                    isEditStep = isEditStep,
                    isViaStep = isViaStep,
                    isTwoWayStep = isTwoWayStep,
                    transactionId = transactionId,
                    isDevelopmentMode = isDevelopmentMode,
                    isCyclicViaStep = isCyclicViaStep,
                )
            }

            "S" -> {

                print("Enter No. of Splits : ")
                val thresholdValue = 0u
                val noOfSplits: UInt = InputUtils.getGreaterUnsignedInt(

                    inputUInt = InputUtils.getValidUnsignedInt(

                        inputText = readln(), invalidMessage = "Please Enter Valid Unsigned Integer"

                    ), thresholdValue = thresholdValue, constructInvalidMessage = fun(currentUInt: UInt): String {

                        return "Please Enter Unsigned Integer > $thresholdValue (Current Value is $currentUInt) : "
                    })

                var localInsertTransactionResult = InsertTransactionResult(

                    isSuccess = false,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount,
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount
                )
                for (index: UInt in 1u..noOfSplits) {

                    localInsertTransactionResult = insertTransactionVariantsInteractive(

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
                        splitIndex = index,
                        isDevelopmentMode = isDevelopmentMode,
                        isCyclicViaStep = isCyclicViaStep
                    )
                }
                return localInsertTransactionResult
            }

            "B" -> {

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

            else -> {


                val timeResetCommand: MatchResult? =
                    ConstantsNative.timeResetPatternRegex.matchEntire(input = localDateTimeInText)
                if (timeResetCommand == null) {

                    return timePartIncrementOrDecrementActions(

                        timePartIncrementOrDecrementMatchResult = ConstantsNative.hourIncrementOrDecrementPatternRegex.matchEntire(
                            input = localDateTimeInText
                        ),
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount,
                        dateTimeInText = dateTimeInText,
                        fromAccount = fromAccount,
                        transactionType = transactionType,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        isEditStep = isEditStep,
                        transactionId = transactionId,
                        isDevelopmentMode = isDevelopmentMode,
                        isTwoWayStep = isTwoWayStep,
                        userId = userId,
                        isViaStep = isViaStep,
                        isCyclicViaStep = isCyclicViaStep,
                        splitIndex = splitIndex,
                        username = username,
                        timePartIncrementOrDecrementCommandIndicator = ConstantsNative.hourIncrementOrDecrementCommandIndicator
                    ) {

                        timePartIncrementOrDecrementActions(

                            timePartIncrementOrDecrementMatchResult = ConstantsNative.minuteIncrementOrDecrementPatternRegex.matchEntire(
                                input = localDateTimeInText
                            ),
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount,
                            dateTimeInText = dateTimeInText,
                            fromAccount = fromAccount,
                            transactionType = transactionType,
                            viaAccount = viaAccount,
                            toAccount = toAccount,
                            isEditStep = isEditStep,
                            transactionId = transactionId,
                            isDevelopmentMode = isDevelopmentMode,
                            isTwoWayStep = isTwoWayStep,
                            userId = userId,
                            isViaStep = isViaStep,
                            isCyclicViaStep = isCyclicViaStep,
                            splitIndex = splitIndex,
                            username = username,
                            timePartIncrementOrDecrementCommandIndicator = ConstantsNative.minuteIncrementOrDecrementCommandIndicator,
                            timePartIncrementOrDecrementNoMatchAction = {

                                timePartIncrementOrDecrementActions(

                                    timePartIncrementOrDecrementMatchResult = ConstantsNative.secondIncrementOrDecrementPatternRegex.matchEntire(
                                        input = localDateTimeInText
                                    ),
                                    transactionParticulars = transactionParticulars,
                                    transactionAmount = transactionAmount,
                                    dateTimeInText = dateTimeInText,
                                    fromAccount = fromAccount,
                                    transactionType = transactionType,
                                    viaAccount = viaAccount,
                                    toAccount = toAccount,
                                    isEditStep = isEditStep,
                                    transactionId = transactionId,
                                    isDevelopmentMode = isDevelopmentMode,
                                    isTwoWayStep = isTwoWayStep,
                                    userId = userId,
                                    isViaStep = isViaStep,
                                    isCyclicViaStep = isCyclicViaStep,
                                    splitIndex = splitIndex,
                                    username = username,
                                    timePartIncrementOrDecrementCommandIndicator = ConstantsNative.secondIncrementOrDecrementCommandIndicator,
                                    timePartIncrementOrDecrementNoMatchAction = {

                                        timePartIncrementOrDecrementActions(

                                            timePartIncrementOrDecrementMatchResult = ConstantsNative.dayIncrementOrDecrementPatternRegex.matchEntire(
                                                input = localDateTimeInText
                                            ),
                                            transactionParticulars = transactionParticulars,
                                            transactionAmount = transactionAmount,
                                            dateTimeInText = dateTimeInText,
                                            fromAccount = fromAccount,
                                            transactionType = transactionType,
                                            viaAccount = viaAccount,
                                            toAccount = toAccount,
                                            isEditStep = isEditStep,
                                            transactionId = transactionId,
                                            isDevelopmentMode = isDevelopmentMode,
                                            isTwoWayStep = isTwoWayStep,
                                            userId = userId,
                                            isViaStep = isViaStep,
                                            isCyclicViaStep = isCyclicViaStep,
                                            splitIndex = splitIndex,
                                            username = username,
                                            timePartIncrementOrDecrementCommandIndicator = ConstantsNative.dayIncrementOrDecrementCommandIndicator,
                                            timePartIncrementOrDecrementNoMatchAction = {

                                                val timePartIncrementOrDecrementMatchResult =
                                                    ConstantsNative.dayIncrementOrDecrementWithTimeResetPatternRegex.matchEntire(
                                                        input = localDateTimeInText
                                                    )
                                                if (timePartIncrementOrDecrementMatchResult == null) {

                                                    insertTransactionAfterEventDateTimeFix(

                                                        transactionParticulars = transactionParticulars,
                                                        transactionAmount = transactionAmount,
                                                        dateTimeInText = dateTimeInText,
                                                        fromAccount = fromAccount,
                                                        transactionType = transactionType,
                                                        viaAccount = viaAccount,
                                                        toAccount = toAccount,
                                                        isEditStep = isEditStep,
                                                        transactionId = transactionId,
                                                        isDevelopmentMode = isDevelopmentMode,
                                                        isTwoWayStep = isTwoWayStep,
                                                        userId = userId,
                                                        isViaStep = isViaStep,
                                                        isCyclicViaStep = isCyclicViaStep,
                                                        splitIndex = splitIndex,
                                                        username = username
                                                    )

                                                } else {

                                                    handleTimeResetPattern(

                                                        dateTimeInText = handleDateIncrementOrDecrementPattern(

                                                            timePart = timePartIncrementOrDecrementMatchResult.groups[1],
                                                            dateTimeInText = dateTimeInText,
                                                            timePartIncrementOrDecrementMatchResult = timePartIncrementOrDecrementMatchResult,
                                                            manipulationOperatorPosition = 3
                                                        ),
                                                        timeResetCommand = ConstantsNative.timeResetPatternRegex.matchEntire(
                                                            input = timePartIncrementOrDecrementMatchResult.groups[4]!!.value
                                                        )!!,
                                                        userId = userId,
                                                        username = username,
                                                        transactionType = transactionType,
                                                        fromAccount = fromAccount,
                                                        viaAccount = viaAccount,
                                                        toAccount = toAccount,
                                                        isViaStep = isViaStep,
                                                        isTwoWayStep = isTwoWayStep,
                                                        transactionId = transactionId,
                                                        transactionParticulars = transactionParticulars,
                                                        transactionAmount = transactionAmount,
                                                        isEditStep = isEditStep,
                                                        splitIndex = splitIndex,
                                                        isDevelopmentMode = isDevelopmentMode,
                                                        isCyclicViaStep = isCyclicViaStep
                                                    )
                                                }
                                            }
                                        )
                                    }
                                )
                            }
                        )
                    }
                } else {

                    return handleTimeResetPattern(

                        dateTimeInText = dateTimeInText,
                        timeResetCommand = timeResetCommand,
                        userId = userId,
                        username = username,
                        transactionType = transactionType,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        isViaStep = isViaStep,
                        isTwoWayStep = isTwoWayStep,
                        transactionId = transactionId,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount,
                        isEditStep = isEditStep,
                        splitIndex = splitIndex,
                        isDevelopmentMode = isDevelopmentMode,
                        isCyclicViaStep = isCyclicViaStep
                    )
                }
            }
        }
    }

    private fun handleTimeResetPattern(

        dateTimeInText: String,
        timeResetCommand: MatchResult,
        userId: UInt,
        username: String,
        transactionType: TransactionTypeEnum,
        fromAccount: AccountResponse,
        viaAccount: AccountResponse,
        toAccount: AccountResponse,
        isViaStep: Boolean,
        isTwoWayStep: Boolean,
        transactionId: UInt,
        transactionParticulars: String,
        transactionAmount: Float,
        isEditStep: Boolean,
        splitIndex: UInt,
        isDevelopmentMode: Boolean,
        isCyclicViaStep: Boolean

    ): InsertTransactionResult {

        var localDateTimeInText = dateTimeInText
        val timePart: MatchGroup? = timeResetCommand.groups.first()
        if (timePart!!.value == ConstantsNative.timeResetCommandIndicator) {

            localDateTimeInText = DateTimeUtils.resetTimeOnNormalDateTimeInTextToX(
                dateTimeInText = dateTimeInText
            )
        } else {

            val timePartValue: String = timeResetCommand.groups[1]!!.value
            if (timePartValue.contains(other = ":")) {

                val timeParts = timePartValue.split(":")
                when (timeParts.size) {

                    2 -> {
                        localDateTimeInText = DateTimeUtils.resetTimeOnNormalDateTimeInTextToX(

                            dateTimeInText = dateTimeInText,
                            resetHour = timeParts.first().toInt(),
                            resetMinute = timeParts[1].toInt()
                        )
                    }

                    3 -> {
                        localDateTimeInText = DateTimeUtils.resetTimeOnNormalDateTimeInTextToX(

                            dateTimeInText = dateTimeInText,
                            resetHour = timeParts.first().toInt(),
                            resetMinute = timeParts[1].toInt(),
                            resetSecond = timeParts[2].toInt()
                        )
                    }
                }
            } else {

                localDateTimeInText = DateTimeUtils.resetTimeOnNormalDateTimeInTextToX(

                    dateTimeInText = dateTimeInText,
                    resetHour = timePartValue.toInt()
                )
            }
        }
        return insertTransactionVariantsInteractive(

            userId = userId,
            username = username,
            transactionType = transactionType,
            fromAccount = fromAccount,
            viaAccount = viaAccount,
            toAccount = toAccount,
            isViaStep = isViaStep,
            isTwoWayStep = isTwoWayStep,
            transactionId = transactionId,
            dateTimeInText = localDateTimeInText,
            transactionParticulars = transactionParticulars,
            transactionAmount = transactionAmount,
            isEditStep = isEditStep,
            splitIndex = splitIndex,
            isDevelopmentMode = isDevelopmentMode,
            isCyclicViaStep = isCyclicViaStep
        )
    }

    private fun timePartIncrementOrDecrementActions(

        timePartIncrementOrDecrementMatchResult: MatchResult?,
        transactionParticulars: String,
        transactionAmount: Float,
        dateTimeInText: String,
        fromAccount: AccountResponse,
        transactionType: TransactionTypeEnum,
        viaAccount: AccountResponse,
        toAccount: AccountResponse,
        isEditStep: Boolean,
        transactionId: UInt,
        isDevelopmentMode: Boolean,
        isTwoWayStep: Boolean,
        userId: UInt,
        isViaStep: Boolean,
        isCyclicViaStep: Boolean,
        splitIndex: UInt,
        username: String,
        timePartIncrementOrDecrementCommandIndicator: String,
        timePartIncrementOrDecrementNoMatchAction: () -> InsertTransactionResult

    ): InsertTransactionResult {

        if (timePartIncrementOrDecrementMatchResult == null) {

            return timePartIncrementOrDecrementNoMatchAction.invoke()

        } else {

            val timePart: MatchGroup? = timePartIncrementOrDecrementMatchResult.groups.first()

            var localDateTimeInText = dateTimeInText
            when (timePartIncrementOrDecrementCommandIndicator) {

                ConstantsNative.hourIncrementOrDecrementCommandIndicator -> {

                    if (timePart!!.value == ("$timePartIncrementOrDecrementCommandIndicator+")) {

                        localDateTimeInText = DateTimeUtils.addHoursToNormalDateTimeInText(

                            dateTimeInText = dateTimeInText,
                            hours = 1
                        )

                    } else if (timePart.value == ("$timePartIncrementOrDecrementCommandIndicator-")) {

                        localDateTimeInText = DateTimeUtils.subtractHoursFromNormalDateTimeInText(

                            dateTimeInText = dateTimeInText,
                            hours = 1
                        )

                    } else if (timePartIncrementOrDecrementMatchResult.groups[2]!!.value == "+") {

                        localDateTimeInText = DateTimeUtils.addHoursToNormalDateTimeInText(

                            dateTimeInText = dateTimeInText,
                            hours = timePartIncrementOrDecrementMatchResult.groups[1]!!.value.toInt()
                        )

                    } else if (timePartIncrementOrDecrementMatchResult.groups[2]!!.value == "-") {

                        localDateTimeInText = DateTimeUtils.subtractHoursFromNormalDateTimeInText(

                            dateTimeInText = dateTimeInText,
                            hours = timePartIncrementOrDecrementMatchResult.groups[1]!!.value.toInt()
                        )
                    }
                }

                ConstantsNative.minuteIncrementOrDecrementCommandIndicator -> {

                    if (timePart!!.value == ("$timePartIncrementOrDecrementCommandIndicator+")) {

                        localDateTimeInText = DateTimeUtils.addMinutesToNormalDateTimeInText(

                            dateTimeInText = dateTimeInText,
                            minutes = 1
                        )

                    } else if (timePart.value == ("$timePartIncrementOrDecrementCommandIndicator-")) {

                        localDateTimeInText = DateTimeUtils.subtractMinutesFromNormalDateTimeInText(

                            dateTimeInText = dateTimeInText,
                            minutes = 1
                        )

                    } else if (timePartIncrementOrDecrementMatchResult.groups[2]!!.value == "+") {

                        localDateTimeInText = DateTimeUtils.addMinutesToNormalDateTimeInText(

                            dateTimeInText = dateTimeInText,
                            minutes = timePartIncrementOrDecrementMatchResult.groups[1]!!.value.toInt()
                        )

                    } else if (timePartIncrementOrDecrementMatchResult.groups[2]!!.value == "-") {

                        localDateTimeInText = DateTimeUtils.subtractMinutesFromNormalDateTimeInText(

                            dateTimeInText = dateTimeInText,
                            minutes = timePartIncrementOrDecrementMatchResult.groups[1]!!.value.toInt()
                        )
                    }
                }

                ConstantsNative.secondIncrementOrDecrementCommandIndicator -> {

                    if (timePart!!.value == ("$timePartIncrementOrDecrementCommandIndicator+")) {

                        localDateTimeInText = DateTimeUtils.addSecondsToNormalDateTimeInText(

                            dateTimeInText = dateTimeInText,
                            seconds = 1
                        )

                    } else if (timePart.value == ("$timePartIncrementOrDecrementCommandIndicator-")) {

                        localDateTimeInText = DateTimeUtils.subtractSecondsFromNormalDateTimeInText(

                            dateTimeInText = dateTimeInText,
                            seconds = 1
                        )

                    } else if (timePartIncrementOrDecrementMatchResult.groups[2]!!.value == "+") {

                        localDateTimeInText = DateTimeUtils.addSecondsToNormalDateTimeInText(

                            dateTimeInText = dateTimeInText,
                            seconds = timePartIncrementOrDecrementMatchResult.groups[1]!!.value.toInt()
                        )

                    } else if (timePartIncrementOrDecrementMatchResult.groups[2]!!.value == "-") {

                        localDateTimeInText = DateTimeUtils.subtractSecondsFromNormalDateTimeInText(

                            dateTimeInText = dateTimeInText,
                            seconds = timePartIncrementOrDecrementMatchResult.groups[1]!!.value.toInt()
                        )
                    }
                }

                ConstantsNative.dayIncrementOrDecrementCommandIndicator -> {

                    localDateTimeInText = handleDateIncrementOrDecrementPattern(

                        timePart = timePart,
                        dateTimeInText = dateTimeInText,
                        timePartIncrementOrDecrementMatchResult = timePartIncrementOrDecrementMatchResult
                    )
                }
            }
            return insertTransactionVariantsInteractive(

                userId = userId,
                username = username,
                transactionType = transactionType,
                fromAccount = fromAccount,
                viaAccount = viaAccount,
                toAccount = toAccount,
                isViaStep = isViaStep,
                isTwoWayStep = isTwoWayStep,
                transactionId = transactionId,
                dateTimeInText = localDateTimeInText,
                transactionParticulars = transactionParticulars,
                transactionAmount = transactionAmount,
                isEditStep = isEditStep,
                splitIndex = splitIndex,
                isDevelopmentMode = isDevelopmentMode,
                isCyclicViaStep = isCyclicViaStep
            )
        }
    }

    private fun handleDateIncrementOrDecrementPattern(

        timePart: MatchGroup?,
        dateTimeInText: String,
        timePartIncrementOrDecrementMatchResult: MatchResult,
        manipulationOperatorPosition: Int = 2

    ): String {

        var localDateTimeInText = dateTimeInText
        if (timePart!!.value == ("${ConstantsNative.dayIncrementOrDecrementCommandIndicator}+")) {

            localDateTimeInText = DateTimeUtils.addDaysToNormalDateTimeInText(

                dateTimeInText = dateTimeInText,
                days = 1
            )

        } else if (timePart.value == ("${ConstantsNative.dayIncrementOrDecrementCommandIndicator}-")) {

            localDateTimeInText = DateTimeUtils.subtractDaysFromNormalDateTimeInText(

                dateTimeInText = dateTimeInText,
                days = 1
            )

        } else if (timePartIncrementOrDecrementMatchResult.groups[manipulationOperatorPosition]!!.value == "+") {

            localDateTimeInText = DateTimeUtils.addDaysToNormalDateTimeInText(

                dateTimeInText = dateTimeInText,
                days = timePartIncrementOrDecrementMatchResult.groups[manipulationOperatorPosition - 1]!!.value.toInt()
            )

        } else if (timePartIncrementOrDecrementMatchResult.groups[manipulationOperatorPosition]!!.value == "-") {

            localDateTimeInText = DateTimeUtils.subtractDaysFromNormalDateTimeInText(

                dateTimeInText = dateTimeInText,
                days = timePartIncrementOrDecrementMatchResult.groups[manipulationOperatorPosition - 1]!!.value.toInt()
            )
        }
        return localDateTimeInText
    }

    private fun insertTransactionAfterEventDateTimeFix(

        transactionParticulars: String,
        transactionAmount: Float,
        dateTimeInText: String,
        fromAccount: AccountResponse,
        transactionType: TransactionTypeEnum,
        viaAccount: AccountResponse,
        toAccount: AccountResponse,
        isEditStep: Boolean,
        transactionId: UInt,
        isDevelopmentMode: Boolean,
        isTwoWayStep: Boolean,
        userId: UInt,
        isViaStep: Boolean,
        isCyclicViaStep: Boolean,
        splitIndex: UInt,
        username: String

    ): InsertTransactionResult {

        var localTransactionParticulars = transactionParticulars
        var localTransactionAmount = transactionAmount

        val reversedTransactionParticulars: String =
            SentenceUtils.reverseOrderOfWords(sentence = localTransactionParticulars)

        print("Enter Particulars (Current Value - $localTransactionParticulars), R to Reverse (Reversed Value - $reversedTransactionParticulars), AS to Add Suffix, AP to Add Prefix : ")

        val transactionParticularsInput: String = readln()
        if (transactionParticularsInput.isNotEmpty()) {

            localTransactionParticulars = if (transactionParticularsInput == "R") {

                reversedTransactionParticulars

            } else if (transactionParticularsInput == "AS") {

                print("Enter Suffix : ")
                val transactionSuffixInput: String = readln()
                val suffixedTransactionParticulars = "$localTransactionParticulars$transactionSuffixInput"

                do {
                    print("Particulars (Current Value - $localTransactionParticulars), (Suffixed Value - $suffixedTransactionParticulars), Do you want to Continue (Y/N) : ")

                    when (readln()) {

                        "Y", "" -> {

                            localTransactionParticulars = suffixedTransactionParticulars
                            break
                        }

                        "N" -> {

                            break
                        }

                        else -> {

                            InteractiveUtils.invalidOptionMessage()
                        }
                    }
                } while (true)

                localTransactionParticulars

            } else if (transactionParticularsInput == "AP") {

                print("Enter Prefix : ")
                val transactionPrefixInput: String = readln()
                val prefixedTransactionParticulars = "$transactionPrefixInput$localTransactionParticulars"

                do {
                    print("Particulars (Current Value - $localTransactionParticulars), (Prefixed Value - $prefixedTransactionParticulars), Do you want to Continue (Y/N) : ")

                    when (readln()) {

                        "Y" -> {

                            localTransactionParticulars = prefixedTransactionParticulars
                        }

                        "N" -> {

                            break
                        }

                        else -> {

                            InteractiveUtils.invalidOptionMessage()
                        }
                    }
                } while (true)

                localTransactionParticulars

            } else {

                transactionParticularsInput
            }
        }

        //                if (isTwoWayStep || isViaStep) {
        //
        //                    // TODO: Prefix Particulars
        //                    // TODO: Suffix Particulars
        //                    // TODO: Other String Manipulations
        //                }

        print("Enter Amount (Current Value - $localTransactionAmount) : ")
        val transactionAmountInput: String = readln()
        if (transactionAmountInput.isNotEmpty()) {

            localTransactionAmount = InputUtils.getValidFloat(

                inputText = transactionAmountInput,
                constructInvalidMessage = fun(inputText: String): String {
                    return "Invalid Amount, Enter Amount (Current Value - $inputText) : "
                })
        }

        do {
            var menuItems: List<String> = listOf(
                "\nTime - $dateTimeInText",
                "Withdraw Account - ${fromAccount.id} : ${fromAccount.fullName}"
            )
            if ((transactionType == TransactionTypeEnum.VIA) || (transactionType == TransactionTypeEnum.CYCLIC_VIA)) {
                menuItems = menuItems + "Intermediate Account - ${viaAccount.id} : ${viaAccount.fullName}"
            }
            menuItems = menuItems + listOf(
                "Deposit Account - ${toAccount.id} : ${toAccount.fullName}",
                "Particulars - $localTransactionParticulars",
                "Amount - $localTransactionAmount",
                "\nCorrect ? (Y/N), Enter ${if ((transactionType == TransactionTypeEnum.VIA) || (transactionType == TransactionTypeEnum.CYCLIC_VIA)) "Ex12 to exchange From & Via A/Cs, Ex23 to exchange Via & To A/Cs, Ex13 to exchange From & To A/Cs" else "Ex to exchange From & To A/Cs"} or B to back : "
            )
            commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                listOfCommands = menuItems
            )
            when (readln()) {

                "Y", "" -> {

                    if (isEditStep) {

                        when (transactionType) {

                            TransactionTypeEnum.NORMAL -> {

                                return InsertTransactionResult(

                                    isSuccess = updateTransactionInteractive(

                                        transactionId = transactionId,
                                        eventDateTime = dateTimeInText,
                                        particulars = localTransactionParticulars,
                                        amount = localTransactionAmount,
                                        fromAccountId = fromAccount.id,
                                        toAccountId = toAccount.id,
                                        isDevelopmentMode = isDevelopmentMode
                                    ),
                                    dateTimeInText = dateTimeInText,
                                    transactionParticulars = localTransactionParticulars,
                                    transactionAmount = localTransactionAmount,
                                    fromAccount = fromAccount,
                                    viaAccount = viaAccount,
                                    toAccount = toAccount
                                )
                            }

                            TransactionTypeEnum.VIA -> ToDoUtils.showTodo()
                            TransactionTypeEnum.TWO_WAY -> ToDoUtils.showTodo()
                            TransactionTypeEnum.CYCLIC_VIA -> ToDoUtils.showTodo()
                        }

                    } else if (isTwoWayStep) {

                        return InsertTransactionResult(

                            isSuccess = insertTransactionInteractive(

                                userId = userId,
                                eventDateTime = dateTimeInText,
                                particulars = localTransactionParticulars,
                                amount = localTransactionAmount,
                                fromAccount = toAccount,
                                toAccount = fromAccount,
                                isDevelopmentMode = isDevelopmentMode
                            ),
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = localTransactionParticulars,
                            transactionAmount = localTransactionAmount,
                            fromAccount = fromAccount,
                            viaAccount = viaAccount,
                            toAccount = toAccount
                        )
                    } else if (isViaStep) {

                        return InsertTransactionResult(

                            isSuccess = insertTransactionInteractive(

                                userId = userId,
                                eventDateTime = dateTimeInText,
                                particulars = localTransactionParticulars,
                                amount = localTransactionAmount,
                                fromAccount = viaAccount,
                                toAccount = toAccount,
                                isDevelopmentMode = isDevelopmentMode
                            ),
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = localTransactionParticulars,
                            transactionAmount = localTransactionAmount,
                            fromAccount = fromAccount,
                            viaAccount = viaAccount,
                            toAccount = toAccount
                        )
                    } else if (isCyclicViaStep) {

                        return InsertTransactionResult(

                            isSuccess = insertTransactionInteractive(

                                userId = userId,
                                eventDateTime = dateTimeInText,
                                particulars = localTransactionParticulars,
                                amount = localTransactionAmount,
                                fromAccount = toAccount,
                                toAccount = fromAccount,
                                isDevelopmentMode = isDevelopmentMode
                            ),
                            dateTimeInText = dateTimeInText,
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

                                    isSuccess = insertTransactionInteractive(

                                        userId = userId,
                                        eventDateTime = dateTimeInText,
                                        particulars = localTransactionParticulars,
                                        amount = localTransactionAmount,
                                        fromAccount = fromAccount,
                                        toAccount = toAccount,
                                        isDevelopmentMode = isDevelopmentMode
                                    ),
                                    dateTimeInText = if (splitIndex > 0u) DateTimeUtils.add5MinutesToNormalDateTimeInText(

                                        dateTimeInText = dateTimeInText

                                    ) else dateTimeInText,
                                    transactionParticulars = localTransactionParticulars,
                                    transactionAmount = localTransactionAmount,
                                    fromAccount = fromAccount,
                                    viaAccount = viaAccount,
                                    toAccount = toAccount
                                )
                            }

                            TransactionTypeEnum.VIA, TransactionTypeEnum.CYCLIC_VIA -> {

                                return InsertTransactionResult(

                                    isSuccess = insertTransactionInteractive(

                                        userId = userId,
                                        eventDateTime = dateTimeInText,
                                        particulars = localTransactionParticulars,
                                        amount = localTransactionAmount,
                                        fromAccount = fromAccount,
                                        toAccount = viaAccount,
                                        isDevelopmentMode = isDevelopmentMode
                                    ),
                                    dateTimeInText = if (splitIndex > 0u) DateTimeUtils.add5MinutesToNormalDateTimeInText(

                                        dateTimeInText = dateTimeInText

                                    ) else dateTimeInText,
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
                "N" -> return insertTransactionVariantsInteractive(

                    userId = userId,
                    username = username,
                    transactionType = transactionType,
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    isViaStep = isViaStep,
                    isTwoWayStep = isTwoWayStep,
                    transactionId = transactionId,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = localTransactionParticulars,
                    transactionAmount = localTransactionAmount,
                    isEditStep = isEditStep,
                    splitIndex = splitIndex,
                    isDevelopmentMode = isDevelopmentMode,
                    isCyclicViaStep = isCyclicViaStep
                )

                "Ex" -> {

                    if (transactionType == TransactionTypeEnum.NORMAL) {

                        return invokeInsertTransactionVariantsInteractiveAfterExchangeOfAccounts(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = fromAccount,
                            viaAccount = viaAccount,
                            toAccount = toAccount,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = localTransactionParticulars,
                            transactionAmount = localTransactionAmount,
                            accountExchangeType = AccountExchangeTypeEnum.FROM_AND_TO,
                            isEditStep = isEditStep,
                            isViaStep = isViaStep,
                            isTwoWayStep = isTwoWayStep,
                            transactionId = transactionId,
                            isDevelopmentMode = isDevelopmentMode,
                            isCyclicViaStep = isCyclicViaStep
                        )

                    } else {

                        InteractiveUtils.invalidOptionMessage()
                    }
                }

                "Ex13" -> {

                    if ((transactionType == TransactionTypeEnum.VIA) || (transactionType == TransactionTypeEnum.CYCLIC_VIA)) {

                        return invokeInsertTransactionVariantsInteractiveAfterExchangeOfAccounts(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = fromAccount,
                            viaAccount = viaAccount,
                            toAccount = toAccount,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = localTransactionParticulars,
                            transactionAmount = localTransactionAmount,
                            accountExchangeType = AccountExchangeTypeEnum.FROM_AND_TO,
                            isEditStep = isEditStep,
                            isViaStep = isViaStep,
                            isTwoWayStep = isTwoWayStep,
                            transactionId = transactionId,
                            isDevelopmentMode = isDevelopmentMode,
                            isCyclicViaStep = isCyclicViaStep
                        )

                    } else {

                        InteractiveUtils.invalidOptionMessage()
                    }
                }

                "Ex12" -> {

                    if ((transactionType == TransactionTypeEnum.VIA) || (transactionType == TransactionTypeEnum.CYCLIC_VIA)) {

                        return invokeInsertTransactionVariantsInteractiveAfterExchangeOfAccounts(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = fromAccount,
                            viaAccount = viaAccount,
                            toAccount = toAccount,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = localTransactionParticulars,
                            transactionAmount = localTransactionAmount,
                            accountExchangeType = AccountExchangeTypeEnum.FROM_AND_VIA,
                            isEditStep = isEditStep,
                            isViaStep = isViaStep,
                            isTwoWayStep = isTwoWayStep,
                            transactionId = transactionId,
                            isDevelopmentMode = isDevelopmentMode,
                            isCyclicViaStep = isCyclicViaStep
                        )
                    } else {

                        InteractiveUtils.invalidOptionMessage()
                    }
                }

                "Ex23" -> {

                    if ((transactionType == TransactionTypeEnum.VIA) || (transactionType == TransactionTypeEnum.CYCLIC_VIA)) {

                        return invokeInsertTransactionVariantsInteractiveAfterExchangeOfAccounts(
                            userId = userId,
                            username = username,
                            transactionType = transactionType,
                            fromAccount = fromAccount,
                            viaAccount = viaAccount,
                            toAccount = toAccount,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = localTransactionParticulars,
                            transactionAmount = localTransactionAmount,
                            accountExchangeType = AccountExchangeTypeEnum.VIA_AND_TO,
                            isEditStep = isEditStep,
                            isViaStep = isViaStep,
                            isTwoWayStep = isTwoWayStep,
                            transactionId = transactionId,
                            isDevelopmentMode = isDevelopmentMode,
                            isCyclicViaStep = isCyclicViaStep
                        )
                    } else {

                        InteractiveUtils.invalidOptionMessage()
                    }
                }

                "B" -> {

                    return TransactionUtils.getFailedInsertTransactionResult(

                        dateTimeInText = dateTimeInText,
                        transactionParticulars = localTransactionParticulars,
                        transactionAmount = localTransactionAmount,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount
                    )
                }

                else -> InteractiveUtils.invalidOptionMessage()
            }
        } while (true)
    }

    private fun getSplitIndicator(splitCount: UInt): String {

        if (splitCount > 0u) {

            return " [S$splitCount]"
        }
        return ""
    }

    private fun invokeInsertTransactionVariantsInteractiveAfterExchangeOfAccounts(

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
        transactionId: UInt,
        isDevelopmentMode: Boolean,
        isCyclicViaStep: Boolean

    ): InsertTransactionResult {

        when (accountExchangeType) {

            AccountExchangeTypeEnum.FROM_AND_TO -> {

                return insertTransactionVariantsInteractive(

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
                    isEditStep = isEditStep,
                    isDevelopmentMode = isDevelopmentMode,
                    isCyclicViaStep = isCyclicViaStep
                )
            }

            AccountExchangeTypeEnum.FROM_AND_VIA -> {

                return insertTransactionVariantsInteractive(

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
                    isEditStep = isEditStep,
                    isDevelopmentMode = isDevelopmentMode,
                    isCyclicViaStep = isCyclicViaStep
                )
            }

            AccountExchangeTypeEnum.VIA_AND_TO -> {

                return insertTransactionVariantsInteractive(

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
                    isEditStep = isEditStep,
                    isDevelopmentMode = isDevelopmentMode,
                    isCyclicViaStep = isCyclicViaStep
                )
            }
        }
    }

    private fun manipulateTransactionInteractive(

        transactionManipulationApiRequest: () -> Result<TransactionManipulationResponse>,
        transactionManipulationSuccessActions: () -> Unit = {},
        transactionManipulationFailureActions: (String) -> Unit = {},
        isConsoleMode: Boolean = true,
        isDevelopmentMode: Boolean

    ): Boolean {

        return InsertOperations.manipulateTransaction(

            transactionManipulationApiRequest = transactionManipulationApiRequest,
            transactionManipulationSuccessActions = {

                println("OK...")
                transactionManipulationSuccessActions.invoke()
            },
            transactionManipulationFailureActions = { data: String ->

                ApiUtilsCommon.printServerExecutionErrorMessage(data)
                transactionManipulationFailureActions.invoke(data)
            },
            isConsoleMode = true,
            isDevelopmentMode = isDevelopmentMode
        )
    }

    private fun insertTransactionInteractive(

        userId: UInt,
        eventDateTime: String,
        particulars: String,
        amount: Float,
        fromAccount: AccountResponse,
        toAccount: AccountResponse,
        isDevelopmentMode: Boolean

    ): Boolean {

        return InsertOperations.insertTransaction(

            userId = userId,
            eventDateTime = eventDateTime,
            particulars = particulars,
            amount = amount,
            fromAccountId = fromAccount.id,
            toAccountId = toAccount.id,
            isConsoleMode = true,
            isDevelopmentMode = isDevelopmentMode,
            eventDateTimeConversionFunction = {

                MysqlUtilsInteractive.dateTimeTextConversionWithMessage(

                    inputDateTimeText = eventDateTime,
                    dateTimeTextConversionFunction = fun(): IsOkModel<String> {

                        return MysqlUtils.normalDateTimeTextToMySqlDateTimeText(

                            normalDateTimeText = eventDateTime,
                        )
                    },
                )
            },
            transactionManipulationSuccessActions = fun() {

                val readFrequencyOfAccountsFileResult: IsOkModel<FrequencyOfAccountsModel> =
                    JsonFileUtils.readJsonFile(

                        fileName = ConstantsNative.frequencyOfAccountsFileName,
                        isDevelopmentMode = isDevelopmentMode
                    )

                if (isDevelopmentMode) {

                    println("readFrequencyOfAccountsFileResult : $readFrequencyOfAccountsFileResult")
                }

                if (readFrequencyOfAccountsFileResult.isOK) {

                    var frequencyOfAccounts: FrequencyOfAccountsModel = readFrequencyOfAccountsFileResult.data!!
                    val user: UserModel? = frequencyOfAccounts.users.find { user: UserModel -> user.id == userId }
                    if (user != null) {

                        frequencyOfAccounts = InsertOperations.updateAccountFrequency(

                            user = user,
                            account = fromAccount,
                            frequencyOfAccounts = frequencyOfAccounts,
                            userId = userId,
                            isDevelopmentMode = isDevelopmentMode
                        )
                        frequencyOfAccounts = InsertOperations.updateAccountFrequency(

                            user = user,
                            account = toAccount,
                            frequencyOfAccounts = frequencyOfAccounts,
                            userId = userId,
                            isDevelopmentMode = isDevelopmentMode
                        )

                    } else {
                        frequencyOfAccounts.users = frequencyOfAccounts.users.plusElement(

                            element = InsertOperations.getInitialAccountFrequencyForUser(

                                userId = userId,
                                fromAccount = fromAccount,
                                toAccount = toAccount
                            )
                        )
                    }
                    JsonFileUtils.writeJsonFile(

                        fileName = ConstantsNative.frequencyOfAccountsFileName,
                        data = frequencyOfAccounts
                    )
                } else {

                    JsonFileUtils.writeJsonFile(

                        fileName = ConstantsNative.frequencyOfAccountsFileName,
                        data = FrequencyOfAccountsModel(

                            users = listOf(
                                InsertOperations.getInitialAccountFrequencyForUser(

                                    userId = userId,
                                    fromAccount = fromAccount,
                                    toAccount = toAccount
                                )
                            )
                        )
                    )
                }
            }
        )
    }

    internal fun updateTransactionInteractive(

        transactionId: UInt,
        eventDateTime: String,
        particulars: String,
        amount: Float,
        fromAccountId: UInt,
        toAccountId: UInt,
        isDateTimeUpdateOperation: Boolean = false,
        isDevelopmentMode: Boolean

    ): Boolean {

        return InsertOperations.updateTransaction(

            transactionId = transactionId,
            eventDateTime = eventDateTime,
            particulars = particulars,
            amount = amount,
            fromAccountId = fromAccountId,
            toAccountId = toAccountId,
            isDateTimeUpdateOperation = isDateTimeUpdateOperation,
            isConsoleMode = true,
            isDevelopmentMode = isDevelopmentMode,
            manipulateTransactionOperation = ::manipulateTransactionInteractive,
            eventDateTimeConversionOperation = fun(): IsOkModel<String> {

                return MysqlUtilsInteractive.dateTimeTextConversionWithMessage(

                    inputDateTimeText = eventDateTime,
                    dateTimeTextConversionFunction = fun(): IsOkModel<String> {
                        return MysqlUtils.normalDateTimeTextToMySqlDateTimeText(
                            normalDateTimeText = eventDateTime,
                        )
                    }
                )
            },
        )
    }

    internal fun deleteTransaction(

        transactionId: UInt,
        isDevelopmentMode: Boolean

    ): Boolean {

        return manipulateTransactionInteractive(
            transactionManipulationApiRequest = fun(): Result<TransactionManipulationResponse> {
                return runBlocking {

                    TransactionDataSource().deleteTransaction(transactionId = transactionId)
                }
            },
            transactionManipulationSuccessActions = fun() {},
            isDevelopmentMode = isDevelopmentMode
        )
    }

    private fun getEnvironmentVariableValueForInsertOperation(

        environmentVariableName: String,
        environmentVariableFormalName: String,
        dotenv: Dotenv

    ): EnvironmentVariableForWholeNumber = EnvironmentFileOperations.getEnvironmentVariableValueForWholeNumber(

        dotenv = dotenv,
        environmentVariableName = environmentVariableName,
        environmentVariableFormalName = environmentVariableFormalName
    )
}
