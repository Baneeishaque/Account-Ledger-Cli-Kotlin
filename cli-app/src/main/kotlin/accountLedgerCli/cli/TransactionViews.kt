package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.AccountsResponse
import accountLedgerCli.api.response.TransactionResponse
import accountLedgerCli.api.response.TransactionsResponse
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.constants.Constants
import accountLedgerCli.enums.FunctionCallSourceEnum
import accountLedgerCli.enums.TransactionTypeEnum
import accountLedgerCli.enums.AccountTypeEnum
import accountLedgerCli.models.InsertTransactionResult
import accountLedgerCli.models.ViewTransactionsOutput
import accountLedgerCli.models.ChooseAccountResult
import accountLedgerCli.to_models.IsOkModel
import accountLedgerCli.to_utils.DateTimeUtils
import accountLedgerCli.to_utils.MysqlUtils
import accountLedgerCli.to_utils.ToDoUtils
import accountLedgerCli.to_utils.invalidOptionMessage
import accountLedgerCli.utils.AccountUtils
import accountLedgerCli.utils.ApiUtils
import accountLedgerCli.utils.TransactionUtils
import accountLedgerCli.utils.ChooseAccountUtils
import accountLedgerCli.to_utils.ApiUtils as CommonApiUtils
import accountLedgerCli.to_utils.HandleResponses as CommonHandleResponses

object TransactionViews {

    internal fun viewTransactionsForAnAccount(

        userId: UInt,
        username: String,
        accountId: UInt,
        accountFullName: String,
        functionCallSource: FunctionCallSourceEnum = FunctionCallSourceEnum.FROM_OTHERS,
        insertTransactionResult: InsertTransactionResult,
        fromAccount: AccountResponse,
        isDevelopmentMode: Boolean

    ): ViewTransactionsOutput {

        return viewTransactions(

            apiResponse = getUserTransactionsForAnAccount(

                userId = userId,
                accountId = accountId
            ),
            insertTransactionResult = insertTransactionResult,
            fromAccount = fromAccount,
            accountFullName = accountFullName,
            username = username,
            accountId = accountId,
            functionCallSource = functionCallSource,
            userId = userId,
            isDevelopmentMode = isDevelopmentMode
        )
    }

    internal fun viewTransactions(

        apiResponse: Result<TransactionsResponse>,
        insertTransactionResult: InsertTransactionResult,
        accountFullName: String,
        username: String,
        accountId: UInt,
        functionCallSource: FunctionCallSourceEnum,
        userId: UInt,
        fromAccount: AccountResponse,
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
                    isDevelopmentMode = isDevelopmentMode
                )
            })

        return viewTransactionsOutput
    }

    internal fun viewTransactions(

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
        isDevelopmentMode: Boolean

    ): ViewTransactionsOutput {

        var localUserTransactionsResponse: TransactionsResponse = userTransactionsResponse
        if (ApiUtils.isNoTransactionsResponseWithMessage(

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
                TransactionUtils.prepareUserTransactionsMap(transactions = localUserTransactionsResponse.transactions)

            var choice: String
            do {
                val userTransactionsText: String = TransactionUtils.userTransactionsToTextFromMap(

                    transactionsMap = userTransactionsMap,
                    currentAccountId = fromAccount.id
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
                            "0 - Back",
                            "",
                            "Enter Your Choice : "
                        )
                    }
                }
                commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(menuItems)

                choice = readLine()!!

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
                                furtherActionsOnFalse = { invalidOptionMessage() })
                        ) {

                            val transactionIndex: UInt = getValidIndexWithInputPrompt(

                                map = userTransactionsMap,
                                itemSpecification = Constants.transactionText,
                                items = userTransactionsText,
                                backValue = 0u
                            )

                            // TODO : Take Confirmation from the user
                            if (InsertOperations.deleteTransaction(transactionId = transactionIndex)) {

                                userTransactionsMap.remove(key = transactionIndex)
                            }
                        }
                    }

                    "2" -> {

                        if (isCallNotFromCheckAccounts(

                                functionCallSource = functionCallSource,
                                furtherActionsOnFalse = { invalidOptionMessage() })
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
                                    items = TransactionUtils.userTransactionsToTextFromMap(

                                        transactionsMap = reducedUserTransactionsMap,
                                        currentAccountId = fromAccount.id
                                    ),
                                    itemSpecificationPrefix = "End ",
                                    backValue = 0u
                                )

                                if (transactionEndIndex != 0u) {

                                    userTransactionsMap.filterKeys { transactionId: UInt ->
                                        transactionId in transactionStartIndex..transactionEndIndex
                                    }
                                        .forEach { transactionMapEntryForDelete: Map.Entry<UInt, TransactionResponse> ->

                                            if (InsertOperations.deleteTransaction(transactionId = transactionMapEntryForDelete.key)) {

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

                    "3", "5", "7", "8", "9", "11", "12", "13", "14" -> {

                        if (isCallNotFromCheckAccounts(

                                functionCallSource = functionCallSource,
                                furtherActionsOnFalse = { invalidOptionMessage() })
                        ) {

                            ToDoUtils.showTodo()
                        }
                    }

                    "4" -> {

                        if (isCallNotFromCheckAccounts(

                                functionCallSource = functionCallSource,
                                furtherActionsOnFalse = { invalidOptionMessage() })
                        ) {

                            val transactionIndex: UInt = getValidIndexWithInputPrompt(

                                map = userTransactionsMap,
                                itemSpecification = Constants.transactionText,
                                items = userTransactionsText,
                                backValue = 0u
                            )
                            val userAccountsMap: LinkedHashMap<UInt, AccountResponse> =
                                AccountUtils.prepareUserAccountsMap(

                                    accounts = ApiUtils.getAccountsFull(userId = userId).getOrNull()!!.accounts
                                )

                            val selectedTransaction: TransactionResponse = userTransactionsMap[transactionIndex]!!
                            val selectedTransactionDateTimeConversionResult: IsOkModel<String> =

                                MysqlUtils.dateTimeTextConversionWithMessage(dateTimeTextConversionFunction = fun(): IsOkModel<String> {

                                    return MysqlUtils.mySqlDateTimeTextToNormalDateTimeText(mySqlDateTimeText = selectedTransaction.event_date_time)
                                })
                            if (selectedTransactionDateTimeConversionResult.isOK) {

                                //TODO : Present Transaction
                                if(selectedTransaction.from_account_id == fromAccount.id){

                                    print("[${selectedTransaction.id}] [${selectedTransactionDateTimeConversionResult.data!!}]\t[${selectedTransaction.particulars}]\t[-${selectedTransaction.amount}]\t[${selectedTransaction.to_account_full_name}]")
                                }
                                else if(selectedTransaction.to_account_id == fromAccount.id){

                                    print("[${selectedTransaction.id}] [${selectedTransactionDateTimeConversionResult.data!!}]\t[${selectedTransaction.particulars}]\t[+${selectedTransaction.amount}]\t[${selectedTransaction.from_account_full_name}]")

                                }
                                else{

                                    //TODO : exceptional case
                                }

                                //TODO : Get User Confirmation

                                var localFromAccount: AccountResponse = userAccountsMap[selectedTransaction.from_account_id]!!
                                do {
                                    print("Do you want to change Withdraw A/C (Y/N) (Default : N) : ")
                                    when (readLine()!!) {
                                        "Y" -> {

                                            var chooseAccountResult: ChooseAccountResult =                                                    ChooseAccountUtils.chooseAccountById(
                                                            userId = userId,
                                                            accountType = AccountTypeEnum.FROM
                                                    )
                                            if(chooseAccountResult.chosenAccountId != 0u){

                                                localFromAccount = chooseAccountResult.chosenAccount!!
                                            }
                                            break
                                        }
                                        "N", "" -> {

                                            break
                                        }
                                        else -> invalidOptionMessage()
                                    }
                                } while (true)

                                var localToAccount: AccountResponse = userAccountsMap[selectedTransaction.to_account_id]!!
                                do {
                                    print("Do you want to change Deposit A/C (Y/N) (Default : N) : ")
                                    when (readLine()!!) {
                                        "Y" -> {

                                            var chooseAccountResult: ChooseAccountResult =                                                    ChooseAccountUtils.chooseAccountById(
                                                            userId = userId,
                                                            accountType = AccountTypeEnum.TO
                                                    )
                                            if(chooseAccountResult.chosenAccountId != 0u){

                                                localToAccount = chooseAccountResult.chosenAccount!!
                                            }
                                            break
                                        }
                                        "N", "" -> {

                                            break
                                        }
                                        else -> invalidOptionMessage()
                                    }
                                } while (true)

                                val updateTransactionResult: InsertTransactionResult =
                                    InsertOperations.addTransactionStep2(

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
                                        isEditStep = true
                                    )

                                // TODO : If from or to is not current account, remove the transaction
                                if((localFromAccount == fromAccount) || (localFromAccount == toAccount)){

                                    if(localFromAccount != fromAccount) {

                                        userTransactionsMap[transactionIndex]!!.from_account_id = localFromAccount.id
                                        userTransactionsMap[transactionIndex]!!.from_account_name = localFromAccount.name
                                        userTransactionsMap[transactionIndex]!!.from_account_full_name = localFromAccount.fullName
                                    }

                                    if(localToAccount != toAccount) {

                                        userTransactionsMap[transactionIndex]!!.to_account_id = localToAccount.id
                                        userTransactionsMap[transactionIndex]!!.to_account_name = localToAccount.name
                                        userTransactionsMap[transactionIndex]!!.to_account_full_name = localToAccount.fullName
                                    }

                                    // TODO : Change to MySQL Datetime format
                                    var toMySqlDateTimeConversionResult:IsOkModel<String> = MysqlUtils.normalDateTimeTextToMySqlDateTimeText(normalDateTimeText=updateTransactionResult.dateTimeInText)
                                    if(toMySqlDateTimeConversionResult.isOK){

                                        userTransactionsMap[transactionIndex]!!.event_date_time = toMySqlDateTimeConversionResult.data!!
                                    }
                                    else{

                                        // TODO : Exceptional Case
                                    }

                                    userTransactionsMap[transactionIndex]!!.particulars =
                                        updateTransactionResult.transactionParticulars
                                    userTransactionsMap[transactionIndex]!!.amount =
                                        updateTransactionResult.transactionAmount
                                }
                                else{

                                    userTransactionsMap.remove(key=transactionIndex)
                                }
                            }
                        }
                    }

                    "6" -> {

                        if (isCallNotFromCheckAccounts(

                                functionCallSource = functionCallSource,
                                furtherActionsOnFalse = { invalidOptionMessage() })
                        ) {
                            val upTransactionKey: UInt = getValidIndexWithInputPrompt(

                                map = userTransactionsMap,
                                itemSpecification = Constants.transactionText,
                                items = userTransactionsText,
                                itemSpecificationPrefix = "Up ",
                                backValue = 0u
                            )
                            var upPreviousTransactionKey: UInt = 0u
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
                                    DateTimeUtils.subtract1SecondFromMySqlDateTimeText(upPreviousTransaction.event_date_time)

                                if (App.isDevelopmentMode) {

                                    println("upTransaction = $upTransaction")
                                    println("upPreviousTransaction = $upPreviousTransaction")
                                    println("newDateTime = $newDateTime")
                                }
                                val getAccountsFullResult: Result<AccountsResponse> =
                                    ApiUtils.getAccountsFull(userId = userId)
                                if (getAccountsFullResult.isSuccess) {

                                    val userAccountsMap: LinkedHashMap<UInt, AccountResponse> =
                                        AccountUtils.prepareUserAccountsMap(

                                            accounts = getAccountsFullResult.getOrNull()!!.accounts
                                        )

                                    if (InsertOperations.updateTransaction(

                                            transactionId = upTransactionKey,
                                            eventDateTime = newDateTime,
                                            particulars = upTransaction.particulars,
                                            amount = upTransaction.amount,
                                            fromAccount = userAccountsMap[upTransaction.from_account_id]!!,
                                            toAccount = userAccountsMap[upTransaction.to_account_id]!!,
                                            isDateTimeUpdateOperation = true
                                        )
                                    ) {
                                        userTransactionsMap[upTransactionKey]!!.event_date_time =
                                            DateTimeUtils.subtract1SecondFromMySqlDateTimeText(upPreviousTransaction.event_date_time)
                                        userTransactionsMap = userTransactionsMap.toList()
                                            .sortedBy { (_: UInt, transaction: TransactionResponse) ->
                                                MysqlUtils.mySqlDateTimeTextToMySqlDateTime(
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
                                furtherActionsOnFalse = { invalidOptionMessage() })
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
                                    accountId = accountId
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

                    "0" -> {

                        return ViewTransactionsOutput(

                            output = "0",
                            addTransactionResult = addTransactionResult
                        )
                    }

                    "" -> {

                        if (isCallFromCheckAccounts(

                                functionCallSource = functionCallSource,
                                furtherActionsOnFalse = { invalidOptionMessage() }
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
                                furtherActionsOnFalse = { invalidOptionMessage() }
                            )
                        ) {

                            return ViewTransactionsOutput(

                                output = "V",
                                addTransactionResult = addTransactionResult
                            )
                        }
                    }

                    else -> invalidOptionMessage()
                }
            } while (true)
        }
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

    internal fun viewTransactionsOfInputAccount(

        userId: UInt,
        username: String,
        insertTransactionResult: InsertTransactionResult,
        isDevelopmentMode: Boolean

    ) {
        print("Enter Account Index or 0 to Back : A")
        val userInputForAccountIndex: String = readLine()!!
        if (userInputForAccountIndex != "0") {

            val getUserAccountsMapResult: IsOkModel<LinkedHashMap<UInt, AccountResponse>> =
                HandleResponses.getUserAccountsMap(apiResponse = ApiUtils.getAccountsFull(userId = userId))

            CommonHandleResponses.isOkModelHandler(

                isOkModel = getUserAccountsMapResult,
                data = Unit,
                successActions = fun() {

                    val accountIndex: UInt = getValidIndexOrBack(

                        userInputForIndex = userInputForAccountIndex,
                        map = getUserAccountsMapResult.data!!,
                        itemSpecification = Constants.accountText,
                        items = AccountUtils.userAccountsToStringFromLinkedHashMap(userAccountsMap = getUserAccountsMapResult.data),
                        backValue = 0u
                    )
                    if (accountIndex != 0u) {

                        val selectedAccount: AccountResponse = getUserAccountsMapResult.data[accountIndex]!!
                        viewTransactionsForAnAccount(

                            userId = userId,
                            username = username,
                            accountId = accountIndex,
                            accountFullName = selectedAccount.fullName,
                            functionCallSource = FunctionCallSourceEnum.FROM_VIEW_TRANSACTIONS_OF_AN_ACCOUNT,
                            insertTransactionResult = insertTransactionResult,
                            fromAccount = selectedAccount,
                            isDevelopmentMode = isDevelopmentMode
                        )
                    }
                })
        }
    }
}