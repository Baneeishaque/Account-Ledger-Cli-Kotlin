package accountLedgerCli.cli

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.api.response.MultipleTransactionResponse
import account.ledger.library.api.response.TransactionResponse
import account.ledger.library.enums.*
import account.ledger.library.models.*
import account.ledger.library.operations.DataOperations
import account.ledger.library.operations.LedgerSheetOperations
import account.ledger.library.operations.ServerOperations
import account.ledger.library.retrofit.data.MultipleTransactionDataSource
import account.ledger.library.utils.*
import account_ledger_library.constants.ConstantsNative
import common.utils.library.models.IsOkModel
import common.utils.library.utils.*
import io.github.cdimascio.dotenv.Dotenv
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

object Screens {

    internal fun userScreen(

        username: String,
        userId: UInt,
        fromAccount: AccountResponse,
        viaAccount: AccountResponse,
        toAccount: AccountResponse,
        dateTimeInText: String,
        transactionParticulars: String,
        transactionAmount: Float,
        isDevelopmentMode: Boolean,
        dotEnv: Dotenv

    ): InsertTransactionResult {

        if (isDevelopmentMode) {

            println("Env. Variables : ${dotEnv.entries()}")
        }
        var insertTransactionResult: InsertTransactionResult = TransactionUtils.getFailedInsertTransactionResult(

            dateTimeInText = dateTimeInText,
            transactionParticulars = transactionParticulars,
            transactionAmount = transactionAmount,
            fromAccount = fromAccount,
            viaAccount = viaAccount,
            toAccount = toAccount
        )
        do {
            App.commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                listOf(
                    "\nUser : $username",
                    AccountUtils.getFrequentlyUsedTop10Accounts(userId = userId, isDevelopmentMode),
                    "1 - List Accounts : Top Levels",
                    "2 - ${getQuickTransactionOnWalletText()}",
                    "3 - ${
                        getQuickTransactionOnWalletToFrequent1Text(

                            dotEnv = dotEnv
                        )
                    }",
                    "4 - ${
                        getQuickTransactionOnWalletToFrequent2Text(

                            dotEnv = dotEnv
                        )
                    }",
                    "5 - ${
                        getQuickTransactionOnWalletToFrequent3Text(

                            dotEnv = dotEnv
                        )
                    }",
                    "6 - ${
                        getQuickTransactionOnBankText(

                            dotEnv = dotEnv
                        )
                    }",
                    "7 - ${
                        getQuickTransactionOnBankToFrequent1Text(

                            dotEnv = dotEnv
                        )
                    }",
                    "8 - ${
                        getQuickTransactionOnBankToFrequent2Text(

                            dotEnv = dotEnv
                        )
                    }",
                    "9 - ${
                        getQuickTransactionOnBankToFrequent3Text(

                            dotEnv = dotEnv
                        )
                    }",
                    "10 - ${
                        getQuickTransactionOnFrequent1Text(

                            dotEnv = dotEnv
                        )
                    }",
                    "11 - ${
                        getQuickTransactionOnFrequent2Text(

                            dotEnv = dotEnv
                        )
                    }",
                    "12 - ${
                        getQuickTransactionOnFrequent3Text(

                            dotEnv = dotEnv
                        )
                    }",
                    "13 - List Accounts : Full Names",
                    /*"14 - Import Transactions To : Bank : ${
                        getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.BANK_ACCOUNT_NAME.name)
                    } From CSV",
                    "15 - Import Transactions To : Bank : ${
                        getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.BANK_ACCOUNT_NAME.name)
                    } From XLX",*/
                    "16 - Check Affected A/Cs : After A Specified Date",
                    "17 - View Transactions of a Specific A/C",
                    //TODO : Move Ledger Sheets to another Menu
                    "18 - View Balance Sheet Ledger (All)",
                    "19 - View Balance Sheet Ledger (Excluding Open Balances)",
                    "20 - View Balance Sheet Ledger (Excluding Open Balances & Misc. Incomes)",
                    "21 - View Balance Sheet Ledger (Excluding Open Balances, Misc. Incomes & Investment Returns)",
                    "22 - View Balance Sheet Ledger (Excluding Open Balances, Misc. Incomes, Investment Returns & Family Accounts)",
                    "23 - View Balance Sheet Ledger (Excluding Open Balances, Misc. Incomes, Investment Returns, Family & Expense Accounts)",
                    //TODO : Use Env. Variable
                    //TODO : Use another menu for view transactions of common accounts
                    "24 - View Transactions of Wallet A/C",
                    "25 - View Transactions of ${
                        getEnvironmentVariableValueForUserScreen(

                            environmentVariableName = EnvironmentFileEntryEnum.BANK_ACCOUNT_NAME.name,
                            dotEnv = dotEnv
                        )
                    } A/C",
                    "26 - View Transactions of ${
                        getEnvironmentVariableValueForUserScreen(
                            environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_1_ACCOUNT_NAME.name,
                            dotEnv = dotEnv
                        )
                    } A/C",
                    "27 - View Transactions of ${
                        getEnvironmentVariableValueForUserScreen(
                            environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_2_ACCOUNT_NAME.name,
                            dotEnv = dotEnv
                        )
                    } A/C",
                    "28 - View Transactions of ${
                        getEnvironmentVariableValueForUserScreen(
                            environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_3_ACCOUNT_NAME.name,
                            dotEnv = dotEnv
                        )
                    } A/C",
//                    "29 - Check Affected A/Cs : From First Entry",
                    "30 - Check Affected A/Cs : From Start Date",
                    "31 - View Last 10 Transactions",
//                    "32 - Check Affected A/Cs : From Start Date to A Specified Date",
                    //TODO : Check this
                    "33 - Check Affected A/Cs : From Start Date to A Specified Time Stamp",
                    /*"34 - Check Affected A/Cs : After A Specified Date to A Specified Date",
                    "35 - Check Affected A/Cs : After A Specified Date to A Specified Time Stamp",
                    "36 - Check Affected A/Cs : After A Specified Time Stamp to A Specified Date",
                    "37 - Check Affected A/Cs : After A Specified Time Stamp to A Specified Time Stamp",*/
                    //TODO : Check this
                    "38 - Check Affected A/Cs : After A Specified Date",
                    //TODO : Move Ledger Sheets to another Menu - continue
                    "39 - View Income Sheet",
                    "40 - View Expense Sheet",
                    "41 - View Profit Sheet",
                    //TODO : Move Not Consider Ledger Sheets to another Menu - continue
                    "42 - View Not Consider for Income / Expense Sheet",
                    "43 - View Debit Credit Sheet",
                    "44 - View Not Consider for Income / Expense / Debit / Credit Sheet",
                    "45 - View Assets Sheet",
                    "46 - View Not Consider for Income / Expense / Debit / Credit / Assets Sheet",
                    "47 - View Debit Sheet",
                    "48 - View Credit Sheet",
                    "49 - View Debit - Credit Sheet",
                    "50 - View All Sheets",
                    "0 - Logout",
                    "",
                    "Enter Your Choice : "
                )
            )
            when (readln()) {

                "1" -> {

                    insertTransactionResult = HandleResponsesInteractiveCli.handleAccountsResponseAndPrintMenu(

                        apiResponse = ServerOperations.getAccounts(

                            userId = userId,
                            isConsoleMode = true,
                            isDevelopmentMode = isDevelopmentMode
                        ),
                        username = username,
                        userId = userId,
                        previousTransactionData = insertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "2" -> {

                    insertTransactionResult = quickTransactionOnWallet(

                        previousTransactionData = insertTransactionResult,
                        userId = userId,
                        username = username,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "3" -> {

                    insertTransactionResult = quickTransactionOnWalletToFrequent1(

                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "4" -> {

                    insertTransactionResult = quickTransactionOnWalletToFrequent2(

                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "5" -> {

                    insertTransactionResult = quickTransactionOnWalletToFrequent3(

                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "6" -> {

                    insertTransactionResult = quickTransactionOnBank(

                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "7" -> {

                    insertTransactionResult = quickTransactionOnBankToFrequent1(

                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "8" -> {

                    insertTransactionResult = quickTransactionOnBankToFrequent2(

                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "9" -> {

                    insertTransactionResult = quickTransactionOnBankToFrequent3(

                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "10" -> {

                    insertTransactionResult = quickTransactionOnFrequent1(

                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "11" -> {

                    insertTransactionResult = quickTransactionOnFrequent2(

                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "12" -> {

                    insertTransactionResult = quickTransactionOnFrequent3(

                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "13" -> {

                    insertTransactionResult = HandleResponsesInteractiveCli.handleAccountsResponseAndPrintMenu(

                        apiResponse = ApiUtilsInteractive.getAccountsFull(

                            userId = userId,
                            isConsoleMode = true,
                            isDevelopmentMode = isDevelopmentMode
                        ),
                        username = username,
                        userId = userId,
                        previousTransactionData = insertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "14", "15", "29", "32", "34", "35", "36", "37" -> {

                    ToDoUtilsInteractive.showTodo()
                }

                "16" -> {

                    insertTransactionResult = checkAffectedAccountsAfterSpecifiedDate(

                        desiredDate = InputUtilsInteractive.getValidDateInNormalPattern(),
                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "17" -> {

                    TransactionViews.viewTransactionsOfInputAccount(

                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "18" -> {

                    LedgerSheetOperations.printBalanceSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.ALL,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = App.reloadDotEnv()
                    )
                }

                "19" -> {

                    LedgerSheetOperations.printBalanceSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_OPEN_BALANCES,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = App.reloadDotEnv()
                    )
                }

                "20" -> {

                    LedgerSheetOperations.printBalanceSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_MISC_INCOMES,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = App.reloadDotEnv()
                    )
                }

                "21" -> {

                    LedgerSheetOperations.printBalanceSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_INVESTMENT_RETURNS,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = App.reloadDotEnv()
                    )
                }

                "22" -> {

                    LedgerSheetOperations.printBalanceSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_FAMILY_ACCOUNTS,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = App.reloadDotEnv()
                    )
                }

                "23" -> {

                    LedgerSheetOperations.printBalanceSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_EXPENSE_ACCOUNTS,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = App.reloadDotEnv()
                    )
                }

                "24" -> {

                    // TODO : Check for env. variable availability
                    insertTransactionResult = viewTransactionsOfAnAccountIndex(

                        userId = userId,
                        previousTransactionData = insertTransactionResult,
                        username = username,
                        desiredAccountIndex = App.walletAccount.value!!,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "25" -> {

                    insertTransactionResult = viewTransactionsOfAnAccountIndex(

                        userId = userId,
                        previousTransactionData = insertTransactionResult,
                        username = username,
                        desiredAccountIndex = App.bankAccount.value!!,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "26" -> {

                    insertTransactionResult = viewTransactionsOfAnAccountIndex(

                        userId = userId,
                        previousTransactionData = insertTransactionResult,
                        username = username,
                        desiredAccountIndex = App.frequent1Account.value!!,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "27" -> {

                    insertTransactionResult = viewTransactionsOfAnAccountIndex(

                        userId = userId,
                        previousTransactionData = insertTransactionResult,
                        username = username,
                        desiredAccountIndex = App.frequent2Account.value!!,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "28" -> {

                    insertTransactionResult = viewTransactionsOfAnAccountIndex(

                        userId = userId,
                        previousTransactionData = insertTransactionResult,
                        username = username,
                        desiredAccountIndex = App.frequent3Account.value!!,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "30" -> {

                    val userInitialTransactionDateFromUsernameResult: IsOkModel<LocalDate> =
                        DataOperations.getUserInitialTransactionDateFromUsername(username = username)
                    if (userInitialTransactionDateFromUsernameResult.isOK) {
                        insertTransactionResult = checkAffectedAccountsAfterSpecifiedDate(

                            desiredDate = userInitialTransactionDateFromUsernameResult.data!!
                                .minusDays(1)
                                .format(DateTimeUtils.normalDatePattern),
                            userId = userId,
                            username = username,
                            previousTransactionData = insertTransactionResult,
                            isConsoleMode = true,
                            isDevelopmentMode = isDevelopmentMode,
                            dotEnv = dotEnv
                        )
                    } else {

                        println(ConstantsNative.DATE_FROM_USERNAME_ERROR)
                    }
                }

                "31" -> {

                    val getTransactionResult: IsOkModel<MultipleTransactionResponse> =
                        ApiUtilsInteractiveCommon.makeApiRequestWithOptionalRetries(
                            apiCallFunction = fun(): Result<MultipleTransactionResponse> {

                                return runBlocking {

                                    MultipleTransactionDataSource().selectTransactions(userId = userId)
                                }
                            },
                            isConsoleMode = true,
                            isDevelopmentMode = isDevelopmentMode
                        )

                    IsOkUtils.isOkHandler(

                        isOkModel = getTransactionResult,
                        successActions = fun() {

                            insertTransactionResult = TransactionViews.viewTransactions(

                                userMultipleTransactionResponse = getTransactionResult.data!!,
                                accountFullName = "Last 10",
                                dateTimeInText = insertTransactionResult.dateTimeInText,
                                transactionParticulars = insertTransactionResult.transactionParticulars,
                                transactionAmount = insertTransactionResult.transactionAmount,
                                fromAccount = insertTransactionResult.fromAccount,
                                viaAccount = insertTransactionResult.viaAccount,
                                toAccount = insertTransactionResult.toAccount,
                                username = username,
                                accountId = 0u,
                                functionCallSource = FunctionCallSourceEnum.FROM_VIEW_TRANSACTIONS_OF_AN_ACCOUNT,
                                userId = userId,
                                isConsoleMode = true,
                                isDevelopmentMode = isDevelopmentMode,
                                dotEnv = dotEnv

                            ).addTransactionResult
                        })
                }

                "33" -> {

                    val userInitialTransactionDateFromUsernameResult: IsOkModel<LocalDate> =
                        DataOperations.getUserInitialTransactionDateFromUsername(username = username)

                    if (userInitialTransactionDateFromUsernameResult.isOK) {
                        insertTransactionResult = checkAffectedAccountsAfterSpecifiedDate(

                            desiredDate = userInitialTransactionDateFromUsernameResult.data!!
                                .minusDays(1)
                                .format(DateTimeUtils.normalDatePattern),
                            userId = userId,
                            username = username,
                            previousTransactionData = insertTransactionResult,
                            isUpToTimeStamp = true,
                            upToTimeStamp = InputUtilsInteractive.getValidDateTimeInNormalPattern(promptPrefix = "Up to "),
                            isConsoleMode = true,
                            isDevelopmentMode = isDevelopmentMode,
                            dotEnv = dotEnv
                        )
                    } else {

                        println(ConstantsNative.DATE_FROM_USERNAME_ERROR)
                    }
                }

                "38" -> {

                    insertTransactionResult = checkAffectedAccountsAfterSpecifiedDate(

                        desiredDate = InputUtilsInteractive.getValidDateInNormalPattern(promptPrefix = "After "),
                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "39" -> {

                    LedgerSheetOperations.printIncomeSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = App.reloadDotEnv()
                    )
                }

                "40" -> {

                    LedgerSheetOperations.printExpenseSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = App.reloadDotEnv()
                    )
                }

                "41" -> {

                    LedgerSheetOperations.printProfitSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = App.reloadDotEnv()
                    )
                }

                "42" -> {

                    LedgerSheetOperations.printNotConsiderForIncomeExpenseSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = App.reloadDotEnv()
                    )
                }

                "43" -> {

                    LedgerSheetOperations.printDebitCreditSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = App.reloadDotEnv()
                    )
                }

                "44" -> {

                    LedgerSheetOperations.printNotConsiderForIncomeExpenseOrDebitCreditSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = App.reloadDotEnv()
                    )
                }

                "45" -> {

                    LedgerSheetOperations.printAssetSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = App.reloadDotEnv()
                    )
                }

                "46" -> {

                    LedgerSheetOperations.printNotConsiderForIncomeExpenseDebitCreditOrAssetSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = App.reloadDotEnv()
                    )
                }

                "47" -> {

                    LedgerSheetOperations.printDebitSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = App.reloadDotEnv()
                    )
                }

                "48" -> {

                    LedgerSheetOperations.printCreditSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = App.reloadDotEnv()
                    )
                }

                "49" -> {

                    LedgerSheetOperations.printDebitCreditBalanceSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = App.reloadDotEnv()
                    )
                }

                "50" -> {

                    LedgerSheetOperations.printProfitSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        isConsoleMode = true,
                        isDevelopmentMode = false,
                        dotEnv = App.reloadDotEnv()
                    )

                    LedgerSheetOperations.printNotConsiderForIncomeExpenseSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        isConsoleMode = true,
                        isDevelopmentMode = false,
                        dotEnv = App.reloadDotEnv()
                    )

                    LedgerSheetOperations.printDebitCreditSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        isConsoleMode = true,
                        isDevelopmentMode = false,
                        dotEnv = App.reloadDotEnv()
                    )

                    LedgerSheetOperations.printNotConsiderForIncomeExpenseOrDebitCreditSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        isConsoleMode = true,
                        isDevelopmentMode = false,
                        dotEnv = App.reloadDotEnv()
                    )

                    LedgerSheetOperations.printAssetSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        isConsoleMode = true,
                        isDevelopmentMode = false,
                        dotEnv = App.reloadDotEnv()
                    )

                    LedgerSheetOperations.printNotConsiderForIncomeExpenseDebitCreditOrAssetSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        isConsoleMode = true,
                        isDevelopmentMode = false,
                        dotEnv = App.reloadDotEnv()
                    )

                    LedgerSheetOperations.printDebitCreditBalanceSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        isConsoleMode = true,
                        isDevelopmentMode = false,
                        dotEnv = App.reloadDotEnv()
                    )
                }

                "0" -> {

                    return insertTransactionResult
                }

                else -> {

                    ErrorUtilsInteractive.printInvalidOptionMessage()
                }
            }
        } while (true)
    }

    @JvmStatic
    fun getQuickTransactionOnBankToFrequent1Text(dotEnv: Dotenv): String =
        getQuickTransactionOnBankToFrequentXText(
            environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_1_ACCOUNT_NAME.name,
            dotEnv = dotEnv
        )

    @JvmStatic
    fun getQuickTransactionOnBankToFrequent2Text(dotEnv: Dotenv): String =
        getQuickTransactionOnBankToFrequentXText(
            environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_2_ACCOUNT_NAME.name,
            dotEnv = dotEnv
        )

    @JvmStatic
    fun getQuickTransactionOnBankToFrequent3Text(dotEnv: Dotenv): String =
        getQuickTransactionOnBankToFrequentXText(
            environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_3_ACCOUNT_NAME.name,
            dotEnv = dotEnv
        )

    @JvmStatic
    fun getQuickTransactionOnWalletToFrequent1Text(dotEnv: Dotenv): String =
        getQuickTransactionOnWalletToFrequentXText(
            environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_1_ACCOUNT_NAME.name,
            dotEnv = dotEnv
        )

    @JvmStatic
    fun getQuickTransactionOnWalletToFrequent2Text(dotEnv: Dotenv): String =
        getQuickTransactionOnWalletToFrequentXText(
            environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_2_ACCOUNT_NAME.name,
            dotEnv = dotEnv
        )

    @JvmStatic
    fun getQuickTransactionOnWalletToFrequent3Text(dotEnv: Dotenv): String =
        getQuickTransactionOnWalletToFrequentXText(
            environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_3_ACCOUNT_NAME.name,
            dotEnv = dotEnv
        )

    @JvmStatic
    fun getQuickTransactionOnWalletToFrequentXText(environmentVariableName: String, dotEnv: Dotenv): String =
        "${getQuickTransactionOnWalletText()} To : ${
            getEnvironmentVariableValueForUserScreen(

                environmentVariableName = environmentVariableName,
                dotEnv = dotEnv
            )
        }"

    private fun getQuickTransactionOnBankToFrequentXText(environmentVariableName: String, dotEnv: Dotenv): String =
        "${
            getQuickTransactionOnBankText(

                dotEnv = dotEnv
            )
        } To : ${
            getEnvironmentVariableValueForUserScreen(

                environmentVariableName = environmentVariableName,
                dotEnv = dotEnv
            )
        }"

    @JvmStatic
    fun getQuickTransactionOnBankText(dotEnv: Dotenv): String = getQuickTransactionOnXText(
        itemSpecification = "Bank : ${
            getEnvironmentVariableValueForUserScreen(
                environmentVariableName = EnvironmentFileEntryEnum.BANK_ACCOUNT_NAME.name,
                dotEnv = dotEnv
            )
        }"
    )

    @JvmStatic
    fun getQuickTransactionOnFrequent1Text(dotEnv: Dotenv): String = getQuickTransactionOnXText(
        itemSpecification = getEnvironmentVariableValueForUserScreen(
            environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_1_ACCOUNT_NAME.name,
            dotEnv = dotEnv
        )
    )

    @JvmStatic
    fun getQuickTransactionOnFrequent2Text(dotEnv: Dotenv): String = getQuickTransactionOnXText(
        itemSpecification = getEnvironmentVariableValueForUserScreen(
            environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_2_ACCOUNT_NAME.name,
            dotEnv = dotEnv
        )
    )

    @JvmStatic
    fun getQuickTransactionOnFrequent3Text(dotEnv: Dotenv): String = getQuickTransactionOnXText(
        itemSpecification = getEnvironmentVariableValueForUserScreen(
            environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_3_ACCOUNT_NAME.name,
            dotEnv = dotEnv
        )
    )

    @JvmStatic
    fun getQuickTransactionOnWalletText() = getQuickTransactionOnXText(itemSpecification = "Wallet")

    private fun getQuickTransactionOnXText(itemSpecification: String) =
        "Insert Quick Transaction On : $itemSpecification"

    @JvmStatic
    fun quickTransactionOnFrequent3(

        userId: UInt,
        username: String,
        previousTransactionData: InsertTransactionResult,
        isDevelopmentMode: Boolean,
        dotEnv: Dotenv

    ): InsertTransactionResult = quickTransactionOnX(

        account = App.frequent3Account,
        previousTransactionData = previousTransactionData,
        userId = userId,
        username = username,
        isDevelopmentMode = isDevelopmentMode,
        dotEnv = dotEnv
    )


    @JvmStatic
    fun quickTransactionOnFrequent2(

        userId: UInt,
        username: String,
        previousTransactionData: InsertTransactionResult,
        isDevelopmentMode: Boolean,
        dotEnv: Dotenv

    ): InsertTransactionResult = quickTransactionOnX(

        account = App.frequent2Account,
        previousTransactionData = previousTransactionData,
        userId = userId,
        username = username,
        isDevelopmentMode = isDevelopmentMode,
        dotEnv = dotEnv
    )


    @JvmStatic
    fun quickTransactionOnFrequent1(

        userId: UInt,
        username: String,
        previousTransactionData: InsertTransactionResult,
        isDevelopmentMode: Boolean,
        dotEnv: Dotenv

    ): InsertTransactionResult = quickTransactionOnX(

        account = App.frequent1Account,
        previousTransactionData = previousTransactionData,
        userId = userId,
        username = username,
        isDevelopmentMode = isDevelopmentMode,
        dotEnv = dotEnv
    )

    @JvmStatic
    fun quickTransactionOnBankToFrequent3(

        userId: UInt,
        username: String,
        previousTransactionData: InsertTransactionResult,
        chosenTransactionForSpecial: TransactionResponse? = null,
        chosenSpecialTransactionType: SpecialTransactionTypeModel? = null,
        isDevelopmentMode: Boolean,
        dotEnv: Dotenv

    ): InsertTransactionResult = quickTransactionOnBankToX(

        account2 = App.frequent3Account,
        userId = userId,
        username = username,
        previousTransactionData = previousTransactionData,
        chosenTransactionForSpecial = chosenTransactionForSpecial,
        chosenSpecialTransactionType = chosenSpecialTransactionType,
        isDevelopmentMode = isDevelopmentMode,
        dotEnv = dotEnv
    )

    @JvmStatic
    fun quickTransactionOnBankToFrequent2(

        userId: UInt,
        username: String,
        previousTransactionData: InsertTransactionResult,
        chosenTransactionForSpecial: TransactionResponse? = null,
        chosenSpecialTransactionType: SpecialTransactionTypeModel? = null,
        isDevelopmentMode: Boolean,
        dotEnv: Dotenv

    ): InsertTransactionResult = quickTransactionOnBankToX(

        account2 = App.frequent2Account,
        userId = userId,
        username = username,
        previousTransactionData = previousTransactionData,
        chosenTransactionForSpecial = chosenTransactionForSpecial,
        chosenSpecialTransactionType = chosenSpecialTransactionType,
        isDevelopmentMode = isDevelopmentMode,
        dotEnv = dotEnv
    )

    @JvmStatic
    fun quickTransactionOnBankToFrequent1(

        userId: UInt,
        username: String,
        previousTransactionData: InsertTransactionResult,
        chosenTransactionForSpecial: TransactionResponse? = null,
        chosenSpecialTransactionType: SpecialTransactionTypeModel? = null,
        isDevelopmentMode: Boolean,
        dotEnv: Dotenv

    ): InsertTransactionResult = quickTransactionOnBankToX(

        account2 = App.frequent1Account,
        userId = userId,
        username = username,
        previousTransactionData = previousTransactionData,
        chosenTransactionForSpecial = chosenTransactionForSpecial,
        chosenSpecialTransactionType = chosenSpecialTransactionType,
        isDevelopmentMode = isDevelopmentMode,
        dotEnv = dotEnv
    )

    @JvmStatic
    fun quickTransactionOnBank(

        userId: UInt,
        username: String,
        previousTransactionData: InsertTransactionResult,
        isDevelopmentMode: Boolean,
        dotEnv: Dotenv

    ): InsertTransactionResult = quickTransactionOnX(

        account = App.bankAccount,
        previousTransactionData = previousTransactionData,
        userId = userId,
        username = username,
        isDevelopmentMode = isDevelopmentMode,
        dotEnv = dotEnv
    )

    @JvmStatic
    fun quickTransactionOnWalletToFrequent3(

        userId: UInt,
        username: String,
        previousTransactionData: InsertTransactionResult,
        chosenTransactionForSpecial: TransactionResponse? = null,
        chosenSpecialTransactionType: SpecialTransactionTypeModel? = null,
        isDevelopmentMode: Boolean,
        dotEnv: Dotenv

    ): InsertTransactionResult = quickTransactionOnWalletToX(

        account2 = App.frequent3Account,
        userId = userId,
        username = username,
        previousTransactionData = previousTransactionData,
        chosenTransactionForSpecial = chosenTransactionForSpecial,
        chosenSpecialTransactionType = chosenSpecialTransactionType,
        isDevelopmentMode = isDevelopmentMode,
        dotEnv = dotEnv
    )

    @JvmStatic
    fun quickTransactionOnWalletToFrequent2(

        userId: UInt,
        username: String,
        previousTransactionData: InsertTransactionResult,
        chosenTransactionForSpecial: TransactionResponse? = null,
        chosenSpecialTransactionType: SpecialTransactionTypeModel? = null,
        isDevelopmentMode: Boolean,
        dotEnv: Dotenv

    ): InsertTransactionResult = quickTransactionOnWalletToX(

        account2 = App.frequent2Account,
        userId = userId,
        username = username,
        previousTransactionData = previousTransactionData,
        chosenTransactionForSpecial = chosenTransactionForSpecial,
        chosenSpecialTransactionType = chosenSpecialTransactionType,
        isDevelopmentMode = isDevelopmentMode,
        dotEnv = dotEnv
    )

    @JvmStatic
    fun quickTransactionOnWalletToFrequent1(

        userId: UInt,
        username: String,
        previousTransactionData: InsertTransactionResult,
        chosenTransactionForSpecial: TransactionResponse? = null,
        chosenSpecialTransactionType: SpecialTransactionTypeModel? = null,
        isDevelopmentMode: Boolean,
        dotEnv: Dotenv

    ): InsertTransactionResult = quickTransactionOnWalletToX(

        account2 = App.frequent1Account,
        userId = userId,
        username = username,
        previousTransactionData = previousTransactionData,
        chosenTransactionForSpecial = chosenTransactionForSpecial,
        chosenSpecialTransactionType = chosenSpecialTransactionType,
        isDevelopmentMode = isDevelopmentMode,
        dotEnv = dotEnv
    )

    private fun quickTransactionOnWalletToX(

        account2: EnvironmentVariableForWholeNumber,
        userId: UInt,
        username: String,
        previousTransactionData: InsertTransactionResult,
        chosenTransactionForSpecial: TransactionResponse?,
        chosenSpecialTransactionType: SpecialTransactionTypeModel?,
        isDevelopmentMode: Boolean,
        dotEnv: Dotenv

    ): InsertTransactionResult = quickTransactionOnXToY(

        account1 = App.walletAccount,
        account2 = account2,
        userId = userId,
        username = username,
        previousTransactionData = previousTransactionData,
        chosenTransactionForSpecial = chosenTransactionForSpecial,
        chosenSpecialTransactionType = chosenSpecialTransactionType,
        isDevelopmentMode = isDevelopmentMode,
        dotEnv = dotEnv
    )

    private fun quickTransactionOnBankToX(

        account2: EnvironmentVariableForWholeNumber,
        userId: UInt,
        username: String,
        previousTransactionData: InsertTransactionResult,
        chosenTransactionForSpecial: TransactionResponse?,
        chosenSpecialTransactionType: SpecialTransactionTypeModel?,
        isDevelopmentMode: Boolean,
        dotEnv: Dotenv

    ): InsertTransactionResult = quickTransactionOnXToY(

        account1 = App.bankAccount,
        account2 = account2,
        userId = userId,
        username = username,
        previousTransactionData = previousTransactionData,
        chosenTransactionForSpecial = chosenTransactionForSpecial,
        chosenSpecialTransactionType = chosenSpecialTransactionType,
        isDevelopmentMode = isDevelopmentMode,
        dotEnv = dotEnv
    )

    private fun quickTransactionOnXToY(

        account1: EnvironmentVariableForWholeNumber,
        account2: EnvironmentVariableForWholeNumber,
        userId: UInt,
        username: String,
        previousTransactionData: InsertTransactionResult,
        chosenTransactionForSpecial: TransactionResponse?,
        chosenSpecialTransactionType: SpecialTransactionTypeModel?,
        isDevelopmentMode: Boolean,
        dotEnv: Dotenv

    ): InsertTransactionResult = InsertOperationsInteractive.insertQuickTransactionFromAccount1toAccount2(

        account1 = account1,
        account2 = account2,
        userId = userId,
        username = username,
        fromAccount = previousTransactionData.fromAccount,
        viaAccount = previousTransactionData.viaAccount,
        toAccount = previousTransactionData.toAccount,
        dateTimeInText = previousTransactionData.dateTimeInText,
        transactionParticulars = previousTransactionData.transactionParticulars,
        transactionAmount = previousTransactionData.transactionAmount,
        chosenTransactionForSpecial = chosenTransactionForSpecial,
        chosenSpecialTransactionType = chosenSpecialTransactionType,
        isDevelopmentMode = isDevelopmentMode,
        dotEnv = dotEnv
    )

    @JvmStatic
    fun quickTransactionOnWallet(

        previousTransactionData: InsertTransactionResult,
        userId: UInt,
        username: String,
        isDevelopmentMode: Boolean,
        dotEnv: Dotenv

    ): InsertTransactionResult = quickTransactionOnX(

        account = App.walletAccount,
        previousTransactionData = previousTransactionData,
        userId = userId,
        username = username,
        isDevelopmentMode = isDevelopmentMode,
        dotEnv = dotEnv
    )

    private fun quickTransactionOnX(

        account: EnvironmentVariableForWholeNumber,
        previousTransactionData: InsertTransactionResult,
        userId: UInt,
        username: String,
        isDevelopmentMode: Boolean,
        dotEnv: Dotenv

    ): InsertTransactionResult {

        return InsertOperationsInteractive.openSpecifiedAccountHome(

            account = account,
            userId = userId,
            username = username,
            fromAccount = previousTransactionData.fromAccount,
            viaAccount = previousTransactionData.viaAccount,
            toAccount = previousTransactionData.toAccount,
            dateTimeInText = previousTransactionData.dateTimeInText,
            transactionParticulars = previousTransactionData.transactionParticulars,
            transactionAmount = previousTransactionData.transactionAmount,
            isDevelopmentMode = isDevelopmentMode,
            dotEnv = dotEnv
        )
    }

    private fun viewTransactionsOfAnAccountIndex(

        userId: UInt,
        previousTransactionData: InsertTransactionResult,
        username: String,
        desiredAccountIndex: UInt,
        isDevelopmentMode: Boolean,
        dotEnv: Dotenv

    ): InsertTransactionResult {

        var localInsertTransactionResult: InsertTransactionResult = previousTransactionData

        val getUserAccountsMapResult: IsOkModel<LinkedHashMap<UInt, AccountResponse>> =
            HandleResponsesInteractiveLibrary.getUserAccountsMap(
                apiResponse = ApiUtilsInteractive.getAccountsFull(

                    userId = userId,
                    isConsoleMode = true,
                    isDevelopmentMode = isDevelopmentMode
                )
            )

        if (getUserAccountsMapResult.isOK && getUserAccountsMapResult.data!!.containsKey(desiredAccountIndex)) {

            val selectedAccount: AccountResponse = getUserAccountsMapResult.data!![desiredAccountIndex]!!
            localInsertTransactionResult = TransactionViews.viewTransactionsForAnAccount(

                userId = userId,
                username = username,
                accountId = desiredAccountIndex,
                accountFullName = selectedAccount.fullName,
                previousTransactionData = previousTransactionData,
                fromAccount = selectedAccount,
                isConsoleMode = true,
                isDevelopmentMode = isDevelopmentMode,
                dotEnv = dotEnv

            ).addTransactionResult
        }
        return localInsertTransactionResult
    }

    private fun getEnvironmentVariableValueForUserScreen(

        environmentVariableName: String,
        dotEnv: Dotenv

    ): String =
        EnvironmentFileOperations.getEnvironmentVariableValueForTextWithDefaultValue(

            dotEnv = dotEnv,
            environmentVariableName = environmentVariableName,
            defaultValue = ConstantsNative.DEFAULT_VALUE_FOR_STRING_ENVIRONMENT_VARIABLES
        )

    fun accountHome(

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

        var localInsertTransactionResult: InsertTransactionResult = TransactionUtils.getFailedInsertTransactionResult(

            dateTimeInText = dateTimeInText,
            transactionParticulars = transactionParticulars,
            transactionAmount = transactionAmount,
            fromAccount = fromAccount,
            viaAccount = viaAccount,
            toAccount = toAccount
        )
        do {
            App.commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(

                listOfCommands = listOf(
                    "\n${ConstantsNative.USER_TEXT} : $username",
                    "${ConstantsNative.ACCOUNT_TEXT} - ${fromAccount.fullName}",
                    "1 - View ${ConstantsNative.TRANSACTION_TEXT}s in Ledger Mode",
                    "2 - View ${ConstantsNative.TRANSACTION_TEXT}s in Credit - Debit Mode",
                    "3 - Add ${ConstantsNative.TRANSACTION_TEXT}",
                    "4 - View Child ${ConstantsNative.ACCOUNT_TEXT}s",
                    "5 - Add Via. ${ConstantsNative.TRANSACTION_TEXT}",
                    "6 - Add Two Way ${ConstantsNative.TRANSACTION_TEXT}",
                    "7 - Add Cyclic Via. ${ConstantsNative.TRANSACTION_TEXT}",
                    "8 - Add ${ConstantsNative.SPECIAL_TEXT} ${ConstantsNative.TRANSACTION_TEXT} for ${ConstantsNative.BAJAJ_COINS_TEXT} (${BajajDiscountTypeEnum.Flat.name})",
                    "9 - Add ${ConstantsNative.SPECIAL_TEXT} ${ConstantsNative.TRANSACTION_TEXT} for ${ConstantsNative.BAJAJ_COINS_TEXT} ${ConstantsNative.WITHOUT_FUNDING_TRANSACTION_TEXT} (${BajajDiscountTypeEnum.Flat.name})",
                    "10 - Add ${ConstantsNative.SPECIAL_TEXT} ${ConstantsNative.TRANSACTION_TEXT} for ${ConstantsNative.BAJAJ_COINS_TEXT} ${ConstantsNative.WITHOUT_BALANCE_CHECK_TEXT} (${BajajDiscountTypeEnum.Flat.name})",
                    "11 - Add ${ConstantsNative.SPECIAL_TEXT} ${ConstantsNative.TRANSACTION_TEXT} for ${ConstantsNative.BAJAJ_CASHBACK_TEXT} (${BajajDiscountTypeEnum.Flat.name})",
                    "12 - Add ${ConstantsNative.SPECIAL_TEXT} ${ConstantsNative.TRANSACTION_TEXT} for ${ConstantsNative.BAJAJ_CASHBACK_TEXT} ${ConstantsNative.WITHOUT_FUNDING_TRANSACTION_TEXT} (${BajajDiscountTypeEnum.Flat.name})",
                    "13 - Add ${ConstantsNative.SPECIAL_TEXT} ${ConstantsNative.TRANSACTION_TEXT} for ${ConstantsNative.BAJAJ_CASHBACK_TEXT} ${ConstantsNative.WITHOUT_BALANCE_CHECK_TEXT} (${BajajDiscountTypeEnum.Flat.name})",
                    "14 - Add ${ConstantsNative.SPECIAL_TEXT} ${ConstantsNative.TRANSACTION_TEXT} for ${ConstantsNative.BAJAJ_COINS_TEXT} (${BajajDiscountTypeEnum.UpTo.name})",
                    "15 - Add ${ConstantsNative.SPECIAL_TEXT} ${ConstantsNative.TRANSACTION_TEXT} for ${ConstantsNative.BAJAJ_COINS_TEXT} ${ConstantsNative.WITHOUT_FUNDING_TRANSACTION_TEXT} (${BajajDiscountTypeEnum.UpTo.name})",
                    "16 - Add ${ConstantsNative.SPECIAL_TEXT} ${ConstantsNative.TRANSACTION_TEXT} for ${ConstantsNative.BAJAJ_COINS_TEXT} ${ConstantsNative.WITHOUT_BALANCE_CHECK_TEXT} (${BajajDiscountTypeEnum.UpTo.name})",
                    "17 - Add ${ConstantsNative.SPECIAL_TEXT} ${ConstantsNative.TRANSACTION_TEXT} for ${ConstantsNative.BAJAJ_CASHBACK_TEXT} (${BajajDiscountTypeEnum.UpTo.name})",
                    "18 - Add ${ConstantsNative.SPECIAL_TEXT} ${ConstantsNative.TRANSACTION_TEXT} for ${ConstantsNative.BAJAJ_CASHBACK_TEXT} ${ConstantsNative.WITHOUT_FUNDING_TRANSACTION_TEXT} (${BajajDiscountTypeEnum.UpTo.name})",
                    "19 - Add ${ConstantsNative.SPECIAL_TEXT} ${ConstantsNative.TRANSACTION_TEXT} for ${ConstantsNative.BAJAJ_CASHBACK_TEXT} ${ConstantsNative.WITHOUT_BALANCE_CHECK_TEXT} (${BajajDiscountTypeEnum.UpTo.name})",
                    "20 - Add ${ConstantsNative.SPECIAL_TEXT} ${ConstantsNative.TRANSACTION_TEXT} (Advanced Configuration)",
                    "21 - ${getQuickTransactionOnWalletText()}",
                    "22 - ${
                        getQuickTransactionOnWalletToFrequent1Text(

                            dotEnv = dotEnv
                        )
                    }",
                    "23 - ${
                        getQuickTransactionOnWalletToFrequent2Text(

                            dotEnv = dotEnv
                        )
                    }",
                    "24 - ${
                        getQuickTransactionOnWalletToFrequent3Text(

                            dotEnv = dotEnv
                        )
                    }",
                    "25 - ${
                        getQuickTransactionOnBankText(

                            dotEnv = dotEnv
                        )
                    }",
                    "26 - ${
                        getQuickTransactionOnBankToFrequent1Text(

                            dotEnv = dotEnv
                        )
                    }",
                    "27 - ${
                        getQuickTransactionOnBankToFrequent2Text(

                            dotEnv = dotEnv
                        )
                    }",
                    "28 - ${
                        getQuickTransactionOnBankToFrequent3Text(

                            dotEnv = dotEnv
                        )
                    }",
                    "29 - ${
                        getQuickTransactionOnFrequent1Text(

                            dotEnv = dotEnv
                        )
                    }",
                    "30 - ${
                        getQuickTransactionOnFrequent2Text(

                            dotEnv = dotEnv
                        )
                    }",
                    "31 - ${
                        getQuickTransactionOnFrequent3Text(

                            dotEnv = dotEnv
                        )
                    }",
                    "0 - Back",
                    "",
                    "Enter Your Choice : "
                )
            )
            when (readln()) {

                "1" -> {
                    localInsertTransactionResult = TransactionViews.viewTransactionsForAnAccount(

                        userId = userId,
                        username = username,
                        accountId = fromAccount.id,
                        accountFullName = fromAccount.fullName,
                        previousTransactionData = localInsertTransactionResult,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv

                    ).addTransactionResult
                }

                "2" -> {
                    localInsertTransactionResult = TransactionViews.viewTransactionsForAnAccount(

                        userId = userId,
                        username = username,
                        accountId = fromAccount.id,
                        accountFullName = fromAccount.fullName,
                        previousTransactionData = localInsertTransactionResult,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        isCreditDebitMode = true,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv

                    ).addTransactionResult
                }

                "3" -> {
                    localInsertTransactionResult = InsertOperationsInteractive.addTransaction(

                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.NORMAL,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "4" -> {
                    localInsertTransactionResult = viewChildAccounts(

                        username = username,
                        userId = userId,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "5" -> {
                    localInsertTransactionResult = InsertOperationsInteractive.addTransaction(

                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.VIA,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "6" -> {
                    localInsertTransactionResult = InsertOperationsInteractive.addTransaction(

                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.TWO_WAY,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "7" -> {
                    localInsertTransactionResult = InsertOperationsInteractive.addTransaction(

                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.CYCLIC_VIA,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "8" -> {

                    localInsertTransactionResult = InsertOperationsInteractive.addTransaction(

                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.BAJAJ_COINS_FLAT,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "9" -> {

                    localInsertTransactionResult = InsertOperationsInteractive.addTransaction(

                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.BAJAJ_COINS_FLAT_WITHOUT_SOURCE,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "10" -> {

//                    Add ${ConstantsNative.SPECIAL_TEXT} ${ConstantsNative.TRANSACTION_TEXT} for ${ConstantsNative.BAJAJ_COINS_TEXT} ${ConstantsNative.WITHOUT_BALANCE_CHECK_TEXT}

                    localInsertTransactionResult = InsertOperationsInteractive.addTransaction(

                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.BAJAJ_COINS_FLAT_WITHOUT_BALANCE_CHECK,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "11" -> {

                    localInsertTransactionResult = InsertOperationsInteractive.addTransaction(

                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.BAJAJ_CASHBACK_FLAT,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "12" -> {

                    localInsertTransactionResult = InsertOperationsInteractive.addTransaction(

                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.BAJAJ_CASHBACK_FLAT_WITHOUT_SOURCE,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "13" -> {

//                    Add ${ConstantsNative.SPECIAL_TEXT} ${ConstantsNative.TRANSACTION_TEXT} for ${ConstantsNative.BAJAJ_SUB_WALLET_TEXT} ${ConstantsNative.WITHOUT_BALANCE_CHECK_TEXT}

                    localInsertTransactionResult = InsertOperationsInteractive.addTransaction(

                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.BAJAJ_CASHBACK_FLAT_WITHOUT_BALANCE_CHECK,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "14" -> {

                    localInsertTransactionResult = InsertOperationsInteractive.addTransaction(

                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.BAJAJ_COINS_UP_TO,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "15" -> {

                    localInsertTransactionResult = InsertOperationsInteractive.addTransaction(

                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.BAJAJ_COINS_UP_TO_WITHOUT_SOURCE,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "16" -> {

//                    Add ${ConstantsNative.SPECIAL_TEXT} ${ConstantsNative.TRANSACTION_TEXT} for ${ConstantsNative.BAJAJ_COINS_TEXT} ${ConstantsNative.WITHOUT_BALANCE_CHECK_TEXT}

                    localInsertTransactionResult = InsertOperationsInteractive.addTransaction(

                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.BAJAJ_COINS_UP_TO_WITHOUT_BALANCE_CHECK,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "17" -> {

                    localInsertTransactionResult = InsertOperationsInteractive.addTransaction(

                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.BAJAJ_CASHBACK_UP_TO,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "18" -> {

                    localInsertTransactionResult = InsertOperationsInteractive.addTransaction(

                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.BAJAJ_CASHBACK_UP_TO_WITHOUT_SOURCE,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "19" -> {

//                    Add ${ConstantsNative.SPECIAL_TEXT} ${ConstantsNative.TRANSACTION_TEXT} for ${ConstantsNative.BAJAJ_SUB_WALLET_TEXT} ${ConstantsNative.WITHOUT_BALANCE_CHECK_TEXT}

                    localInsertTransactionResult = InsertOperationsInteractive.addTransaction(

                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.BAJAJ_CASHBACK_UP_TO_WITHOUT_BALANCE_CHECK,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "20" -> {

                    val readSpecialTransactionTypesFileResult: IsOkModel<SpecialTransactionTypesModel> =
                        JsonFileUtilsInteractive.readJsonFile(

                            fileName = ConstantsNative.SPECIAL_TRANSACTION_TYPES_FILE_NAME,
                            isDevelopmentMode = isDevelopmentMode
                        )

                    if (isDevelopmentMode) {

                        println("readSpecialTransactionTypesFileResult : $readSpecialTransactionTypesFileResult")
                    }

                    if (readSpecialTransactionTypesFileResult.isOK && (readSpecialTransactionTypesFileResult.data!!.specialTransactionTypeModels.isNotEmpty())) {

                        val chooseSpecialTransactionTypeResult: ChooseSpecialTransactionTypeResultModel =
                            HandleSpecialTransactionTypesInteractive.chooseSpecialTransactionType(

                                specialTransactionTypes = (readSpecialTransactionTypesFileResult.data!!).specialTransactionTypeModels,
                                isDevelopmentMode = isDevelopmentMode

                            )
                        if (chooseSpecialTransactionTypeResult.isSpecialTransactionTypeSelected
                        ) {

                            val relatedAccountTransactions: MutableList<TransactionResponse> = mutableListOf()
                            var isUserTransactionForRelatedAccountFetchingSuccess = true

                            chooseSpecialTransactionTypeResult.selectedSpecialTransactionType!!.relatedAccounts.forEach { relatedAccountId: UInt ->

                                if (isUserTransactionForRelatedAccountFetchingSuccess) {

                                    ApiUtilsCommon.apiResponseHandler(

                                        apiResponse = ServerOperations.getUserTransactionsForAnAccount(

                                            userId = userId,
                                            accountId = relatedAccountId,
                                            isDevelopmentMode = isDevelopmentMode
                                        ),
                                        apiSuccessActions = fun(apiResponseData: MultipleTransactionResponse) {

                                            if (ApiUtilsInteractive.isTransactionResponseWithMessage(

                                                    multipleTransactionResponse = apiResponseData
                                                )
                                            ) {

                                                relatedAccountTransactions.addAll(apiResponseData.transactions)
                                            }
                                        },
                                        apiFailureActions = fun() {

                                            isUserTransactionForRelatedAccountFetchingSuccess = false
                                        }
                                    )
                                } else {

                                    return@forEach
                                }
                            }

                            if (isUserTransactionForRelatedAccountFetchingSuccess) {

                                val chooseTransactionResult: ChooseTransactionResultModel =
                                    HandleTransactionsInteractive.chooseTransaction(

                                        transactions = relatedAccountTransactions
                                            .filter { transaction: TransactionResponse ->

                                                chooseSpecialTransactionTypeResult.selectedSpecialTransactionType!!.particularsPatterns.any { particularsPattern: String ->

                                                    Regex(particularsPattern).containsMatchIn(transaction.particulars)
                                                }
                                            },
                                        isDevelopmentMode = isDevelopmentMode
                                    )

                                if (chooseTransactionResult.isTransactionSelected) {

                                    localInsertTransactionResult = InsertOperationsInteractive.addTransaction(

                                        userId = userId,
                                        username = username,
                                        transactionType = TransactionTypeEnum.SPECIAL,
                                        fromAccount = localInsertTransactionResult.fromAccount,
                                        viaAccount = localInsertTransactionResult.viaAccount,
                                        toAccount = localInsertTransactionResult.toAccount,
                                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                                        transactionAmount = localInsertTransactionResult.transactionAmount,
                                        isDevelopmentMode = isDevelopmentMode,
                                        chosenTransactionForSpecial = chooseTransactionResult.selectedTransaction!!,
                                        chosenSpecialTransactionType = chooseSpecialTransactionTypeResult.selectedSpecialTransactionType!!,
                                        dotEnv = dotEnv
                                    )
                                }
                            }
                        }
                    } else {

                        println("No Special Transaction Types...")
                    }
                }

                //TODO : Use common function
                "21" -> {

                    localInsertTransactionResult = quickTransactionOnWallet(

                        previousTransactionData = localInsertTransactionResult,
                        userId = userId,
                        username = username,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "22" -> {

                    localInsertTransactionResult = quickTransactionOnWalletToFrequent1(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "23" -> {

                    localInsertTransactionResult = quickTransactionOnWalletToFrequent2(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "24" -> {

                    localInsertTransactionResult = quickTransactionOnWalletToFrequent3(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "25" -> {

                    localInsertTransactionResult = quickTransactionOnBank(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "26" -> {

                    localInsertTransactionResult = quickTransactionOnBankToFrequent1(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "27" -> {

                    localInsertTransactionResult = quickTransactionOnBankToFrequent2(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "28" -> {

                    localInsertTransactionResult = quickTransactionOnBankToFrequent3(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "29" -> {

                    localInsertTransactionResult = quickTransactionOnFrequent1(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "30" -> {

                    localInsertTransactionResult = quickTransactionOnFrequent2(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "31" -> {

                    localInsertTransactionResult = quickTransactionOnFrequent3(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                }

                "0" -> {

                    return localInsertTransactionResult
                }

                else -> {
                    ErrorUtilsInteractive.printInvalidOptionMessage()
                }
            }
        } while (true)
    }

    @JvmStatic
    fun getUserWithCurrentAccountSelectionsAsText(

        username: String,
        fromAccount: AccountResponse,
        viaAccount: AccountResponse,
        toAccount: AccountResponse,
        transactionType: TransactionTypeEnum,
        userId: UInt,
        isDevelopmentMode: Boolean

    ): List<String> {

        var menuItems: List<String> = listOf(
            "\nUser : $username",
            "Transaction Type : ${
                EnumUtils.getEnumNameForPrint(localEnum = transactionType)
            }",
            "From Account - ${fromAccount.id} : ${fromAccount.fullName}"
        )
        if ((transactionType == TransactionTypeEnum.VIA) || (transactionType == TransactionTypeEnum.CYCLIC_VIA)) {

            menuItems = menuItems + listOf("Via. Account - ${viaAccount.id} : ${viaAccount.fullName}")
        }
        menuItems = menuItems + listOf(

            "To Account - ${toAccount.id} : ${toAccount.fullName}",
            AccountUtils.getFrequentlyUsedTop40Accounts(userId = userId, isDevelopmentMode)
        )
        return menuItems
    }
}
