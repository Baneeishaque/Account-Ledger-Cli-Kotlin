package transactionInserterForAccountLedger.cli

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.coroutines.runBlocking
import transactionInserterForAccountLedger.api.response.AccountResponse
import transactionInserterForAccountLedger.api.response.AccountsResponse
import transactionInserterForAccountLedger.api.response.InsertionResponse
import transactionInserterForAccountLedger.api.response.LoginResponse
import transactionInserterForAccountLedger.retrofit.ResponseHolder
import transactionInserterForAccountLedger.retrofit.data.AccountsDataSource
import transactionInserterForAccountLedger.retrofit.data.TransactionDataSource
import transactionInserterForAccountLedger.retrofit.data.UserDataSource
import transactionInserterForAccountLedger.to_utils.DateTimeUtils
import transactionInserterForAccountLedger.to_utils.DateTimeUtils.normalPattern
import transactionInserterForAccountLedger.to_utils.PrintUtils
import transactionInserterForAccountLedger.to_utils.ToDoUtils
import transactionInserterForAccountLedger.utils.AccountUtils
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

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

private var userAccountsMap = LinkedHashMap<Int, AccountResponse>()
private val accountsResponseResult = AccountsResponse(1, listOf(AccountUtils.getBlankAccount()))

fun main(args: Array<String>) {

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
                "2 - Insert Quick Transaction : Wallet",
                "3 - Insert Quick Transaction : Bank : $baneeBankAccountName",
                "4 - List Accounts : Full Names",
                "0 - Logout",
                "",
                "Enter Your Choice : "))
        val choice = readLine()
        when (choice) {

            "1" -> listAccountsTop(username = username, userId = userId)
            "2" -> insertQuickTransactionWallet(userId = userId, username = username)
            "3" -> insertQuickTransactionBank(userId = userId, username = username)
            "4" -> listAccountsFull(username = username, userId = userId)
            "0" -> return
            else -> println("Invalid option, try again...")
        }
    } while (choice != "0")
}

private fun insertQuickTransactionBank(userId: Int, username: String) {

    if (handleAccountsResponse(getAccountsFull(userId = userId))) {

        fromAccount = userAccountsMap[baneeBankAccountId]!!
        accountHome(userId = userId, username = username)
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

    //Use all accounts for general account addition, or from account for child account addition
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
        "Y" -> {

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
                "Account - ${fromAccount.id} : ${fromAccount.fullName}",
                "Deposit Account - ${toAccount.id} : ${toAccount.fullName}",
                "1 - Choose Deposit Account From List - Top Levels",
                "2 - Choose Deposit Account From List - Full Names",
                "3 - Continue Transaction",
                "4 - Exchange Accounts",
                "5 - Exchange Accounts,Then Continue Transaction",
                "0 - Back",
                "",
                "Enter Your Choice : "))
        val choice = readLine()
        when (choice) {

            "1" -> {
                if (chooseDepositTop(userId)) {

                    addTransactionWithAccountAvailabilityCheck(
                            userId = userId,
                            username = username
                    )
                    return
                }
            }
            "2" -> {
                if (chooseDepositFull(userId)) {

                    addTransactionWithAccountAvailabilityCheck(
                            userId = userId,
                            username = username
                    )
                    return
                }
            }
            "3" -> {

                addTransactionWithAccountAvailabilityCheck(
                        userId = userId,
                        username = username
                )
                return
            }
            "4" -> {
                exchangeAccounts()
                addTransaction(
                        userId = userId,
                        username = username
                )
                return
            }
            "5" -> {

                exchangeAccounts()
                addTransactionWithAccountAvailabilityCheck(
                        userId = userId,
                        username = username
                )
                return
            }
            "0" -> return
            else -> println("Invalid option, try again...")
        }
    } while (choice != "0")
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
            //TODO : Complete back
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
            //TODO : Back to fields, or complete back
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

                    "Y" -> {
                        if (insertTransaction(
                                        userid = userId,
                                        eventDateTime = dateTimeString,
                                        particulars = transactionParticulars,
                                        amount = transactionAmount
                                )) {

                            return true
                        }
                    }
                    //TODO : Back to fields
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
        "Y" -> {

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

    //TODO : Implement Back
    print("Enter Time (MM/DD/YYYY HH:MM:SS) : ")
    //TODO : To Utils
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

    return handleDepositAccountsResponse(getAccountsFull(userId))
}

private fun handleDepositAccountsResponse(apiResponse: ResponseHolder<AccountsResponse>): Boolean {

    if (apiResponse.isError()) {

        println("Error : ${(apiResponse.getValue() as Exception).localizedMessage}")

    } else {

        val accountsResponseResult = apiResponse.getValue() as AccountsResponse
        if (accountsResponseResult.status == 1) {

            println("No Accounts...")

        } else {

            prepareUserAccountsMap(accountsResponseResult.accounts)
            do {
                PrintUtils.printMenu(listOf("\nAccounts",
                        userAccountsToStringFromLinkedHashMap(userAccountsMap = userAccountsMap),
                        "1 - Choose Deposit Account - By Index Number",
                        "2 - Search Deposit Account - By Part Of Name",
                        "0 - Back",
                        "",
                        "Enter Your Choice : "))
                val choice = readLine()
                when (choice) {

                    "1" -> {
                        if (handleToAccountSelection(chooseAccountByIndex(userAccountsMap), userAccountsMap)) {

                            return true
                        }
                    }
                    "2" -> {
                        if (handleToAccountSelection(searchAccount(userAccountsMap), userAccountsMap)) {

                            return true
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

    return handleDepositAccountsResponse(getAccounts(userId))
}

@Suppress("UNUSED_PARAMETER")
private fun viewTransactions(accountId: Int) {

    ToDoUtils.showTodo()
}
