package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.AccountsResponse
import accountLedgerCli.api.response.InsertionResponse
import accountLedgerCli.api.response.LoginResponse
import accountLedgerCli.retrofit.ResponseHolder
import accountLedgerCli.retrofit.data.AccountsDataSource
import accountLedgerCli.retrofit.data.TransactionDataSource
import accountLedgerCli.retrofit.data.UserDataSource
import accountLedgerCli.to_utils.DateTimeUtils
import accountLedgerCli.to_utils.DateTimeUtils.normalPattern
import accountLedgerCli.to_utils.PrintUtils
import accountLedgerCli.to_utils.ToDoUtils
import accountLedgerCli.utils.AccountUtils
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.coroutines.runBlocking

internal object App {

    internal const val appName = "Account Ledger CLI App"
    internal const val version = "0.0.1"
}

internal var dateTimeString = LocalDateTime.now().format(normalPattern)
private var fromAccount = AccountUtils.getBlankAccount()
private var toAccount = AccountUtils.getBlankAccount()
private var transactionParticulars = ""
private var transactionAmount = 0F

private const val baneeUserName = "banee_ishaque_k_10_04_2019"
private const val baneePassword = "9895204814"
private const val baneeWalletAccountId = 6
private const val baneeBankAccountId = 11
private const val baneeBankAccountName = "Punjab National Bank, Tirur"
private const val baneeFrequent1AccountName = "Hisham Banee Ishaque K Brother"
private const val baneeFrequent2AccountName = "Ismail K Father Banee Ishaque K"
private const val baneeFrequent3AccountName = "Account Shortages"
private const val baneeFrequent1AccountId = 688
private const val baneeFrequent2AccountId = 38
private const val baneeFrequent3AccountId = 367

private var userAccountsMap = LinkedHashMap<Int, AccountResponse>()
private val accountsResponseResult = AccountsResponse(1, listOf(AccountUtils.getBlankAccount()))

fun main(args: Array<String>) {

    readCsv()
    return

    val parser = ArgParser(programName = "${App.appName}:: ${App.version}")
    val version by parser.option(type = ArgType.Boolean, shortName = "V", description = "Version").default(value = false)

    if (args.isEmpty()) {

//        println("No options...")
//        TODO : Implement common back & exit for menus
        do {
            PrintUtils.printMenu(listOf("\nAccount Ledger",
                    "1 : Login",
                    "2 : Registration",
                    "0 : Exit",
                    "",
                    "Enter Your Choice : "))
            val choice = readLine()
            when (choice) {

                "1" -> login()
                "2" -> register()
                "0" -> println("Thanks...")
                else -> println("Invalid option, try again...")
            }
        } while (choice != "0")
    } else {

        // Add all input to parser
        parser.parse(args)

        if (version) println(App.version)
    }
}

fun readCsv() {

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

private fun register() {

    ToDoUtils.showTodo()
}

private fun login() {

    println("\nAccount Ledger Authentication")
//    print("Enter Your Username : ")
//    val username = readLine()
    val username = baneeUserName
//    print("Enter Your Password : ")
//    val password = readLine()
    val password = baneePassword

    val user = UserDataSource()
    println("Contacting Server...")
    val apiResponse: ResponseHolder<LoginResponse>
    runBlocking {

        apiResponse = user.selectUser(username = username, password = password)
    }
//    println("Response : $apiResponse")
    if (apiResponse.isError()) {

        println("Error : ${(apiResponse.getValue() as Exception).localizedMessage}")
        do {
            print("Retry (Y/N) ? : ")
            val input = readLine()
            when (input) {
                "Y", "" -> {
                    login()
                    return
                }
                "N" -> {
                }
                else -> println("Invalid option, try again...")
            }
        } while (input != "N")
    } else {

        val loginResponseResult = apiResponse.getValue() as LoginResponse
        when (loginResponseResult.userCount) {
            0 -> println("Invalid Credentials...")
            1 -> {

                println("Login Success...")
                userScreen(username = username, userId = loginResponseResult.id)
            }
            else -> println("Server Execution Error...")
        }
    }
}

@Suppress("SameParameterValue")
private fun userScreen(username: String, userId: Int) {

    do {
        PrintUtils.printMenu(listOf("\nUser : $username",
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
                "0 - Logout",
                "",
                "Enter Your Choice : "))
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
            "0" -> return
            else -> println("Invalid option, try again...")
        }
    } while (choice != "0")
}

fun importBankFromXlx() {

    ToDoUtils.showTodo()
}

fun importBankFromCsv() {

    ToDoUtils.showTodo()
}

private fun insertQuickTransactionFrequent1(userId: Int, username: String) {

    if (handleAccountsResponse(getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeFrequent1AccountId]!!
        accountHome(userId = userId, username = username)
    }
}

private fun insertQuickTransactionFrequent2(userId: Int, username: String) {

    if (handleAccountsResponse(getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeFrequent2AccountId]!!
        accountHome(userId = userId, username = username)
    }
}

private fun insertQuickTransactionFrequent3(userId: Int, username: String) {

    if (handleAccountsResponse(getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeFrequent3AccountId]!!
        accountHome(userId = userId, username = username)
    }
}

private fun insertQuickTransactionBank(userId: Int, username: String) {

    if (handleAccountsResponse(getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeBankAccountId]!!
        accountHome(userId = userId, username = username)
    }
}

private fun insertQuickTransactionBankToFrequent1(userId: Int, username: String) {

    if (handleAccountsResponse(getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeBankAccountId]!!
        toAccount = userAccountsMap[baneeFrequent1AccountId]!!
        transactionContinueCheck(
                userId = userId,
                username = username
        )
    }
}

private fun insertQuickTransactionBankToFrequent2(userId: Int, username: String) {

    if (handleAccountsResponse(getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeBankAccountId]!!
        toAccount = userAccountsMap[baneeFrequent2AccountId]!!
        transactionContinueCheck(
                userId = userId,
                username = username
        )
    }
}

private fun insertQuickTransactionBankToFrequent3(userId: Int, username: String) {

    if (handleAccountsResponse(getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeBankAccountId]!!
        toAccount = userAccountsMap[baneeFrequent3AccountId]!!
        transactionContinueCheck(
                userId = userId,
                username = username
        )
    }
}

private fun listAccountsFull(username: String, userId: Int) {

    handleAccountsResponseAndPrintMenu(
            apiResponse = getAccountsFull(userId = userId),
            username = username,
            userId = userId
    )
}

fun handleAccountsResponseAndPrintMenu(apiResponse: ResponseHolder<AccountsResponse>, username: String, userId: Int) {

    if (handleAccountsResponse(apiResponse)) {

        do {
            PrintUtils.printMenu(listOf("\nUser : $username",
                    "Accounts",
                    userAccountsToStringFromList(accounts = accountsResponseResult.accounts),
                    "1 - Choose Account - By Index Number",
                    "2 - Choose Account - By Search",
                    "3 - Add Account",
                    "0 - Back",
                    "",
                    "Enter Your Choice : "))

            val choice = processChildAccountScreenInput(userAccountsMap, userId, username)
        } while (choice != "0")
    }
}

private fun insertQuickTransactionWallet(userId: Int, username: String) {

    if (handleAccountsResponse(getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeWalletAccountId]!!
        accountHome(userId = userId, username = username)
    }
}

private fun insertQuickTransactionWalletToFrequent1(userId: Int, username: String) {

    if (handleAccountsResponse(getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeWalletAccountId]!!
        toAccount = userAccountsMap[baneeFrequent1AccountId]!!
        transactionContinueCheck(
                userId = userId,
                username = username
        )
    }
}

private fun insertQuickTransactionWalletToFrequent2(userId: Int, username: String) {

    if (handleAccountsResponse(getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeWalletAccountId]!!
        toAccount = userAccountsMap[baneeFrequent2AccountId]!!
        transactionContinueCheck(
                userId = userId,
                username = username
        )
    }
}

private fun insertQuickTransactionWalletToFrequent3(userId: Int, username: String) {

    if (handleAccountsResponse(getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeWalletAccountId]!!
        toAccount = userAccountsMap[baneeFrequent3AccountId]!!
        transactionContinueCheck(
                userId = userId,
                username = username
        )
    }
}

private fun listAccountsTop(username: String, userId: Int) {

    handleAccountsResponseAndPrintMenu(
            apiResponse = getAccounts(userId = userId),
            username = username,
            userId = userId
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

            prepareUserAccountsMap(localAccountsResponseWithStatus.accounts)
            true
        }
    }
}

private fun prepareUserAccountsMap(accounts: List<AccountResponse>) {

    userAccountsMap = LinkedHashMap()
    accounts.forEach {

        currentAccount ->
        userAccountsMap[currentAccount.id] = currentAccount
    }
}

private fun getAccounts(userId: Int, parentAccountId: Int = 0): ResponseHolder<AccountsResponse> {

    val apiResponse: ResponseHolder<AccountsResponse>
    val userAccountsDataSource = AccountsDataSource()
    println("Contacting Server...")
    runBlocking {

        apiResponse = userAccountsDataSource.selectUserAccounts(
                userId = userId,
                parentAccountId = parentAccountId
        )
    }
//    println("Response : $apiResponse")
    return apiResponse
}

private fun userAccountsToStringFromList(accounts: List<AccountResponse>): String {

    var result = ""
    accounts.forEach { account ->
        result += "A${account.id} - ${account.name}\n"
    }
    return result
}

private fun addAccount() {

    // Use all accounts for general account addition, or from account for child account addition
    ToDoUtils.showTodo()
}

private fun chooseAccountByIndex(userAccountsMap: LinkedHashMap<Int, AccountResponse>): Int {

    PrintUtils.printMenu(listOf("\nAccounts",
            userAccountsToStringFromLinkedHashMap(userAccountsMap = userAccountsMap),
            "Enter Account Index, or O to back : A"))
    val accountIdInput = readLine()!!
    if (accountIdInput == "0") return 0
    try {

        val accountId = accountIdInput.toInt()
        if (userAccountsMap.containsKey(accountId)) {

            return accountId
        }
    } catch (exception: NumberFormatException) {
    }
    PrintUtils.printMenuWithTryPrompt(listOf("Invalid Account Index, Try again ? (Y/N) : "))
    return when (readLine()) {
        "Y", "" -> {

            chooseAccountByIndex(userAccountsMap = userAccountsMap)
        }
        "N" -> {

            0
        }
        else -> {

            PrintUtils.printMenu(listOf("Invalid Entry..."))
            chooseAccountByIndex(userAccountsMap = userAccountsMap)
        }
    }
}

private fun accountHome(userId: Int, username: String) {

    do {
        PrintUtils.printMenu(listOf("\nUser : $username",
                "Account - ${fromAccount.fullName}",
                "1 - View Transactions",
                "2 - Add Transaction",
                "3 - View Child Accounts",
                "0 - Back",
                "",
                "Enter Your Choice : "))
        val choiceInput = readLine()
        when (choiceInput) {

            "1" -> viewTransactions(accountId = fromAccount.id)
            "2" -> addTransaction(
                    userId = userId,
                    username = username
            )
            "3" -> viewChildAccounts(
                    username = username,
                    userId = userId
            )
            "0" -> return
            else -> println("Invalid option, try again...")
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
                    viewChildAccounts(
                            username = username,
                            userId = userId
                    )
                    return
                }
                "N" -> {
                }
                else -> println("Invalid option, try again...")
            }
        } while (input != "N")
    } else {

        val accountsResponseResult = apiResponse.getValue() as AccountsResponse
        if (accountsResponseResult.status == 1) {

            println("No Child Accounts...")
        } else {

            val userAccountsMap = LinkedHashMap<Int, AccountResponse>()
            accountsResponseResult.accounts.forEach {

                currentAccount ->
                userAccountsMap[currentAccount.id] = currentAccount
            }
            do {
                PrintUtils.printMenu(listOf("\nUser : $username",
                        "${fromAccount.fullName} - Child Accounts",
                        userAccountsToStringFromList(accounts = accountsResponseResult.accounts),
                        "1 - Choose Account - By Index Number",
                        "2 - Choose Account - By Search",
                        "3 - Add Child Account",
                        "0 - Back",
                        "",
                        "Enter Your Choice : "))

                val choice = processChildAccountScreenInput(userAccountsMap, userId, username)
            } while (choice != "0")
        }
    }
}

private fun processChildAccountScreenInput(userAccountsMap: LinkedHashMap<Int, AccountResponse>, userId: Int, username: String): String? {

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
        else -> println("Invalid option, try again...")
    }
    return choice
}

private fun handleFromAccountSelection(accountId: Int, userAccountsMap: LinkedHashMap<Int, AccountResponse>, userId: Int, username: String) {

    if (accountId != 0) {

        fromAccount = userAccountsMap[accountId]!!
        accountHome(userId = userId, username = username)
    }
}

private fun addTransaction(userId: Int, username: String) {

    do {
        PrintUtils.printMenu(listOf("\nUser : $username",
                "From Account - ${fromAccount.id} : ${fromAccount.fullName}",
                "To Account - ${toAccount.id} : ${toAccount.fullName}",
                "1 - Choose To Account From List - Top Levels",
                "2 - Choose To Account From List - Full Names",
                "3 - Choose From Account From List - Top Levels",
                "4 - Choose From Account From List - Full Names",
                "5 - Continue Transaction",
                "6 - Exchange Accounts",
                "7 - Exchange Accounts, Then Continue Transaction",
                "0 - Back",
                "",
                "Enter Your Choice : "))
        val choice = readLine()
        when (choice) {

            "1" -> {
                if (chooseDepositTop(userId)) {

                    transactionContinueCheck(userId, username)
                    return
                }
            }
            "2" -> {
                if (chooseDepositFull(userId)) {

                    transactionContinueCheck(userId, username)
                    return
                }
            }
            "3" -> {
                if (chooseFromAccountTop(userId)) {

                    transactionContinueCheck(userId, username)
                    return
                }
            }
            "4" -> {
                if (chooseFromAccountFull(userId)) {

                    transactionContinueCheck(userId, username)
                    return
                }
            }
            "5" -> {

                transactionContinueCheck(userId, username)
                return
            }
            "6" -> {
                exchangeAccounts()
                addTransaction(
                        userId = userId,
                        username = username
                )
                return
            }
            "7" -> {

                exchangeAccounts()
                transactionContinueCheck(userId, username)
                return
            }
            "0" -> return
            else -> println("Invalid option, try again...")
        }
    } while (choice != "0")
}

private fun transactionContinueCheck(userId: Int, username: String) {

    do {

        PrintUtils.printMenuWithContinuePrompt(listOf("\nUser : $username",
                "From Account - ${fromAccount.id} : ${fromAccount.fullName}",
                "To Account - ${toAccount.id} : ${toAccount.fullName}",
                "",
                "Continue (Y/N) : "))

        val input = readLine()
        when (input) {

            "Y", "" -> {

                addTransactionWithAccountAvailabilityCheck(
                        userId = userId,
                        username = username
                )
                return
            }
            "N" -> {
            }
            else -> println("Invalid option, try again...")
        }
    } while (input != "N")
}

private fun addTransactionWithAccountAvailabilityCheck(userId: Int, username: String) {

    if (isAccountsAreAvailable()) {

        if (addTransactionStep2(
                        userId = userId,
                        username = username
                )) {

            dateTimeString = ((LocalDateTime.parse(dateTimeString, normalPattern) as LocalDateTime).plusMinutes(5) as LocalDateTime).format(normalPattern)
        }
    } else {

        addTransaction(
                userId = userId,
                username = username
        )
    }
}

private fun isAccountsAreAvailable(): Boolean {

    if (toAccount.id == 0) {

        println("Please choose deposit account...")
        return false
    } else if (fromAccount.id == 0) {

        println("Please choose from account...")
        return false
    }
    return true
}

private fun addTransactionStep2(
        userId: Int,
        username: String
): Boolean {

    PrintUtils.printMenu(listOf("\nUser : $username",
            "Account - ${fromAccount.id} : ${fromAccount.fullName}",
            "Deposit Account - ${toAccount.id} : ${toAccount.fullName}",
            // TODO : Complete back
            "Enter Time : "
    ))
    when (val inputDateTimeString = enterDateWithTime()) {
        "D+Tr" -> {

            dateTimeString = DateTimeUtils.add1DayWith9ClockTimeToDateTimeString(dateTimeString = dateTimeString)
            return addTransactionStep2(userId = userId, username = username)
        }
        "D+" -> {

            dateTimeString = DateTimeUtils.add1DayToDateTimeString(dateTimeString = dateTimeString)
            return addTransactionStep2(userId = userId, username = username)
        }
        "D2+Tr" -> {

            dateTimeString = DateTimeUtils.add2DaysWith9ClockTimeToDateTimeString(dateTimeString = dateTimeString)
            return addTransactionStep2(userId = userId, username = username)
        }
        "D2+" -> {

            dateTimeString = DateTimeUtils.add2DaysToDateTimeString(dateTimeString = dateTimeString)
            return addTransactionStep2(userId = userId, username = username)
        }
        "Ex" -> {

            exchangeAccounts()
            return addTransactionStep2(userId = userId, username = username)
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

                transactionAmount = getValidAmount(transactionAmountInput)
            }

            do {
                PrintUtils.printMenu(listOf("\nTime - $dateTimeString",
                        "Account - ${fromAccount.id} : ${fromAccount.fullName}",
                        "Deposit Account - ${toAccount.id} : ${toAccount.fullName}",
                        "Particulars - $transactionParticulars",
                        "Amount - $transactionAmount",
                        "\nCorrect ? (Y/N), Enter Ex to exchange accounts or B to back : "
                ))
                val isCorrect = readLine()
                when (isCorrect) {

                    "Y", "" -> {
                        if (insertTransaction(
                                        userid = userId,
                                        eventDateTime = dateTimeString,
                                        particulars = transactionParticulars,
                                        amount = transactionAmount
                                )) {

                            return true
                        }
                    }
                    // TODO : Back to fields
                    "N" -> return addTransactionStep2(
                            userId = userId,
                            username = username
                    )
                    "Ex" -> {

                        exchangeAccounts()
                        return addTransactionStep2(userId = userId, username = username)
                    }
                    else -> println("Invalid option, try again...")
                }
            } while (isCorrect != "B")
        }
    }
    return false
}

fun getValidAmount(transactionAmountInput: String): Float {

    try {

        return transactionAmountInput.toFloat()
    } catch (exception: NumberFormatException) {

        println("Invalid Amount : Try Again")
        return getValidAmount(readLine()!!)
    }
}

private fun exchangeAccounts() {

    val tempAccount = fromAccount
    fromAccount = toAccount
    toAccount = tempAccount
}

private fun enterDateWithTime(): String {

    print("$dateTimeString Correct? (Y/N), D+Tr to increase 1 Day with Time Reset, D+ to increase 1 Day, D2+Tr to increase 2 Days with Time Reset, D2+ to increase 2 Days, Ex to exchange accounts or B to Back : ")
    when (readLine()) {
        "Y", "" -> {

            return dateTimeString
        }
        "N" -> {

            return inputDateTime()
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

            return "Ex"
        }
        "B" -> {

            return "B"
        }
        else -> {

            println("Invalid option, try again...")
            return enterDateWithTime()
        }
    }
}

private fun inputDateTime(): String {

    // TODO : Implement Back
    print("Enter Time (MM/DD/YYYY HH:MM:SS) : ")
    // TODO : To Utils
    try {

//        val normalMonthDay = ofPattern("dd")!!
//        val dateTimeInput= readLine()!!
//        while (dateTimeInput.isEmpty())
        return LocalDateTime.parse(readLine(), normalPattern).format(normalPattern)
    } catch (e: DateTimeParseException) {

        println("Invalid Date...")
        return inputDateTime()
    }
}

private fun insertTransaction(userid: Int, eventDateTime: String, particulars: String, amount: Float): Boolean {

    val apiResponse: ResponseHolder<InsertionResponse>
    val userTransactionDataSource = TransactionDataSource()
    println("Contacting Server...")
    runBlocking {

        apiResponse = userTransactionDataSource.insertTransaction(
                userId = userid,
                fromAccountId = fromAccount.id,
                eventDateTimeString = eventDateTime,
                particulars = particulars,
                amount = amount,
                toAccountId = toAccount.id
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
    return false
}

private fun chooseDepositFull(userId: Int): Boolean {

    return handleAccountsApiResponse(
            apiResponse = getAccountsFull(userId = userId),
            purpose = "To"
    )
}

private fun chooseFromAccountFull(userId: Int): Boolean {

    return handleAccountsApiResponse(
            apiResponse = getAccountsFull(userId),
            purpose = "From"
    )
}

private fun handleAccountsApiResponse(apiResponse: ResponseHolder<AccountsResponse>, purpose: String): Boolean {

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

            prepareUserAccountsMap(accountsResponseResult.accounts)
            do {
                PrintUtils.printMenu(listOf("\nAccounts",
                        userAccountsToStringFromLinkedHashMap(userAccountsMap = userAccountsMap),
                        "1 - Choose $purpose Account - By Index Number",
                        "2 - Search $purpose Account - By Part Of Name",
                        "0 - Back",
                        "",
                        "Enter Your Choice : "))
                val choice = readLine()
                when (choice) {

                    "1" -> {
                        if (purpose == "To") {

                            if (handleToAccountSelection(chooseAccountByIndex(userAccountsMap), userAccountsMap)) {

                                return true
                            }
                        } else if (purpose == "From") {

                            if (handleFromAccountSelection(chooseAccountByIndex(userAccountsMap), userAccountsMap)) {

                                return true
                            }
                        }
                    }
                    "2" -> {
                        if (purpose == "To") {

                            if (handleToAccountSelection(searchAccount(userAccountsMap), userAccountsMap)) {

                                return true
                            }
                        } else if (purpose == "From") {

                            if (handleFromAccountSelection(searchAccount(userAccountsMap), userAccountsMap)) {

                                return true
                            }
                        }
                    }
                    "0" -> {
                    }
                    else -> println("Invalid option, try again...")
                }
            } while (choice != "0")
        }
    }
    return false
}

private fun handleToAccountSelection(depositAccountId: Int, userAccountsMap: LinkedHashMap<Int, AccountResponse>): Boolean {

    if (depositAccountId != 0) {

        toAccount = userAccountsMap[depositAccountId]!!
        return true
    }
    return false
}

private fun handleFromAccountSelection(fromAccountId: Int, userAccountsMap: LinkedHashMap<Int, AccountResponse>): Boolean {

    if (fromAccountId != 0) {

        fromAccount = userAccountsMap[fromAccountId]!!
        return true
    }
    return false
}

private fun getAccountsFull(userId: Int): ResponseHolder<AccountsResponse> {

    val apiResponse: ResponseHolder<AccountsResponse>
    val userAccountsDataSource = AccountsDataSource()
    println("Contacting Server...")
    runBlocking {

        apiResponse = userAccountsDataSource.selectUserAccountsFull(userId = userId)
    }
//    println("Response : $apiResponse")
    return apiResponse
}

private fun searchAccount(userAccountsMap: LinkedHashMap<Int, AccountResponse>): Int {

    PrintUtils.printMenu(listOf("\nEnter Search Key : "))
    val searchKeyInput = readLine()
    val searchResult = searchOnHashMapValues(hashMap = userAccountsMap, searchKey = searchKeyInput!!)
    if (searchResult.isEmpty()) {

        do {
            PrintUtils.printMenu(listOf("No Matches....",
                    "1 - Try Again",
                    "0 - Back",
                    "",
                    "Enter Your Choice : "))
            val input = readLine()
            if (input == "1")
                return searchAccount(userAccountsMap = userAccountsMap)
            else if (input != "0")
                println("Invalid option, try again...")
        } while (input != "0")
    } else {

        do {
            PrintUtils.printMenu(listOf("\nSearch Results",
                    userAccountsToStringFromLinkedHashMap(userAccountsMap = searchResult),
                    "1 - Choose Deposit Account - By Index Number",
                    "0 - Back",
                    "",
                    "Enter Your Choice : "))
            val input = readLine()
            if (input == "1")
                return chooseAccountByIndex(searchResult)
            else if (input != "0")
                println("Invalid option, try again...")
        } while (input != "0")
    }
    return 0
}

private fun searchOnHashMapValues(hashMap: LinkedHashMap<Int, AccountResponse>, searchKey: String): LinkedHashMap<Int, AccountResponse> {

    val result = LinkedHashMap<Int, AccountResponse>()
    hashMap.forEach { account ->

        if (account.value.fullName.contains(other = searchKey, ignoreCase = true)) {

            result[account.key] = account.value
        }
    }
    return result
}

private fun userAccountsToStringFromLinkedHashMap(userAccountsMap: LinkedHashMap<Int, AccountResponse>): String {

    var result = ""
    userAccountsMap.forEach { account ->
        result += "A${account.key} - ${account.value.fullName}\n"
    }
    return result
}

private fun chooseDepositTop(userId: Int): Boolean {

    return handleAccountsApiResponse(
            apiResponse = getAccounts(userId = userId),
            purpose = "To"
    )
}

private fun chooseFromAccountTop(userId: Int): Boolean {

    return handleAccountsApiResponse(
            apiResponse = getAccounts(userId = userId),
            purpose = "From"
    )
}

@Suppress("UNUSED_PARAMETER")
private fun viewTransactions(accountId: Int) {

    ToDoUtils.showTodo()
}
