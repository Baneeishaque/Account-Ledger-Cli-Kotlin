package accountLedgerCli.cli

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.api.response.AccountsResponse
import account.ledger.library.api.response.TransactionResponse
import account.ledger.library.api.response.MultipleTransactionResponse
import account.ledger.library.enums.AccountTypeEnum
import account.ledger.library.enums.FunctionCallSourceEnum
import account.ledger.library.enums.TransactionTypeEnum
import account.ledger.library.models.ChooseAccountResult
import account.ledger.library.models.InsertTransactionResult
import account.ledger.library.models.ViewTransactionsOutput
import account.ledger.library.operations.ServerOperations
import account.ledger.library.utils.*
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.utils.ChooseAccountUtilsInteractive
import account_ledger_library.constants.ConstantsNative
import common.utils.library.constants.ConstantsCommon
import common.utils.library.models.IsOkModel
import common.utils.library.utils.*
import io.github.cdimascio.dotenv.Dotenv


object TransactionViews {

    @JvmStatic
    fun viewTransactionsForAnAccount(

        userId: UInt,
        username: String,
        accountId: UInt,
        accountFullName: String,
        functionCallSource: FunctionCallSourceEnum = FunctionCallSourceEnum.FROM_OTHERS,
        previousTransactionData: InsertTransactionResult,
        fromAccount: AccountResponse,
        isUpToTimeStamp: Boolean = false,
        upToTimeStamp: String = "",
        isCreditDebitMode: Boolean = false,
        isConsoleMode: Boolean,
        isNotApiCall: Boolean = true,
        isDevelopmentMode: Boolean,
        dotEnv: Dotenv

    ): ViewTransactionsOutput {

        return viewTransactions(

            apiResponse = ServerOperations.getUserTransactionsForAnAccount(

                userId = userId,
                accountId = accountId,
                isDevelopmentMode = isDevelopmentMode
            ),
            insertTransactionResult = previousTransactionData,
            accountFullName = accountFullName,
            username = username,
            accountId = accountId,
            functionCallSource = functionCallSource,
            userId = userId,
            fromAccount = fromAccount,
            isUpToTimeStamp = isUpToTimeStamp,
            upToTimeStamp = upToTimeStamp,
            isCreditDebitMode = isCreditDebitMode,
            isConsoleMode = isConsoleMode,
            isNotApiCall = isNotApiCall,
            isDevelopmentMode = isDevelopmentMode,
            dotEnv = dotEnv
        )
    }

    @JvmStatic
    internal fun viewTransactions(

        apiResponse: Result<MultipleTransactionResponse>,
        insertTransactionResult: InsertTransactionResult,
        accountFullName: String,
        username: String,
        accountId: UInt,
        functionCallSource: FunctionCallSourceEnum,
        userId: UInt,
        fromAccount: AccountResponse,
        isUpToTimeStamp: Boolean = false,
        upToTimeStamp: String = "",
        isCreditDebitMode: Boolean = false,
        isConsoleMode: Boolean,
        isNotApiCall: Boolean = true,
        isDevelopmentMode: Boolean,
        dotEnv: Dotenv

    ): ViewTransactionsOutput {

        insertTransactionResult.isSuccess = false

        var viewTransactionsOutput = ViewTransactionsOutput(

            output = "E",
            addTransactionResult = insertTransactionResult
        )

        ApiUtilsCommon.apiResponseHandler(

            apiResponse = apiResponse,
            apiSuccessActions = fun(apiResponseData: MultipleTransactionResponse) {

                viewTransactionsOutput = viewTransactions(

                    userMultipleTransactionResponse = apiResponseData,
                    accountFullName = accountFullName,
                    dateTimeInText = insertTransactionResult.dateTimeInText,
                    transactionParticulars = insertTransactionResult.transactionParticulars,
                    transactionAmount = insertTransactionResult.transactionAmount,
                    fromAccount = fromAccount,
                    viaAccount = insertTransactionResult.viaAccount,
                    toAccount = insertTransactionResult.toAccount,
                    username = username,
                    accountId = accountId,
                    functionCallSource = functionCallSource,
                    userId = userId,
                    isUpToTimeStamp = isUpToTimeStamp,
                    upToTimeStamp = upToTimeStamp,
                    isCreditDebitMode = isCreditDebitMode,
                    isConsoleMode = isConsoleMode,
                    isNotApiCall = isNotApiCall,
                    isDevelopmentMode = isDevelopmentMode,
                    dotEnv = dotEnv
                )
            })

        return viewTransactionsOutput
    }

    fun viewTransactions(

        userMultipleTransactionResponse: MultipleTransactionResponse,
        accountFullName: String,
        dateTimeInText: String,
        transactionParticulars: String,
        transactionAmount: Float,
        fromAccount: AccountResponse,
        viaAccount: AccountResponse,
        toAccount: AccountResponse,
        username: String,
        accountId: UInt,
        functionCallSource: FunctionCallSourceEnum,
        userId: UInt,
        isUpToTimeStamp: Boolean = false,
        upToTimeStamp: String = "",
        isCreditDebitMode: Boolean = false,
        isConsoleMode: Boolean,
        isNotApiCall: Boolean = true,
        isDevelopmentMode: Boolean,
        dotEnv: Dotenv

    ): ViewTransactionsOutput {

        var localUserMultipleTransactionResponse: MultipleTransactionResponse = userMultipleTransactionResponse
        if (ApiUtilsInteractive.isNoTransactionResponseWithMessage(

                responseStatus = localUserMultipleTransactionResponse.status,
                noDataActions = fun() {

                    println("Account - $accountFullName")
                })
        ) {

            return ViewTransactionsOutput(

                output = "0",
                addTransactionResult = InsertTransactionResult(
                    isSuccess = false,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount,
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount
                )
            )
        } else {

            var userTransactionsMap: LinkedHashMap<UInt, TransactionResponse> =
                TransactionUtils.prepareUserTransactionsMap(
                    transactions = TransactionUtils.filterTransactionsForUpToDateTime(
                        isUpToTimeStamp = isUpToTimeStamp,
                        upToTimeStamp = upToTimeStamp,
                        transactions = localUserMultipleTransactionResponse.transactions
                    )
                )

            var choice: String
            do {
                val userTransactionsToTextFromListForLedgerResult: IsOkModel<String> =
                    TransactionUtilsInteractive.userTransactionsToTextFromListForLedger(

                        transactions = TransactionUtils.convertTransactionResponseListToTransactionListForLedger(
                            transactions = userTransactionsMap.values.toList()
                        ),
                        currentAccountId = fromAccount.id,
                        isCreditDebitMode = isCreditDebitMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                if (userTransactionsToTextFromListForLedgerResult.isOK) {

                    val userTransactionsText: String = userTransactionsToTextFromListForLedgerResult.data!!

                    var menuItems: List<String> = listOf(

                        "\nUser : $username",
                        "$accountFullName [$accountId] - Transactions",
                        "==================================================",
                        userTransactionsText
                    )
                    when (functionCallSource) {

                        FunctionCallSourceEnum.FROM_CHECK_ACCOUNTS -> {

                            menuItems =
                                menuItems + listOf("0 to Back, V to View Transactions of the Current Account, Enter to Continue : ")
                        }

                        FunctionCallSourceEnum.FROM_VIEW_TRANSACTIONS_OF_AN_ACCOUNT -> {

                            commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(menuItems)

                            return ViewTransactionsOutput(

                                output = "",
                                addTransactionResult = InsertTransactionResult(
                                    isSuccess = false,
                                    dateTimeInText = dateTimeInText,
                                    transactionParticulars = transactionParticulars,
                                    transactionAmount = transactionAmount,
                                    fromAccount = fromAccount,
                                    viaAccount = viaAccount,
                                    toAccount = toAccount
                                )
                            )
                        }

                        else -> {

                            if (isNotApiCall) {

                                menuItems = menuItems + listOf(

                                    "1 - Delete Transaction - By Index Number",
                                    "2 - Delete Transactions - From Index to Index",
                                    "3 - Delete Transaction - By Search",
                                    "4 - Edit Transaction - By Index Number",
                                    "5 - Edit Transaction - By Search",
                                    "6 - Rearrange Transaction - Up a Transaction (Key wise)",
                                    "7 - Rearrange Transaction - Up a Series of Transactions (Key wise)",
                                    "8 - Rearrange Transaction - Down a Transaction (Key wise)",
                                    "9 - Rearrange Transaction - Down a Series of Transactions (Key wise)",
                                    "10 - Add Transaction",
                                    "11 - Rearrange Transaction - Up a Transaction (Time wise)",
                                    "12 - Rearrange Transaction - Up a Series of Transactions (Time wise)",
                                    "13 - Rearrange Transaction - Down a Transaction (Time wise)",
                                    "14 - Rearrange Transaction - Down a Series of Transactions (Time wise)",
                                    "15 - Rearrange Transaction - Transaction to above of Specified Index",
                                    "0 - Back",
                                    "",
                                    "Enter Your Choice : "
                                )
                            } else {

                                commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(menuItems)

                                return ViewTransactionsOutput(

                                    output = "",
                                    addTransactionResult = InsertTransactionResult(

                                        isSuccess = false,
                                        dateTimeInText = dateTimeInText,
                                        transactionParticulars = transactionParticulars,
                                        transactionAmount = transactionAmount,
                                        fromAccount = fromAccount,
                                        viaAccount = viaAccount,
                                        toAccount = toAccount
                                    )
                                )
                            }
                        }
                    }
                    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(menuItems)

                    choice = readln()

                    var addTransactionResult = InsertTransactionResult(

                        isSuccess = false,
                        dateTimeInText = dateTimeInText,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount
                    )

                    when (choice) {

                        "1" -> {

                            if (isCallNotFromCheckAccounts(

                                    functionCallSource = functionCallSource,
                                    furtherActionsOnFalse = { ErrorUtilsInteractive.printInvalidOptionMessage() })
                            ) {

                                val transactionIndex: UInt =
                                    ListUtilsInteractive.getValidIndexFromCollectionWithSelectionPromptAndZeroAsBack(

                                        map = userTransactionsMap,
                                        itemSpecification = ConstantsNative.TRANSACTION_TEXT,
                                        items = userTransactionsText
                                    )

                                // TODO : Take Confirmation from the user
                                if (InsertOperationsInteractive.deleteTransaction(
                                        transactionId = transactionIndex,
                                        isDevelopmentMode = isDevelopmentMode
                                    )
                                ) {

                                    userTransactionsMap.remove(key = transactionIndex)
                                }
                            }
                        }

                        "2" -> {

                            if (isCallNotFromCheckAccounts(

                                    functionCallSource = functionCallSource,
                                    furtherActionsOnFalse = { ErrorUtilsInteractive.printInvalidOptionMessage() })
                            ) {

                                val transactionStartIndex: UInt =
                                    ListUtilsInteractive.getValidIndexFromCollectionWithSelectionPromptAndZeroAsBack(

                                        map = userTransactionsMap,
                                        itemSpecificationPrefix = "Start ",
                                        itemSpecification = ConstantsNative.TRANSACTION_TEXT,
                                        items = userTransactionsText
                                    )

                                if (transactionStartIndex != 0u) {

                                    val reducedUserTransactionsMap: Map<UInt, TransactionResponse> =
                                        userTransactionsMap.filterKeys { transactionId: UInt -> transactionId > transactionStartIndex }

                                    val userTransactionsToTextFromListForLedgerResult2: IsOkModel<String> =
                                        TransactionUtilsInteractive.userTransactionsToTextFromListForLedger(

                                            transactions = TransactionUtils.convertTransactionResponseListToTransactionListForLedger(
                                                transactions = reducedUserTransactionsMap.values.toList()
                                            ),
                                            currentAccountId = fromAccount.id,
                                            isDevelopmentMode = isDevelopmentMode
                                        )
                                    if (userTransactionsToTextFromListForLedgerResult2.isOK) {

                                        val transactionEndIndex: UInt =
                                            ListUtilsInteractive.getValidIndexFromCollectionWithSelectionPromptAndZeroAsBack(

                                                map = reducedUserTransactionsMap,
                                                itemSpecificationPrefix = "End ",
                                                itemSpecification = ConstantsNative.TRANSACTION_TEXT,
                                                items = userTransactionsToTextFromListForLedgerResult2.data!!
                                            )

                                        if (transactionEndIndex != 0u) {

                                            userTransactionsMap.filterKeys { transactionId: UInt ->
                                                transactionId in transactionStartIndex..transactionEndIndex
                                            }
                                                .forEach { transactionMapEntryForDelete: Map.Entry<UInt, TransactionResponse> ->

                                                    if (InsertOperationsInteractive.deleteTransaction(
                                                            transactionId = transactionMapEntryForDelete.key,
                                                            isDevelopmentMode = isDevelopmentMode
                                                        )
                                                    ) {

                                                        userTransactionsMap.remove(key = transactionMapEntryForDelete.key)

                                                    } else {

                                                        // TODO : Continue with confirmation
                                                        return@forEach
                                                    }
                                                }
                                        }
                                    } else {

                                        TransactionUtilsInteractive.printUserTransactionsToTextFromListForLedgerError(

                                            dataSpecification = "userTransactionsToTextFromListForLedger 2",
                                            userTransactionsToTextFromListForLedgerInstance = userTransactionsToTextFromListForLedgerResult2
                                        )
                                    }
                                }
                            }
                        }

                        "3", "5", "7", "8", "9", "12", "13", "14" -> {

                            if (isCallNotFromCheckAccounts(

                                    functionCallSource = functionCallSource,
                                    furtherActionsOnFalse = { ErrorUtilsInteractive.printInvalidOptionMessage() })
                            ) {

                                ToDoUtilsInteractive.showTodo()
                            }
                        }

                        "4" -> {

                            if (isCallNotFromCheckAccounts(

                                    functionCallSource = functionCallSource,
                                    furtherActionsOnFalse = { ErrorUtilsInteractive.printInvalidOptionMessage() })
                            ) {

                                val transactionIndex: UInt =
                                    ListUtilsInteractive.getValidIndexFromCollectionWithSelectionPromptAndZeroAsBack(

                                        map = userTransactionsMap,
                                        itemSpecification = ConstantsNative.TRANSACTION_TEXT,
                                        items = userTransactionsText
                                    )
                                val userAccountsMap: LinkedHashMap<UInt, AccountResponse> =
                                    AccountUtils.prepareUserAccountsMap(

                                        accounts = ApiUtilsInteractive.getAccountsFull(

                                            userId = userId,
                                            isConsoleMode = isConsoleMode,
                                            isDevelopmentMode = isDevelopmentMode
                                        ).getOrNull()!!.accounts
                                    )

                                val selectedTransaction: TransactionResponse = userTransactionsMap[transactionIndex]!!
                                val selectedTransactionDateTimeConversionResult: IsOkModel<String> =
                                    MysqlUtilsInteractive.mySqlDateTimeTextToNormalDateTimeTextWithMessage(

                                        mySqlDateTimeText = selectedTransaction.eventDateTime
                                    )

                                if (selectedTransactionDateTimeConversionResult.isOK) {

                                    //TODO : Present Transaction
                                    if (selectedTransaction.fromAccountId == fromAccount.id) {

                                        print("[${selectedTransaction.id}] [${selectedTransactionDateTimeConversionResult.data!!}]\t[${selectedTransaction.particulars}]\t[-${selectedTransaction.amount}]\t[${selectedTransaction.toAccountFullName}]")
                                    } else if (selectedTransaction.toAccountId == fromAccount.id) {

                                        print("[${selectedTransaction.id}] [${selectedTransactionDateTimeConversionResult.data!!}]\t[${selectedTransaction.particulars}]\t[+${selectedTransaction.amount}]\t[${selectedTransaction.fromAccountFullName}]")

                                    } else {

                                        //TODO : exceptional case
                                    }

                                    //TODO : Get User Confirmation

                                    var localFromAccount: AccountResponse =
                                        userAccountsMap[selectedTransaction.fromAccountId]!!
                                    do {
                                        println("Do you want to change Withdraw A/C (Y/N) (Default : N) : ")
                                        when (readln()) {
                                            "Y" -> {

                                                val chooseAccountResult: ChooseAccountResult =
                                                    ChooseAccountUtilsInteractive.chooseAccountById(

                                                        userId = userId,
                                                        accountType = AccountTypeEnum.FROM,
                                                        isDevelopmentMode = isDevelopmentMode
                                                    )
                                                if (chooseAccountResult.chosenAccountId != 0u) {

                                                    localFromAccount = chooseAccountResult.chosenAccount!!
                                                }
                                                break
                                            }

                                            "N", "" -> {

                                                break
                                            }

                                            else -> ErrorUtilsInteractive.printInvalidOptionMessage()
                                        }
                                    } while (true)

                                    var localToAccount: AccountResponse =
                                        userAccountsMap[selectedTransaction.toAccountId]!!
                                    do {
                                        print("Do you want to change Deposit A/C (Y/N) (Default : N) : ")
                                        when (readln()) {
                                            "Y" -> {

                                                val chooseAccountResult: ChooseAccountResult =
                                                    ChooseAccountUtilsInteractive.chooseAccountById(

                                                        userId = userId,
                                                        accountType = AccountTypeEnum.TO,
                                                        isDevelopmentMode = isDevelopmentMode
                                                    )
                                                if (chooseAccountResult.chosenAccountId != 0u) {

                                                    localToAccount = chooseAccountResult.chosenAccount!!
                                                }
                                                break
                                            }

                                            "N", "" -> {

                                                break
                                            }

                                            else -> ErrorUtilsInteractive.printInvalidOptionMessage()
                                        }
                                    } while (true)

                                    val updateTransactionResult: InsertTransactionResult =
                                        InsertOperationsInteractive.insertTransactionVariantsInteractive(

                                            userId = userId,
                                            username = username,
                                            transactionType = TransactionTypeEnum.NORMAL,
                                            fromAccount = localFromAccount,
                                            viaAccount = viaAccount,
                                            toAccount = localToAccount,
                                            transactionId = transactionIndex,
                                            dateTimeInText = selectedTransactionDateTimeConversionResult.data!!,
                                            transactionParticulars = selectedTransaction.particulars,
                                            transactionAmount = selectedTransaction.amount,
                                            isEditStep = true,
                                            isDevelopmentMode = isDevelopmentMode,
                                            dotEnv = dotEnv
                                        )

                                    // TODO : If from or to is not current account, remove the transaction
                                    if ((localFromAccount == fromAccount) || (localFromAccount == toAccount)) {

                                        if (localFromAccount != fromAccount) {

                                            userTransactionsMap[transactionIndex]!!.fromAccountId = localFromAccount.id
                                            userTransactionsMap[transactionIndex]!!.fromAccountName =
                                                localFromAccount.name
                                            userTransactionsMap[transactionIndex]!!.fromAccountFullName =
                                                localFromAccount.fullName
                                        }

                                        if (localToAccount != toAccount) {

                                            userTransactionsMap[transactionIndex]!!.toAccountId = localToAccount.id
                                            userTransactionsMap[transactionIndex]!!.toAccountName = localToAccount.name
                                            userTransactionsMap[transactionIndex]!!.toAccountFullName =
                                                localToAccount.fullName
                                        }

                                        // TODO : Change to MySQL Datetime format
                                        val toMySqlDateTimeConversionResult: IsOkModel<String> =
                                            MysqlUtils.normalDateTimeTextToMySqlDateTimeText(normalDateTimeText = updateTransactionResult.dateTimeInText)
                                        if (toMySqlDateTimeConversionResult.isOK) {

                                            userTransactionsMap[transactionIndex]!!.eventDateTime =
                                                toMySqlDateTimeConversionResult.data!!
                                        } else {

                                            // TODO : Exceptional Case
                                        }

                                        userTransactionsMap[transactionIndex]!!.particulars =
                                            updateTransactionResult.transactionParticulars
                                        userTransactionsMap[transactionIndex]!!.amount =
                                            updateTransactionResult.transactionAmount
                                    } else {

                                        userTransactionsMap.remove(key = transactionIndex)
                                    }
                                }
                            }
                        }

                        "6" -> {

                            if (isCallNotFromCheckAccounts(functionCallSource = functionCallSource)) {

                                val upTransactionKeyResult: IsOkModel<UInt> = getUpTransactionKey(

                                    userTransactionsMap = userTransactionsMap,
                                    currentAccountId = fromAccount.id,
                                    isDevelopmentMode = isDevelopmentMode
                                )
                                if (upTransactionKeyResult.isOK) {

                                    val upTransactionKey: UInt = upTransactionKeyResult.data!!

                                    var upPreviousTransactionKey = 0u
                                    userTransactionsMap.keys.forEach { key ->
                                        if (key == upTransactionKey) {
                                            return@forEach
                                        }
                                        upPreviousTransactionKey = key
                                    }
                                    if (upPreviousTransactionKey == 0u) {

                                        println("Up of Transaction T$upTransactionKey is NA")

                                    } else {

                                        val upTransaction: TransactionResponse = userTransactionsMap[upTransactionKey]!!
                                        val upPreviousTransaction: TransactionResponse =
                                            userTransactionsMap[upPreviousTransactionKey]!!
                                        val newDateTime: String =
                                            DateTimeUtils.subtract1SecondFromMySqlDateTimeInText(upPreviousTransaction.eventDateTime)

                                        if (isDevelopmentMode) {

                                            println("upTransaction = $upTransaction")
                                            println("upPreviousTransaction = $upPreviousTransaction")
                                            println("newDateTime = $newDateTime")
                                        }
                                        val getAccountsFullResult: Result<AccountsResponse> =
                                            ApiUtilsInteractive.getAccountsFull(

                                                userId = userId,
                                                isConsoleMode = isConsoleMode,
                                                isDevelopmentMode = isDevelopmentMode
                                            )
                                        if (getAccountsFullResult.isSuccess) {

                                            val userAccountsMap: LinkedHashMap<UInt, AccountResponse> =
                                                AccountUtils.prepareUserAccountsMap(

                                                    accounts = getAccountsFullResult.getOrNull()!!.accounts
                                                )

                                            if (InsertOperationsInteractive.updateTransactionInteractive(

                                                    transactionId = upTransactionKey,
                                                    eventDateTime = newDateTime,
                                                    particulars = upTransaction.particulars,
                                                    amount = upTransaction.amount,
                                                    fromAccountId = userAccountsMap[upTransaction.fromAccountId]!!.id,
                                                    toAccountId = userAccountsMap[upTransaction.toAccountId]!!.id,
                                                    isDateTimeUpdateOperation = true,
                                                    isDevelopmentMode = isDevelopmentMode
                                                )
                                            ) {
                                                userTransactionsMap[upTransactionKey]!!.eventDateTime =
                                                    DateTimeUtils.subtract1SecondFromMySqlDateTimeInText(
                                                        upPreviousTransaction.eventDateTime
                                                    )
                                                userTransactionsMap = userTransactionsMap.toList()
                                                    .sortedBy { (_: UInt, transaction: TransactionResponse) ->
                                                        MysqlUtils.mySqlDateTimeTextToDateTime(
                                                            mySqlDateTimeText = transaction.eventDateTime
                                                        ).data!!
                                                    }
                                                    .toMap() as LinkedHashMap<UInt, TransactionResponse>
                                            }
                                        }
                                    }
                                } else {

                                    TransactionUtilsInteractive.printUserTransactionsToTextFromListForLedgerError(

                                        userTransactionsToTextFromListForLedgerInstance = upTransactionKeyResult
                                    )
                                }
                            }
                        }

                        "10" -> {

                            if (isCallNotFromCheckAccounts(

                                    functionCallSource = functionCallSource,
                                    furtherActionsOnFalse = { ErrorUtilsInteractive.printInvalidOptionMessage() })
                            ) {

                                addTransactionResult = Screens.accountHome(

                                    userId = userId,
                                    username = username,
                                    fromAccount = fromAccount,
                                    viaAccount = viaAccount,
                                    toAccount = toAccount,
                                    dateTimeInText = addTransactionResult.dateTimeInText,
                                    transactionParticulars = addTransactionResult.transactionParticulars,
                                    transactionAmount = addTransactionResult.transactionAmount,
                                    isDevelopmentMode = isDevelopmentMode,
                                    dotEnv = dotEnv
                                )
                                if (addTransactionResult.isSuccess) {

                                    val apiResponse: Result<MultipleTransactionResponse> =
                                        ServerOperations.getUserTransactionsForAnAccount(

                                            userId = userId,
                                            accountId = accountId,
                                            isDevelopmentMode = isDevelopmentMode
                                        )
                                    if (apiResponse.isSuccess) {

                                        localUserMultipleTransactionResponse = apiResponse.getOrNull()!!
                                        if (localUserMultipleTransactionResponse.status != 1u) {

                                            userTransactionsMap =
                                                TransactionUtils.prepareUserTransactionsMap(transactions = localUserMultipleTransactionResponse.transactions)
                                        }
                                    }
                                }
                            }
                        }

                        "11" -> {

                            if (isCallNotFromCheckAccounts(functionCallSource = functionCallSource)
                            ) {
                                val upTransactionKeyResult: IsOkModel<UInt> = getUpTransactionKey(

                                    userTransactionsMap = userTransactionsMap,
                                    currentAccountId = fromAccount.id,
                                    isDevelopmentMode = isDevelopmentMode
                                )
                                if (upTransactionKeyResult.isOK) {

                                    val upTransactionKey: UInt = upTransactionKeyResult.data!!
                                    val upTransaction: TransactionResponse = userTransactionsMap[upTransactionKey]!!

                                    val userTransactionsMapSortedByTime: Map<UInt, TransactionResponse> =
                                        userTransactionsMap.toList()
                                            .sortedBy { (_: UInt, transaction: TransactionResponse): Pair<UInt, TransactionResponse> ->
                                                MysqlUtils.mySqlDateTimeTextToDateTime(mySqlDateTimeText = transaction.eventDateTime).data!!
                                            }.toMap()
                                    if (isDevelopmentMode) {

//                                println("userTransactionsMapSortedByTime = $userTransactionsMapSortedByTime")

                                        println("userTransactionsMapSortedByTime Event Timestamps")
                                        println(ConstantsCommon.dashedLineSeparator)
                                        userTransactionsMapSortedByTime.forEach { transaction: Map.Entry<UInt, TransactionResponse> ->

                                            println("${transaction.value.id} - ${transaction.value.eventDateTime}")
                                        }
                                    }

                                    var upPreviousTransactionKey = 0u
                                    var isUpPreviousTransactionKeyNotFound = true
                                    userTransactionsMapSortedByTime.values.forEach { currentTransaction ->

                                        println("Current transaction = ${currentTransaction.id} - ${currentTransaction.eventDateTime}")
                                        println("upTransaction = ${upTransaction.id} - ${upTransaction.eventDateTime}")

                                        if (currentTransaction.eventDateTime == upTransaction.eventDateTime) {

                                            isUpPreviousTransactionKeyNotFound = false
                                        }
                                        if (isUpPreviousTransactionKeyNotFound) {

                                            upPreviousTransactionKey = currentTransaction.id
                                            println("upPreviousTransactionKey = $upPreviousTransactionKey")
                                        }
                                    }
                                    if (upPreviousTransactionKey == 0u) {

                                        println("Up of Transaction T$upTransactionKey is NA")

                                    } else {

                                        val upPreviousTransaction: TransactionResponse =
                                            userTransactionsMap[upPreviousTransactionKey]!!
                                        val newDateTime: String =
                                            DateTimeUtils.subtract1SecondFromMySqlDateTimeInText(upPreviousTransaction.eventDateTime)

                                        if (isDevelopmentMode) {

                                            println("upTransaction = $upTransaction")
                                            println("upPreviousTransaction = $upPreviousTransaction")
                                            println("newDateTime = $newDateTime")
                                        }
                                        val getAccountsFullResult: Result<AccountsResponse> =
                                            ApiUtilsInteractive.getAccountsFull(

                                                userId = userId,
                                                isConsoleMode = isConsoleMode,
                                                isDevelopmentMode = isDevelopmentMode
                                            )
                                        if (getAccountsFullResult.isSuccess) {

                                            val userAccountsMap: LinkedHashMap<UInt, AccountResponse> =
                                                AccountUtils.prepareUserAccountsMap(

                                                    accounts = getAccountsFullResult.getOrNull()!!.accounts
                                                )

                                            if (InsertOperationsInteractive.updateTransactionInteractive(

                                                    transactionId = upTransactionKey,
                                                    eventDateTime = newDateTime,
                                                    particulars = upTransaction.particulars,
                                                    amount = upTransaction.amount,
                                                    fromAccountId = userAccountsMap[upTransaction.fromAccountId]!!.id,
                                                    toAccountId = userAccountsMap[upTransaction.toAccountId]!!.id,
                                                    isDateTimeUpdateOperation = true,
                                                    isDevelopmentMode = isDevelopmentMode
                                                )
                                            ) {
                                                userTransactionsMap[upTransactionKey]!!.eventDateTime =
                                                    DateTimeUtils.subtract1SecondFromMySqlDateTimeInText(
                                                        upPreviousTransaction.eventDateTime
                                                    )
                                                userTransactionsMap = userTransactionsMap.toList()
                                                    .sortedBy { (_: UInt, transaction: TransactionResponse) ->
                                                        MysqlUtils.mySqlDateTimeTextToDateTime(
                                                            mySqlDateTimeText = transaction.eventDateTime
                                                        ).data!!
                                                    }
                                                    .toMap() as LinkedHashMap<UInt, TransactionResponse>
                                            }
                                        }
                                    }
                                } else {

                                    TransactionUtilsInteractive.printUserTransactionsToTextFromListForLedgerError(

                                        userTransactionsToTextFromListForLedgerInstance = upTransactionKeyResult
                                    )
                                }

                            }
                        }

                        "15" -> {

                            if (isCallNotFromCheckAccounts(functionCallSource = functionCallSource)
                            ) {
                                val upTransactionKeyResult: IsOkModel<UInt> = getUpTransactionKey(

                                    userTransactionsMap = userTransactionsMap,
                                    currentAccountId = fromAccount.id,
                                    isDevelopmentMode = isDevelopmentMode
                                )
                                if (upTransactionKeyResult.isOK) {

                                    val upTransactionKey: UInt = upTransactionKeyResult.data!!
                                    val upTransaction: TransactionResponse = userTransactionsMap[upTransactionKey]!!

                                    val upToAboveTransactionKeyResult: IsOkModel<UInt> = getUpToAboveTransactionKey(

                                        userTransactionsMap = userTransactionsMap.filterKeys { transactionId: UInt -> transactionId != upTransactionKey },
                                        currentAccountId = fromAccount.id,
                                        isDevelopmentMode = isDevelopmentMode
                                    )
                                    if (upToAboveTransactionKeyResult.isOK) {

                                        val upPreviousTransactionKey: UInt = upToAboveTransactionKeyResult.data!!
                                        val upPreviousTransaction: TransactionResponse =
                                            userTransactionsMap[upPreviousTransactionKey]!!
                                        val newDateTime: String =
                                            DateTimeUtils.subtract1SecondFromMySqlDateTimeInText(upPreviousTransaction.eventDateTime)

                                        if (isDevelopmentMode) {

                                            println("upTransaction = $upTransaction")
                                            println("upPreviousTransaction = $upPreviousTransaction")
                                            println("newDateTime = $newDateTime")
                                        }
                                        val getAccountsFullResult: Result<AccountsResponse> =
                                            ApiUtilsInteractive.getAccountsFull(

                                                userId = userId,
                                                isConsoleMode = isConsoleMode,
                                                isDevelopmentMode = isDevelopmentMode
                                            )
                                        if (getAccountsFullResult.isSuccess) {

                                            val userAccountsMap: LinkedHashMap<UInt, AccountResponse> =
                                                AccountUtils.prepareUserAccountsMap(

                                                    accounts = getAccountsFullResult.getOrNull()!!.accounts
                                                )

                                            if (InsertOperationsInteractive.updateTransactionInteractive(

                                                    transactionId = upTransactionKey,
                                                    eventDateTime = newDateTime,
                                                    particulars = upTransaction.particulars,
                                                    amount = upTransaction.amount,
                                                    fromAccountId = userAccountsMap[upTransaction.fromAccountId]!!.id,
                                                    toAccountId = userAccountsMap[upTransaction.toAccountId]!!.id,
                                                    isDateTimeUpdateOperation = true,
                                                    isDevelopmentMode = isDevelopmentMode
                                                )
                                            ) {
                                                userTransactionsMap[upTransactionKey]!!.eventDateTime =
                                                    DateTimeUtils.subtract1SecondFromMySqlDateTimeInText(
                                                        upPreviousTransaction.eventDateTime
                                                    )
                                                userTransactionsMap = userTransactionsMap.toList()
                                                    .sortedBy { (_: UInt, transaction: TransactionResponse) ->
                                                        MysqlUtils.mySqlDateTimeTextToDateTime(
                                                            mySqlDateTimeText = transaction.eventDateTime
                                                        ).data!!
                                                    }
                                                    .toMap() as LinkedHashMap<UInt, TransactionResponse>
                                            }
                                        }
                                    } else {

                                        TransactionUtilsInteractive.printUserTransactionsToTextFromListForLedgerError(

                                            userTransactionsToTextFromListForLedgerInstance = upToAboveTransactionKeyResult
                                        )
                                    }

                                } else {

                                    TransactionUtilsInteractive.printUserTransactionsToTextFromListForLedgerError(

                                        userTransactionsToTextFromListForLedgerInstance = upTransactionKeyResult
                                    )
                                }

                            }
                        }

                        "0" -> {

                            return ViewTransactionsOutput(

                                output = "0",
                                addTransactionResult = addTransactionResult
                            )
                        }

                        "" -> {

                            if (isCallFromCheckAccounts(

                                    functionCallSource = functionCallSource,
                                    furtherActionsOnFalse = { ErrorUtilsInteractive.printInvalidOptionMessage() }
                                )
                            ) {

                                return ViewTransactionsOutput(

                                    output = "",
                                    addTransactionResult = addTransactionResult
                                )
                            }
                        }

                        "V" -> {

                            if (isCallFromCheckAccounts(

                                    functionCallSource = functionCallSource,
                                    furtherActionsOnFalse = { ErrorUtilsInteractive.printInvalidOptionMessage() }
                                )
                            ) {

                                return ViewTransactionsOutput(

                                    output = "V",
                                    addTransactionResult = addTransactionResult
                                )
                            }
                        }

                        else -> ErrorUtilsInteractive.printInvalidOptionMessage()
                    }

                } else {

                    return ViewTransactionsOutput(

                        output = "0",
                        addTransactionResult = InsertTransactionResult(
                            isSuccess = false,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount,
                            fromAccount = fromAccount,
                            viaAccount = viaAccount,
                            toAccount = toAccount
                        )
                    )
                }
            } while (true)
        }
    }

    private fun getUpTransactionKey(

        userTransactionsMap: Map<UInt, TransactionResponse>,
        currentAccountId: UInt,
        isDevelopmentMode: Boolean

    ): IsOkModel<UInt> = getTransactionKey(

        userTransactionsMap = userTransactionsMap,
        currentAccountId = currentAccountId,
        transactionPrefix = "Up ",
        isDevelopmentMode = isDevelopmentMode
    )

    private fun getUpToAboveTransactionKey(

        userTransactionsMap: Map<UInt, TransactionResponse>,
        currentAccountId: UInt,
        isDevelopmentMode: Boolean

    ): IsOkModel<UInt> = getTransactionKey(

        userTransactionsMap = userTransactionsMap,
        currentAccountId = currentAccountId,
        transactionPrefix = "Up to Above ",
        isDevelopmentMode = isDevelopmentMode
    )

    private fun getTransactionKey(

        userTransactionsMap: Map<UInt, TransactionResponse>,
        currentAccountId: UInt,
        transactionPrefix: String,
        isDevelopmentMode: Boolean

    ): IsOkModel<UInt> {

        val userTransactionsToTextFromListForLedgerResult: IsOkModel<String> =
            TransactionUtilsInteractive.userTransactionsToTextFromListForLedger(

                transactions = TransactionUtils.convertTransactionResponseListToTransactionListForLedger(transactions = userTransactionsMap.values.toList()),
                currentAccountId = currentAccountId,
                isDevelopmentMode = isDevelopmentMode
            )
        if (userTransactionsToTextFromListForLedgerResult.isOK) {

            return IsOkModel(
                isOK = true, data = ListUtilsInteractive.getValidIndexFromCollectionWithSelectionPromptAndZeroAsBack(

                    map = userTransactionsMap,
                    itemSpecificationPrefix = transactionPrefix,
                    itemSpecification = ConstantsNative.TRANSACTION_TEXT,
                    items = userTransactionsToTextFromListForLedgerResult.data!!
                )
            )
        }
        return IsOkModel(

            isOK = false,
            error = userTransactionsToTextFromListForLedgerResult.error
        )
    }

    private fun isCallNotFromCheckAccounts(functionCallSource: FunctionCallSourceEnum): Boolean {

        return isCallNotFromCheckAccounts(

            functionCallSource = functionCallSource,
            furtherActionsOnFalse = { ErrorUtilsInteractive.printInvalidOptionMessage() })
    }

    private fun isCallNotFromCheckAccounts(

        functionCallSource: FunctionCallSourceEnum,
        furtherActionsOnTrue: () -> Unit = fun() {},
        furtherActionsOnFalse: () -> Unit = fun() {}

    ): Boolean {

        return !isCallFromCheckAccounts(
            functionCallSource = functionCallSource,
            furtherActionsOnTrue = furtherActionsOnFalse,
            furtherActionsOnFalse = furtherActionsOnTrue
        )
    }

    private fun isCallFromCheckAccounts(

        functionCallSource: FunctionCallSourceEnum,
        furtherActionsOnTrue: () -> Unit = fun() {},
        furtherActionsOnFalse: () -> Unit = fun() {}

    ): Boolean {

        if (functionCallSource == FunctionCallSourceEnum.FROM_CHECK_ACCOUNTS) {

            furtherActionsOnTrue.invoke()
            return true
        }
        furtherActionsOnFalse.invoke()
        return false
    }

    fun viewTransactionsOfInputAccount(

        userId: UInt,
        username: String,
        previousTransactionData: InsertTransactionResult,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean,
        dotEnv: Dotenv

    ) {
        print("Enter Account Index or 0 to Back : A")
        val userInputForAccountIndex: String = readln()
        if (userInputForAccountIndex != "0") {

            AccountUtilsInteractive.processUserAccountsMap<Any>(

                userId = userId,
                isConsoleMode = isConsoleMode,
                isDevelopmentMode = isDevelopmentMode,
                successActions = fun(userAccountsMap: LinkedHashMap<UInt, AccountResponse>) {

                    val accountIndex: UInt = ListUtilsInteractive.getValidIndexFromCollectionWithZeroAsBack(

                        map = userAccountsMap,
                        inputForIndex = userInputForAccountIndex,
                        itemSpecification = ConstantsNative.ACCOUNT_TEXT,
                        items = AccountUtils.userAccountsToStringFromList(

                            accounts = userAccountsMap.values.toList()
                        )
                    )
                    if (accountIndex != 0u) {

                        val selectedAccount: AccountResponse = userAccountsMap[accountIndex]!!
                        viewTransactionsForAnAccount(

                            userId = userId,
                            username = username,
                            accountId = accountIndex,
                            accountFullName = selectedAccount.fullName,
                            functionCallSource = FunctionCallSourceEnum.FROM_VIEW_TRANSACTIONS_OF_AN_ACCOUNT,
                            previousTransactionData = previousTransactionData,
                            fromAccount = selectedAccount,
                            isConsoleMode = isConsoleMode,
                            isDevelopmentMode = isDevelopmentMode,
                            dotEnv = dotEnv
                        )
                    }
                }
            )
        }
    }
}
