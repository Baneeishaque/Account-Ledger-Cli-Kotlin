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
import transactionInserterForAccountLedger.to_utils.DateTimeUtils.normalPattern
import transactionInserterForAccountLedger.to_utils.PrintUtils
import transactionInserterForAccountLedger.to_utils.ToDoUtils
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import java.util.*
import kotlin.collections.LinkedHashMap

private object App {

    internal const val appName = "Account Ledger CLI App"
    internal const val version = "0.0.1"
}

private val reader = Scanner(System.`in`)
private var dateTimeString = LocalDateTime.now().format(normalPattern)

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
            val choice = reader.nextInt()
            when (choice) {

                1 -> login()
                2 -> register()
                0 -> {
                    println("Thanks...")
                }
                else -> println("Invalid option, try again...")
            }
        } while (choice != 0)

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
    val username = "banee_ishaque_k_10_04_2019"
//    print("Enter Your Password : ")
//    val password = readLine()
    val password = "9895204814"

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
            0 -> {

                println("Invalid Credentials...")

            }
            1 -> {

                println("Login Success...")
                userScreen(username = username, userId = loginResponseResult.id)

            }
            else -> {

                println("Server Execution Error...")
            }
        }
    }
}

private fun userScreen(username: String?, userId: Int) {

    do {
        PrintUtils.printMenu(listOf("\nUser : $username",
                "1 - List Accounts : Top Levels",
                "2 - Insert Quick Transaction",
                "3 - List Accounts : Full Names",
                "0 - Logout",
                "",
                "Enter Your Choice : "))
        val choice = reader.nextInt()
        when (choice) {

            1 -> listAccountsTop(username = username, userId = userId)
            2 -> insertQuickTransaction()
            3 -> listAccountsFull(username = username, userId = userId)
            else -> println("Invalid option, try again...")
        }
    } while (choice != 0)
}

private fun listAccountsFull(username: String?, userId: Int) {

    handleAccountsResponse(getAccountsFull(userId = userId), username, userId)
}

private fun insertQuickTransaction() {

    ToDoUtils.showTodo()
}

private fun listAccountsTop(username: String?, userId: Int) {

    handleAccountsResponse(getAccounts(userId = userId), username, userId)
}

private fun handleAccountsResponse(apiResponse: ResponseHolder<AccountsResponse>, username: String?, userId: Int) {

    if (apiResponse.isError()) {

        println("Error : ${(apiResponse.getValue() as Exception).localizedMessage}")

    } else {

        val accountsResponseResult = apiResponse.getValue() as AccountsResponse
        if (accountsResponseResult.status == 1) {

            println("No Accounts...")

        } else {

            val userAccountsMap = prepareUserAccountsMap(accountsResponseResult.accounts)
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
            } while (choice != 0)
        }
    }
}

private fun prepareUserAccountsMap(accounts: List<AccountResponse>): LinkedHashMap<Int, AccountResponse> {

    val userAccountsMap = LinkedHashMap<Int, AccountResponse>()
    accounts.forEach {

        currentAccount ->
        userAccountsMap[currentAccount.id] = currentAccount
    }
    return userAccountsMap
}

private fun getAccounts(userId: Int, parentAccountId: Int? = 0): ResponseHolder<AccountsResponse> {

    val apiResponse: ResponseHolder<AccountsResponse>
    val userAccountsDataSource = AccountsDataSource()
    println("Contacting Server...")
    runBlocking {

        apiResponse = userAccountsDataSource.selectUserAccounts(userId = userId, parentAccountId = parentAccountId)
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

private fun addAccount(userAccountsMap: LinkedHashMap<Int, AccountResponse>) {

    ToDoUtils.showTodo()
}

private fun chooseAccountByIndex(userAccountsMap: LinkedHashMap<Int, AccountResponse>): Int {

    PrintUtils.printMenu(listOf("\nAccounts",
            userAccountsToStringFromLinkedHashMap(userAccountsMap = userAccountsMap),
            "\nEnter Account Index, or O to back : A"))
    val accountIdInput = reader.nextInt()
    if (accountIdInput == 0) return 0
    if (userAccountsMap.containsKey(accountIdInput)) {

        return accountIdInput
    }
    PrintUtils.printMenu(listOf("Invalid Account Index, Try again ? (Y/N) : "))
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

private fun accountHome(userId: Int, username: String?, accountId: Int, userAccountsMap: LinkedHashMap<Int, AccountResponse>) {

    val account = userAccountsMap[accountId]
    val accountName = account?.fullName
    do {
        PrintUtils.printMenu(listOf("\nUser : $username",
                "Account - $accountName",
                "1 - View Transactions",
                "2 - Add Transaction",
                "3 - View Child Accounts",
                "0 - Back",
                "",
                "Enter Your Choice : "))
        val choiceInput = reader.nextInt()
        when (choiceInput) {

            1 -> viewTransactions(accountId = accountId)
            2 -> addTransaction(
                    userId = userId,
                    username = username,
                    accountId = accountId,
                    accountName = accountName
            )
            3 -> viewChildAccounts(
                    username = username,
                    userId = userId,
                    parentAccount = account
            )
            else -> println("Invalid option, try again...")
        }
    } while (choiceInput != 0)
}

private fun viewChildAccounts(username: String?, userId: Int, parentAccount: AccountResponse?) {

    val apiResponse = getAccounts(userId = userId, parentAccountId = parentAccount?.id)

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
                        "${parentAccount?.fullName} - Child Accounts",
                        userAccountsToStringFromList(accounts = accountsResponseResult.accounts),
                        "1 - Choose Account - By Index Number",
                        "2 - Choose Account - By Search",
                        "3 - Add Child Account",
                        "0 - Back",
                        "",
                        "Enter Your Choice : "))

                val choice = processChildAccountScreenInput(userAccountsMap, userId, username)
            } while (choice != 0)
        }
    }

}

private fun processChildAccountScreenInput(userAccountsMap: LinkedHashMap<Int, AccountResponse>, userId: Int, username: String?): Int {
    val choice = reader.nextInt()
    when (choice) {

        1 -> {
            val accountId = chooseAccountByIndex(userAccountsMap = userAccountsMap)
            if (accountId != 0) {
                accountHome(userId = userId, username = username, accountId = accountId, userAccountsMap = userAccountsMap)
            }
        }
        2 -> {
            val accountId = searchAccount(userAccountsMap = userAccountsMap)
            if (accountId != 0) {
                accountHome(userId = userId, username = username, accountId = accountId, userAccountsMap = userAccountsMap)
            }
        }
        3 -> addAccount(userAccountsMap = userAccountsMap)
        else -> println("Invalid option, try again...")
    }
    return choice
}

private fun addTransaction(userId: Int, username: String?, accountId: Int, accountName: String?) {

    do {
        PrintUtils.printMenu(listOf("\nUser : $username",
                "Account - $accountId : $accountName",
                "1 - Choose Deposit Account From List - Top Levels",
                "2 - Choose Deposit Account From List - Full Names",
                "0 - Back",
                "",
                "Enter Your Choice : "))
        val choice = reader.nextInt()
        when (choice) {

            1 -> {
                val depositAccount = chooseDepositTop(userId)
                if (depositAccount?.id != 0) {

                    if (addTransactionStep2(
                                    userId = userId,
                                    username = username,
                                    fromAccountId = accountId,
                                    toAccountName = accountName,
                                    depositAccount = depositAccount
                            )) {

                        dateTimeString = ((LocalDateTime.parse(dateTimeString) as LocalDateTime).plusMinutes(5) as LocalDateTime).format(normalPattern)
                        return
                    }
                }
            }
            2 -> {
                val depositAccount = chooseDepositFull(userId)
                if (depositAccount?.id != 0) {

                    addTransactionStep2(
                            userId = userId,
                            username = username,
                            fromAccountId = accountId,
                            toAccountName = accountName,
                            depositAccount = depositAccount
                    )
                    return
                }
            }
            else -> println("Invalid option, try again...")
        }
    } while (choice != 0)
}

private fun addTransactionStep2(
        userId: Int,
        username: String?,
        fromAccountId: Int,
        toAccountName: String?,
        depositAccount: AccountResponse?
): Boolean {

    PrintUtils.printMenu(listOf("\nUser : $username",
            "Account - $fromAccountId : $toAccountName",
            "Deposit Account - ${depositAccount?.id} : ${depositAccount?.fullName}",
            //TODO : Complete back
            "Enter Time : "
    ))
    //TODO : Date Time Format Check
    dateTimeString = enterDateWithTime()
    if (dateTimeString == "B") {

        dateTimeString = LocalDateTime.now().format(normalPattern).toString()
        return false
    }
    print("Enter Particulars : ")
    //TODO : Back to fields, or complete back
    val particulars = readLine()
    print("Enter Amount : ")
    val amount = reader.nextFloat()

    do {
        PrintUtils.printMenu(listOf("\nTime - $dateTimeString",
                "Account - $fromAccountId : $toAccountName",
                "Deposit Account - ${depositAccount?.id} : ${depositAccount?.fullName}",
                "Particulars - $particulars",
                "Amount - $amount",
                "\nCorrect ? (Y/N), Enter B to back, Ex to exchange accounts : "
        ))
        val isCorrect = readLine()
        when (isCorrect) {

            "Y" -> {
                if (insertTransaction(
                                userid = userId,
                                fromAccountId = fromAccountId,
                                toAccountId = depositAccount?.id,
                                eventDateTime = dateTimeString,
                                particulars = particulars,
                                amount = amount
                        )) {

                    return true
                }
            }
            //TODO : Back to fields
            "N" -> return addTransactionStep2(
                    userId = userId,
                    username = username,
                    fromAccountId = fromAccountId,
                    toAccountName = toAccountName,
                    depositAccount = depositAccount
            )
            "Ex" -> {
                //TODO
            }
            else -> println("Invalid option, try again...")
        }
    } while (isCorrect != "B")
    return false
}

private fun enterDateWithTime(): String {

    print("$dateTimeString Correct? (Y/N), or B to Back : ")
    when (readLine()) {
        "Y" -> {

            return dateTimeString

        }
        "N" -> {

            return inputDateTime()
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

    print("Enter Time (MM/DD/YYYY HH:MM:SS) : ")
    try {

        return (LocalDateTime.parse(readLine(), normalPattern) as LocalDateTime).format(normalPattern)

    } catch (e: DateTimeParseException) {

        println("Invalid Date...")
        return inputDateTime()
    }
}

private fun insertTransaction(userid: Int, fromAccountId: Int, toAccountId: Int?, eventDateTime: String, particulars: String?, amount: Float): Boolean {

    val apiResponse: ResponseHolder<InsertionResponse>
    val userTransactionDataSource = TransactionDataSource()
    println("Contacting Server...")
    runBlocking {

        apiResponse = userTransactionDataSource.insertTransaction(
                userId = userid,
                fromAccountId = fromAccountId,
                eventDateTimeString = eventDateTime,
                particulars = particulars,
                amount = amount,
                toAccountId = toAccountId
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

private fun chooseDepositFull(userId: Int): AccountResponse? {

    return handleDepositAccountsResponse(getAccountsFull(userId))
}

private fun handleDepositAccountsResponse(apiResponse: ResponseHolder<AccountsResponse>): AccountResponse? {
    if (apiResponse.isError()) {

        println("Error : ${(apiResponse.getValue() as Exception).localizedMessage}")

    } else {

        val accountsResponseResult = apiResponse.getValue() as AccountsResponse
        if (accountsResponseResult.status == 1) {

            println("No Accounts...")

        } else {

            val userAccountsMap = prepareUserAccountsMap(accountsResponseResult.accounts)
            do {
                PrintUtils.printMenu(listOf("\nAccounts",
                        userAccountsToStringFromLinkedHashMap(userAccountsMap = userAccountsMap),
                        "1 - Choose Deposit Account - By Index Number",
                        "2 - Search Deposit Account - By Part Of Name",
                        "0 - Back",
                        "",
                        "Enter Your Choice : "))
                val choice = reader.nextInt()
                when (choice) {

                    1 -> {
                        val depositAccountId = chooseAccountByIndex(userAccountsMap)
                        if (depositAccountId != 0) return userAccountsMap[depositAccountId]
                    }
                    2 -> {
                        val depositAccountId = searchAccount(userAccountsMap)
                        if (depositAccountId != 0) return userAccountsMap[depositAccountId]
                    }
                    else -> println("Invalid option, try again...")
                }
            } while (choice != 0)
        }
    }
    return AccountResponse(
            id = 0,
            fullName = "",
            name = "",
            parentAccountId = 0,
            accountType = "",
            notes = "",
            commodityType = "",
            commodityValue = "",
            ownerId = 0,
            taxable = "",
            placeHolder = ""
    )
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
    val searchResult = searchOnHashMapValues(hashMap = userAccountsMap, searchKey = searchKeyInput)
    if (searchResult.isEmpty()) {

        do {
            PrintUtils.printMenu(listOf("No Matches....",
                    "1 - Try Again",
                    "0 - Back",
                    "",
                    "Enter Your Choice : "))
            val input = reader.nextInt()
            if (input == 1)
                return searchAccount(userAccountsMap = userAccountsMap)
            else if (input != 0)
                println("Invalid option, try again...")

        } while (input != 0)

    } else {

        do {
            PrintUtils.printMenu(listOf("\nSearch Results",
                    userAccountsToStringFromLinkedHashMap(userAccountsMap = searchResult),
                    "1 - Choose Deposit Account - By Index Number",
                    "0 - Back",
                    "",
                    "Enter Your Choice : "))
            val input = reader.nextInt()
            if (input == 1)
                return chooseAccountByIndex(searchResult)
            else if (input != 0)
                println("Invalid option, try again...")

        } while (input != 0)
    }
    return 0
}

private fun searchOnHashMapValues(hashMap: LinkedHashMap<Int, AccountResponse>, searchKey: String?): LinkedHashMap<Int, AccountResponse> {

    val result = LinkedHashMap<Int, AccountResponse>()
    hashMap.forEach { account ->

        if (account.value.fullName.contains(searchKey.toString(), ignoreCase = true)) {

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

private fun chooseDepositTop(userId: Int): AccountResponse? {

    return handleDepositAccountsResponse(getAccounts(userId))
}

private fun viewTransactions(accountId: Int) {

    ToDoUtils.showTodo()
}

