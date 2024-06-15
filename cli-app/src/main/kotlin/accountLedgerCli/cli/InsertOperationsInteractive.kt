package accountLedgerCli.cli

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.api.response.TransactionManipulationResponse
import account.ledger.library.api.response.TransactionResponse
import account.ledger.library.enums.AccountExchangeTypeEnum
import account.ledger.library.enums.AccountTypeEnum
import account.ledger.library.enums.HandleAccountsApiResponseResult
import account.ledger.library.enums.TransactionTypeEnum
import account.ledger.library.models.*
import account.ledger.library.operations.InsertOperations
import account.ledger.library.retrofit.data.TransactionDataSource
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.utils.ChooseAccountUtilsInteractive
import account_ledger_library.constants.ConstantsNative
import account.ledger.library.models.TransactionModel
import account.ledger.library.utils.*
import common.utils.library.enums.PatternQuestionAnswerTypesEnum
import common.utils.library.models.IsOkModel
import common.utils.library.models.SuccessWithoutDataBasedOnIsOkModel
import common.utils.library.utils.*
import io.github.cdimascio.dotenv.Dotenv
import kotlinx.coroutines.runBlocking

object InsertOperationsInteractive {

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
        chosenTransactionForSpecial: TransactionResponse?,
        chosenSpecialTransactionType: SpecialTransactionTypeModel?,
        isDevelopmentMode: Boolean,
        dotEnv: Dotenv

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
                HandleResponsesInteractiveLibrary.getUserAccountsMap(
                    apiResponse = ApiUtilsInteractive.getAccountsFull(

                        userId = userId,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode
                    )
                )

            return IsOkUtils.isOkHandler(

                isOkModel = getUserAccountsMapResult,
                dataOnFailure = insertTransactionResult,
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
                        chosenTransactionForSpecial = chosenTransactionForSpecial,
                        chosenSpecialTransactionType = chosenSpecialTransactionType,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                })!!
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
        isDevelopmentMode: Boolean,
        dotEnv: Dotenv

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
                HandleResponsesInteractiveLibrary.getUserAccountsMap(

                    apiResponse = ApiUtilsInteractive.getAccountsFull(

                        userId = userId,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode
                    )
                )

            return IsOkUtils.isOkHandler(

                isOkModel = getUserAccountsMapResult,
                dataOnFailure = insertTransactionResult,
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
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                })!!
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
        isDevelopmentMode: Boolean,
        chosenTransactionForSpecial: TransactionResponse? = null,
        chosenSpecialTransactionType: SpecialTransactionTypeModel? = null,
        dotEnv: Dotenv

    ): InsertTransactionResult {

        var localInsertTransactionResult: InsertTransactionResult = TransactionUtils.getFailedInsertTransactionResult(

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

                    *if ((transactionType == TransactionTypeEnum.VIA) || (transactionType == TransactionTypeEnum.CYCLIC_VIA)) {

                        arrayOf(
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

                        arrayOf(
                            "8 - Exchange Accounts",
                            "9 - Exchange Accounts, Then Continue Transaction",
                            "10 - Input To Account ID Directly, Exchange Accounts, Then Continue Transaction",
                            "11 - Input From Account ID Directly, Exchange Accounts, Then Continue Transaction",
                            "12 - Choose To Account From List - Top Levels, Exchange Accounts, Then Continue Transaction",
                            "13 - Choose To Account From List - Full Names, Exchange Accounts, Then Continue Transaction",
                            "14 - Choose From Account From List - Top Levels, Exchange Accounts, Then Continue Transaction",
                            "15 - Choose From Account From List - Full Names, Exchange Accounts, Then Continue Transaction"
                        )
                    },
                    /*"17 - ${Screens.getQuickTransactionOnWalletText()}",
                    "18 - ${Screens.getQuickTransactionOnWalletToFrequent1Text()}",
                    "19 - ${Screens.getQuickTransactionOnWalletToFrequent2Text()}",
                    "20 - ${Screens.getQuickTransactionOnWalletToFrequent3Text()}",
                    "21 - ${Screens.getQuickTransactionOnBankText()}",
                    "22 - ${Screens.getQuickTransactionOnBankToFrequent1Text()}",
                    "23 - ${Screens.getQuickTransactionOnBankToFrequent2Text()}",
                    "24 - ${Screens.getQuickTransactionOnBankToFrequent3Text()}",
                    "25 - ${Screens.getQuickTransactionOnFrequent1Text()}",
                    "26 - ${Screens.getQuickTransactionOnFrequent2Text()}",
                    "27 - ${Screens.getQuickTransactionOnFrequent3Text()}",*/
                    "0 - Back",
                    "",
                    "Enter Your Choice : "
                )
            )
            when (readln()) {

                "1" -> {

                    localInsertTransactionResult = processChooseAccountResult(

                        chooseAccountResult = InputOperations.chooseDepositTop(

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
                        chosenTransactionForSpecial = chosenTransactionForSpecial,
                        chosenSpecialTransactionType = chosenSpecialTransactionType,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "2" -> {

                    localInsertTransactionResult = processChooseAccountResult(

                        chooseAccountResult = InputOperations.chooseDepositFull(

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
                        chosenTransactionForSpecial = chosenTransactionForSpecial,
                        chosenSpecialTransactionType = chosenSpecialTransactionType,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
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
                            chosenTransactionForSpecial = chosenTransactionForSpecial,
                            chosenSpecialTransactionType = chosenSpecialTransactionType,
                            isDevelopmentMode = isDevelopmentMode,
                            dotEnv = dotEnv
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

                        chooseAccountResult = InputOperations.chooseWithdrawTop(

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
                        chosenTransactionForSpecial = chosenTransactionForSpecial,
                        chosenSpecialTransactionType = chosenSpecialTransactionType,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "5" -> {

                    localInsertTransactionResult = processChooseAccountResult(

                        chooseAccountResult = InputOperations.chooseWithdrawFull(

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
                        chosenTransactionForSpecial = chosenTransactionForSpecial,
                        chosenSpecialTransactionType = chosenSpecialTransactionType,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
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
                            chosenTransactionForSpecial = chosenTransactionForSpecial,
                            chosenSpecialTransactionType = chosenSpecialTransactionType,
                            isDevelopmentMode = isDevelopmentMode,
                            dotEnv = dotEnv
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
                        chosenTransactionForSpecial = chosenTransactionForSpecial,
                        chosenSpecialTransactionType = chosenSpecialTransactionType,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
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
                            isDevelopmentMode = isDevelopmentMode,
                            chosenTransactionForSpecial = chosenTransactionForSpecial,
                            chosenSpecialTransactionType = chosenSpecialTransactionType,
                            dotEnv = dotEnv
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
                            isDevelopmentMode = isDevelopmentMode,
                            chosenTransactionForSpecial = chosenTransactionForSpecial,
                            chosenSpecialTransactionType = chosenSpecialTransactionType,
                            dotEnv = dotEnv
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
                            chosenTransactionForSpecial = chosenTransactionForSpecial,
                            chosenSpecialTransactionType = chosenSpecialTransactionType,
                            isDevelopmentMode = isDevelopmentMode,
                            dotEnv = dotEnv
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
                            chosenTransactionForSpecial = chosenTransactionForSpecial,
                            chosenSpecialTransactionType = chosenSpecialTransactionType,
                            isDevelopmentMode = isDevelopmentMode,
                            dotEnv = dotEnv
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
                            isDevelopmentMode = isDevelopmentMode,
                            chosenTransactionForSpecial = chosenTransactionForSpecial,
                            chosenSpecialTransactionType = chosenSpecialTransactionType,
                            dotEnv = dotEnv
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
                                chosenTransactionForSpecial = chosenTransactionForSpecial,
                                chosenSpecialTransactionType = chosenSpecialTransactionType,
                                isDevelopmentMode = isDevelopmentMode,
                                dotEnv = dotEnv
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
                            chosenTransactionForSpecial = chosenTransactionForSpecial,
                            chosenSpecialTransactionType = chosenSpecialTransactionType,
                            isDevelopmentMode = isDevelopmentMode,
                            dotEnv = dotEnv
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
                                chosenTransactionForSpecial = chosenTransactionForSpecial,
                                chosenSpecialTransactionType = chosenSpecialTransactionType,
                                isDevelopmentMode = isDevelopmentMode,
                                dotEnv = dotEnv
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
                            isDevelopmentMode = isDevelopmentMode,
                            chosenTransactionForSpecial = chosenTransactionForSpecial,
                            chosenSpecialTransactionType = chosenSpecialTransactionType,
                            dotEnv = dotEnv
                        )

                    } else {

                        localInsertTransactionResult = processChooseAccountResult(

                            chooseAccountResult = InputOperations.chooseDepositTop(

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
                            chosenTransactionForSpecial = chosenTransactionForSpecial,
                            chosenSpecialTransactionType = chosenSpecialTransactionType,
                            isDevelopmentMode = isDevelopmentMode,
                            dotEnv = dotEnv
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
                            chosenTransactionForSpecial = chosenTransactionForSpecial,
                            chosenSpecialTransactionType = chosenSpecialTransactionType,
                            isDevelopmentMode = isDevelopmentMode,
                            dotEnv = dotEnv
                        )
                    } else {

                        localInsertTransactionResult = processChooseAccountResult(

                            chooseAccountResult = InputOperations.chooseDepositFull(

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
                            chosenTransactionForSpecial = chosenTransactionForSpecial,
                            chosenSpecialTransactionType = chosenSpecialTransactionType,
                            isDevelopmentMode = isDevelopmentMode,
                            dotEnv = dotEnv
                        )
                    }
                }

                "14" -> {

                    if ((transactionType == TransactionTypeEnum.VIA) || (transactionType == TransactionTypeEnum.CYCLIC_VIA)) {

                        localInsertTransactionResult = processChooseAccountResult(

                            chooseAccountResult = InputOperations.chooseViaTop(

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
                            chosenTransactionForSpecial = chosenTransactionForSpecial,
                            chosenSpecialTransactionType = chosenSpecialTransactionType,
                            isDevelopmentMode = isDevelopmentMode,
                            dotEnv = dotEnv
                        )
                    } else {

                        localInsertTransactionResult = processChooseAccountResult(

                            chooseAccountResult = InputOperations.chooseWithdrawTop(

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
                            chosenTransactionForSpecial = chosenTransactionForSpecial,
                            chosenSpecialTransactionType = chosenSpecialTransactionType,
                            isDevelopmentMode = isDevelopmentMode,
                            dotEnv = dotEnv
                        )
                    }
                }

                "15" -> {

                    if ((transactionType == TransactionTypeEnum.VIA) || (transactionType == TransactionTypeEnum.CYCLIC_VIA)) {

                        localInsertTransactionResult = processChooseAccountResult(

                            chooseAccountResult = InputOperations.chooseViaFull(

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
                            chosenTransactionForSpecial = chosenTransactionForSpecial,
                            chosenSpecialTransactionType = chosenSpecialTransactionType,
                            isDevelopmentMode = isDevelopmentMode,
                            dotEnv = dotEnv
                        )
                    } else {

                        localInsertTransactionResult = processChooseAccountResult(

                            chooseAccountResult = InputOperations.chooseWithdrawFull(

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
                            chosenTransactionForSpecial = chosenTransactionForSpecial,
                            chosenSpecialTransactionType = chosenSpecialTransactionType,
                            isDevelopmentMode = isDevelopmentMode,
                            dotEnv = dotEnv
                        )
                    }
                }

                "16" -> {

                    if ((transactionType == TransactionTypeEnum.VIA) || (transactionType == TransactionTypeEnum.CYCLIC_VIA)) {

                        val chooseAccountResult: ChooseAccountResult =
                            ChooseAccountUtilsInteractive.chooseAccountById(

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
                                chosenTransactionForSpecial = chosenTransactionForSpecial,
                                chosenSpecialTransactionType = chosenSpecialTransactionType,
                                isDevelopmentMode = isDevelopmentMode,
                                dotEnv = dotEnv
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

                        ErrorUtilsInteractive.printInvalidOptionMessage()
                    }
                }

                //TODO : Use common function
                /*"17" -> {

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
                        chosenTransactionForSpecial = chosenTransactionForSpecial,
                        chosenSpecialTransactionType = chosenSpecialTransactionType,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "19" -> {

                    localInsertTransactionResult = Screens.quickTransactionOnWalletToFrequent2(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        chosenTransactionForSpecial = chosenTransactionForSpecial,
                        chosenSpecialTransactionType = chosenSpecialTransactionType,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "20" -> {

                    localInsertTransactionResult = Screens.quickTransactionOnWalletToFrequent3(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        chosenTransactionForSpecial = chosenTransactionForSpecial,
                        chosenSpecialTransactionType = chosenSpecialTransactionType,
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
                        chosenTransactionForSpecial = chosenTransactionForSpecial,
                        chosenSpecialTransactionType = chosenSpecialTransactionType,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "23" -> {

                    localInsertTransactionResult = Screens.quickTransactionOnBankToFrequent2(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        chosenTransactionForSpecial = chosenTransactionForSpecial,
                        chosenSpecialTransactionType = chosenSpecialTransactionType,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "24" -> {

                    localInsertTransactionResult = Screens.quickTransactionOnBankToFrequent3(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        chosenTransactionForSpecial = chosenTransactionForSpecial,
                        chosenSpecialTransactionType = chosenSpecialTransactionType,
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
                }*/

                "0" -> {

                    return localInsertTransactionResult
                }

                else -> ErrorUtilsInteractive.printInvalidOptionMessage()
            }
        } while (true)
    }

    private fun chooseFromAccount(

        userId: UInt,
        isDevelopmentMode: Boolean

    ): ChooseAccountResult {

        return ChooseAccountUtilsInteractive.chooseAccountById(

            userId = userId,
            accountType = AccountTypeEnum.FROM,
            isDevelopmentMode = isDevelopmentMode
        )
    }

    private fun chooseToAccount(userId: UInt, isDevelopmentMode: Boolean): ChooseAccountResult {

        return ChooseAccountUtilsInteractive.chooseAccountById(

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
        chosenTransactionForSpecial: TransactionResponse?,
        chosenSpecialTransactionType: SpecialTransactionTypeModel?,
        isDevelopmentMode: Boolean,
        dotEnv: Dotenv

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
                chosenTransactionForSpecial = chosenTransactionForSpecial,
                chosenSpecialTransactionType = chosenSpecialTransactionType,
                isDevelopmentMode = isDevelopmentMode,
                dotEnv = dotEnv
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
        chosenTransactionForSpecial: TransactionResponse?,
        chosenSpecialTransactionType: SpecialTransactionTypeModel?,
        isDevelopmentMode: Boolean,
        dotEnv: Dotenv

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
                        chosenTransactionForSpecial = chosenTransactionForSpecial,
                        chosenSpecialTransactionType = chosenSpecialTransactionType,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
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
                        chosenTransactionForSpecial = chosenTransactionForSpecial,
                        chosenSpecialTransactionType = chosenSpecialTransactionType,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
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
                        chosenTransactionForSpecial = chosenTransactionForSpecial,
                        chosenSpecialTransactionType = chosenSpecialTransactionType,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
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
                        chosenTransactionForSpecial = chosenTransactionForSpecial,
                        chosenSpecialTransactionType = chosenSpecialTransactionType,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
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
                    chosenTransactionForSpecial = chosenTransactionForSpecial,
                    chosenSpecialTransactionType = chosenSpecialTransactionType,
                    isDevelopmentMode = isDevelopmentMode,
                    dotEnv = dotEnv
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
        isCyclicViaStep: Boolean = false,
        chosenTransactionForSpecial: TransactionResponse? = null,
        chosenSpecialTransactionType: SpecialTransactionTypeModel? = null,
        dotEnv: Dotenv

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

        localDateTimeInText = InputOperations.enterDateWithTime(

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
                    isCyclicViaStep = isCyclicViaStep,
                    chosenTransactionForSpecial = chosenTransactionForSpecial,
                    chosenSpecialTransactionType = chosenSpecialTransactionType,
                    dotEnv = dotEnv
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
                    isCyclicViaStep = isCyclicViaStep,
                    chosenTransactionForSpecial = chosenTransactionForSpecial,
                    chosenSpecialTransactionType = chosenSpecialTransactionType,
                    dotEnv = dotEnv
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
                    chosenTransactionForSpecial = chosenTransactionForSpecial,
                    chosenSpecialTransactionType = chosenSpecialTransactionType,
                    dotEnv = dotEnv
                )
            }

            "S" -> {

                print("Enter No. of Splits : ")
                val thresholdValue = 0u
                val noOfSplits: UInt = InputUtilsInteractive.getGreaterUnsignedInt(

                    inputUInt = InputUtilsInteractive.getValidUnsignedInt(

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
                        isCyclicViaStep = isCyclicViaStep,
                        chosenTransactionForSpecial = chosenTransactionForSpecial,
                        chosenSpecialTransactionType = chosenSpecialTransactionType,
                        dotEnv = dotEnv
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
                        timePartIncrementOrDecrementCommandIndicator = ConstantsNative.HOUR_INCREMENT_OR_DECREMENT_COMMAND_INDICATOR,
                        timePartIncrementOrDecrementNoMatchAction = {

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
                                timePartIncrementOrDecrementCommandIndicator = ConstantsNative.MINUTE_INCREMENT_OR_DECREMENT_COMMAND_INDICATOR,
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
                                        timePartIncrementOrDecrementCommandIndicator = ConstantsNative.SECOND_INCREMENT_OR_DECREMENT_COMMAND_INDICATOR,
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
                                                timePartIncrementOrDecrementCommandIndicator = ConstantsNative.DAY_INCREMENT_OR_DECREMENT_COMMAND_INDICATOR,
                                                timePartIncrementOrDecrementNoMatchAction = {

                                                    val timePartIncrementOrDecrementMatchResult: MatchResult? =
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
                                                            username = username,
                                                            chosenTransactionForSpecial = chosenTransactionForSpecial,
                                                            chosenSpecialTransactionType = chosenSpecialTransactionType,
                                                            dotEnv = dotEnv
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
                                                            isCyclicViaStep = isCyclicViaStep,
                                                            chosenTransactionForSpecial = chosenTransactionForSpecial,
                                                            chosenSpecialTransactionType = chosenSpecialTransactionType,
                                                            dotEnv = dotEnv
                                                        )
                                                    }
                                                },
                                                chosenTransactionForSpecial = chosenTransactionForSpecial,
                                                chosenSpecialTransactionType = chosenSpecialTransactionType,
                                                dotEnv = dotEnv
                                            )
                                        },
                                        chosenTransactionForSpecial = chosenTransactionForSpecial,
                                        chosenSpecialTransactionType = chosenSpecialTransactionType,
                                        dotEnv = dotEnv
                                    )
                                },
                                chosenTransactionForSpecial = chosenTransactionForSpecial,
                                chosenSpecialTransactionType = chosenSpecialTransactionType,
                                dotEnv = dotEnv
                            )
                        },
                        chosenTransactionForSpecial = chosenTransactionForSpecial,
                        chosenSpecialTransactionType = chosenSpecialTransactionType,
                        dotEnv = dotEnv
                    )
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
                        isCyclicViaStep = isCyclicViaStep,
                        chosenTransactionForSpecial = chosenTransactionForSpecial,
                        chosenSpecialTransactionType = chosenSpecialTransactionType,
                        dotEnv = dotEnv
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
        isCyclicViaStep: Boolean,
        chosenTransactionForSpecial: TransactionResponse?,
        chosenSpecialTransactionType: SpecialTransactionTypeModel?,
        dotEnv: Dotenv

    ): InsertTransactionResult {

        var localDateTimeInText = dateTimeInText
        val timePart: MatchGroup? = timeResetCommand.groups.first()
        if (timePart!!.value == ConstantsNative.TIME_RESET_COMMAND_INDICATOR) {

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
            isCyclicViaStep = isCyclicViaStep,
            chosenTransactionForSpecial = chosenTransactionForSpecial,
            chosenSpecialTransactionType = chosenSpecialTransactionType,
            dotEnv = dotEnv
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
        timePartIncrementOrDecrementNoMatchAction: () -> InsertTransactionResult,
        chosenTransactionForSpecial: TransactionResponse?,
        chosenSpecialTransactionType: SpecialTransactionTypeModel?,
        dotEnv: Dotenv

    ): InsertTransactionResult {

        if (timePartIncrementOrDecrementMatchResult == null) {

            return timePartIncrementOrDecrementNoMatchAction.invoke()

        } else {

            val timePart: MatchGroup? = timePartIncrementOrDecrementMatchResult.groups.first()

            var localDateTimeInText = dateTimeInText
            when (timePartIncrementOrDecrementCommandIndicator) {

                ConstantsNative.HOUR_INCREMENT_OR_DECREMENT_COMMAND_INDICATOR -> {

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

                ConstantsNative.MINUTE_INCREMENT_OR_DECREMENT_COMMAND_INDICATOR -> {

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

                ConstantsNative.SECOND_INCREMENT_OR_DECREMENT_COMMAND_INDICATOR -> {

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

                ConstantsNative.DAY_INCREMENT_OR_DECREMENT_COMMAND_INDICATOR -> {

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
                isCyclicViaStep = isCyclicViaStep,
                chosenTransactionForSpecial = chosenTransactionForSpecial,
                chosenSpecialTransactionType = chosenSpecialTransactionType,
                dotEnv = dotEnv
            )
        }
    }

    private fun handleDateIncrementOrDecrementPattern(

        timePart: MatchGroup?,
        dateTimeInText: String,
        timePartIncrementOrDecrementMatchResult: MatchResult,
        manipulationOperatorPosition: Int = 2

    ): String {

        var localDateTimeInText: String = dateTimeInText
        if (timePart!!.value == ("${ConstantsNative.DAY_INCREMENT_OR_DECREMENT_COMMAND_INDICATOR}+")) {

            localDateTimeInText = DateTimeUtils.addDaysToNormalDateTimeInText(

                dateTimeInText = dateTimeInText,
                days = 1
            )

        } else if (timePart.value == ("${ConstantsNative.DAY_INCREMENT_OR_DECREMENT_COMMAND_INDICATOR}-")) {

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
        username: String,
        chosenTransactionForSpecial: TransactionResponse?,
        chosenSpecialTransactionType: SpecialTransactionTypeModel?,
        dotEnv: Dotenv

    ): InsertTransactionResult {

        var localTransactionParticulars: String = transactionParticulars
        var localTransactionAmount: Float = transactionAmount

        if (!TransactionForBajajUtils.bajajTransactionTypes.contains(transactionType)) {

            val reversedTransactionParticulars: String =
                SentenceUtils.reverseOrderOfWords(sentence = localTransactionParticulars)

            if (chosenTransactionForSpecial == null) {

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

                                    ErrorUtilsInteractive.printInvalidOptionMessage()
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

                                    ErrorUtilsInteractive.printInvalidOptionMessage()
                                }
                            }
                        } while (true)

                        localTransactionParticulars

                    } else {

                        transactionParticularsInput
                    }
                }

                if (isViaStep || isTwoWayStep) {

                    // TODO: Prefix Particulars
                    // TODO: Suffix Particulars
                    // TODO: Other String Manipulations
                }

                print("Enter Amount (Current Value - $localTransactionAmount) : ")
                val transactionAmountInput: String = readln()
                if (transactionAmountInput.isNotEmpty()) {

                    localTransactionAmount = InputUtilsInteractive.getValidFloat(

                        inputText = transactionAmountInput,
                        constructInvalidMessage = fun(inputText: String): String {
                            return "Invalid Amount, Enter Amount (Current Value - $inputText) : "
                        })
                }

            } else {

                commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(

                    listOfCommands = listOf(

                        "The Selected Transaction : ${chosenTransactionForSpecial.particulars} => ${chosenTransactionForSpecial.amount}",
                        "C to continue as it is / P to continue using it's pattern : "
                    )
                )
                when (readln()) {

                    "C" -> {

                        localTransactionParticulars = chosenTransactionForSpecial.particulars
                        localTransactionAmount = chosenTransactionForSpecial.amount
                    }

                    "P" -> {

                        //TODO : Complete the code
                        val answers: MutableList<String> = mutableListOf()
                        var totalNoOfTransactions = 1u
                        chosenSpecialTransactionType!!.patternQuestions.forEach { patternQuestion: PatternQuestionModel ->

                            val answer: String
                            when (patternQuestion.answerType) {

                                PatternQuestionAnswerTypesEnum.TextFromListOfOptions -> {

                                    val optionsList: List<String> = patternQuestion.question.split('/')
                                    answer = optionsList[
                                        (ListUtilsInteractive.getValidIndexWithSelectionPromptForNonCollections(

                                            list = optionsList,
                                            itemSpecification = "${patternQuestion.question}?",
                                            items = ListUtils.indexedListTextFromList(list = optionsList)

                                        ).toInt() - 1)
                                    ].trim()
                                }

                                PatternQuestionAnswerTypesEnum.Number -> {

                                    print("Enter ${patternQuestion.question} : ")
                                    answer = InputUtilsInteractive.getValidUnsignedInt(inputText = readln()).toString()

                                }

                                PatternQuestionAnswerTypesEnum.Float -> {

                                    print("Enter ${patternQuestion.question} : ")
                                    answer = InputUtilsInteractive.getValidFloat(inputText = readln()).toString()

                                }

                                PatternQuestionAnswerTypesEnum.TextFromPrefixListOfOptions -> {

                                    val optionsList: List<String> = patternQuestion.question.substring(

                                        patternQuestion.question.indexOf('{') + 1,
                                        patternQuestion.question.indexOf('}')

                                    ).split('/')

                                    val position: Int =
                                        ListUtilsInteractive.getValidIndexWithSelectionPromptForNonCollections(

                                            list = optionsList,
                                            itemSpecification = "${
                                                patternQuestion.question.substring(
                                                    0,
                                                    patternQuestion.question.indexOf('{')
                                                )
                                            } __________________",
                                            items = ListUtils.indexedListTextFromList(list = optionsList)

                                        ).toInt() - 1

                                    if (chosenSpecialTransactionType.isRepeatingTransaction) {

                                        if (patternQuestion.isSizeIndicator) {

                                            totalNoOfTransactions = patternQuestion.positionValues[position].toUInt()
                                        }
                                    }
                                    answer = optionsList[position].trim()
                                }
                            }
                            answers.add(answer)
                        }
                    }

                    else -> {

                        ErrorUtilsInteractive.printInvalidOptionMessage()
                    }
                }
            }
        }
        do {
            var menuItems: List<String>
            var transactions: List<TransactionModel> = emptyList()

            if (TransactionForBajajUtils.bajajTransactionTypes.contains(transactionType)) {

                if (transactionType.value != null) {

                    menuItems = listOf(
                        "$transactionType [${toAccount.name} -> ${fromAccount.name}] - Transactions",
                        "==================================================",
                    )

                    var upToValueResult: IsOkModel<UInt> = SuccessWithoutDataBasedOnIsOkModel()
                    if (TransactionForBajajUtils.bajajTransactionTypesForUpToDiscountType.contains(transactionType)) {

                        upToValueResult = InputUtilsInteractive.getValidUnsignedIntOrBackWithPrompt(

                            dataSpecification = "upToValue"
                        )
                    }
                    if (upToValueResult.isOK) {

                        if (TransactionForBajajCoinsUtils.bajajCoinTransactionTypes.contains(transactionType)) {

                            val generateTransactionsForBajajCoinsResult: IsOkModel<List<TransactionModel>> =
                                InsertTransactionForBajajCoins.generateTransactionsForBajajCoins(

                                    isFundingTransactionPresent = transactionType == TransactionTypeEnum.BAJAJ_COINS_FLAT,
                                    sourceAccount = fromAccount,
                                    secondPartyAccount = toAccount,
                                    eventDateTimeInText = dateTimeInText,
                                    dotEnv = dotEnv,
                                    userId = userId,
                                    isConsoleMode = true,
                                    isDevelopmentMode = isDevelopmentMode,
                                    isBalanceCheckByPassed = transactionType == TransactionTypeEnum.BAJAJ_COINS_FLAT_WITHOUT_BALANCE_CHECK,
                                    discountType = transactionType.value!!,
                                    upToValue = upToValueResult.data
                                )
                            if (generateTransactionsForBajajCoinsResult.isOK) {

                                transactions = generateTransactionsForBajajCoinsResult.data!!

                            } else {

                                ErrorUtilsInteractive.printErrorMessage(
                                    dataSpecification = "generateTransactionsForBajajCoinsResult",
                                    message = generateTransactionsForBajajCoinsResult.error!!
                                )
                                return TransactionUtils.getFailedInsertTransactionResult(

                                    dateTimeInText = dateTimeInText,
                                    transactionParticulars = localTransactionParticulars,
                                    transactionAmount = localTransactionAmount,
                                    fromAccount = fromAccount,
                                    viaAccount = viaAccount,
                                    toAccount = toAccount
                                )
                            }

                        } else if (TransactionForBajajCashbackUtils.bajajCashbackTransactionTypes.contains(
                                transactionType
                            )
                        ) {

                            val generateTransactionsForBajajCashbackResult: IsOkModel<List<TransactionModel>> =
                                InsertTransactionForBajajCashback.generateTransactionsForBajajSubWallet(

                                    isFundingTransactionPresent = transactionType == TransactionTypeEnum.BAJAJ_CASHBACK_FLAT,
                                    sourceAccount = fromAccount,
                                    secondPartyAccount = toAccount,
                                    eventDateTimeInText = dateTimeInText,
                                    dotEnv = dotEnv,
                                    userId = userId,
                                    isConsoleMode = true,
                                    isDevelopmentMode = isDevelopmentMode,
                                    isBalanceCheckByPassed = transactionType == TransactionTypeEnum.BAJAJ_CASHBACK_FLAT_WITHOUT_BALANCE_CHECK,
                                    discountType = transactionType.value!!,
                                    upToValue = upToValueResult.data
                                )
                            if (generateTransactionsForBajajCashbackResult.isOK) {

                                transactions = generateTransactionsForBajajCashbackResult.data!!

                            } else {

                                ErrorUtilsInteractive.printErrorMessage(

                                    dataSpecification = "generateTransactionsForBajajCashbackResult",
                                    message = generateTransactionsForBajajCashbackResult.error!!
                                )
                                return TransactionUtils.getFailedInsertTransactionResult(

                                    dateTimeInText = dateTimeInText,
                                    transactionParticulars = localTransactionParticulars,
                                    transactionAmount = localTransactionAmount,
                                    fromAccount = fromAccount,
                                    viaAccount = viaAccount,
                                    toAccount = toAccount
                                )
                            }
                        }

                        val userTransactionsToTextFromListForLedgerResult: IsOkModel<String> =
                            TransactionUtilsInteractive.userTransactionsToTextFromListForLedger(

                                transactions = TransactionUtils.convertTransactionModelListToToTransactionListForLedger(

                                    transactions = transactions
                                ),
                                isDevelopmentMode = isDevelopmentMode
                            )
                        if (userTransactionsToTextFromListForLedgerResult.isOK) {

                            menuItems = menuItems + listOf(

                                element = userTransactionsToTextFromListForLedgerResult.data!!
                            )

                        } else {

                            TransactionUtilsInteractive.printUserTransactionsToTextFromListForLedgerError(

                                dataSpecification = "userTransactionsToTextFromListForLedger 2",
                                userTransactionsToTextFromListForLedgerInstance = userTransactionsToTextFromListForLedgerResult
                            )
                        }

                    } else {

                        ErrorUtilsInteractive.printErrorMessage(

                            dataSpecification = "upToValueResult",
                            message = upToValueResult.error
                        )
                        return TransactionUtils.getFailedInsertTransactionResult(

                            dateTimeInText = dateTimeInText,
                            transactionParticulars = localTransactionParticulars,
                            transactionAmount = localTransactionAmount,
                            fromAccount = fromAccount,
                            viaAccount = viaAccount,
                            toAccount = toAccount
                        )
                    }
                } else {

                    ErrorUtilsInteractive.printErrorMessage(dataSpecification = "discountType")
                    return TransactionUtils.getFailedInsertTransactionResult(

                        dateTimeInText = dateTimeInText,
                        transactionParticulars = localTransactionParticulars,
                        transactionAmount = localTransactionAmount,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount
                    )
                }
            } else {

                menuItems = listOf(
                    "\nTime - $dateTimeInText",
                    "Withdraw Account - ${fromAccount.id} : ${fromAccount.fullName}"
                )
                if ((transactionType == TransactionTypeEnum.VIA) || (transactionType == TransactionTypeEnum.CYCLIC_VIA)) {
                    menuItems = menuItems + "Intermediate Account - ${viaAccount.id} : ${viaAccount.fullName}"
                }
                menuItems = menuItems + listOf(
                    "Deposit Account - ${toAccount.id} : ${toAccount.fullName}",
                    "Particulars - $localTransactionParticulars",
                    "Amount - $localTransactionAmount"
                )
            }

            menuItems = menuItems + listOf(
                "\nCorrect ? (Y/N), " +
                        (if (transactionType != TransactionTypeEnum.BAJAJ_COINS_FLAT)
                            "Enter " +
                                    (if ((transactionType == TransactionTypeEnum.VIA) || (transactionType == TransactionTypeEnum.CYCLIC_VIA))
                                        "Ex12 to exchange From & Via A/Cs, Ex23 to exchange Via & To A/Cs, Ex13 to exchange From & To A/Cs"
                                    else "Ex to exchange From & To A/Cs") +
                                    " or "
                        else "") +
                        "B to back : "
            )
            commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(

                listOfCommands = menuItems
            )
            when (readln()) {

                "Y", "" -> {

                    if (!TransactionForBajajUtils.bajajTransactionTypes.contains(transactionType)) {

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

                                else -> ToDoUtilsInteractive.showTodo()
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

                                else -> ToDoUtilsInteractive.showTodo()
                            }
                        }

                    } else {

                        for (transaction: TransactionModel in transactions) {

                            if (!insertTransactionInteractive(

                                    userId = userId,
                                    eventDateTime = transaction.eventDateTimeInText,
                                    particulars = transaction.particulars,
                                    amount = transaction.amount,
                                    fromAccount = transaction.fromAccount,
                                    toAccount = transaction.toAccount,
                                    isDevelopmentMode = isDevelopmentMode
                                )
                            ) {
                                //TODO : Get added transaction ids, then we can delete these transactions - just like rollback, may need to rollback frequency of accounts too. - check for other impacts too...

                                return TransactionUtils.getFailedInsertTransactionResult(

                                    dateTimeInText = dateTimeInText,
                                    transactionParticulars = localTransactionParticulars,
                                    transactionAmount = localTransactionAmount,
                                    fromAccount = fromAccount,
                                    viaAccount = viaAccount,
                                    toAccount = toAccount
                                )
                            }
                        }

                        return InsertTransactionResult(

                            isSuccess = true,
                            dateTimeInText = DateTimeUtils.add5MinutesToNormalDateTimeInText(

                                dateTimeInText = transactions.last().eventDateTimeInText
                            ),
                            transactionParticulars = localTransactionParticulars,
                            transactionAmount = localTransactionAmount,
                            fromAccount = fromAccount,
                            viaAccount = viaAccount,
                            toAccount = toAccount
                        )
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
                    isCyclicViaStep = isCyclicViaStep,
                    chosenTransactionForSpecial = chosenTransactionForSpecial,
                    chosenSpecialTransactionType = chosenSpecialTransactionType,
                    dotEnv = dotEnv
                )

                "Ex" -> {

                    if (transactionType != TransactionTypeEnum.BAJAJ_COINS_FLAT) {

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
                                isCyclicViaStep = isCyclicViaStep,
                                chosenTransactionForSpecial = chosenTransactionForSpecial,
                                chosenSpecialTransactionType = chosenSpecialTransactionType,
                                dotEnv = dotEnv
                            )

                        } else {

                            ErrorUtilsInteractive.printInvalidOptionMessage()
                        }
                    } else {

                        ErrorUtilsInteractive.printInvalidOptionMessage()
                    }
                }

                "Ex13" -> {

                    if (transactionType != TransactionTypeEnum.BAJAJ_COINS_FLAT) {

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
                                isCyclicViaStep = isCyclicViaStep,
                                chosenTransactionForSpecial = chosenTransactionForSpecial,
                                chosenSpecialTransactionType = chosenSpecialTransactionType,
                                dotEnv = dotEnv
                            )

                        } else {

                            ErrorUtilsInteractive.printInvalidOptionMessage()
                        }
                    } else {

                        ErrorUtilsInteractive.printInvalidOptionMessage()
                    }
                }

                "Ex12" -> {

                    if (transactionType != TransactionTypeEnum.BAJAJ_COINS_FLAT) {

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
                                isCyclicViaStep = isCyclicViaStep,
                                chosenTransactionForSpecial = chosenTransactionForSpecial,
                                chosenSpecialTransactionType = chosenSpecialTransactionType,
                                dotEnv = dotEnv
                            )
                        } else {

                            ErrorUtilsInteractive.printInvalidOptionMessage()
                        }
                    } else {

                        ErrorUtilsInteractive.printInvalidOptionMessage()
                    }
                }

                "Ex23" -> {

                    if (transactionType != TransactionTypeEnum.BAJAJ_COINS_FLAT) {

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
                                isCyclicViaStep = isCyclicViaStep,
                                chosenTransactionForSpecial = chosenTransactionForSpecial,
                                chosenSpecialTransactionType = chosenSpecialTransactionType,
                                dotEnv = dotEnv
                            )
                        } else {

                            ErrorUtilsInteractive.printInvalidOptionMessage()
                        }
                    } else {

                        ErrorUtilsInteractive.printInvalidOptionMessage()
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

                else -> ErrorUtilsInteractive.printInvalidOptionMessage()
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
        isCyclicViaStep: Boolean,
        chosenTransactionForSpecial: TransactionResponse?,
        chosenSpecialTransactionType: SpecialTransactionTypeModel?,
        dotEnv: Dotenv

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
                    isCyclicViaStep = isCyclicViaStep,
                    chosenTransactionForSpecial = chosenTransactionForSpecial,
                    chosenSpecialTransactionType = chosenSpecialTransactionType,
                    dotEnv = dotEnv
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
                    isCyclicViaStep = isCyclicViaStep,
                    chosenTransactionForSpecial = chosenTransactionForSpecial,
                    chosenSpecialTransactionType = chosenSpecialTransactionType,
                    dotEnv = dotEnv
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
                    isCyclicViaStep = isCyclicViaStep,
                    chosenTransactionForSpecial = chosenTransactionForSpecial,
                    chosenSpecialTransactionType = chosenSpecialTransactionType,
                    dotEnv = dotEnv
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

                ApiUtilsInteractiveCommon.printServerExecutionErrorMessage(data)
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

                MysqlUtilsInteractive.normalDateTimeTextToMySqlDateTimeTextWithMessage(inputDateTimeText = eventDateTime)
            },
            transactionManipulationSuccessActions = fun() {

                val readFrequencyOfAccountsFileResult: IsOkModel<FrequencyOfAccountsModel> =
                    JsonFileUtilsInteractive.readJsonFile(

                        fileName = ConstantsNative.FREQUENCY_OF_ACCOUNTS_FILE_NAME,
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

                        fileName = ConstantsNative.FREQUENCY_OF_ACCOUNTS_FILE_NAME,
                        data = frequencyOfAccounts
                    )
                } else {

                    JsonFileUtils.writeJsonFile(

                        fileName = ConstantsNative.FREQUENCY_OF_ACCOUNTS_FILE_NAME,
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

                return MysqlUtilsInteractive.normalDateTimeTextToMySqlDateTimeTextWithMessage(inputDateTimeText = eventDateTime)
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
}
