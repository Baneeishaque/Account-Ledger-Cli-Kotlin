package accountLedgerCli.cli

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.api.response.MultipleTransactionResponse
import account.ledger.library.api.response.TransactionResponse
import account.ledger.library.enums.BalanceSheetRefineLevelEnum
import account.ledger.library.enums.EnvironmentFileEntryEnum
import account.ledger.library.enums.FunctionCallSourceEnum
import account.ledger.library.enums.TransactionTypeEnum
import account.ledger.library.models.*
import account.ledger.library.operations.DataOperations
import account.ledger.library.operations.ServerOperations
import account.ledger.library.retrofit.data.MultipleTransactionDataSource
import account.ledger.library.utils.AccountUtils
import account.ledger.library.utils.ApiUtils
import account.ledger.library.utils.HandleResponses
import account.ledger.library.utils.TransactionUtils
import account_ledger_library.constants.ConstantsNative
import common.utils.library.models.IsOkModel
import common.utils.library.utils.*
import io.github.cdimascio.dotenv.Dotenv
import kotlinx.coroutines.runBlocking

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
                    "3 - ${getQuickTransactionOnWalletToFrequent1Text()}",
                    "4 - ${getQuickTransactionOnWalletToFrequent2Text()}",
                    "5 - ${getQuickTransactionOnWalletToFrequent3Text()}",
                    "6 - ${getQuickTransactionOnBankText()}",
                    "7 - ${getQuickTransactionOnBankToFrequent1Text()}",
                    "8 - ${getQuickTransactionOnBankToFrequent2Text()}",
                    "9 - ${getQuickTransactionOnBankToFrequent3Text()}",
                    "10 - ${getQuickTransactionOnFrequent1Text()}",
                    "11 - ${getQuickTransactionOnFrequent2Text()}",
                    "12 - ${getQuickTransactionOnFrequent3Text()}",
                    "13 - List Accounts : Full Names",
                    "14 - Import Transactions To : Bank : ${
                        getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.BANK_ACCOUNT_NAME.name)
                    } From CSV",
                    "15 - Import Transactions To : Bank : ${
                        getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.BANK_ACCOUNT_NAME.name)
                    } From XLX",
                    "16 - Check Affected A/Cs : After A Specified Date",
                    "17 - View Transactions of a Specific A/C",
                    "18 - View Balance Sheet Ledger (All)",
                    "19 - View Balance Sheet Ledger (Excluding Open Balances)",
                    "20 - View Balance Sheet Ledger (Excluding Open Balances & Misc. Incomes)",
                    "21 - View Balance Sheet Ledger (Excluding Open Balances, Misc. Incomes & Investment Returns)",
                    "22 - View Balance Sheet Ledger (Excluding Open Balances, Misc. Incomes, Investment Returns & Family Accounts)",
                    "23 - View Balance Sheet Ledger (Excluding Open Balances, Misc. Incomes, Investment Returns, Family & Expense Accounts)",
                    "24 - View Transactions of Wallet A/C",
                    "25 - View Transactions of ${getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.BANK_ACCOUNT_NAME.name)} A/C",
                    "26 - View Transactions of ${getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_1_ACCOUNT_NAME.name)} A/C",
                    "27 - View Transactions of ${getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_2_ACCOUNT_NAME.name)} A/C",
                    "28 - View Transactions of ${getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_3_ACCOUNT_NAME.name)} A/C",
                    "29 - Check Affected A/Cs : From First Entry",
                    "30 - Check Affected A/Cs : From Start Date",
                    "31 - View Last 10 Transactions",
                    "32 - Check Affected A/Cs : From Start Date to A Specified Date",
                    "33 - Check Affected A/Cs : From Start Date to A Specified Time Stamp",
                    "34 - Check Affected A/Cs : After A Specified Date to A Specified Date",
                    "35 - Check Affected A/Cs : After A Specified Date to A Specified Time Stamp",
                    "36 - Check Affected A/Cs : After A Specified Time Stamp to A Specified Date",
                    "37 - Check Affected A/Cs : After A Specified Time Stamp to A Specified Time Stamp",
                    "38 - Check Affected A/Cs : After A Specified Date",
                    "39 - View Income Sheet",
                    "40 - View Expense Sheet",
                    "41 - View Profit Sheet",
                    "42 - View Not Consider for Income / Expense Sheet",
                    "0 - Logout",
                    "",
                    "Enter Your Choice : "
                )
            )
            when (readln()) {

                "1" -> {

                    insertTransactionResult = HandleResponsesInteractive.handleAccountsResponseAndPrintMenu(

                        apiResponse = ServerOperations.getAccounts(

                            userId = userId,
                            isConsoleMode = true,
                            isDevelopmentMode = isDevelopmentMode
                        ),
                        username = username,
                        userId = userId,
                        previousTransactionData = insertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "2" -> {

                    insertTransactionResult = quickTransactionOnWallet(

                        previousTransactionData = insertTransactionResult,
                        userId = userId,
                        username = username,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "3" -> {

                    insertTransactionResult = quickTransactionOnWalletToFrequent1(

                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "4" -> {

                    insertTransactionResult = quickTransactionOnWalletToFrequent2(

                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "5" -> {

                    insertTransactionResult = quickTransactionOnWalletToFrequent3(

                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "6" -> {

                    insertTransactionResult = quickTransactionOnBank(

                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "7" -> {

                    insertTransactionResult = quickTransactionOnBankToFrequent1(

                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "8" -> {

                    insertTransactionResult = quickTransactionOnBankToFrequent2(

                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "9" -> {

                    insertTransactionResult = quickTransactionOnBankToFrequent3(

                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "10" -> {

                    insertTransactionResult = quickTransactionOnFrequent1(

                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "11" -> {

                    insertTransactionResult = quickTransactionOnFrequent2(

                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "12" -> {

                    insertTransactionResult = quickTransactionOnFrequent3(

                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "13" -> {

                    insertTransactionResult = HandleResponsesInteractive.handleAccountsResponseAndPrintMenu(

                        apiResponse = ApiUtils.getAccountsFull(

                            userId = userId,
                            isConsoleMode = true,
                            isDevelopmentMode = isDevelopmentMode
                        ),
                        username = username,
                        userId = userId,
                        previousTransactionData = insertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "14", "15", "29", "32", "34", "35", "36", "37" -> {

                    ToDoUtils.showTodo()
                }

                "16" -> {

                    insertTransactionResult = checkAffectedAccountsAfterSpecifiedDate(

                        desiredDate = InputUtils.getValidDateInNormalPattern(),
                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "17" -> {

                    TransactionViews.viewTransactionsOfInputAccount(

                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "18" -> {

                    LedgerSheetOperations.printBalanceSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.ALL,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "19" -> {

                    LedgerSheetOperations.printBalanceSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_OPEN_BALANCES,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "20" -> {

                    LedgerSheetOperations.printBalanceSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_MISC_INCOMES,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "21" -> {

                    LedgerSheetOperations.printBalanceSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_INVESTMENT_RETURNS,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "22" -> {

                    LedgerSheetOperations.printBalanceSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_FAMILY_ACCOUNTS,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "23" -> {

                    LedgerSheetOperations.printBalanceSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_EXPENSE_ACCOUNTS,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "24" -> {

                    // TODO : Check for env. variable availability
                    insertTransactionResult = viewTransactionsOfAnAccountIndex(

                        userId = userId,
                        previousTransactionData = insertTransactionResult,
                        username = username,
                        desiredAccountIndex = InsertOperationsInteractive.walletAccount.value!!,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "25" -> {

                    insertTransactionResult = viewTransactionsOfAnAccountIndex(

                        userId = userId,
                        previousTransactionData = insertTransactionResult,
                        username = username,
                        desiredAccountIndex = InsertOperationsInteractive.bankAccount.value!!,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "26" -> {

                    insertTransactionResult = viewTransactionsOfAnAccountIndex(

                        userId = userId,
                        previousTransactionData = insertTransactionResult,
                        username = username,
                        desiredAccountIndex = InsertOperationsInteractive.frequent1Account.value!!,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "27" -> {

                    insertTransactionResult = viewTransactionsOfAnAccountIndex(

                        userId = userId,
                        previousTransactionData = insertTransactionResult,
                        username = username,
                        desiredAccountIndex = InsertOperationsInteractive.frequent2Account.value!!,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "28" -> {

                    insertTransactionResult = viewTransactionsOfAnAccountIndex(

                        userId = userId,
                        previousTransactionData = insertTransactionResult,
                        username = username,
                        desiredAccountIndex = InsertOperationsInteractive.frequent3Account.value!!,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "30" -> {

                    insertTransactionResult = checkAffectedAccountsAfterSpecifiedDate(

                        desiredDate = DataOperations.getUserInitialTransactionDateFromUsername(username = username)
                            .minusDays(1)
                            .format(DateTimeUtils.normalDatePattern),
                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "31" -> {

                    val getTransactionResult: IsOkModel<MultipleTransactionResponse> =
                        ApiUtilsCommon.makeApiRequestWithOptionalRetries(
                            apiCallFunction = fun(): Result<MultipleTransactionResponse> {

                                return runBlocking {

                                    MultipleTransactionDataSource().selectTransactions(userId = userId)
                                }
                            },
                            isConsoleMode = true,
                            isDevelopmentMode = isDevelopmentMode
                        )

                    HandleResponsesCommon.isOkModelHandler(

                        isOkModel = getTransactionResult,
                        data = Unit,
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
                                isDevelopmentMode = isDevelopmentMode

                            ).addTransactionResult
                        })
                }

                "33" -> {

                    insertTransactionResult = checkAffectedAccountsAfterSpecifiedDate(

                        desiredDate = DataOperations.getUserInitialTransactionDateFromUsername(username = username)
                            .minusDays(1)
                            .format(DateTimeUtils.normalDatePattern),
                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isUpToTimeStamp = true,
                        upToTimeStamp = InputUtils.getValidDateTimeInNormalPattern(promptPrefix = "Up to "),
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "38" -> {

                    insertTransactionResult = checkAffectedAccountsAfterSpecifiedDate(

                        desiredDate = InputUtils.getValidDateInNormalPattern(promptPrefix = "After "),
                        userId = userId,
                        username = username,
                        previousTransactionData = insertTransactionResult,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "39" -> {

                    LedgerSheetOperations.printIncomeSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "40" -> {

                    LedgerSheetOperations.printExpenseSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "41" -> {

                    LedgerSheetOperations.printProfitSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "42" -> {

                    LedgerSheetOperations.printNotConsiderForIncomeExpenseSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        isConsoleMode = true,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "0" -> {

                    return insertTransactionResult
                }

                else -> {

                    InteractiveUtils.invalidOptionMessage()
                }
            }
        } while (true)
    }

    fun getQuickTransactionOnBankToFrequent1Text() =
        getQuickTransactionOnBankToFrequentXText(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_1_ACCOUNT_NAME.name)

    fun getQuickTransactionOnBankToFrequent2Text() =
        getQuickTransactionOnBankToFrequentXText(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_2_ACCOUNT_NAME.name)

    fun getQuickTransactionOnBankToFrequent3Text() =
        getQuickTransactionOnBankToFrequentXText(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_3_ACCOUNT_NAME.name)

    fun getQuickTransactionOnWalletToFrequent1Text() =
        getQuickTransactionOnWalletToFrequentXText(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_1_ACCOUNT_NAME.name)

    fun getQuickTransactionOnWalletToFrequent2Text() =
        getQuickTransactionOnWalletToFrequentXText(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_2_ACCOUNT_NAME.name)

    fun getQuickTransactionOnWalletToFrequent3Text() =
        getQuickTransactionOnWalletToFrequentXText(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_3_ACCOUNT_NAME.name)

    private fun getQuickTransactionOnWalletToFrequentXText(environmentVariableName: String) =
        "${getQuickTransactionOnWalletText()} To : ${
            getEnvironmentVariableValueForUserScreen(environmentVariableName = environmentVariableName)
        }"

    private fun getQuickTransactionOnBankToFrequentXText(environmentVariableName: String) =
        "${getQuickTransactionOnBankText()} To : ${
            getEnvironmentVariableValueForUserScreen(environmentVariableName = environmentVariableName)
        }"

    fun getQuickTransactionOnBankText() = getQuickTransactionOnXText(
        itemSpecification = "Bank : ${
            getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.BANK_ACCOUNT_NAME.name)
        }"
    )

    fun getQuickTransactionOnFrequent1Text() = getQuickTransactionOnXText(
        itemSpecification = getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_1_ACCOUNT_NAME.name)
    )

    fun getQuickTransactionOnFrequent2Text() = getQuickTransactionOnXText(
        itemSpecification = getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_2_ACCOUNT_NAME.name)
    )

    fun getQuickTransactionOnFrequent3Text() = getQuickTransactionOnXText(
        itemSpecification = getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_3_ACCOUNT_NAME.name)
    )

    fun getQuickTransactionOnWalletText() = getQuickTransactionOnXText(itemSpecification = "Wallet")

    private fun getQuickTransactionOnXText(itemSpecification: String) =
        "Insert Quick Transaction On : $itemSpecification"

    fun quickTransactionOnFrequent3(

        userId: UInt,
        username: String,
        previousTransactionData: InsertTransactionResult,
        isDevelopmentMode: Boolean

    ): InsertTransactionResult = quickTransactionOnX(

        account = InsertOperationsInteractive.frequent3Account,
        previousTransactionData = previousTransactionData,
        userId = userId,
        username = username,
        isDevelopmentMode = isDevelopmentMode
    )


    fun quickTransactionOnFrequent2(

        userId: UInt,
        username: String,
        previousTransactionData: InsertTransactionResult,
        isDevelopmentMode: Boolean

    ): InsertTransactionResult = quickTransactionOnX(

        account = InsertOperationsInteractive.frequent2Account,
        previousTransactionData = previousTransactionData,
        userId = userId,
        username = username,
        isDevelopmentMode = isDevelopmentMode
    )


    fun quickTransactionOnFrequent1(

        userId: UInt,
        username: String,
        previousTransactionData: InsertTransactionResult,
        isDevelopmentMode: Boolean

    ): InsertTransactionResult = quickTransactionOnX(

        account = InsertOperationsInteractive.frequent1Account,
        previousTransactionData = previousTransactionData,
        userId = userId,
        username = username,
        isDevelopmentMode = isDevelopmentMode
    )

    fun quickTransactionOnBankToFrequent3(

        userId: UInt,
        username: String,
        previousTransactionData: InsertTransactionResult,
        chosenTransactionForSpecial: TransactionResponse? = null,
        chosenSpecialTransactionType: SpecialTransactionTypeModel? = null,
        isDevelopmentMode: Boolean

    ): InsertTransactionResult = quickTransactionOnBankToX(

        account2 = InsertOperationsInteractive.frequent3Account,
        userId = userId,
        username = username,
        previousTransactionData = previousTransactionData,
        chosenTransactionForSpecial = chosenTransactionForSpecial,
        chosenSpecialTransactionType = chosenSpecialTransactionType,
        isDevelopmentMode = isDevelopmentMode
    )

    fun quickTransactionOnBankToFrequent2(

        userId: UInt,
        username: String,
        previousTransactionData: InsertTransactionResult,
        chosenTransactionForSpecial: TransactionResponse? = null,
        chosenSpecialTransactionType: SpecialTransactionTypeModel? = null,
        isDevelopmentMode: Boolean

    ): InsertTransactionResult = quickTransactionOnBankToX(

        account2 = InsertOperationsInteractive.frequent2Account,
        userId = userId,
        username = username,
        previousTransactionData = previousTransactionData,
        chosenTransactionForSpecial = chosenTransactionForSpecial,
        chosenSpecialTransactionType = chosenSpecialTransactionType,
        isDevelopmentMode = isDevelopmentMode
    )

    fun quickTransactionOnBankToFrequent1(

        userId: UInt,
        username: String,
        previousTransactionData: InsertTransactionResult,
        chosenTransactionForSpecial: TransactionResponse? = null,
        chosenSpecialTransactionType: SpecialTransactionTypeModel? = null,
        isDevelopmentMode: Boolean

    ): InsertTransactionResult = quickTransactionOnBankToX(

        account2 = InsertOperationsInteractive.frequent1Account,
        userId = userId,
        username = username,
        previousTransactionData = previousTransactionData,
        chosenTransactionForSpecial = chosenTransactionForSpecial,
        chosenSpecialTransactionType = chosenSpecialTransactionType,
        isDevelopmentMode = isDevelopmentMode
    )

    fun quickTransactionOnBank(

        userId: UInt,
        username: String,
        previousTransactionData: InsertTransactionResult,
        isDevelopmentMode: Boolean

    ): InsertTransactionResult = quickTransactionOnX(

        account = InsertOperationsInteractive.bankAccount,
        previousTransactionData = previousTransactionData,
        userId = userId,
        username = username,
        isDevelopmentMode = isDevelopmentMode
    )

    fun quickTransactionOnWalletToFrequent3(

        userId: UInt,
        username: String,
        previousTransactionData: InsertTransactionResult,
        chosenTransactionForSpecial: TransactionResponse? = null,
        chosenSpecialTransactionType: SpecialTransactionTypeModel? = null,
        isDevelopmentMode: Boolean

    ): InsertTransactionResult = quickTransactionOnWalletToX(

        account2 = InsertOperationsInteractive.frequent3Account,
        userId = userId,
        username = username,
        previousTransactionData = previousTransactionData,
        chosenTransactionForSpecial = chosenTransactionForSpecial,
        chosenSpecialTransactionType = chosenSpecialTransactionType,
        isDevelopmentMode = isDevelopmentMode
    )

    fun quickTransactionOnWalletToFrequent2(

        userId: UInt,
        username: String,
        previousTransactionData: InsertTransactionResult,
        chosenTransactionForSpecial: TransactionResponse? = null,
        chosenSpecialTransactionType: SpecialTransactionTypeModel? = null,
        isDevelopmentMode: Boolean

    ): InsertTransactionResult = quickTransactionOnWalletToX(

        account2 = InsertOperationsInteractive.frequent2Account,
        userId = userId,
        username = username,
        previousTransactionData = previousTransactionData,
        chosenTransactionForSpecial = chosenTransactionForSpecial,
        chosenSpecialTransactionType = chosenSpecialTransactionType,
        isDevelopmentMode = isDevelopmentMode
    )

    fun quickTransactionOnWalletToFrequent1(

        userId: UInt,
        username: String,
        previousTransactionData: InsertTransactionResult,
        chosenTransactionForSpecial: TransactionResponse? = null,
        chosenSpecialTransactionType: SpecialTransactionTypeModel? = null,
        isDevelopmentMode: Boolean

    ): InsertTransactionResult = quickTransactionOnWalletToX(

        account2 = InsertOperationsInteractive.frequent1Account,
        userId = userId,
        username = username,
        previousTransactionData = previousTransactionData,
        chosenTransactionForSpecial = chosenTransactionForSpecial,
        chosenSpecialTransactionType = chosenSpecialTransactionType,
        isDevelopmentMode = isDevelopmentMode
    )

    private fun quickTransactionOnWalletToX(

        account2: EnvironmentVariableForWholeNumber,
        userId: UInt,
        username: String,
        previousTransactionData: InsertTransactionResult,
        chosenTransactionForSpecial: TransactionResponse?,
        chosenSpecialTransactionType: SpecialTransactionTypeModel?,
        isDevelopmentMode: Boolean

    ): InsertTransactionResult = quickTransactionOnXToY(

        account1 = InsertOperationsInteractive.walletAccount,
        account2 = account2,
        userId = userId,
        username = username,
        previousTransactionData = previousTransactionData,
        chosenTransactionForSpecial = chosenTransactionForSpecial,
        chosenSpecialTransactionType = chosenSpecialTransactionType,
        isDevelopmentMode = isDevelopmentMode
    )

    private fun quickTransactionOnBankToX(

        account2: EnvironmentVariableForWholeNumber,
        userId: UInt,
        username: String,
        previousTransactionData: InsertTransactionResult,
        chosenTransactionForSpecial: TransactionResponse?,
        chosenSpecialTransactionType: SpecialTransactionTypeModel?,
        isDevelopmentMode: Boolean

    ): InsertTransactionResult = quickTransactionOnXToY(

        account1 = InsertOperationsInteractive.bankAccount,
        account2 = account2,
        userId = userId,
        username = username,
        previousTransactionData = previousTransactionData,
        chosenTransactionForSpecial = chosenTransactionForSpecial,
        chosenSpecialTransactionType = chosenSpecialTransactionType,
        isDevelopmentMode = isDevelopmentMode
    )

    private fun quickTransactionOnXToY(

        account1: EnvironmentVariableForWholeNumber,
        account2: EnvironmentVariableForWholeNumber,
        userId: UInt,
        username: String,
        previousTransactionData: InsertTransactionResult,
        chosenTransactionForSpecial: TransactionResponse?,
        chosenSpecialTransactionType: SpecialTransactionTypeModel?,
        isDevelopmentMode: Boolean

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
        isDevelopmentMode = isDevelopmentMode
    )

    fun quickTransactionOnWallet(

        previousTransactionData: InsertTransactionResult,
        userId: UInt,
        username: String,
        isDevelopmentMode: Boolean

    ): InsertTransactionResult = quickTransactionOnX(

        account = InsertOperationsInteractive.walletAccount,
        previousTransactionData = previousTransactionData,
        userId = userId,
        username = username,
        isDevelopmentMode = isDevelopmentMode
    )

    private fun quickTransactionOnX(

        account: EnvironmentVariableForWholeNumber,
        previousTransactionData: InsertTransactionResult,
        userId: UInt,
        username: String,
        isDevelopmentMode: Boolean

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
            isDevelopmentMode = isDevelopmentMode
        )
    }

    private fun viewTransactionsOfAnAccountIndex(

        userId: UInt,
        previousTransactionData: InsertTransactionResult,
        username: String,
        desiredAccountIndex: UInt,
        isDevelopmentMode: Boolean

    ): InsertTransactionResult {

        var localInsertTransactionResult: InsertTransactionResult = previousTransactionData

        val getUserAccountsMapResult: IsOkModel<LinkedHashMap<UInt, AccountResponse>> =
            HandleResponses.getUserAccountsMap(
                apiResponse = ApiUtils.getAccountsFull(

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
                isDevelopmentMode = isDevelopmentMode

            ).addTransactionResult
        }
        return localInsertTransactionResult
    }

    private fun getEnvironmentVariableValueForUserScreen(environmentVariableName: String) =
        EnvironmentFileOperations.getEnvironmentVariableValueForTextWithDefaultValue(

            dotenv = App.dotEnv,
            environmentVariableName = environmentVariableName,
            defaultValue = ConstantsNative.defaultValueForStringEnvironmentVariables
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
        isDevelopmentMode: Boolean

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
                    "\nUser : $username",
                    "Account - ${fromAccount.fullName}",
                    "1 - View Transactions in Ledger Mode",
                    "2 - View Transactions in Credit - Debit Mode",
                    "3 - Add Transaction",
                    "4 - View Child Accounts",
                    "5 - Add Via. Transaction",
                    "6 - Add Two Way Transaction",
                    "7 - Add Cyclic Via. Transaction",
                    "8 - Add Special Transaction",
                    "17 - ${getQuickTransactionOnWalletText()}",
                    "18 - ${getQuickTransactionOnWalletToFrequent1Text()}",
                    "19 - ${getQuickTransactionOnWalletToFrequent2Text()}",
                    "20 - ${getQuickTransactionOnWalletToFrequent3Text()}",
                    "21 - ${getQuickTransactionOnBankText()}",
                    "22 - ${getQuickTransactionOnBankToFrequent1Text()}",
                    "23 - ${getQuickTransactionOnBankToFrequent2Text()}",
                    "24 - ${getQuickTransactionOnBankToFrequent3Text()}",
                    "25 - ${getQuickTransactionOnFrequent1Text()}",
                    "26 - ${getQuickTransactionOnFrequent2Text()}",
                    "27 - ${getQuickTransactionOnFrequent3Text()}",
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
                        isDevelopmentMode = isDevelopmentMode

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
                        isDevelopmentMode = isDevelopmentMode

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
                        isDevelopmentMode = isDevelopmentMode
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
                        isDevelopmentMode = isDevelopmentMode
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
                        isDevelopmentMode = isDevelopmentMode
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
                        isDevelopmentMode = isDevelopmentMode
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
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "8" -> {

                    val readSpecialTransactionTypesFileResult: IsOkModel<SpecialTransactionTypesModel> =
                        JsonFileUtils.readJsonFile(

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

                                            if (ApiUtils.isNotNoTransactionResponseWithMessage(

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
                                        chosenSpecialTransactionType = chooseSpecialTransactionTypeResult.selectedSpecialTransactionType!!
                                    )
                                }
                            }
                        }
                    } else {

                        println("No Special Transaction Types...")
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

                    localInsertTransactionResult = quickTransactionOnWalletToFrequent1(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "19" -> {

                    localInsertTransactionResult = quickTransactionOnWalletToFrequent2(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "20" -> {

                    localInsertTransactionResult = quickTransactionOnWalletToFrequent3(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "21" -> {

                    localInsertTransactionResult = quickTransactionOnBank(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "22" -> {

                    localInsertTransactionResult = quickTransactionOnBankToFrequent1(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "23" -> {

                    localInsertTransactionResult = quickTransactionOnBankToFrequent2(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "24" -> {

                    localInsertTransactionResult = quickTransactionOnBankToFrequent3(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "25" -> {

                    localInsertTransactionResult = quickTransactionOnFrequent1(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "26" -> {

                    localInsertTransactionResult = quickTransactionOnFrequent2(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "27" -> {

                    localInsertTransactionResult = quickTransactionOnFrequent3(

                        userId = userId,
                        username = username,
                        previousTransactionData = localInsertTransactionResult,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "0" -> {

                    return localInsertTransactionResult
                }

                else -> {
                    InteractiveUtils.invalidOptionMessage()
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
