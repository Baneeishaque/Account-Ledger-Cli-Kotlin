package accountLedgerCli.cli

import accountLedgerCli.api.response.*
import accountLedgerCli.retrofit.ResponseHolder
import accountLedgerCli.retrofit.data.AccountsDataSource
import accountLedgerCli.retrofit.data.TransactionDataSource
import accountLedgerCli.retrofit.data.TransactionsDataSource
import accountLedgerCli.retrofit.data.UserDataSource
import accountLedgerCli.to_utils.*
import accountLedgerCli.to_utils.DateTimeUtils.normalDateTimePattern
import accountLedgerCli.utils.AccountUtils
import accountLedgerCli.utils.ApiUtils
import accountLedgerCli.utils.ChooseAccountUtils

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import io.github.cdimascio.dotenv.dotenv

import kotlinx.coroutines.runBlocking
import java.nio.file.Paths

import java.time.LocalDateTime

internal var dateTimeString = LocalDateTime.now().format(normalDateTimePattern)

private var fromAccount = AccountUtils.getBlankAccount()
private var viaAccount = AccountUtils.getBlankAccount()
private var toAccount = AccountUtils.getBlankAccount()
private var transactionParticulars = ""
private var transactionAmount = 0F

private const val baneeWalletAccountId = 6
private const val baneeBankAccountId = 11
private const val baneeBankAccountName = "Punjab National Bank, Tirur"
private const val baneeFrequent1AccountId = 688
private const val baneeFrequent1AccountName = "Hisham Banee Ishaque K Brother"
private const val baneeFrequent2AccountId = 38
private const val baneeFrequent2AccountName = "Ismail K Father Banee Ishaque K"
private const val baneeFrequent3AccountId = 367
private const val baneeFrequent3AccountName = "Account Shortages"

private var userAccountsMap = LinkedHashMap<Int, AccountResponse>()
private val accountsResponseResult = AccountsResponse(1, listOf(AccountUtils.getBlankAccount()))
private val commandLinePrintMenu = CommandLinePrintMenu()
private val commandLinePrintMenuWithEnterPrompt =
    CommandLinePrintMenuWithEnterPrompt(commandLinePrintMenu)
private val commandLinePrintMenuWithTryPrompt =
    CommandLinePrintMenuWithTryPrompt(commandLinePrintMenu)
private val commandLinePrintMenuWithContinuePrompt =
    CommandLinePrintMenuWithContinuePrompt(commandLinePrintMenu)

fun main() {
    do {
        commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
            listOf(
                "Account Ledger",
                "---------------",
                "1 : Login",
                "2 : Registration",
                "0 : Exit",
                "",
                "Enter Your Choice : "
            )
        )
        val choice = readLine()
        when (choice) {
            "1" -> login()
            "2" -> register()
            "0" -> println("Thanks...")
            else -> invalidOptionMessage()
        }
    } while (choice != "0")
}

private fun login() {

//    println("Directory : ${Paths.get("").toAbsolutePath()}")
    println("\nAccount Ledger Authentication")
    println("--------------------------------")

    val dotenv = dotenv {
        directory = Paths.get("").toAbsolutePath().toString()
        ignoreIfMissing = true
    }
    var user = UserCredentials(dotenv["USER_NAME"] ?: "", dotenv["PASSWORD"] ?: "")
    if (user.username.isEmpty() || user.passcode.isEmpty()) {

        user = InputUtils.getUserCredentials()

    } else {
        do {
            println("The recognised user is ${user.username}")
            print("Do you want to continue (Y/N) : ")
            when (readLine().toString()) {
                "Y", "" -> {
                    break
                }
                "N" -> {
                    user = InputUtils.getUserCredentials()
                }
                else -> invalidOptionMessage()
            }

        } while (true)
    }

    val userDataSource = UserDataSource()
    println("Contacting Server...")
    val apiResponse: ResponseHolder<LoginResponse>
    runBlocking { apiResponse = userDataSource.selectUser(username = user.username, password = user.passcode) }
    // println("Response : $apiResponse")
    if (apiResponse.isError()) {

        println("Error : ${(apiResponse.getValue() as Exception).localizedMessage}")
        do {
            print("Retry (Y/N) ? : ")
            val input = readLine()
            when (input) {
                "Y", "" -> {
                    login()
                }
                "N" -> {
                }
                else -> invalidOptionMessage()
            }
        } while (input != "N")
    } else {

        val loginResponseResult = apiResponse.getValue() as LoginResponse
        when (loginResponseResult.userCount) {
            0 -> println("Invalid Credentials...")
            1 -> {

                println("Login Success...")
                userScreen(username = user.username, userId = loginResponseResult.id)
            }
            else -> println("Server Execution Error...")
        }
    }
}

private fun userScreen(username: String, userId: Int) {

    do {
        commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
            listOf(
                "\nUser : $username",
                "1 - List Accounts : Top Levels",
                "2 - Insert Quick Transaction On : Wallet",
                "3 - Insert Quick Transaction On : Wallet To : $baneeFrequent1AccountName",
                "4 - Insert Quick Transaction On : Wallet To : $baneeFrequent2AccountName",
                "5 - Insert Quick Transaction On : Wallet To : $baneeFrequent3AccountName",
                "6 - Insert Quick Transaction On : Bank : $baneeBankAccountName",
                "7 - Insert Quick Transaction On : Bank : $baneeBankAccountName To : $baneeFrequent1AccountName",
                "8 - Insert Quick Transaction On : Bank : $baneeBankAccountName To : $baneeFrequent2AccountName",
                "9 - Insert Quick Transaction On : Bank : $baneeBankAccountName To : $baneeFrequent3AccountName",
                "10 - Insert Quick Transaction On : $baneeFrequent1AccountName",
                "11 - Insert Quick Transaction On : $baneeFrequent2AccountName",
                "12 - Insert Quick Transaction On : $baneeFrequent3AccountName",
                "13 - List Accounts : Full Names",
                "14 - Import Transactions To : Bank : $baneeBankAccountName From CSV",
                "15 - Import Transactions To : Bank : $baneeBankAccountName From XLX",
                "16 - Check A/Cs affected after a specified date",
                "17 - View Transactions of a specific A/C",
                "0 - Logout",
                "",
                "Enter Your Choice : "
            )
        )
        val choice = readLine()
        when (choice) {
            "1" -> listAccountsTop(username = username, userId = userId)
            "2" -> insertQuickTransactionWallet(userId = userId, username = username)
            "3" -> insertQuickTransactionWalletToFrequent1(userId = userId, username = username)
            "4" -> insertQuickTransactionWalletToFrequent2(userId = userId, username = username)
            "5" -> insertQuickTransactionWalletToFrequent3(userId = userId, username = username)
            "6" -> insertQuickTransactionBank(userId = userId, username = username)
            "7" -> insertQuickTransactionBankToFrequent1(userId = userId, username = username)
            "8" -> insertQuickTransactionBankToFrequent2(userId = userId, username = username)
            "9" -> insertQuickTransactionBankToFrequent3(userId = userId, username = username)
            "10" -> insertQuickTransactionFrequent1(userId = userId, username = username)
            "11" -> insertQuickTransactionFrequent2(userId = userId, username = username)
            "12" -> insertQuickTransactionFrequent3(userId = userId, username = username)
            "13" -> listAccountsFull(username = username, userId = userId)
            "14" -> importBankFromCsv()
            "15" -> importBankFromXlx()
            "16" -> checkAccountsAffectedAfterSpecifiedDate(userId = userId, username = username)
            "17" -> viewTransactionsOfSpecificAccount(userId = userId, username = username)
            "0" -> {}
            else -> invalidOptionMessage()
        }
    } while (choice != "0")
}

private fun viewTransactionsOfSpecificAccount(userId: Int, username: String) {
    print("Enter Account Index or 0 to Back : A")
    val inputAccountIndex = readLine()!!
    if (inputAccountIndex != "0") {
        var accountIndex = InputUtils.getValidInt(inputAccountIndex, "Invalid Account Index")
        if (handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {
            accountIndex = getValidAccountIndex(userAccountsMap = userAccountsMap, accountId = accountIndex)
            if (accountIndex != 0) {
                viewTransactions(
                    userId = userId,
                    username = username,
                    accountId = accountIndex,
                    accountFullName = userAccountsMap[accountIndex]!!.fullName,
                    functionCallSource = FunctionCallSource.FROM_VIEW_TRANSACTIONS_OF_ACCOUNT
                )
            }
        }
    }
}

private fun checkAccountsAffectedAfterSpecifiedDate(userId: Int, username: String) {

    val inputDate = InputUtils.getValidDateInNormalPattern()
    val transactionsDataSource = TransactionsDataSource()
    println("Contacting Server...")
    val apiResponse: ResponseHolder<TransactionsResponse>
    val specifiedDate = MysqlUtils.normalDateStringToMysqlDateString(normalDateString = inputDate)
    if (specifiedDate.first) {
        runBlocking {
            apiResponse =
                transactionsDataSource.selectUserTransactionsAfterSpecifiedDate(
                    userId = userId,
                    specifiedDate = specifiedDate.second
                )
        }
        // println("Response : $apiResponse")
        if (apiResponse.isError()) {

            println("Error : ${(apiResponse.getValue() as Exception).localizedMessage}")
            do {
                print("Retry (Y/N) ? : ")
                val input = readLine()
                when (input) {
                    "Y", "" -> {
                        checkAccountsAffectedAfterSpecifiedDate(userId = userId, username = username)
                        return
                    }
                    "N" -> {
                    }
                    else -> invalidOptionMessage()
                }
            } while (input != "N")
        } else {

            val selectUserTransactionsAfterSpecifiedDateResult = apiResponse.getValue() as TransactionsResponse
            if (selectUserTransactionsAfterSpecifiedDateResult.status == 1) {

                println("No Transactions...")

            } else {

                val accounts = mutableMapOf<Int, String>()
                selectUserTransactionsAfterSpecifiedDateResult.transactions.forEach { transaction ->

                    accounts.putIfAbsent(transaction.from_account_id, transaction.from_account_full_name)
                    accounts.putIfAbsent(transaction.to_account_id, transaction.to_account_full_name)
                }
                println("Affected A/Cs : $accounts")
                for (account in accounts) {

                    when (viewTransactions(
                        userId = userId,
                        username = username,
                        accountId = account.key,
                        accountFullName = account.value,
                        functionCallSource = FunctionCallSource.FROM_CHECK_ACCOUNTS
                    )) {
                        "E", "0" -> {
                            break
                        }
                    }
                }
            }
        }
    }
}

private fun register() {

    ToDoUtils.showTodo()
}

private fun readCsv() {

    // @v Input file path from user
    // @cr Hardcoded file path
    val filePath = "E:\\To_DK\\4356XXXXXXXXX854522-08-2020.xls"
    csvReader().open(filePath) {
        readAllAsSequence().forEach { row ->
            // Do something
            println(row) // [a, b, c]
        }
    }
}

private fun importBankFromXlx() {

    ToDoUtils.showTodo()
}

private fun importBankFromCsv() {

    ToDoUtils.showTodo()
}

private fun insertQuickTransactionFrequent1(userId: Int, username: String) {

    if (handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeFrequent1AccountId]!!
        accountHome(userId = userId, username = username)
    }
}

private fun insertQuickTransactionFrequent2(userId: Int, username: String) {

    if (handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeFrequent2AccountId]!!
        accountHome(userId = userId, username = username)
    }
}

private fun insertQuickTransactionFrequent3(userId: Int, username: String) {

    if (handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeFrequent3AccountId]!!
        accountHome(userId = userId, username = username)
    }
}

private fun insertQuickTransactionBank(userId: Int, username: String) {

    if (handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeBankAccountId]!!
        accountHome(userId = userId, username = username)
    }
}

private fun insertQuickTransactionBankToFrequent1(userId: Int, username: String) {

    if (handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeBankAccountId]!!
        toAccount = userAccountsMap[baneeFrequent1AccountId]!!
        transactionContinueCheck(userId = userId, username = username, transactionType = TransactionType.NORMAL)
    }
}

private fun insertQuickTransactionBankToFrequent2(userId: Int, username: String) {

    if (handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeBankAccountId]!!
        toAccount = userAccountsMap[baneeFrequent2AccountId]!!
        transactionContinueCheck(userId = userId, username = username, transactionType = TransactionType.NORMAL)
    }
}

private fun insertQuickTransactionBankToFrequent3(userId: Int, username: String) {

    if (handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeBankAccountId]!!
        toAccount = userAccountsMap[baneeFrequent3AccountId]!!
        transactionContinueCheck(userId = userId, username = username, transactionType = TransactionType.NORMAL)
    }
}

private fun listAccountsFull(username: String, userId: Int) {

    handleAccountsResponseAndPrintMenu(
        apiResponse = ApiUtils.getAccountsFull(userId = userId), username = username, userId = userId
    )
}

private fun handleAccountsResponseAndPrintMenu(
    apiResponse: ResponseHolder<AccountsResponse>,
    username: String,
    userId: Int
) {

    if (handleAccountsResponse(apiResponse)) {

        do {
            commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                listOf(
                    "\nUser : $username",
                    "Accounts",
                    userAccountsToStringFromLinkedHashMapLimitedTo10(
                        userAccountsMap = userAccountsMap
                    ),
                    "1 - Choose Account - By Index Number",
                    "2 - Choose Account - By Search",
                    "3 - Add Account",
                    "0 - Back",
                    "",
                    "Enter Your Choice : "
                )
            )

            val choice =
                processChildAccountScreenInput(
                    userAccountsMap = userAccountsMap, userId = userId, username = username
                )
        } while (choice != "0")
    }
}

private fun insertQuickTransactionWallet(userId: Int, username: String) {

    if (handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeWalletAccountId]!!
        accountHome(userId = userId, username = username)
    }
}

private fun insertQuickTransactionWalletToFrequent1(userId: Int, username: String) {

    if (handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeWalletAccountId]!!
        toAccount = userAccountsMap[baneeFrequent1AccountId]!!
        transactionContinueCheck(userId = userId, username = username, transactionType = TransactionType.NORMAL)
    }
}

private fun insertQuickTransactionWalletToFrequent2(userId: Int, username: String) {

    if (handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeWalletAccountId]!!
        toAccount = userAccountsMap[baneeFrequent2AccountId]!!
        transactionContinueCheck(userId = userId, username = username, transactionType = TransactionType.NORMAL)
    }
}

private fun insertQuickTransactionWalletToFrequent3(userId: Int, username: String) {

    if (handleAccountsResponse(ApiUtils.getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeWalletAccountId]!!
        toAccount = userAccountsMap[baneeFrequent3AccountId]!!
        transactionContinueCheck(userId = userId, username = username, transactionType = TransactionType.NORMAL)
    }
}

private fun listAccountsTop(username: String, userId: Int) {

    handleAccountsResponseAndPrintMenu(
        apiResponse = getAccounts(userId = userId), username = username, userId = userId
    )
}

private fun handleAccountsResponse(apiResponse: ResponseHolder<AccountsResponse>): Boolean {

    if (apiResponse.isError()) {

        println("Error : ${(apiResponse.getValue() as Exception).localizedMessage}")
        //        do {
        //            print("Retry (Y/N) ? : ")
        //            val input = readLine()
        //            when (input) {
        //                "Y", "" -> {
        //                    return handleAccountsResponse(apiResponse)
        //                }
        //                "N" -> {
        //                }
        //                else -> println("Invalid option, try again...")
        //            }
        //        } while (input != "N")
        return false
    } else {

        val localAccountsResponseWithStatus = apiResponse.getValue() as AccountsResponse
        return if (localAccountsResponseWithStatus.status == 1) {

            println("No Accounts...")
            false

        } else {

            userAccountsMap = AccountUtils.prepareUserAccountsMap(localAccountsResponseWithStatus.accounts)
            true
        }
    }
}

private fun getAccounts(userId: Int, parentAccountId: Int = 0): ResponseHolder<AccountsResponse> {

    val apiResponse: ResponseHolder<AccountsResponse>
    val userAccountsDataSource = AccountsDataSource()
    println("Contacting Server...")
    runBlocking {
        apiResponse =
            userAccountsDataSource.selectUserAccounts(
                userId = userId, parentAccountId = parentAccountId
            )
    }
    //    println("Response : $apiResponse")
    return apiResponse
}

private fun userAccountsToStringFromList(accounts: List<AccountResponse>): String {

    var result = ""
    accounts.forEach { account -> result += "A${account.id} - ${account.name}\n" }
    return result
}

private fun addAccount() {

    // Use all accounts for general account addition, or from account for child account addition
    ToDoUtils.showTodo()
}

private fun chooseAccountByIndex(userAccountsMap: LinkedHashMap<Int, AccountResponse>): Int {

    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
        listOf(
            "\nAccounts",
            userAccountsToStringFromLinkedHashMap(userAccountsMap = userAccountsMap),
            "Enter Account Index, or O to back : A"
        )
    )
    val accountIdInput = readLine()!!
    if (accountIdInput == "0") return 0
    val accountId = InputUtils.getValidInt(accountIdInput, "Invalid Account Index")
    return getValidAccountIndex(userAccountsMap = userAccountsMap, accountId = accountId)
}

private fun getValidAccountIndex(userAccountsMap: LinkedHashMap<Int, AccountResponse>, accountId: Int): Int {
    if (userAccountsMap.containsKey(accountId)) {

        return accountId
    }
    commandLinePrintMenuWithTryPrompt.printMenuWithTryPromptFromListOfCommands(
        listOf("Invalid Account Index, Try again ? (Y/N) : ")
    )
    return when (readLine()) {
        "Y", "" -> {
            getValidAccountIndex(userAccountsMap = userAccountsMap, accountId = accountId)
        }
        "N" -> {
            0
        }
        else -> {

            commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                listOf("Invalid Entry...")
            )
            getValidAccountIndex(userAccountsMap = userAccountsMap, accountId = accountId)
        }
    }
}

private fun accountHome(userId: Int, username: String) {

    do {
        commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
            listOf(
                "\nUser : $username",
                "Account - ${fromAccount.fullName}",
                "1 - View Transactions",
                "2 - Add Transaction",
                "3 - View Child Accounts",
                "4 - Add Via. Transaction",
                "5 - Add Two Way Transaction",
                "0 - Back",
                "",
                "Enter Your Choice : "
            )
        )
        val choiceInput = readLine()
        when (choiceInput) {
            "1" ->
                viewTransactions(
                    userId = userId,
                    username = username,
                    accountId = fromAccount.id,
                    accountFullName = fromAccount.fullName
                )
            "2" -> addTransaction(userId = userId, username = username, transactionType = TransactionType.NORMAL)
            "3" ->
                viewChildAccounts(
                    userId = userId,
                    username = username,
                )
            "4" -> addTransaction(userId = userId, username = username, transactionType = TransactionType.VIA)
            "5" -> addTransaction(userId = userId, username = username, transactionType = TransactionType.TWO_WAY)
            "0" -> {}
            else -> invalidOptionMessage()
        }
    } while (choiceInput != "0")
}

private fun viewChildAccounts(username: String, userId: Int) {

    val apiResponse = getAccounts(userId = userId, parentAccountId = fromAccount.id)

    if (apiResponse.isError()) {

        println("Error : ${(apiResponse.getValue() as Exception).localizedMessage}")
        do {
            print("Retry (Y/N) ? : ")
            val input = readLine()
            when (input) {
                "Y", "" -> {
                    viewChildAccounts(username = username, userId = userId)
                    return
                }
                "N" -> {
                }
                else -> invalidOptionMessage()
            }
        } while (input != "N")
    } else {

        val accountsResponseResult = apiResponse.getValue() as AccountsResponse
        if (accountsResponseResult.status == 1) {

            println("No Child Accounts...")
        } else {

            val userAccountsMap = LinkedHashMap<Int, AccountResponse>()
            accountsResponseResult.accounts.forEach { currentAccount ->
                userAccountsMap[currentAccount.id] = currentAccount
            }
            do {
                commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                    listOf(
                        "\nUser : $username",
                        "${fromAccount.fullName} - Child Accounts",
                        userAccountsToStringFromList(
                            accounts = accountsResponseResult.accounts
                        ),
                        "1 - Choose Account - By Index Number",
                        "2 - Choose Account - By Search",
                        "3 - Add Child Account",
                        "0 - Back",
                        "",
                        "Enter Your Choice : "
                    )
                )

                val choice = processChildAccountScreenInput(userAccountsMap, userId, username)
            } while (choice != "0")
        }
    }
}

private fun processChildAccountScreenInput(
    userAccountsMap: LinkedHashMap<Int, AccountResponse>,
    userId: Int,
    username: String
): String? {

    val choice = readLine()
    when (choice) {
        "1" -> {
            handleFromAccountSelection(
                accountId = chooseAccountByIndex(userAccountsMap = userAccountsMap),
                userAccountsMap = userAccountsMap,
                userId = userId,
                username = username
            )
        }
        "2" -> {
            handleFromAccountSelection(
                accountId = searchAccount(userAccountsMap = userAccountsMap),
                userAccountsMap = userAccountsMap,
                userId = userId,
                username = username
            )
        }
        "3" -> addAccount()
        "0" -> {
        }
        else -> invalidOptionMessage()
    }
    return choice
}

private fun handleFromAccountSelection(
    accountId: Int,
    userAccountsMap: LinkedHashMap<Int, AccountResponse>,
    userId: Int,
    username: String
) {

    if (accountId != 0) {

        fromAccount = userAccountsMap[accountId]!!
        accountHome(userId = userId, username = username)
    }
}

private fun addTransaction(userId: Int, username: String, transactionType: TransactionType) {

    do {
        var menuItems = listOf(
            "\nUser : $username",
            "Transaction Type : $transactionType",
            "From Account - ${fromAccount.id} : ${fromAccount.fullName}"
        )
        if (transactionType == TransactionType.VIA) {
            menuItems = menuItems + listOf(
                "Via. Account - ${viaAccount.id} : ${viaAccount.fullName}"
            )
        }
        menuItems = menuItems + listOf(
            "To Account - ${toAccount.id} : ${toAccount.fullName}",
            "1 - Choose To Account From List - Top Levels",
            "2 - Choose To Account From List - Full Names",
            "3 - Input To Account ID Directly",
            "4 - Choose From Account From List - Top Levels",
            "5 - Choose From Account From List - Full Names",
            "6 - Input From Account ID Directly",
            "7 - Continue Transaction"
        )
        if (transactionType == TransactionType.VIA) {
            menuItems = menuItems + listOf(
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
            menuItems = menuItems + listOf(
                "8 - Exchange Accounts",
                "9 - Exchange Accounts, Then Continue Transaction",
            )
        }
        menuItems = menuItems + listOf(
            "0 - Back",
            "",
            "Enter Your Choice : "
        )
        commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
            menuItems
        )
        val choice = readLine()
        when (choice) {
            "1" -> {
                if (chooseDepositTop(userId)) {

                    transactionContinueCheck(userId, username, transactionType)
                    return
                }
            }
            "2" -> {
                if (chooseDepositFull(userId)) {

                    transactionContinueCheck(userId, username, transactionType)
                    return
                }
            }
            "3" -> {

                val chooseAccountResult = ChooseAccountUtils.chooseAccountById(userId)
                if (chooseAccountResult.choosedAccountId != 0) {

                    toAccount = chooseAccountResult.choosedAccount
                    transactionContinueCheck(userId, username, transactionType)
                    return
                }
            }
            "4" -> {
                if (chooseFromAccountTop(userId)) {

                    transactionContinueCheck(userId, username, transactionType)
                    return
                }
            }
            "5" -> {
                if (chooseFromAccountFull(userId)) {

                    transactionContinueCheck(userId, username, transactionType)
                    return
                }
            }
            "6" -> {
                val chooseAccountResult = ChooseAccountUtils.chooseAccountById(userId)
                if (chooseAccountResult.choosedAccountId != 0) {

                    fromAccount = chooseAccountResult.choosedAccount
                    transactionContinueCheck(userId, username, transactionType)
                    return
                }
            }
            "7" -> {

                transactionContinueCheck(userId, username, transactionType)
                return
            }
            "8" -> {

                if (transactionType == TransactionType.VIA) {

                    exchangeFromAndViaAccounts()

                } else {

                    exchangeFromAndToAccounts()
                }
                addTransaction(userId = userId, username = username, transactionType = transactionType)
                return
            }
            "9" -> {

                if (transactionType == TransactionType.VIA) {

                    exchangeFromAndViaAccounts()

                } else {

                    exchangeFromAndToAccounts()
                }
                transactionContinueCheck(userId, username, transactionType)
                return
            }
            "10" -> {
                if (transactionType == TransactionType.VIA) {

                    exchangeToAndViaAccounts()
                    addTransaction(userId = userId, username = username, transactionType = transactionType)
                    return

                } else {
                    invalidOptionMessage()
                }
            }
            "11" -> {
                if (transactionType == TransactionType.VIA) {

                    exchangeToAndViaAccounts()
                    transactionContinueCheck(userId, username, transactionType)
                    return

                } else {
                    invalidOptionMessage()
                }
            }
            "12" -> {
                if (transactionType == TransactionType.VIA) {

                    exchangeFromAndToAccounts()
                    addTransaction(userId = userId, username = username, transactionType = transactionType)
                    return

                } else {
                    invalidOptionMessage()
                }
            }
            "13" -> {
                if (transactionType == TransactionType.VIA) {

                    exchangeFromAndToAccounts()
                    transactionContinueCheck(userId, username, transactionType)
                    return

                } else {
                    invalidOptionMessage()
                }
            }
            "14" -> {
                if (transactionType == TransactionType.VIA) {

                    ToDoUtils.showTodo()
                    return

                } else {
                    invalidOptionMessage()
                }
            }
            "15" -> {
                if (transactionType == TransactionType.VIA) {

                    if (chooseViaAccountFull(userId)) {

                        transactionContinueCheck(userId, username, TransactionType.VIA)
                        return
                    }
                } else {
                    invalidOptionMessage()
                }
            }
            "16" -> {
                if (transactionType == TransactionType.VIA) {

                    val chooseAccountResult = ChooseAccountUtils.chooseAccountById(userId)
                    if (chooseAccountResult.choosedAccountId != 0) {

                        viaAccount = chooseAccountResult.choosedAccount
                        transactionContinueCheck(userId, username, transactionType)
                        return
                    }
                } else {
                    invalidOptionMessage()
                }
            }
            "0" -> {}
            else -> invalidOptionMessage()
        }
    } while (choice != "0")
}

private fun transactionContinueCheck(
    userId: Int,
    username: String,
    transactionType: TransactionType
) {

    do {

        var menuItems = listOf(
            "\nUser : $username",
            "From Account - ${fromAccount.id} : ${fromAccount.fullName}",
        )
        if (transactionType == TransactionType.VIA) {
            menuItems = menuItems + listOf(
                "Via. Account - ${viaAccount.id} : ${viaAccount.fullName}",
            )
        }
        menuItems = menuItems + listOf(
            "To Account - ${toAccount.id} : ${toAccount.fullName}",
            "",
            "Continue (Y/N) : "
        )
        commandLinePrintMenuWithContinuePrompt.printMenuWithContinuePromptFromListOfCommands(menuItems)

        val input = readLine()
        when (input) {
            "Y", "" -> {

                addTransactionWithAccountAvailabilityCheck(
                    userId = userId,
                    username = username,
                    transactionType = transactionType
                )
                return
            }
            "N" -> {
            }
            else -> invalidOptionMessage()
        }
    } while (input != "N")
}

private fun addTransactionWithAccountAvailabilityCheck(
    userId: Int,
    username: String,
    transactionType: TransactionType
) {

    if (isAccountsAreAvailable(transactionType)) {

        if (transactionType == TransactionType.VIA) {

            if (addTransactionStep2(
                    userId = userId,
                    username = username,
                    localFromAccount = fromAccount,
                    localToAccount = viaAccount,
                    transactionType = transactionType,
                    localViaAccount = AccountUtils.getBlankAccount()
                )
            ) {
                dateTimeString =
                    ((LocalDateTime.parse(dateTimeString, normalDateTimePattern) as LocalDateTime)
                        .plusMinutes(5) as
                            LocalDateTime)
                        .format(normalDateTimePattern)

                if (addTransactionStep2(
                        userId = userId,
                        username = username,
                        localFromAccount = viaAccount,
                        localToAccount = toAccount,
                        transactionType = transactionType,
                        localViaAccount = AccountUtils.getBlankAccount(),
                        isViaStep = true
                    )
                ) {
                    dateTimeString =
                        ((LocalDateTime.parse(dateTimeString, normalDateTimePattern) as LocalDateTime)
                            .plusMinutes(5) as
                                LocalDateTime)
                            .format(normalDateTimePattern)
                }
            }
        } else if (transactionType == TransactionType.NORMAL) {

            if (addTransactionStep2(
                    userId = userId,
                    username = username,
                    localFromAccount = fromAccount,
                    localToAccount = toAccount,
                    transactionType = transactionType,
                    localViaAccount = AccountUtils.getBlankAccount()
                )
            ) {
                dateTimeString =
                    ((LocalDateTime.parse(dateTimeString, normalDateTimePattern) as LocalDateTime)
                        .plusMinutes(5) as
                            LocalDateTime)
                        .format(normalDateTimePattern)
            }
        } else if (transactionType == TransactionType.TWO_WAY) {

            if (addTransactionStep2(
                    userId = userId,
                    username = username,
                    localFromAccount = fromAccount,
                    localToAccount = toAccount,
                    transactionType = transactionType,
                    localViaAccount = AccountUtils.getBlankAccount()
                )
            ) {
                dateTimeString =
                    ((LocalDateTime.parse(dateTimeString, normalDateTimePattern) as LocalDateTime)
                        .plusMinutes(5) as
                            LocalDateTime)
                        .format(normalDateTimePattern)

                if (addTransactionStep2(
                        userId = userId,
                        username = username,
                        localFromAccount = toAccount,
                        localToAccount = fromAccount,
                        transactionType = transactionType,
                        localViaAccount = AccountUtils.getBlankAccount(),
                        isTwoWayStep = true
                    )
                ) {
                    dateTimeString =
                        ((LocalDateTime.parse(dateTimeString, normalDateTimePattern) as LocalDateTime)
                            .plusMinutes(5) as
                                LocalDateTime)
                            .format(normalDateTimePattern)
                }
            }
        }
    } else {

        addTransaction(userId = userId, username = username, transactionType = transactionType)
    }
}

private fun isAccountsAreAvailable(transactionType: TransactionType): Boolean {

    if (toAccount.id == 0) {

        println("Please choose deposit account...")
        return false

    } else if (fromAccount.id == 0) {

        println("Please choose from account...")
        return false

    } else if ((transactionType == TransactionType.VIA) && (viaAccount.id == 0)) {

        println("Please choose via. account...")
        return false
    }
    return true
}

private fun addTransactionStep2(
    userId: Int,
    username: String,
    localFromAccount: AccountResponse,
    localToAccount: AccountResponse,
    transactionType: TransactionType,
    localViaAccount: AccountResponse,
    isViaStep: Boolean = false,
    isTwoWayStep: Boolean = false
): Boolean {

    var menuItems = listOf(
        "\nUser : $username",
        "Withdraw Account - ${fromAccount.id} : ${fromAccount.fullName}",
    )
    if (transactionType == TransactionType.VIA) {
        menuItems = menuItems + listOf(
            "Intermediate Account - ${viaAccount.id} : ${viaAccount.fullName}",
        )
    }
    menuItems = menuItems + listOf(
        "Deposit Account - ${toAccount.id} : ${toAccount.fullName}",
    )
    if (isViaStep || isTwoWayStep) {

        return invokeAutomatedInsertTransaction(
            userId = userId,
            eventDateTime = dateTimeString,
            particulars = transactionParticulars,
            amount = transactionAmount,
            localFromAccount = localFromAccount,
            localToAccount = localToAccount
        )

    } else {

        menuItems = menuItems + listOf(
            // TODO : Complete back
            "Enter Time : "
        )
        commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(menuItems)
        when (val inputDateTimeString = enterDateWithTime(transactionType = transactionType)) {
            "D+Tr" -> {

                dateTimeString =
                    DateTimeUtils.add1DayWith9ClockTimeToDateTimeString(
                        dateTimeString = dateTimeString
                    )
                return invokeAddTransactionStep2(
                    userId = userId,
                    username = username,
                    localFromAccount = localFromAccount,
                    localToAccount = localToAccount,
                    transactionType = transactionType,
                    localViaAccount = localViaAccount
                )
            }
            "D+" -> {

                dateTimeString = DateTimeUtils.add1DayToDateTimeString(dateTimeString = dateTimeString)
                return invokeAddTransactionStep2(
                    userId = userId,
                    username = username,
                    localFromAccount = localFromAccount,
                    localToAccount = localToAccount,
                    transactionType = transactionType,
                    localViaAccount = localViaAccount
                )
            }
            "D2+Tr" -> {

                dateTimeString =
                    DateTimeUtils.add2DaysWith9ClockTimeToDateTimeString(
                        dateTimeString = dateTimeString
                    )
                return invokeAddTransactionStep2(
                    userId = userId,
                    username = username,
                    localFromAccount = localFromAccount,
                    localToAccount = localToAccount,
                    transactionType = transactionType,
                    localViaAccount = localViaAccount
                )
            }
            "D2+" -> {

                dateTimeString = DateTimeUtils.add2DaysToDateTimeString(dateTimeString = dateTimeString)
                return invokeAddTransactionStep2(
                    userId = userId,
                    username = username,
                    localFromAccount = localFromAccount,
                    localToAccount = localToAccount,
                    transactionType = transactionType,
                    localViaAccount = localViaAccount
                )
            }
            "Ex" -> {

                return ex13(userId, username, localFromAccount, localToAccount, transactionType, localViaAccount)
            }
            "Ex13" -> {

                return ex13(userId, username, localFromAccount, localToAccount, transactionType, localViaAccount)
            }
            "Ex12" -> {

                return ex12(userId, username, localFromAccount, localToAccount, transactionType, localViaAccount)
            }
            "Ex23" -> {

                return ex23(userId, username, localFromAccount, localToAccount, transactionType, localViaAccount)
            }
            "B" -> {

                return false
            }
            else -> {

                dateTimeString = inputDateTimeString

                print("Enter Particulars (Current Value - $transactionParticulars): ")
                // TODO : Back to fields, or complete back
                val transactionParticularsInput = readLine()!!
                if (transactionParticularsInput.isNotEmpty()) {

                    transactionParticulars = transactionParticularsInput
                }

                print("Enter Amount (Current Value - $transactionAmount) : ")
                val transactionAmountInput = readLine()!!
                if (transactionAmountInput.isNotEmpty()) {

                    transactionAmount =
                        InputUtils.getValidFloat(transactionAmountInput, "Invalid Amount : Try Again")
                }

                do {
                    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                        listOf(
                            "\nTime - $dateTimeString",
                            "Account - ${localFromAccount.id} : ${localFromAccount.fullName}",
                            "Deposit Account - ${localToAccount.id} : ${localToAccount.fullName}",
                            "Particulars - $transactionParticulars",
                            "Amount - $transactionAmount",
                            "\nCorrect ? (Y/N),${if (transactionType == TransactionType.VIA) " Ex12 to exchange From & Via A/Cs, Ex23 to exchange Via & To A/Cs, Ex13 to exchange From & To A/Cs" else " Ex to exchange From & To A/Cs"} or B to back : "
                        )
                    )
                    val isCorrect = readLine()
                    when (isCorrect) {
                        "Y", "" -> {
                            return invokeInsertTransaction(
                                userId = userId,
                                eventDateTime = dateTimeString,
                                particulars = transactionParticulars,
                                amount = transactionAmount,
                                localFromAccount = localFromAccount,
                                localToAccount = localToAccount
                            )
                        }
                        // TODO : Back to fields
                        "N" ->
                            return invokeAddTransactionStep2(
                                userId = userId,
                                username = username,
                                localFromAccount = localFromAccount,
                                localToAccount = localToAccount,
                                transactionType = transactionType,
                                localViaAccount = localViaAccount
                            )
                        "Ex" -> {

                            if (transactionType == TransactionType.NORMAL) {
                                return ex13(
                                    userId,
                                    username,
                                    localFromAccount,
                                    localToAccount,
                                    transactionType,
                                    localViaAccount
                                )
                            } else {
                                invalidOptionMessage()
                            }
                        }
                        "Ex13" -> {
                            if (transactionType == TransactionType.VIA) {
                                return ex13(
                                    userId,
                                    username,
                                    localFromAccount,
                                    localToAccount,
                                    transactionType,
                                    localViaAccount
                                )
                            } else {
                                invalidOptionMessage()
                            }
                        }
                        "Ex12" -> {

                            if (transactionType == TransactionType.VIA) {
                                return ex12(
                                    userId,
                                    username,
                                    localFromAccount,
                                    localToAccount,
                                    transactionType,
                                    localViaAccount
                                )
                            } else {
                                invalidOptionMessage()
                            }
                        }
                        "Ex23" -> {

                            if (transactionType == TransactionType.VIA) {
                                return ex23(
                                    userId,
                                    username,
                                    localFromAccount,
                                    localToAccount,
                                    transactionType,
                                    localViaAccount
                                )
                            } else {
                                invalidOptionMessage()
                            }
                        }
                        else -> invalidOptionMessage()
                    }
                } while (isCorrect != "B")
            }
        }
        return false
    }
}

private fun invokeAutomatedInsertTransaction(
    userId: Int,
    eventDateTime: String,
    particulars: String,
    amount: Float,
    localFromAccount: AccountResponse,
    localToAccount: AccountResponse,
): Boolean {
    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
        listOf(
            "\nTime - $eventDateTime",
            "Withdraw Account - ${localFromAccount.id} : ${localFromAccount.fullName}",
            "Deposit Account - ${localToAccount.id} : ${localToAccount.fullName}",
            "Particulars - $particulars",
            "Amount - $amount"
        )
    )
    return invokeInsertTransaction(
        userId = userId, eventDateTime = eventDateTime, particulars = particulars,
        amount = amount, localFromAccount = localFromAccount, localToAccount = localToAccount
    )
}

private fun ex23(
    userId: Int,
    username: String,
    localFromAccount: AccountResponse,
    localToAccount: AccountResponse,
    transactionType: TransactionType,
    localViaAccount: AccountResponse
): Boolean {
    exchangeToAndViaAccounts()
    return invokeAddTransactionStep2(
        userId = userId,
        username = username,
        localFromAccount = localFromAccount,
        localToAccount = localToAccount,
        transactionType = transactionType,
        localViaAccount = localViaAccount
    )
}

private fun ex12(
    userId: Int,
    username: String,
    localFromAccount: AccountResponse,
    localToAccount: AccountResponse,
    transactionType: TransactionType,
    localViaAccount: AccountResponse
): Boolean {
    exchangeFromAndViaAccounts()
    return invokeAddTransactionStep2(
        userId = userId,
        username = username,
        localFromAccount = localFromAccount,
        localToAccount = localToAccount,
        transactionType = transactionType,
        localViaAccount = localViaAccount
    )
}

private fun ex13(
    userId: Int,
    username: String,
    localFromAccount: AccountResponse,
    localToAccount: AccountResponse,
    transactionType: TransactionType,
    localViaAccount: AccountResponse
): Boolean {
    exchangeFromAndToAccounts()
    return invokeAddTransactionStep2(
        userId = userId,
        username = username,
        localFromAccount = localFromAccount,
        localToAccount = localToAccount,
        transactionType = transactionType,
        localViaAccount = localViaAccount
    )
}

private fun invokeInsertTransaction(
    userId: Int,
    eventDateTime: String,
    particulars: String,
    amount: Float,
    localFromAccount: AccountResponse,
    localToAccount: AccountResponse
): Boolean {
    if (insertTransaction(
            userid = userId,
            eventDateTime = eventDateTime,
            particulars = particulars,
            amount = amount,
            localFromAccount = localFromAccount,
            localToAccount = localToAccount
        )
    ) {
        return true
    }
    return false
}

private fun invokeAddTransactionStep2(
    userId: Int,
    username: String,
    localFromAccount: AccountResponse,
    localToAccount: AccountResponse,
    transactionType: TransactionType,
    localViaAccount: AccountResponse
): Boolean {
    return addTransactionStep2(
        userId = userId,
        username = username,
        localFromAccount = localFromAccount,
        localToAccount = localToAccount,
        transactionType = transactionType,
        localViaAccount = localViaAccount
    )
}

private fun exchangeFromAndToAccounts() {

    val tempAccount = fromAccount
    fromAccount = toAccount
    toAccount = tempAccount
}

private fun exchangeFromAndViaAccounts() {

    val tempAccount = fromAccount
    fromAccount = viaAccount
    viaAccount = tempAccount
}

private fun exchangeToAndViaAccounts() {

    val tempAccount = toAccount
    toAccount = viaAccount
    viaAccount = tempAccount
}

private fun enterDateWithTime(transactionType: TransactionType): String {

    print(
        "$dateTimeString Correct? (Y/N), D+Tr to increase 1 Day with Time Reset, D+ to increase 1 Day, D2+Tr to increase 2 Days with Time Reset, D2+ to increase 2 Days,${if (transactionType == TransactionType.VIA) " Ex12 to exchange From & Via A/Cs, Ex23 to exchange Via & To A/Cs, Ex13 to exchange From & To A/Cs" else " Ex to exchange From & To A/Cs"} or B to Back : "
    )
    when (readLine()) {
        "Y", "" -> {

            return dateTimeString
        }
        "N" -> {

            return InputUtils.getValidDateTimeInNormalPattern()
        }
        "D+Tr" -> {

            return "D+Tr"
        }
        "D+" -> {

            return "D+"
        }
        "D2+Tr" -> {

            return "D2+Tr"
        }
        "D2+" -> {

            return "D2+"
        }
        "Ex" -> {

            if (transactionType == TransactionType.VIA) {
                invalidOptionMessage()
                return enterDateWithTime(transactionType = transactionType)
            }
            return "Ex"
        }
        "Ex12" -> {

            if (transactionType != TransactionType.VIA) {
                invalidOptionMessage()
                return enterDateWithTime(transactionType = transactionType)
            }
            return "Ex12"
        }
        "Ex23" -> {

            if (transactionType != TransactionType.VIA) {
                invalidOptionMessage()
                return enterDateWithTime(transactionType = transactionType)
            }
            return "Ex23"
        }
        "Ex13" -> {

            if (transactionType != TransactionType.VIA) {
                invalidOptionMessage()
                return enterDateWithTime(transactionType = transactionType)
            }
            return "Ex13"
        }
        "B" -> {

            return "B"
        }
        else -> {

            invalidOptionMessage()
            return enterDateWithTime(transactionType = transactionType)
        }
    }
}

private fun insertTransaction(
    userid: Int,
    eventDateTime: String,
    particulars: String,
    amount: Float,
    localFromAccount: AccountResponse,
    localToAccount: AccountResponse
): Boolean {

    val apiResponse: ResponseHolder<InsertionResponse>
    val userTransactionDataSource = TransactionDataSource()
    println("Contacting Server...")
    val eventDateTimeConversionResult =
        MysqlUtils.normalDateTimeStringToMysqlDateTimeString(normalDateTimeString = eventDateTime)
    if (eventDateTimeConversionResult.first) {
        runBlocking {
            apiResponse =
                userTransactionDataSource.insertTransaction(
                    userId = userid,
                    fromAccountId = localFromAccount.id,
                    eventDateTimeString = eventDateTimeConversionResult.second,
                    particulars = particulars,
                    amount = amount,
                    toAccountId = localToAccount.id
                )
        }
        //    println("Response : $apiResponse")
        if (apiResponse.isError()) {

            println("Error : ${(apiResponse.getValue() as Exception).localizedMessage}")
            //        do {
            //            print("Retry (Y/N) ? : ")
            //            val input = readLine()
            //            when (input) {
            //                "Y", "" -> {
            //                    login()
            //                    return
            //                }
            //                "N" -> {
            //                }
            //                else -> println("Invalid option, try again...")
            //            }
            //        } while (input != "N")
        } else {

            val insertionResponseResult = apiResponse.getValue() as InsertionResponse
            if (insertionResponseResult.status == 0) {

                println("OK...")
                return true
            } else {

                println("Server Execution Error : ${insertionResponseResult.error}")
            }
        }
    }
    return false
}

private fun chooseDepositFull(userId: Int): Boolean {

    return handleAccountsApiResponse(apiResponse = ApiUtils.getAccountsFull(userId = userId), purpose = "To")
}

private fun chooseFromAccountFull(userId: Int): Boolean {

    return handleAccountsApiResponse(apiResponse = ApiUtils.getAccountsFull(userId), purpose = "From")
}

private fun chooseViaAccountFull(userId: Int): Boolean {

    return handleAccountsApiResponse(apiResponse = ApiUtils.getAccountsFull(userId), purpose = "Via.")
}

private fun handleAccountsApiResponse(
    apiResponse: ResponseHolder<AccountsResponse>,
    purpose: String
): Boolean {

    if (apiResponse.isError()) {

        println("Error : ${(apiResponse.getValue() as Exception).localizedMessage}")
        //        do {
        //            print("Retry (Y/N) ? : ")
        //            val input = readLine()
        //            when (input) {
        //                "Y", "" -> {
        //                    login()
        //                    return
        //                }
        //                "N" -> {
        //                }
        //                else -> println("Invalid option, try again...")
        //            }
        //        } while (input != "N")
    } else {

        val accountsResponseResult = apiResponse.getValue() as AccountsResponse
        if (accountsResponseResult.status == 1) {

            println("No Accounts...")

        } else {

            userAccountsMap = AccountUtils.prepareUserAccountsMap(accountsResponseResult.accounts)
            do {
                commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                    listOf(
                        "\nAccounts",
                        userAccountsToStringFromLinkedHashMap(
                            userAccountsMap = userAccountsMap
                        ),
                        "1 - Choose $purpose Account - By Index Number",
                        "2 - Search $purpose Account - By Part Of Name",
                        "0 - Back",
                        "",
                        "Enter Your Choice : "
                    )
                )
                val choice = readLine()
                when (choice) {
                    "1" -> {
                        if (purpose == "To") {

                            if (handleToAccountSelection(
                                    chooseAccountByIndex(userAccountsMap), userAccountsMap
                                )
                            ) {

                                return true
                            }
                        } else if (purpose == "From") {

                            if (handleFromAccountSelection(
                                    chooseAccountByIndex(userAccountsMap), userAccountsMap
                                )
                            ) {

                                return true
                            }
                        } else if (purpose == "Via.") {

                            if (handleViaAccountSelection(
                                    chooseAccountByIndex(userAccountsMap), userAccountsMap
                                )
                            ) {

                                return true
                            }
                        }
                    }
                    "2" -> {
                        if (purpose == "To") {

                            if (handleToAccountSelection(
                                    searchAccount(userAccountsMap), userAccountsMap
                                )
                            ) {

                                return true
                            }
                        } else if (purpose == "From") {

                            if (handleFromAccountSelection(
                                    searchAccount(userAccountsMap), userAccountsMap
                                )
                            ) {

                                return true
                            }
                        } else if (purpose == "Via.") {

                            if (handleViaAccountSelection(
                                    searchAccount(userAccountsMap), userAccountsMap
                                )
                            ) {

                                return true
                            }
                        }
                    }
                    "0" -> {
                    }
                    else -> invalidOptionMessage()
                }
            } while (choice != "0")
        }
    }
    return false
}

private fun handleToAccountSelection(
    depositAccountId: Int,
    userAccountsMap: LinkedHashMap<Int, AccountResponse>
): Boolean {

    if (depositAccountId != 0) {

        toAccount = userAccountsMap[depositAccountId]!!
        return true
    }
    return false
}

private fun handleFromAccountSelection(
    fromAccountId: Int,
    userAccountsMap: LinkedHashMap<Int, AccountResponse>
): Boolean {

    if (fromAccountId != 0) {

        fromAccount = userAccountsMap[fromAccountId]!!
        return true
    }
    return false
}

private fun handleViaAccountSelection(
    viaAccountId: Int,
    userAccountsMap: LinkedHashMap<Int, AccountResponse>
): Boolean {

    if (viaAccountId != 0) {

        viaAccount = userAccountsMap[viaAccountId]!!
        return true
    }
    return false
}

private fun searchAccount(userAccountsMap: LinkedHashMap<Int, AccountResponse>): Int {

    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
        listOf("\nEnter Search Key : ")
    )
    val searchKeyInput = readLine()
    val searchResult =
        searchOnHashMapValues(hashMap = userAccountsMap, searchKey = searchKeyInput!!)
    if (searchResult.isEmpty()) {

        do {
            commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                listOf(
                    "No Matches....",
                    "1 - Try Again",
                    "0 - Back",
                    "",
                    "Enter Your Choice : "
                )
            )
            val input = readLine()
            if (input == "1") return searchAccount(userAccountsMap = userAccountsMap)
            else if (input != "0") invalidOptionMessage()
        } while (input != "0")
    } else {

        do {
            commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                listOf(
                    "\nSearch Results",
                    userAccountsToStringFromLinkedHashMap(userAccountsMap = searchResult),
                    "1 - Choose Deposit Account - By Index Number",
                    "0 - Back",
                    "",
                    "Enter Your Choice : "
                )
            )
            val input = readLine()
            if (input == "1") return chooseAccountByIndex(searchResult)
            else if (input != "0") invalidOptionMessage()
        } while (input != "0")
    }
    return 0
}

private fun searchOnHashMapValues(
    hashMap: LinkedHashMap<Int, AccountResponse>,
    searchKey: String
): LinkedHashMap<Int, AccountResponse> {

    val result = LinkedHashMap<Int, AccountResponse>()
    hashMap.forEach { account ->
        if (account.value.fullName.contains(other = searchKey, ignoreCase = true)) {

            result[account.key] = account.value
        }
    }
    return result
}

private fun userAccountsToStringFromLinkedHashMap(
    userAccountsMap: LinkedHashMap<Int, AccountResponse>
): String {

    var result = ""
    userAccountsMap.forEach { account -> result += "A${account.key} - ${account.value.fullName}\n" }
    return result
}

private fun userAccountsToStringFromLinkedHashMapLimitedTo10(
    userAccountsMap: LinkedHashMap<Int, AccountResponse>
): String {

    return userAccountsToStringFromListPair(getLast10ItemsFromLinkedHashMap(userAccountsMap))
}

private fun userAccountsToStringFromListPair(
    userAccountsList: List<Pair<Int, AccountResponse>>
): String {

    var result = ""
    userAccountsList.forEach { accountEntry -> result += "A${accountEntry.first} - ${accountEntry.second.fullName}\n" }
    return result
}

private fun getLast10ItemsFromLinkedHashMap(userAccountsMap: LinkedHashMap<Int, AccountResponse>): List<Pair<Int, AccountResponse>> {

    if (userAccountsMap.size > 10) {

        return userAccountsMap.toList().takeLast(10)
    }
    return userAccountsMap.toList()
}

private fun chooseDepositTop(userId: Int): Boolean {

    return handleAccountsApiResponse(apiResponse = getAccounts(userId = userId), purpose = "To")
}

private fun chooseFromAccountTop(userId: Int): Boolean {

    return handleAccountsApiResponse(apiResponse = getAccounts(userId = userId), purpose = "From")
}

private fun viewTransactions(
    userId: Int,
    username: String,
    accountId: Int,
    accountFullName: String,
    functionCallSource: FunctionCallSource = FunctionCallSource.FROM_OTHERS
): String {

    val apiResponse = getUserTransactions(userId = userId, accountId = accountId)
    if (apiResponse.isError()) {

        println("Error : ${(apiResponse.getValue() as Exception).localizedMessage}")
        do {
            print("Retry (Y/N) ? : ")
            val input = readLine()
            when (input) {
                "Y", "" -> {
                    return viewTransactions(
                        userId = userId,
                        username = username,
                        accountId = accountId,
                        accountFullName = accountFullName,
                        functionCallSource = functionCallSource
                    )
                }
                "N" -> {}
                else -> invalidOptionMessage()
            }
        } while (input != "N")
        return "E"
    } else {

        val userTransactionsResponseResult = apiResponse.getValue() as TransactionsResponse
        if (userTransactionsResponseResult.status == 1) {

            println("Account - $accountFullName")
            println("No Transactions...")
            return "0"

        } else {

            var choice = ""
            do {
                var menuItems = listOf(
                    "\nUser : $username",
                    "$accountFullName - Transactions",
                    printAccountLedger(
                        transactions = userTransactionsResponseResult.transactions,
                        currentAccountId = accountId
                    )
                )
                if (functionCallSource == FunctionCallSource.FROM_CHECK_ACCOUNTS) {
                    menuItems = menuItems + listOf("0 to Back Enter to Continue : ")
                } else if (functionCallSource == FunctionCallSource.FROM_VIEW_TRANSACTIONS_OF_ACCOUNT) {
                    break
                } else {
                    menuItems = menuItems + listOf(
                        "1 - Delete Transaction - By Index Number",
                        "2 - Delete Transaction - By Search",
                        "3 - Edit Transaction - By Index Number",
                        "4 - Edit Transaction - By Search",
                        "5 - Add Transaction",
                        "0 - Back",
                        "",
                        "Enter Your Choice : "
                    )
                }
                commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(menuItems)

                choice = readLine()!!
                when (choice) {
                    "1", "2", "3", "4" -> {
                        if (functionCallSource == FunctionCallSource.FROM_CHECK_ACCOUNTS) {
                            invalidOptionMessage()
                        } else {
                            ToDoUtils.showTodo()
                        }
                    }
                    "5" -> {
                        if (functionCallSource == FunctionCallSource.FROM_CHECK_ACCOUNTS) {
                            invalidOptionMessage()
                        } else {
                            addTransaction(
                                userId = userId,
                                username = username,
                                transactionType = TransactionType.NORMAL
                            )
                        }
                    }
                    "0" -> {}
                    "" -> {
                        if (functionCallSource == FunctionCallSource.FROM_CHECK_ACCOUNTS) {
                            break
                        } else {
                            invalidOptionMessage()
                        }
                    }
                    else -> invalidOptionMessage()
                }
            } while (choice != "0")
            return choice
        }
    }
}

private fun invalidOptionMessage() {
    println("Invalid option, try again...")
}

private fun printAccountLedger(transactions: List<TransactionResponse>, currentAccountId: Int): String {

    //    println("transactions = [${transactions}]")

    var outPut = ""
    var currentBalance = 0.0F
    var transactionDirection: String
    var secondAccountName: String
    transactions.forEach { currentTransaction: TransactionResponse ->
        if (currentTransaction.from_account_id == currentAccountId) {

            currentBalance -= currentTransaction.amount
            transactionDirection = "-"
            secondAccountName = currentTransaction.to_account_name

        } else {

            currentBalance += currentTransaction.amount
            transactionDirection = ""
            secondAccountName = currentTransaction.from_account_name
        }
        outPut +=
            "${currentTransaction.event_date_time}\t${currentTransaction.particulars}\t${transactionDirection}${currentTransaction.amount}\t${secondAccountName}\t${currentBalance}\n"
    }
    return outPut
}

private fun getUserTransactions(userId: Int, accountId: Int): ResponseHolder<TransactionsResponse> {

    val apiResponse: ResponseHolder<TransactionsResponse>
    val userTransactionsDataSource = TransactionsDataSource()
    println("Contacting Server...")
    runBlocking {
        apiResponse =
            userTransactionsDataSource.selectUserTransactions(
                userId = userId, accountId = accountId
            )
    }
    //    println("Response : $apiResponse")
    return apiResponse
}
