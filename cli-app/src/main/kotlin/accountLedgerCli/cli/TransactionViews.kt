package accountLedgerCli.cli

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.api.response.AccountsResponse
import account.ledger.library.api.response.TransactionResponse
import account.ledger.library.api.response.TransactionsResponse
import account.ledger.library.operations.getUserTransactionsForAnAccount
import account_ledger_library.constants.Constants
import account.ledger.library.enums.FunctionCallSourceEnum
import account.ledger.library.enums.TransactionTypeEnum
import account.ledger.library.enums.AccountTypeEnum
import account.ledger.library.models.InsertTransactionResult
import account.ledger.library.models.ViewTransactionsOutput
import account.ledger.library.models.ChooseAccountResult
import account.ledger.library.utils.ApiUtils
import account.ledger.library.utils.TransactionUtils
import accountLedgerCli.utils.ChooseAccountUtils
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import account.ledger.library.utils.AccountUtils
import common.utils.library.models.IsOkModel
import common.utils.library.utils.*
import common.utils.library.utils.ApiUtils as CommonApiUtils
import common.utils.library.utils.HandleResponses as CommonHandleResponses
import common.utils.library.constants.Constants as CommonConstants


object TransactionViews {

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
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ): ViewTransactionsOutput {

        return viewTransactions(

            apiResponse = getUserTransactionsForAnAccount(

                userId = userId,
                accountId = accountId,
                isDevelopmentMode = isDevelopmentMode
            ),
            insertTransactionResult = previousTransactionData,
            fromAccount = fromAccount,
            accountFullName = accountFullName,
            username = username,
            accountId = accountId,
            functionCallSource = functionCallSource,
            userId = userId,
            isUpToTimeStamp = isUpToTimeStamp,
            upToTimeStamp = upToTimeStamp,
            isConsoleMode = isConsoleMode,
            isDevelopmentMode = isDevelopmentMode
        )
    }

    @JvmStatic
    internal fun viewTransactions(

        apiResponse: Result<TransactionsResponse>,
        insertTransactionResult: InsertTransactionResult,
        accountFullName: String,
        username: String,
        accountId: UInt,
        functionCallSource: FunctionCallSourceEnum,
        userId: UInt,
        fromAccount: AccountResponse,
        isUpToTimeStamp: Boolean = false,
        upToTimeStamp: String = "",
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ): ViewTransactionsOutput {

        insertTransactionResult.isSuccess = false

        var viewTransactionsOutput = ViewTransactionsOutput(

            output = "E",
            addTransactionResult = insertTransactionResult
        )

        CommonApiUtils.apiResponseHandler(

            apiResponse = apiResponse,
            apiSuccessActions = fun() {

                viewTransactionsOutput = viewTransactions(

                    userTransactionsResponse = apiResponse.getOrNull()!!,
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
                    isConsoleMode = isConsoleMode,
                    isDevelopmentMode = isDevelopmentMode
                )
            })

        return viewTransactionsOutput
    }

    fun viewTransactions(

        userTransactionsResponse: TransactionsResponse,
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
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ): ViewTransactionsOutput {

        var localUserTransactionsResponse: TransactionsResponse = userTransactionsResponse
        if (ApiUtils.isNoTransactionResponseWithMessage(

                responseStatus = localUserTransactionsResponse.status,
                noDataBeforeMessageActions = fun() {

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
                        transactions = localUserTransactionsResponse.transactions
                    )
                )

            var choice: String
            do {
                val userTransactionsText: String = TransactionUtils.userTransactionsToTextFromList(

                    transactions = userTransactionsMap.values.toList(),
                    currentAccountId = fromAccount.id,
                    isDevelopmentMode = isDevelopmentMode
                )

                var menuItems: List<String> = listOf(

                    "\nUser : $username",
                    "$accountFullName [$accountId] - Transactions",
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
                                furtherActionsOnFalse = { InteractiveUtils.invalidOptionMessage() })
                        ) {

                            val transactionIndex: UInt = getValidIndexWithInputPrompt(

                                map = userTransactionsMap,
                                itemSpecification = Constants.transactionText,
                                items = userTransactionsText,
                                backValue = 0u
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
                                furtherActionsOnFalse = { InteractiveUtils.invalidOptionMessage() })
                        ) {

                            val transactionStartIndex: UInt = getValidIndexWithInputPrompt(

                                map = userTransactionsMap,
                                itemSpecification = Constants.transactionText,
                                items = userTransactionsText,
                                itemSpecificationPrefix = "Start ",
                                backValue = 0u
                            )

                            if (transactionStartIndex != 0u) {

                                val reducedUserTransactionsMap: Map<UInt, TransactionResponse> =
                                    userTransactionsMap.filterKeys { transactionId: UInt -> transactionId > transactionStartIndex }

                                val transactionEndIndex: UInt = getValidIndexWithInputPrompt(

                                    map = reducedUserTransactionsMap,
                                    itemSpecification = Constants.transactionText,
                                    items = TransactionUtils.userTransactionsToTextFromList(

                                        transactions = reducedUserTransactionsMap.values.toList(),
                                        currentAccountId = fromAccount.id,
                                        isDevelopmentMode = isDevelopmentMode
                                    ),
                                    itemSpecificationPrefix = "End ",
                                    backValue = 0u
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
                            }
                        }
                    }

                    "3", "5", "7", "8", "9", "12", "13", "14" -> {

                        if (isCallNotFromCheckAccounts(

                                functionCallSource = functionCallSource,
                                furtherActionsOnFalse = { InteractiveUtils.invalidOptionMessage() })
                        ) {

                            ToDoUtils.showTodo()
                        }
                    }

                    "4" -> {

                        if (isCallNotFromCheckAccounts(

                                functionCallSource = functionCallSource,
                                furtherActionsOnFalse = { InteractiveUtils.invalidOptionMessage() })
                        ) {

                            val transactionIndex: UInt = getValidIndexWithInputPrompt(

                                map = userTransactionsMap,
                                itemSpecification = Constants.transactionText,
                                items = userTransactionsText,
                                backValue = 0u
                            )
                            val userAccountsMap: LinkedHashMap<UInt, AccountResponse> =
                                AccountUtils.prepareUserAccountsMap(

                                    accounts = ApiUtils.getAccountsFull(

                                        userId = userId,
                                        isConsoleMode = isConsoleMode,
                                        isDevelopmentMode = isDevelopmentMode
                                    ).getOrNull()!!.accounts
                                )

                            val selectedTransaction: TransactionResponse = userTransactionsMap[transactionIndex]!!
                            val selectedTransactionDateTimeConversionResult: IsOkModel<String> =

                                MysqlUtilsInteractive.dateTimeTextConversionWithMessage(

                                    inputDateTimeText = selectedTransaction.event_date_time,
                                    dateTimeTextConversionFunction = fun(): IsOkModel<String> {

                                        return MysqlUtils.mySqlDateTimeTextToNormalDateTimeText(mySqlDateTimeText = selectedTransaction.event_date_time)
                                    },
                                )
                            if (selectedTransactionDateTimeConversionResult.isOK) {

                                //TODO : Present Transaction
                                if (selectedTransaction.from_account_id == fromAccount.id) {

                                    print("[${selectedTransaction.id}] [${selectedTransactionDateTimeConversionResult.data!!}]\t[${selectedTransaction.particulars}]\t[-${selectedTransaction.amount}]\t[${selectedTransaction.to_account_full_name}]")
                                } else if (selectedTransaction.to_account_id == fromAccount.id) {

                                    print("[${selectedTransaction.id}] [${selectedTransactionDateTimeConversionResult.data!!}]\t[${selectedTransaction.particulars}]\t[+${selectedTransaction.amount}]\t[${selectedTransaction.from_account_full_name}]")

                                } else {

                                    //TODO : exceptional case
                                }

                                //TODO : Get User Confirmation

                                var localFromAccount: AccountResponse =
                                    userAccountsMap[selectedTransaction.from_account_id]!!
                                do {
                                    println("Do you want to change Withdraw A/C (Y/N) (Default : N) : ")
                                    when (readln()) {
                                        "Y" -> {

                                            val chooseAccountResult: ChooseAccountResult =
                                                ChooseAccountUtils.chooseAccountById(

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

                                        else -> InteractiveUtils.invalidOptionMessage()
                                    }
                                } while (true)

                                var localToAccount: AccountResponse =
                                    userAccountsMap[selectedTransaction.to_account_id]!!
                                do {
                                    print("Do you want to change Deposit A/C (Y/N) (Default : N) : ")
                                    when (readln()) {
                                        "Y" -> {

                                            val chooseAccountResult: ChooseAccountResult =
                                                ChooseAccountUtils.chooseAccountById(

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

                                        else -> InteractiveUtils.invalidOptionMessage()
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
                                        isDevelopmentMode = isDevelopmentMode
                                    )

                                // TODO : If from or to is not current account, remove the transaction
                                if ((localFromAccount == fromAccount) || (localFromAccount == toAccount)) {

                                    if (localFromAccount != fromAccount) {

                                        userTransactionsMap[transactionIndex]!!.from_account_id = localFromAccount.id
                                        userTransactionsMap[transactionIndex]!!.from_account_name =
                                            localFromAccount.name
                                        userTransactionsMap[transactionIndex]!!.from_account_full_name =
                                            localFromAccount.fullName
                                    }

                                    if (localToAccount != toAccount) {

                                        userTransactionsMap[transactionIndex]!!.to_account_id = localToAccount.id
                                        userTransactionsMap[transactionIndex]!!.to_account_name = localToAccount.name
                                        userTransactionsMap[transactionIndex]!!.to_account_full_name =
                                            localToAccount.fullName
                                    }

                                    // TODO : Change to MySQL Datetime format
                                    val toMySqlDateTimeConversionResult: IsOkModel<String> =
                                        MysqlUtils.normalDateTimeTextToMySqlDateTimeText(normalDateTimeText = updateTransactionResult.dateTimeInText)
                                    if (toMySqlDateTimeConversionResult.isOK) {

                                        userTransactionsMap[transactionIndex]!!.event_date_time =
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

                            val upTransactionKey: UInt = getUpTransactionKey(

                                userTransactionsMap = userTransactionsMap,
                                currentAccountId = fromAccount.id,
                                isDevelopmentMode = isDevelopmentMode
                            )

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
                                    DateTimeUtils.subtract1SecondFromMySqlDateTimeInText(upPreviousTransaction.event_date_time)

                                if (App.isDevelopmentMode) {

                                    println("upTransaction = $upTransaction")
                                    println("upPreviousTransaction = $upPreviousTransaction")
                                    println("newDateTime = $newDateTime")
                                }
                                val getAccountsFullResult: Result<AccountsResponse> =
                                    ApiUtils.getAccountsFull(

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
                                            fromAccountId = userAccountsMap[upTransaction.from_account_id]!!.id,
                                            toAccountId = userAccountsMap[upTransaction.to_account_id]!!.id,
                                            isDateTimeUpdateOperation = true,
                                            isDevelopmentMode = isDevelopmentMode
                                        )
                                    ) {
                                        userTransactionsMap[upTransactionKey]!!.event_date_time =
                                            DateTimeUtils.subtract1SecondFromMySqlDateTimeInText(upPreviousTransaction.event_date_time)
                                        userTransactionsMap = userTransactionsMap.toList()
                                            .sortedBy { (_: UInt, transaction: TransactionResponse) ->
                                                MysqlUtils.mySqlDateTimeTextToDateTime(
                                                    mySqlDateTimeText = transaction.event_date_time
                                                ).data!!
                                            }
                                            .toMap() as LinkedHashMap<UInt, TransactionResponse>
                                    }
                                }
                            }
                        }
                    }

                    "10" -> {

                        if (isCallNotFromCheckAccounts(

                                functionCallSource = functionCallSource,
                                furtherActionsOnFalse = { InteractiveUtils.invalidOptionMessage() })
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
                                isDevelopmentMode = isDevelopmentMode
                            )
                            if (addTransactionResult.isSuccess) {

                                val apiResponse: Result<TransactionsResponse> = getUserTransactionsForAnAccount(

                                    userId = userId,
                                    accountId = accountId,
                                    isDevelopmentMode = isDevelopmentMode
                                )
                                if (apiResponse.isSuccess) {

                                    localUserTransactionsResponse = apiResponse.getOrNull()!!
                                    if (localUserTransactionsResponse.status != 1u) {

                                        userTransactionsMap =
                                            TransactionUtils.prepareUserTransactionsMap(transactions = localUserTransactionsResponse.transactions)
                                    }
                                }
                            }
                        }
                    }

                    "11" -> {

                        if (isCallNotFromCheckAccounts(functionCallSource = functionCallSource)
                        ) {
                            val upTransactionKey: UInt = getUpTransactionKey(

                                userTransactionsMap = userTransactionsMap,
                                currentAccountId = fromAccount.id,
                                isDevelopmentMode = isDevelopmentMode
                            )
                            val upTransaction: TransactionResponse = userTransactionsMap[upTransactionKey]!!

                            val userTransactionsMapSortedByTime: Map<UInt, TransactionResponse> =
                                userTransactionsMap.toList()
                                    .sortedBy { (_: UInt, transaction: TransactionResponse): Pair<UInt, TransactionResponse> ->
                                        MysqlUtils.mySqlDateTimeTextToDateTime(mySqlDateTimeText = transaction.event_date_time).data!!
                                    }.toMap()
                            if (isDevelopmentMode) {

//                                println("userTransactionsMapSortedByTime = $userTransactionsMapSortedByTime")

                                println("userTransactionsMapSortedByTime Event Timestamps")
                                println(CommonConstants.dashedLineSeparator)
                                userTransactionsMapSortedByTime.forEach { transaction: Map.Entry<UInt, TransactionResponse> ->

                                    println("${transaction.value.id} - ${transaction.value.event_date_time}")
                                }
                            }

                            var upPreviousTransactionKey = 0u
                            var isUpPreviousTransactionKeyNotFound = true
                            userTransactionsMapSortedByTime.values.forEach { currentTransaction ->

                                println("Current transaction = ${currentTransaction.id} - ${currentTransaction.event_date_time}")
                                println("upTransaction = ${upTransaction.id} - ${upTransaction.event_date_time}")

                                if (currentTransaction.event_date_time == upTransaction.event_date_time) {

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
                                    DateTimeUtils.subtract1SecondFromMySqlDateTimeInText(upPreviousTransaction.event_date_time)

                                if (isDevelopmentMode) {

                                    println("upTransaction = $upTransaction")
                                    println("upPreviousTransaction = $upPreviousTransaction")
                                    println("newDateTime = $newDateTime")
                                }
                                val getAccountsFullResult: Result<AccountsResponse> =
                                    ApiUtils.getAccountsFull(

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
                                            fromAccountId = userAccountsMap[upTransaction.from_account_id]!!.id,
                                            toAccountId = userAccountsMap[upTransaction.to_account_id]!!.id,
                                            isDateTimeUpdateOperation = true,
                                            isDevelopmentMode = isDevelopmentMode
                                        )
                                    ) {
                                        userTransactionsMap[upTransactionKey]!!.event_date_time =
                                            DateTimeUtils.subtract1SecondFromMySqlDateTimeInText(upPreviousTransaction.event_date_time)
                                        userTransactionsMap = userTransactionsMap.toList()
                                            .sortedBy { (_: UInt, transaction: TransactionResponse) ->
                                                MysqlUtils.mySqlDateTimeTextToDateTime(
                                                    mySqlDateTimeText = transaction.event_date_time
                                                ).data!!
                                            }
                                            .toMap() as LinkedHashMap<UInt, TransactionResponse>
                                    }
                                }
                            }
                        }
                    }

                    "15" -> {

                        if (isCallNotFromCheckAccounts(functionCallSource = functionCallSource)
                        ) {
                            val upTransactionKey: UInt = getUpTransactionKey(

                                userTransactionsMap = userTransactionsMap,
                                currentAccountId = fromAccount.id,
                                isDevelopmentMode = isDevelopmentMode
                            )
                            val upTransaction: TransactionResponse = userTransactionsMap[upTransactionKey]!!

                            val upPreviousTransactionKey: UInt = getUpToAboveTransactionKey(

                                userTransactionsMap = userTransactionsMap.filterKeys { transactionId: UInt -> transactionId != upTransactionKey },
                                currentAccountId = fromAccount.id,
                                isDevelopmentMode = isDevelopmentMode
                            )
                            val upPreviousTransaction: TransactionResponse =
                                userTransactionsMap[upPreviousTransactionKey]!!
                            val newDateTime: String =
                                DateTimeUtils.subtract1SecondFromMySqlDateTimeInText(upPreviousTransaction.event_date_time)

                            if (isDevelopmentMode) {

                                println("upTransaction = $upTransaction")
                                println("upPreviousTransaction = $upPreviousTransaction")
                                println("newDateTime = $newDateTime")
                            }
                            val getAccountsFullResult: Result<AccountsResponse> =
                                ApiUtils.getAccountsFull(

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
                                        fromAccountId = userAccountsMap[upTransaction.from_account_id]!!.id,
                                        toAccountId = userAccountsMap[upTransaction.to_account_id]!!.id,
                                        isDateTimeUpdateOperation = true,
                                        isDevelopmentMode = isDevelopmentMode
                                    )
                                ) {
                                    userTransactionsMap[upTransactionKey]!!.event_date_time =
                                        DateTimeUtils.subtract1SecondFromMySqlDateTimeInText(upPreviousTransaction.event_date_time)
                                    userTransactionsMap = userTransactionsMap.toList()
                                        .sortedBy { (_: UInt, transaction: TransactionResponse) ->
                                            MysqlUtils.mySqlDateTimeTextToDateTime(
                                                mySqlDateTimeText = transaction.event_date_time
                                            ).data!!
                                        }
                                        .toMap() as LinkedHashMap<UInt, TransactionResponse>
                                }

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
                                furtherActionsOnFalse = { InteractiveUtils.invalidOptionMessage() }
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
                                furtherActionsOnFalse = { InteractiveUtils.invalidOptionMessage() }
                            )
                        ) {

                            return ViewTransactionsOutput(

                                output = "V",
                                addTransactionResult = addTransactionResult
                            )
                        }
                    }

                    else -> InteractiveUtils.invalidOptionMessage()
                }
            } while (true)
        }
    }

    private fun getUpTransactionKey(

        userTransactionsMap: Map<UInt, TransactionResponse>,
        currentAccountId: UInt,
        isDevelopmentMode: Boolean

    ) = getTransactionKey(

        userTransactionsMap = userTransactionsMap,
        currentAccountId = currentAccountId,
        transactionPrefix = "Up ",
        isDevelopmentMode = isDevelopmentMode
    )

    private fun getUpToAboveTransactionKey(

        userTransactionsMap: Map<UInt, TransactionResponse>,
        currentAccountId: UInt,
        isDevelopmentMode: Boolean

    ) = getTransactionKey(

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

    ) = getValidIndexWithInputPrompt(

        map = userTransactionsMap,
        itemSpecification = Constants.transactionText,
        items = TransactionUtils.userTransactionsToTextFromList(

            transactions = userTransactionsMap.values.toList(),
            currentAccountId = currentAccountId,
            isDevelopmentMode = isDevelopmentMode
        ),
        itemSpecificationPrefix = transactionPrefix,
        backValue = 0u
    )

    private fun isCallNotFromCheckAccounts(functionCallSource: FunctionCallSourceEnum): Boolean {

        return isCallNotFromCheckAccounts(

            functionCallSource = functionCallSource,
            furtherActionsOnFalse = { InteractiveUtils.invalidOptionMessage() })
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
        isDevelopmentMode: Boolean

    ) {
        print("Enter Account Index or 0 to Back : A")
        val userInputForAccountIndex: String = readln()
        if (userInputForAccountIndex != "0") {

            val getUserAccountsMapResult: IsOkModel<LinkedHashMap<UInt, AccountResponse>> =
                HandleResponses.getUserAccountsMap(
                    apiResponse = ApiUtils.getAccountsFull(

                        userId = userId,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                )

            CommonHandleResponses.isOkModelHandler(

                isOkModel = getUserAccountsMapResult,
                data = Unit,
                successActions = fun() {

                    val accountIndex: UInt = getValidIndexOrBack(

                        userInputForIndex = userInputForAccountIndex,
                        map = getUserAccountsMapResult.data!!,
                        itemSpecification = Constants.accountText,
                        items = AccountUtils.userAccountsToStringFromList(

                            accounts = getUserAccountsMapResult.data!!.values.toList()
                        ),
                        backValue = 0u
                    )
                    if (accountIndex != 0u) {

                        val selectedAccount: AccountResponse = getUserAccountsMapResult.data!![accountIndex]!!
                        viewTransactionsForAnAccount(

                            userId = userId,
                            username = username,
                            accountId = accountIndex,
                            accountFullName = selectedAccount.fullName,
                            functionCallSource = FunctionCallSourceEnum.FROM_VIEW_TRANSACTIONS_OF_AN_ACCOUNT,
                            previousTransactionData = previousTransactionData,
                            fromAccount = selectedAccount,
                            isConsoleMode = isConsoleMode,
                            isDevelopmentMode = isDevelopmentMode
                        )
                    }
                })
        }
    }
}
