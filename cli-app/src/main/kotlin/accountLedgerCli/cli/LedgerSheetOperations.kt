package accountLedgerCli.cli

import account.ledger.library.api.response.MultipleTransactionResponse
import account.ledger.library.api.response.TransactionResponse
import account.ledger.library.api.response.UserResponse
import account.ledger.library.enums.BalanceSheetRefineLevelEnum
import account.ledger.library.models.BalanceSheetDataRowModel
import account.ledger.library.models.ChooseUserResult
import account.ledger.library.operations.DataOperations
import account.ledger.library.operations.ServerOperations
import account.ledger.library.retrofit.data.MultipleTransactionDataSource
import account.ledger.library.utils.UserUtils
import account_ledger_library.constants.ConstantsNative
import common.utils.library.constants.CommonConstants
import common.utils.library.models.CommonDataModel
import common.utils.library.models.IsOkModel
import common.utils.library.utils.DateTimeUtils
import common.utils.library.utils.InteractiveUtils
import common.utils.library.utils.IsOkUtils
import common.utils.library.utils.MysqlUtils
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlin.math.absoluteValue

object LedgerSheetOperations {

    @JvmStatic
    fun balanceSheetOfUser(

        usersMap: LinkedHashMap<UInt, UserResponse>,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean
    ) {
        val chooseUserResult: ChooseUserResult = handleUserSelection(
            chosenUserId = InputOperations.getValidIndexFromCollectionWithSelectionPromptAndZeroAsBack(

                map = usersMap,
                itemSpecification = ConstantsNative.userText,
                items = UserUtils.usersToStringFromLinkedHashMap(usersMap = usersMap)

            ), usersMap = usersMap
        )
        if (chooseUserResult.isChosen) {

            printBalanceSheetOfUser(

                currentUserName = chooseUserResult.chosenUser!!.username,
                currentUserId = chooseUserResult.chosenUser!!.id,
                isConsoleMode = isConsoleMode,
                isDevelopmentMode = isDevelopmentMode
            )
        }
    }

    @JvmStatic
    fun printSheetOfUser(

        currentUserName: String,
        currentUserId: UInt,
        getDesiredAccountIdsForSheetOfUser: (MultipleTransactionResponse) -> MutableMap<UInt, String>,
        sheetTitle: String,
        isNotApiCall: Boolean = true,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean,
        operationsAfterPrint: (List<BalanceSheetDataRowModel>) -> Unit = fun(_: List<BalanceSheetDataRowModel>) {}

    ): IsOkModel<List<BalanceSheetDataRowModel>> {

        var isOkModel = IsOkModel<List<BalanceSheetDataRowModel>>(isOK = false)

        IsOkUtils.handleIsOkObject(

            isOkModel = generateSheetOfUser(

                currentUserName = currentUserName,
                currentUserId = currentUserId,
                getDesiredAccountIdsForSheetOfUser = getDesiredAccountIdsForSheetOfUser,
                isNotApiCall = isNotApiCall,
                isConsoleMode = isConsoleMode,
                isDevelopmentMode = isDevelopmentMode
            ),
            dataOperation = fun(data: String) {

                val balanceSheetDataRows = Json.decodeFromString(

                    deserializer = CommonDataModel.serializer(BalanceSheetDataRowModel.serializer()),
                    string = data

                ).data!!

                if (isConsoleMode) {

                    println("\nUser : $currentUserName $sheetTitle Sheet Ledger")
                    println(CommonConstants.dashedLineSeparator)
                    for (balanceSheetDataRow: BalanceSheetDataRowModel in balanceSheetDataRows) {

                        println("${balanceSheetDataRow.accountId} : ${balanceSheetDataRow.accountName} : ${balanceSheetDataRow.accountBalance}")
                    }
                    operationsAfterPrint.invoke(balanceSheetDataRows)

                } else {

                    println(data)
                }
                isOkModel = IsOkModel(

                    isOK = true,
                    data = balanceSheetDataRows
                )
            },
            errorOperation = fun(error: String) {

                println(error)
                isOkModel.error = error
            }
        )
        return isOkModel
    }

    @JvmStatic
    fun printBalanceSheetOfUser(

        currentUserName: String,
        currentUserId: UInt,
        refineLevel: BalanceSheetRefineLevelEnum = BalanceSheetRefineLevelEnum.WITHOUT_EXPENSE_ACCOUNTS,
        isNotApiCall: Boolean = true,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ): IsOkModel<List<BalanceSheetDataRowModel>> {

        return printSheetOfUser(

            currentUserName = currentUserName,
            currentUserId = currentUserId,
            getDesiredAccountIdsForSheetOfUser = fun(selectUserTransactionsAfterSpecifiedDateResult: MultipleTransactionResponse): MutableMap<UInt, String> {

                return getDesiredAccountIdsForBalanceSheetOfUser(

                    refineLevel = refineLevel,
                    selectUserTransactionsAfterSpecifiedDateResult = selectUserTransactionsAfterSpecifiedDateResult
                )
            },
            sheetTitle = "Balance",
            isNotApiCall = isNotApiCall,
            isConsoleMode = isConsoleMode,
            isDevelopmentMode = isDevelopmentMode
        )
    }

    @JvmStatic
    fun printSheetOfUserWithFinalBalance(

        currentUserName: String,
        currentUserId: UInt,
        getDesiredAccountIdsForSheetOfUser: (MultipleTransactionResponse) -> MutableMap<UInt, String>,
        sheetTitle: String,
        isNotApiCall: Boolean = true,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ): IsOkModel<List<BalanceSheetDataRowModel>> {

        return printSheetOfUser(

            currentUserName = currentUserName,
            currentUserId = currentUserId,
            getDesiredAccountIdsForSheetOfUser = getDesiredAccountIdsForSheetOfUser,
            sheetTitle = sheetTitle,
            isNotApiCall = isNotApiCall,
            isConsoleMode = isConsoleMode,
            isDevelopmentMode = isDevelopmentMode,
            operationsAfterPrint = ::printFinalBalanceOfBalanceSheetDataRows
        )
    }

    @JvmStatic
    fun printFinalBalanceOfBalanceSheetDataRows(balanceSheetDataRows: List<BalanceSheetDataRowModel>) {

        println(CommonConstants.dashedLineSeparator)
        println(calculateFinalBalanceOfBalanceSheetDataRows(balanceSheetDataRows))
    }

    @JvmStatic
    fun calculateFinalBalanceOfBalanceSheetDataRows(balanceSheetDataRows: List<BalanceSheetDataRowModel>): Float {

        var finalBalance = 0F
        balanceSheetDataRows.forEach { balanceSheetDataRow: BalanceSheetDataRowModel ->

            finalBalance += balanceSheetDataRow.accountBalance.absoluteValue
        }
        return finalBalance
    }

    @JvmStatic
    fun printSheetOfUserByAccountIdsFromEnvironment(

        currentUserName: String,
        currentUserId: UInt,
        sheetTitle: String,
        isNotApiCall: Boolean = true,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean,
        operationsAfterPrint: (List<BalanceSheetDataRowModel>) -> Unit = fun(_: List<BalanceSheetDataRowModel>) {},
        environmentVariable: String

    ): IsOkModel<List<BalanceSheetDataRowModel>> {

        return printSheetOfUser(

            currentUserName = currentUserName,
            currentUserId = currentUserId,
            getDesiredAccountIdsForSheetOfUser = fun(selectUserTransactionsAfterSpecifiedDateResult: MultipleTransactionResponse): MutableMap<UInt, String> {

                return getDesiredAccountIdsForSheetOfUserBasedOnEnvironment(

                    environmentVariable = environmentVariable,
                    selectUserTransactionsAfterSpecifiedDateResult = selectUserTransactionsAfterSpecifiedDateResult
                )
            },
            sheetTitle = sheetTitle,
            isNotApiCall = isNotApiCall,
            isConsoleMode = isConsoleMode,
            isDevelopmentMode = isDevelopmentMode,
            operationsAfterPrint = operationsAfterPrint
        )
    }

    @JvmStatic
    fun printSheetOfUserWithFinalBalanceByAccountIdsFromEnvironment(

        currentUserName: String,
        currentUserId: UInt,
        sheetTitle: String,
        isNotApiCall: Boolean = true,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean,
        environmentVariable: String

    ): IsOkModel<List<BalanceSheetDataRowModel>> = printSheetOfUserByAccountIdsFromEnvironment(

        currentUserName = currentUserName,
        currentUserId = currentUserId,
        sheetTitle = sheetTitle,
        isNotApiCall = isNotApiCall,
        isConsoleMode = isConsoleMode,
        isDevelopmentMode = isDevelopmentMode,
        operationsAfterPrint = ::printFinalBalanceOfBalanceSheetDataRows,
        environmentVariable = environmentVariable
    )

    @JvmStatic
    fun printExpenseSheetOfUser(

        currentUserName: String,
        currentUserId: UInt,
        isNotApiCall: Boolean = true,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ): IsOkModel<List<BalanceSheetDataRowModel>> {

        return printSheetOfUserWithFinalBalanceByAccountIdsFromEnvironment(

            currentUserName = currentUserName,
            currentUserId = currentUserId,
            sheetTitle = "Expense",
            isNotApiCall = isNotApiCall,
            isConsoleMode = isConsoleMode,
            isDevelopmentMode = isDevelopmentMode,
            environmentVariable = "EXPENSE_ACCOUNT_IDS_FOR_SHEET"
        )
    }

    @JvmStatic
    fun printProfitSheetOfUser(

        currentUserName: String,
        currentUserId: UInt,
        isNotApiCall: Boolean = true,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ) {
        val printIncomeSheetOfUserResult: IsOkModel<List<BalanceSheetDataRowModel>> = printIncomeSheetOfUser(

            currentUserName = currentUserName,
            currentUserId = currentUserId,
            isNotApiCall = isNotApiCall,
            isConsoleMode = isConsoleMode,
            isDevelopmentMode = isDevelopmentMode
        )
        val printExpenseSheetOfUserResult: IsOkModel<List<BalanceSheetDataRowModel>> = printExpenseSheetOfUser(

            currentUserName = currentUserName,
            currentUserId = currentUserId,
            isNotApiCall = isNotApiCall,
            isConsoleMode = isConsoleMode,
            isDevelopmentMode = isDevelopmentMode
        )
        if (printIncomeSheetOfUserResult.isOK && printExpenseSheetOfUserResult.isOK) {

            println(CommonConstants.DOUBLE_DASHED_LINE_SEPARATOR)

            val finalIncome = calculateFinalBalanceOfBalanceSheetDataRows(printIncomeSheetOfUserResult.data!!)
            val finalExpense = calculateFinalBalanceOfBalanceSheetDataRows(printExpenseSheetOfUserResult.data!!)

            println("$finalIncome - $finalExpense = ${finalIncome - finalExpense}")
        }
    }

    @JvmStatic
    fun printIncomeSheetOfUser(

        currentUserName: String,
        currentUserId: UInt,
        isNotApiCall: Boolean = true,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ): IsOkModel<List<BalanceSheetDataRowModel>> {

        return printSheetOfUserWithFinalBalanceByAccountIdsFromEnvironment(

            currentUserName = currentUserName,
            currentUserId = currentUserId,
            sheetTitle = "Income",
            isNotApiCall = isNotApiCall,
            isConsoleMode = isConsoleMode,
            isDevelopmentMode = isDevelopmentMode,
            environmentVariable = "INCOME_ACCOUNT_IDS_FOR_SHEET"
        )
    }

    private fun generateSheetOfUser(

        currentUserName: String,
        currentUserId: UInt,
        getDesiredAccountIdsForSheetOfUser: (MultipleTransactionResponse) -> MutableMap<UInt, String>,
        isNotApiCall: Boolean = true,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ): IsOkModel<String> {

        if (isConsoleMode) {

            println("currentUser : $currentUserName")
        }

        val multipleTransactionDataSource = MultipleTransactionDataSource()

        if (isNotApiCall) {

            println("Contacting Server...")
        }

        val apiResponse: Result<MultipleTransactionResponse>

        //TODO : Only applicable for user_first_entry_date usernames
        val specifiedDate: IsOkModel<String> = MysqlUtils.normalDateTextToMySqlDateText(

            //TODO : migrate to DateTimeUtils
            normalDateText = DataOperations.getUserInitialTransactionDateFromUsername(username = currentUserName)
                .minusDays(
                    /* daysToSubtract = */ 1
                ).format(DateTimeUtils.normalDatePattern)
        )
        if (specifiedDate.isOK) {

            //TODO : migrate to ApiUtilsCommon
            runBlocking {

                apiResponse = multipleTransactionDataSource.selectUserTransactionsAfterSpecifiedDate(

                    userId = currentUserId,
                    specifiedDate = specifiedDate.data!!
                )
            }

            // println("Response : $apiResponse2")
            if (apiResponse.isFailure) {

                if (isNotApiCall) {

                    println("Error : ${(apiResponse.exceptionOrNull() as Exception).localizedMessage}")
                    do {

                        print("Retry (Y/N) ? : ")
                        when (readln()) {

                            "Y", "" -> {

                                return generateSheetOfUser(

                                    currentUserName = currentUserName,
                                    currentUserId = currentUserId,
                                    getDesiredAccountIdsForSheetOfUser = getDesiredAccountIdsForSheetOfUser,
                                    isConsoleMode = isConsoleMode,
                                    isDevelopmentMode = isDevelopmentMode
                                )
                            }

                            "N" -> {

                                return IsOkModel(

                                    isOK = false,
                                    error = CommonConstants.USER_CANCELED_MESSAGE
                                )
                            }

                            else -> InteractiveUtils.invalidOptionMessage()
                        }
                    } while (true)

                } else {

                    return IsOkModel(

                        isOK = false,
                        error = Json.encodeToString(

                            serializer = CommonDataModel.serializer(Unit.serializer()),
                            value = CommonDataModel(

                                status = 1,
                                error = "Error : ${(apiResponse.exceptionOrNull() as Exception).localizedMessage}"
                            )
                        )
                    )
                }
            } else {

                val selectUserTransactionsAfterSpecifiedDateResult: MultipleTransactionResponse =
                    apiResponse.getOrNull()!!
                // TODO : migrate to ApiUtils
                if (selectUserTransactionsAfterSpecifiedDateResult.status == 1u) {

                    return IsOkModel(

                        isOK = false,
                        error = Json.encodeToString(

                            serializer = CommonDataModel.serializer(Unit.serializer()),
                            value = CommonDataModel(

                                status = 2,
                                error = "No Transactions"
                            )
                        )
                    )
                } else {

                    val accounts: MutableMap<UInt, String> =
                        getDesiredAccountIdsForSheetOfUser(
                            selectUserTransactionsAfterSpecifiedDateResult
                        )
                    val sheetDataRows: MutableList<BalanceSheetDataRowModel> = mutableListOf()
                    for (account: MutableMap.MutableEntry<UInt, String> in accounts) {

                        //TODO : migrate to ApiUtilsCommon
                        val apiResponse2: Result<MultipleTransactionResponse> =
                            ServerOperations.getUserTransactionsForAnAccount(

                                userId = currentUserId,
                                accountId = account.key,
                                isNotFromBalanceSheet = false,
                                isDevelopmentMode = isDevelopmentMode
                            )
                        if (apiResponse2.isFailure) {

                            if (isNotApiCall) {

                                println("Error : ${(apiResponse2.exceptionOrNull() as Exception).localizedMessage}")
//                            do {
//                                print("Retry (Y/N) ? : ")
//                                val input: String = readLine()!!
//                                when (input) {
//                                    "Y", "" -> {
//                                    }
//
//                                    "N" -> {}
//                                    else -> invalidOptionMessage()
//                                }
//                            } while (input != "N")
                            } else {

                                return IsOkModel(

                                    isOK = false,
                                    error = Json.encodeToString(

                                        serializer = CommonDataModel.serializer(Unit.serializer()),
                                        value = CommonDataModel(

                                            status = 1,
                                            error = "Error : ${(apiResponse2.exceptionOrNull() as Exception).localizedMessage}"
                                        )
                                    )
                                )
                            }
                        } else {

                            val userMultipleTransactionResponseResult: MultipleTransactionResponse =
                                apiResponse2.getOrNull()!!
                            if (userMultipleTransactionResponseResult.status == 0u) {

                                var currentBalance = 0.0F
                                userMultipleTransactionResponseResult.transactions.forEach { currentTransaction: TransactionResponse ->

                                    if (currentTransaction.fromAccountId == account.key) {

                                        currentBalance -= currentTransaction.amount

                                    } else {

                                        currentBalance += currentTransaction.amount
                                    }
                                }
                                if (currentBalance != 0.0F) {

                                    sheetDataRows.add(

                                        element = BalanceSheetDataRowModel(

                                            accountId = account.key,
                                            accountName = account.value,
                                            accountBalance = currentBalance
                                        )
                                    )
                                }
                            } else {

                                return IsOkModel(

                                    isOK = false,
                                    error = Json.encodeToString(

                                        serializer = CommonDataModel.serializer(Unit.serializer()),
                                        value = CommonDataModel(

                                            status = 1,
                                            error = "Server Execution Error, Execution Status is ${userMultipleTransactionResponseResult.status}"
                                        )
                                    )
                                )
                            }
                        }
                    }

                    //TODO : print Formatted Sheet on Console
                    return IsOkModel(

                        isOK = true,
                        data = Json.encodeToString(

                            serializer = CommonDataModel.serializer(BalanceSheetDataRowModel.serializer()),
                            value = CommonDataModel(

                                status = 0,
                                data = sheetDataRows
                            )
                        )
                    )
                }
            }
        } else {

            return IsOkModel(

                isOK = false,
                error = Json.encodeToString(

                    serializer = CommonDataModel.serializer(Unit.serializer()),
                    value = CommonDataModel(

                        status = 1,
                        error = "Error : ${specifiedDate.data!!}"
                    )
                )
            )
        }
    }

    private fun getDesiredAccountIdsForBalanceSheetOfUser(

        refineLevel: BalanceSheetRefineLevelEnum,
        selectUserTransactionsAfterSpecifiedDateResult: MultipleTransactionResponse

    ): MutableMap<UInt, String> {

        var accountsToExclude: List<String> = emptyList()
        App.dotEnv = App.reloadDotEnv()
        if (refineLevel != BalanceSheetRefineLevelEnum.ALL) {

            when (refineLevel) {

                BalanceSheetRefineLevelEnum.WITHOUT_OPEN_BALANCES -> {

                    // TODO : Change to new api methods
                    accountsToExclude = (App.dotEnv["OPEN_BALANCE_ACCOUNT_IDS"] ?: "0").split(',')
                }

                BalanceSheetRefineLevelEnum.WITHOUT_MISC_INCOMES -> {

                    accountsToExclude = (App.dotEnv["OPEN_BALANCE_ACCOUNT_IDS"]
                        ?: "0").split(',') + (App.dotEnv["MISC_INCOME_ACCOUNT_IDS"]
                        ?: "0").split(',')
                }

                BalanceSheetRefineLevelEnum.WITHOUT_INVESTMENT_RETURNS -> {

                    accountsToExclude =
                        (App.dotEnv["OPEN_BALANCE_ACCOUNT_IDS"]
                            ?: "0").split(',') + (App.dotEnv["MISC_INCOME_ACCOUNT_IDS"]
                            ?: "0").split(',') + (App.dotEnv["INVESTMENT_RETURNS_ACCOUNT_IDS"]
                            ?: "0").split(',')
                }

                BalanceSheetRefineLevelEnum.WITHOUT_FAMILY_ACCOUNTS -> {

                    accountsToExclude =
                        (App.dotEnv["OPEN_BALANCE_ACCOUNT_IDS"]
                            ?: "0").split(',') + (App.dotEnv["MISC_INCOME_ACCOUNT_IDS"]
                            ?: "0").split(',') + (App.dotEnv["INVESTMENT_RETURNS_ACCOUNT_IDS"]
                            ?: "0").split(',') + (App.dotEnv["FAMILY_ACCOUNT_IDS"]
                            ?: "0").split(',')
                }

                BalanceSheetRefineLevelEnum.WITHOUT_EXPENSE_ACCOUNTS -> {

                    accountsToExclude =
                        (App.dotEnv["OPEN_BALANCE_ACCOUNT_IDS"]
                            ?: "0").split(',') + (App.dotEnv["MISC_INCOME_ACCOUNT_IDS"]
                            ?: "0").split(',') + (App.dotEnv["INVESTMENT_RETURNS_ACCOUNT_IDS"]
                            ?: "0").split(',') + (App.dotEnv["FAMILY_ACCOUNT_IDS"]
                            ?: "0").split(',') + (App.dotEnv["EXPENSE_ACCOUNT_IDS"]
                            ?: "0").split(',')
                }
                //TODO : Report this
                else -> {}
            }
        }
        val accounts: MutableMap<UInt, String> = mutableMapOf()
        selectUserTransactionsAfterSpecifiedDateResult.transactions.forEach { transaction: TransactionResponse ->

            if (!accountsToExclude.contains(transaction.fromAccountId.toString())) {

                accounts.putIfAbsent(transaction.fromAccountId, transaction.fromAccountFullName)
            }

            if (!accountsToExclude.contains(transaction.toAccountId.toString())) {

                accounts.putIfAbsent(transaction.toAccountId, transaction.toAccountFullName)
            }
        }

//    println("Affected A/Cs : $accounts")
        return accounts
    }

    private fun getDesiredAccountIdsForSheetOfUserBasedOnEnvironment(

        environmentVariable: String,
        selectUserTransactionsAfterSpecifiedDateResult: MultipleTransactionResponse

    ): MutableMap<UInt, String> {

        App.dotEnv = App.reloadDotEnv()
        val accountsToInclude: List<String> = (App.dotEnv[environmentVariable] ?: "0").split(',')

        val accounts: MutableMap<UInt, String> = mutableMapOf()
        selectUserTransactionsAfterSpecifiedDateResult.transactions.forEach { transaction: TransactionResponse ->

            if (accountsToInclude.contains(transaction.fromAccountId.toString())) {

                accounts.putIfAbsent(transaction.fromAccountId, transaction.fromAccountFullName)
            }

            if (accountsToInclude.contains(transaction.toAccountId.toString())) {

                accounts.putIfAbsent(transaction.toAccountId, transaction.toAccountFullName)
            }
        }

        println("Affected Expense A/Cs : $accounts")
        return accounts
    }
}