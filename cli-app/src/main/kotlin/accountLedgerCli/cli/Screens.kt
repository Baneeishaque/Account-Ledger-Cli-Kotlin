package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.TransactionsResponse
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.constants.Constants
import accountLedgerCli.enums.BalanceSheetRefineLevelEnum
import accountLedgerCli.enums.EnvironmentFileEntryEnum
import accountLedgerCli.enums.FunctionCallSourceEnum
import accountLedgerCli.enums.TransactionTypeEnum
import accountLedgerCli.models.AccountFrequencyModel
import accountLedgerCli.models.FrequencyOfAccountsModel
import accountLedgerCli.models.InsertTransactionResult
import accountLedgerCli.models.UserModel
import accountLedgerCli.retrofit.data.TransactionsDataSource
import accountLedgerCli.to_models.IsOkModel
import accountLedgerCli.to_utils.*
import accountLedgerCli.utils.AccountUtils
import accountLedgerCli.utils.ApiUtils
import kotlinx.coroutines.runBlocking
import accountLedgerCli.to_utils.ApiUtils as CommonApiUtils
import accountLedgerCli.to_utils.HandleResponses as CommonHandleResponses

object Screens {

    internal fun userScreen(

        username: String,
        userId: UInt,
        fromAccount: AccountResponse,
        viaAccount: AccountResponse,
        toAccount: AccountResponse,
        dateTimeInText: String,
        transactionParticulars: String,
        transactionAmount: Float

    ): InsertTransactionResult {

        if (App.isDevelopmentMode) {

            println("Env. Variables : ${App.dotenv.entries()}")
        }
        var insertTransactionResult = InsertTransactionResult(

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
                listOf(
                    "\nUser : $username",
                    AccountUtils.getFrequentlyUsedTop10Accounts(userId = userId),
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
                    "0 - Logout",
                    "",
                    "Enter Your Choice : "
                )
            )
            when (readLine()!!) {

                "1" -> {

                    insertTransactionResult = HandleResponses.handleAccountsResponseAndPrintMenu(

                        apiResponse = getAccounts(userId = userId),
                        username = username,
                        userId = userId,
                        insertTransactionResult = insertTransactionResult
                    )
                }

                "2" -> {

                    insertTransactionResult = quickTransactionOnWallet(

                        insertTransactionResult = insertTransactionResult,
                        userId = userId,
                        username = username
                    )
                }

                "3" -> {

                    insertTransactionResult = quickTransactionOnWalletToFrequent1(

                        userId = userId,
                        username = username,
                        insertTransactionResult = insertTransactionResult
                    )
                }

                "4" -> {

                    insertTransactionResult = quickTransactionOnWalletToFrequent2(

                        userId = userId,
                        username = username,
                        insertTransactionResult = insertTransactionResult
                    )
                }

                "5" -> {

                    insertTransactionResult = quickTransactionOnWalletToFrequent3(

                        userId = userId,
                        username = username,
                        insertTransactionResult = insertTransactionResult
                    )
                }

                "6" -> {

                    insertTransactionResult = quickTransactionOnBank(

                        userId = userId,
                        username = username,
                        insertTransactionResult = insertTransactionResult
                    )
                }

                "7" -> {

                    insertTransactionResult = quickTransactionOnBankToFrequent1(

                        userId = userId,
                        username = username,
                        insertTransactionResult = insertTransactionResult
                    )
                }

                "8" -> {

                    insertTransactionResult = quickTransactionOnBankToFrequent2(

                        userId = userId,
                        username = username,
                        insertTransactionResult = insertTransactionResult
                    )
                }

                "9" -> {

                    insertTransactionResult = quickTransactionOnBankToFrequent3(

                        userId = userId,
                        username = username,
                        insertTransactionResult = insertTransactionResult
                    )
                }

                "10" -> {

                    insertTransactionResult = quickTransactionOnFrequent1(

                        userId = userId,
                        username = username,
                        insertTransactionResult = insertTransactionResult
                    )
                }

                "11" -> {

                    insertTransactionResult = quickTransactionOnFrequent2(

                        userId = userId,
                        username = username,
                        insertTransactionResult = insertTransactionResult
                    )
                }

                "12" -> {

                    insertTransactionResult = quickTransactionOnFrequent3(

                        userId = userId,
                        username = username,
                        insertTransactionResult = insertTransactionResult
                    )
                }

                "13" -> {

                    insertTransactionResult = HandleResponses.handleAccountsResponseAndPrintMenu(

                        apiResponse = ApiUtils.getAccountsFull(userId = userId),
                        username = username,
                        userId = userId,
                        insertTransactionResult = insertTransactionResult
                    )
                }

                "14", "15", "29" -> {

                    ToDoUtils.showTodo()
                }

                "16" -> {

                    insertTransactionResult = checkAffectedAccountsAfterSpecifiedDate(

                        desiredDate = InputUtils.getValidDateInNormalPattern(),
                        userId = userId,
                        username = username,
                        insertTransactionResult = insertTransactionResult
                    )
                }

                "17" -> {

                    TransactionViews.viewTransactionsOfInputAccount(

                        userId = userId,
                        username = username,
                        insertTransactionResult = insertTransactionResult
                    )
                }

                "18" -> {

                    printBalanceSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.ALL
                    )
                }

                "19" -> {

                    printBalanceSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_OPEN_BALANCES
                    )
                }

                "20" -> {

                    printBalanceSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_MISC_INCOMES
                    )
                }

                "21" -> {

                    printBalanceSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_INVESTMENT_RETURNS
                    )
                }

                "22" -> {

                    printBalanceSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_FAMILY_ACCOUNTS
                    )
                }

                "23" -> {

                    printBalanceSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_EXPENSE_ACCOUNTS
                    )
                }

                "24" -> {

                    insertTransactionResult = viewTransactionsOfAnAccountIndex(

                        userId = userId,
                        insertTransactionResult = insertTransactionResult,
                        username = username,
                        desiredAccountIndex = InsertOperations.walletAccount.value!!
                    )
                }

                "25" -> {

                    insertTransactionResult = viewTransactionsOfAnAccountIndex(

                        userId = userId,
                        insertTransactionResult = insertTransactionResult,
                        username = username,
                        desiredAccountIndex = InsertOperations.bankAccount.value!!
                    )
                }

                "26" -> {

                    insertTransactionResult = viewTransactionsOfAnAccountIndex(

                        userId = userId,
                        insertTransactionResult = insertTransactionResult,
                        username = username,
                        desiredAccountIndex = InsertOperations.frequent1Account.value!!
                    )
                }

                "27" -> {

                    insertTransactionResult = viewTransactionsOfAnAccountIndex(

                        userId = userId,
                        insertTransactionResult = insertTransactionResult,
                        username = username,
                        desiredAccountIndex = InsertOperations.frequent2Account.value!!
                    )
                }

                "28" -> {

                    insertTransactionResult = viewTransactionsOfAnAccountIndex(

                        userId = userId,
                        insertTransactionResult = insertTransactionResult,
                        username = username,
                        desiredAccountIndex = InsertOperations.frequent3Account.value!!
                    )
                }

                "30" -> {

                    insertTransactionResult = checkAffectedAccountsAfterSpecifiedDate(

                        desiredDate = getUserInitialTransactionDateFromUsername(username = username).minusDays(1)
                            .format(DateTimeUtils.normalDatePattern),
                        userId = userId,
                        username = username,
                        insertTransactionResult = insertTransactionResult
                    )
                }

                "31" -> {

                    val getTransactionsResult: IsOkModel<TransactionsResponse> =
                        CommonApiUtils.makeApiRequestWithOptionalRetries(apiCallFunction = fun(): Result<TransactionsResponse> {

                            return runBlocking {

                                TransactionsDataSource().selectTransactions(userId = userId)
                            }
                        })

                    CommonHandleResponses.isOkModelHandler(

                        isOkModel = getTransactionsResult,
                        data = Unit,
                        successActions = fun() {

                            insertTransactionResult = TransactionViews.viewTransactions(

                                userTransactionsResponse = getTransactionsResult.data!!,
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
                                userId = userId

                            ).addTransactionResult
                        })
                }

                "0" -> {

                    return insertTransactionResult
                }

                else -> {

                    invalidOptionMessage()
                }
            }
        } while (true)
    }

    internal fun getQuickTransactionOnBankToFrequent1Text() =
        getQuickTransactionOnBankToFrequentXText(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_1_ACCOUNT_NAME.name)

    internal fun getQuickTransactionOnBankToFrequent2Text() =
        getQuickTransactionOnBankToFrequentXText(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_2_ACCOUNT_NAME.name)

    internal fun getQuickTransactionOnBankToFrequent3Text() =
        getQuickTransactionOnBankToFrequentXText(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_3_ACCOUNT_NAME.name)

    internal fun getQuickTransactionOnWalletToFrequent1Text() =
        getQuickTransactionOnWalletToFrequentXText(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_1_ACCOUNT_NAME.name)

    internal fun getQuickTransactionOnWalletToFrequent2Text() =
        getQuickTransactionOnWalletToFrequentXText(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_2_ACCOUNT_NAME.name)

    internal fun getQuickTransactionOnWalletToFrequent3Text() =
        getQuickTransactionOnWalletToFrequentXText(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_3_ACCOUNT_NAME.name)

    private fun getQuickTransactionOnWalletToFrequentXText(environmentVariableName: String) =
        "${getQuickTransactionOnWalletText()} To : ${
            getEnvironmentVariableValueForUserScreen(environmentVariableName = environmentVariableName)
        }"

    private fun getQuickTransactionOnBankToFrequentXText(environmentVariableName: String) =
        "${getQuickTransactionOnBankText()} To : ${
            getEnvironmentVariableValueForUserScreen(environmentVariableName = environmentVariableName)
        }"

    internal fun getQuickTransactionOnBankText() = getQuickTransactionOnXText(
        itemSpecification = "Bank : ${
            getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.BANK_ACCOUNT_NAME.name)
        }"
    )

    internal fun getQuickTransactionOnFrequent1Text() = getQuickTransactionOnXText(
        itemSpecification = getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_1_ACCOUNT_NAME.name)
    )

    internal fun getQuickTransactionOnFrequent2Text() = getQuickTransactionOnXText(
        itemSpecification = getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_2_ACCOUNT_NAME.name)
    )

    internal fun getQuickTransactionOnFrequent3Text() = getQuickTransactionOnXText(
        itemSpecification = getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_3_ACCOUNT_NAME.name)
    )

    internal fun getQuickTransactionOnWalletText() = getQuickTransactionOnXText(itemSpecification = "Wallet")

    private fun getQuickTransactionOnXText(itemSpecification: String) =
        "Insert Quick Transaction On : $itemSpecification"

    internal fun quickTransactionOnFrequent3(

        userId: UInt,
        username: String,
        insertTransactionResult: InsertTransactionResult

    ) = quickTransactionOnX(

        account = InsertOperations.frequent3Account,
        insertTransactionResult = insertTransactionResult,
        userId = userId,
        username = username
    )


    internal fun quickTransactionOnFrequent2(

        userId: UInt,
        username: String,
        insertTransactionResult: InsertTransactionResult

    ) = quickTransactionOnX(

        account = InsertOperations.frequent2Account,
        insertTransactionResult = insertTransactionResult,
        userId = userId,
        username = username
    )


    internal fun quickTransactionOnFrequent1(

        userId: UInt,
        username: String,
        insertTransactionResult: InsertTransactionResult

    ) = quickTransactionOnX(

        account = InsertOperations.frequent1Account,
        insertTransactionResult = insertTransactionResult,
        userId = userId,
        username = username
    )

    internal fun quickTransactionOnBankToFrequent3(

        userId: UInt,
        username: String,
        insertTransactionResult: InsertTransactionResult

    ) = quickTransactionOnBankToX(

        account2 = InsertOperations.frequent3Account,
        userId = userId,
        username = username,
        insertTransactionResult = insertTransactionResult
    )

    internal fun quickTransactionOnBankToFrequent2(

        userId: UInt,
        username: String,
        insertTransactionResult: InsertTransactionResult

    ) = quickTransactionOnBankToX(

        account2 = InsertOperations.frequent2Account,
        userId = userId,
        username = username,
        insertTransactionResult = insertTransactionResult
    )

    internal fun quickTransactionOnBankToFrequent1(

        userId: UInt,
        username: String,
        insertTransactionResult: InsertTransactionResult

    ) = quickTransactionOnBankToX(

        account2 = InsertOperations.frequent1Account,
        userId = userId,
        username = username,
        insertTransactionResult = insertTransactionResult
    )

    internal fun quickTransactionOnBank(

        userId: UInt,
        username: String,
        insertTransactionResult: InsertTransactionResult

    ) = quickTransactionOnX(

        account = InsertOperations.bankAccount,
        insertTransactionResult = insertTransactionResult,
        userId = userId,
        username = username
    )

    internal fun quickTransactionOnWalletToFrequent3(

        userId: UInt,
        username: String,
        insertTransactionResult: InsertTransactionResult

    ) = quickTransactionOnWalletToX(

        account2 = InsertOperations.frequent3Account,
        userId = userId,
        username = username,
        insertTransactionResult = insertTransactionResult
    )

    internal fun quickTransactionOnWalletToFrequent2(

        userId: UInt,
        username: String,
        insertTransactionResult: InsertTransactionResult

    ) = quickTransactionOnWalletToX(

        account2 = InsertOperations.frequent2Account,
        userId = userId,
        username = username,
        insertTransactionResult = insertTransactionResult
    )

    internal fun quickTransactionOnWalletToFrequent1(

        userId: UInt,
        username: String,
        insertTransactionResult: InsertTransactionResult

    ) = quickTransactionOnWalletToX(

        account2 = InsertOperations.frequent1Account,
        userId = userId,
        username = username,
        insertTransactionResult = insertTransactionResult
    )

    private fun quickTransactionOnWalletToX(

        account2: EnvironmentVariableForWholeNumber,
        userId: UInt,
        username: String,
        insertTransactionResult: InsertTransactionResult

    ) = quickTransactionOnXToY(

        account1 = InsertOperations.walletAccount,
        account2 = account2,
        userId = userId,
        username = username,
        insertTransactionResult = insertTransactionResult
    )

    private fun quickTransactionOnBankToX(

        account2: EnvironmentVariableForWholeNumber,
        userId: UInt,
        username: String,
        insertTransactionResult: InsertTransactionResult

    ) = quickTransactionOnXToY(

        account1 = InsertOperations.bankAccount,
        account2 = account2,
        userId = userId,
        username = username,
        insertTransactionResult = insertTransactionResult
    )

    private fun quickTransactionOnXToY(

        account1: EnvironmentVariableForWholeNumber,
        account2: EnvironmentVariableForWholeNumber,
        userId: UInt,
        username: String,
        insertTransactionResult: InsertTransactionResult

    ) = InsertOperations.insertQuickTransactionFromAccount1toAccount2(

        account1 = account1,
        account2 = account2,
        userId = userId,
        username = username,
        fromAccount = insertTransactionResult.fromAccount,
        viaAccount = insertTransactionResult.viaAccount,
        toAccount = insertTransactionResult.toAccount,
        dateTimeInText = insertTransactionResult.dateTimeInText,
        transactionParticulars = insertTransactionResult.transactionParticulars,
        transactionAmount = insertTransactionResult.transactionAmount
    )

    internal fun quickTransactionOnWallet(

        insertTransactionResult: InsertTransactionResult,
        userId: UInt,
        username: String

    ) = quickTransactionOnX(

        account = InsertOperations.walletAccount,
        insertTransactionResult = insertTransactionResult,
        userId = userId,
        username = username
    )

    private fun quickTransactionOnX(

        account: EnvironmentVariableForWholeNumber,
        insertTransactionResult: InsertTransactionResult,
        userId: UInt,
        username: String

    ): InsertTransactionResult {

        return InsertOperations.openSpecifiedAccountHome(

            account = account,
            userId = userId,
            username = username,
            fromAccount = insertTransactionResult.fromAccount,
            viaAccount = insertTransactionResult.viaAccount,
            toAccount = insertTransactionResult.toAccount,
            dateTimeInText = insertTransactionResult.dateTimeInText,
            transactionParticulars = insertTransactionResult.transactionParticulars,
            transactionAmount = insertTransactionResult.transactionAmount
        )
    }

    private fun viewTransactionsOfAnAccountIndex(

        userId: UInt,
        insertTransactionResult: InsertTransactionResult,
        username: String,
        desiredAccountIndex: UInt

    ): InsertTransactionResult {

        var localInsertTransactionResult: InsertTransactionResult = insertTransactionResult

        val getUserAccountsMapResult: IsOkModel<LinkedHashMap<UInt, AccountResponse>> =
            HandleResponses.getUserAccountsMap(apiResponse = ApiUtils.getAccountsFull(userId = userId))

        if (getUserAccountsMapResult.isOK && getUserAccountsMapResult.data!!.containsKey(desiredAccountIndex)) {

            val selectedAccount: AccountResponse = getUserAccountsMapResult.data[desiredAccountIndex]!!
            localInsertTransactionResult = TransactionViews.viewTransactionsForAnAccount(

                userId = userId,
                username = username,
                accountId = desiredAccountIndex,
                accountFullName = selectedAccount.fullName,
                insertTransactionResult = insertTransactionResult,
                fromAccount = selectedAccount

            ).addTransactionResult
        }
        return localInsertTransactionResult
    }


    internal fun getAccountFrequenciesForUser(

        frequencyOfAccounts: FrequencyOfAccountsModel,
        userId: UInt

    ): List<AccountFrequencyModel>? {

        return frequencyOfAccounts.users.find { user: UserModel -> user.id == userId }?.accountFrequencies
    }

    private fun getEnvironmentVariableValueForUserScreen(environmentVariableName: String) =
        EnvironmentFileOperations.getEnvironmentVariableValueForTextWithDefaultValue(

            dotenv = App.dotenv,
            environmentVariableName = environmentVariableName,
            defaultValue = Constants.defaultValueForStringEnvironmentVariables
        )

    internal fun accountHome(

        userId: UInt,
        username: String,
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

                listOfCommands = listOf(
                    "\nUser : $username",
                    "Account - ${fromAccount.fullName}",
                    "1 - View Transactions",
                    "2 - Add Transaction",
                    "3 - View Child Accounts",
                    "4 - Add Via. Transaction",
                    "5 - Add Two Way Transaction",
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
            when (readLine()!!) {

                "1" -> {
                    localInsertTransactionResult = TransactionViews.viewTransactionsForAnAccount(

                        userId = userId,
                        username = username,
                        accountId = fromAccount.id,
                        accountFullName = fromAccount.fullName,
                        insertTransactionResult = localInsertTransactionResult,
                        fromAccount = localInsertTransactionResult.fromAccount

                    ).addTransactionResult
                }

                "2" -> {
                    localInsertTransactionResult = InsertOperations.addTransaction(

                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.NORMAL,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount
                    )
                }

                "3" -> {
                    localInsertTransactionResult = viewChildAccounts(
                        username = username,
                        userId = userId,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount
                    )
                }

                "4" -> {
                    localInsertTransactionResult = InsertOperations.addTransaction(
                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.VIA,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount
                    )
                }

                "5" -> {
                    localInsertTransactionResult = InsertOperations.addTransaction(

                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.TWO_WAY,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount
                    )
                }

                "17" -> {

                    localInsertTransactionResult = quickTransactionOnWallet(

                        insertTransactionResult = localInsertTransactionResult,
                        userId = userId,
                        username = username
                    )
                }

                "18" -> {

                    localInsertTransactionResult = quickTransactionOnWalletToFrequent1(

                        userId = userId,
                        username = username,
                        insertTransactionResult = localInsertTransactionResult
                    )
                }

                "19" -> {

                    localInsertTransactionResult = quickTransactionOnWalletToFrequent2(

                        userId = userId,
                        username = username,
                        insertTransactionResult = localInsertTransactionResult
                    )
                }

                "20" -> {

                    localInsertTransactionResult = quickTransactionOnWalletToFrequent3(

                        userId = userId,
                        username = username,
                        insertTransactionResult = localInsertTransactionResult
                    )
                }

                "21" -> {

                    localInsertTransactionResult = quickTransactionOnBank(

                        userId = userId,
                        username = username,
                        insertTransactionResult = localInsertTransactionResult
                    )
                }

                "22" -> {

                    localInsertTransactionResult = quickTransactionOnBankToFrequent1(

                        userId = userId,
                        username = username,
                        insertTransactionResult = localInsertTransactionResult
                    )
                }

                "23" -> {

                    localInsertTransactionResult = quickTransactionOnBankToFrequent2(

                        userId = userId,
                        username = username,
                        insertTransactionResult = localInsertTransactionResult
                    )
                }

                "24" -> {

                    localInsertTransactionResult = quickTransactionOnBankToFrequent3(

                        userId = userId,
                        username = username,
                        insertTransactionResult = localInsertTransactionResult
                    )
                }

                "25" -> {

                    localInsertTransactionResult = quickTransactionOnFrequent1(

                        userId = userId,
                        username = username,
                        insertTransactionResult = localInsertTransactionResult
                    )
                }

                "26" -> {

                    localInsertTransactionResult = quickTransactionOnFrequent2(

                        userId = userId,
                        username = username,
                        insertTransactionResult = localInsertTransactionResult
                    )
                }

                "27" -> {

                    localInsertTransactionResult = quickTransactionOnFrequent3(

                        userId = userId,
                        username = username,
                        insertTransactionResult = localInsertTransactionResult
                    )
                }

                "0" -> {

                    return localInsertTransactionResult
                }

                else -> {
                    invalidOptionMessage()
                }
            }
        } while (true)
    }

    internal fun getUserWithCurrentAccountSelectionsAsText(

        username: String,
        fromAccount: AccountResponse,
        viaAccount: AccountResponse,
        toAccount: AccountResponse,
        transactionType: TransactionTypeEnum

    ): List<String> {

        var menuItems: List<String> = listOf(
            "\nUser : $username",
            "Transaction Type : ${
                EnumUtils.getEnumNameForPrint(localEnum = transactionType)
            }",
            "From Account - ${fromAccount.id} : ${fromAccount.fullName}"
        )
        if (transactionType == TransactionTypeEnum.VIA) {
            menuItems = menuItems + listOf("Via. Account - ${viaAccount.id} : ${viaAccount.fullName}")
        }
        menuItems = menuItems + listOf("To Account - ${toAccount.id} : ${toAccount.fullName}")
        return menuItems
    }
}