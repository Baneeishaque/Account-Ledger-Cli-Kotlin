package accountLedgerCli.cli

import accountLedgerCli.api.response.*
import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.AccountsResponse
import accountLedgerCli.api.response.TransactionResponse
import accountLedgerCli.api.response.TransactionsResponse
import accountLedgerCli.api.response.UsersResponse
import accountLedgerCli.retrofit.ResponseHolder
import accountLedgerCli.retrofit.data.TransactionsDataSource
import accountLedgerCli.retrofit.data.UsersDataSource
import accountLedgerCli.to_utils.*
import accountLedgerCli.to_utils.DateTimeUtils.normalDateTimePattern
import accountLedgerCli.utils.AccountUtils
import accountLedgerCli.utils.UserUtils
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.LinkedHashMap

internal var dateTimeString = LocalDateTime.now().format(normalDateTimePattern)

internal var fromAccount = AccountUtils.getBlankAccount()
internal var viaAccount = AccountUtils.getBlankAccount()
internal var toAccount = AccountUtils.getBlankAccount()
internal var transactionParticulars = ""
internal var transactionAmount = 0F

internal var chosenUser = UserUtils.getBlankUser();

internal var userAccountsMap = LinkedHashMap<Int, AccountResponse>()
private val accountsResponseResult = AccountsResponse(1, listOf(AccountUtils.getBlankAccount()))
private val commandLinePrintMenu = CommandLinePrintMenu()
internal val commandLinePrintMenuWithEnterPrompt =
    CommandLinePrintMenuWithEnterPrompt(commandLinePrintMenu)
internal val commandLinePrintMenuWithTryPrompt =
    CommandLinePrintMenuWithTryPrompt(commandLinePrintMenu)
internal val commandLinePrintMenuWithContinuePrompt =
    CommandLinePrintMenuWithContinuePrompt(commandLinePrintMenu)

fun main() {
    do {
        commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
            listOf(
                "Account Ledger",
                "---------------",
                "1 : Login",
                "2 : Registration",
                "3 : List Users",
                "4 : Balance Sheet for an User",
                "5 : Balance Sheet for all Users",
                "0 : Exit",
                "",
                "Enter Your Choice : "
            )
        )
        val choice = readLine()
        when (choice) {
            "1" -> login()
            "2" -> register()
            "3" -> listUsers()
            "4", "5" -> ToDoUtils.showTodo()
            "0" -> println("Thanks...")
            else -> invalidOptionMessage()
        }
    } while (choice != "0")
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

private fun listUsers() {

    val usersDataSource = UsersDataSource()
    println("Contacting Server...")
    val apiResponse: ResponseHolder<UsersResponse>
    runBlocking { apiResponse = usersDataSource.selectUsers() }
//    println("Response : $apiResponse")
    if (apiResponse.isError()) {

        println("Error : ${(apiResponse.getValue() as Exception).localizedMessage}")
        do {
            print("Retry (Y/N) ? : ")
            val input = readLine()
            when (input) {
                "Y", "" -> {
                    listUsers()
                }
                "N" -> {
                }
                else -> invalidOptionMessage()
            }
        } while (input != "N")
    } else {

        val usersResponse = apiResponse.getValue() as UsersResponse
        if (usersResponse.status == 1) {

            println("No Users...")

        } else {

            val usersMap = UserUtils.prepareUsersMap(usersResponse.users)
            do {
                commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                    listOf(
                        "\nUsers",
                        usersToStringFromLinkedHashMap(
                            usersMap = usersMap
                        ),
                        "1 - Balance Sheet for an User",
                        "0 - Back",
                        "",
                        "Enter Your Choice : "
                    )
                )
                val choice = readLine()
                when (choice) {
                    "1" -> {
                        if (handleUserSelection(
                                chosenUserId = chooseUserByIndex(usersMap = usersMap),
                                usersMap = usersMap
                            )
                        ) {

                            val transactionsDataSource = TransactionsDataSource()
                            println("Contacting Server...")
                            val apiResponse2: ResponseHolder<TransactionsResponse>
                            val specifiedDate = MysqlUtils.normalDateStringToMysqlDateString(
                                normalDateString = getUserInitialTransactionFromUsername(username = chosenUser.username).minusDays(
                                    1
                                ).format(DateTimeUtils.normalDatePattern)
                            )
                            if (specifiedDate.first) {
                                runBlocking {
                                    apiResponse2 =
                                        transactionsDataSource.selectUserTransactionsAfterSpecifiedDate(
                                            userId = chosenUser.id,
                                            specifiedDate = specifiedDate.second
                                        )
                                }
                                // println("Response : $apiResponse2")
                                if (apiResponse2.isError()) {

                                    println("Error : ${(apiResponse2.getValue() as Exception).localizedMessage}")
                                    do {
                                        print("Retry (Y/N) ? : ")
                                        val input = readLine()
                                        when (input) {
                                            "Y", "" -> {
                                                return
                                            }
                                            "N" -> {
                                            }
                                            else -> invalidOptionMessage()
                                        }
                                    } while (input != "N")
                                } else {

                                    val selectUserTransactionsAfterSpecifiedDateResult =
                                        apiResponse2.getValue() as TransactionsResponse
                                    if (selectUserTransactionsAfterSpecifiedDateResult.status == 1) {

                                        println("No Transactions...")

                                    } else {

                                        val accounts = mutableMapOf<Int, String>()
                                        selectUserTransactionsAfterSpecifiedDateResult.transactions.forEach { transaction ->

                                            accounts.putIfAbsent(
                                                transaction.from_account_id,
                                                transaction.from_account_full_name
                                            )
                                            accounts.putIfAbsent(
                                                transaction.to_account_id,
                                                transaction.to_account_full_name
                                            )
                                        }
                                        println("Affected A/Cs : $accounts")
                                        var menuItems = listOf("\nUser : ${chosenUser.username} Balance Sheet Ledger")
                                        for (account in accounts) {

                                            val apiResponse3 =
                                                getUserTransactions(userId = chosenUser.id, accountId = account.key)
                                            if (apiResponse3.isError()) {

                                                println("Error : ${(apiResponse3.getValue() as Exception).localizedMessage}")
                                                do {
                                                    print("Retry (Y/N) ? : ")
                                                    val input = readLine()
                                                    when (input) {
                                                        "Y", "" -> {
                                                        }
                                                        "N" -> {}
                                                        else -> invalidOptionMessage()
                                                    }
                                                } while (input != "N")
                                            } else {

                                                val userTransactionsResponseResult =
                                                    apiResponse3.getValue() as TransactionsResponse
                                                if (userTransactionsResponseResult.status == 0) {

                                                    var currentBalance = 0.0F
                                                    userTransactionsResponseResult.transactions.forEach { currentTransaction: TransactionResponse ->
                                                        if (currentTransaction.from_account_id == account.key) {

                                                            currentBalance -= currentTransaction.amount

                                                        } else {

                                                            currentBalance += currentTransaction.amount
                                                        }
                                                    }
                                                    if (currentBalance == 0.0F) {
                                                        menuItems =
                                                            menuItems + listOf("\n${account.key} : ${account.value}")
                                                    }
                                                }
                                            }
                                        }
                                        var choice2: String
                                        do {

                                            menuItems = menuItems + listOf("0 to Back Enter to Continue : ")
                                            commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                                                menuItems
                                            )
                                            choice2 = readLine()!!
                                            when (choice2) {
                                                "0" -> {}
                                                "" -> {
                                                    break
                                                }
                                                else -> invalidOptionMessage()
                                            }
                                        } while (choice2 != "0")
                                    }
                                }
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
}

private fun getUserInitialTransactionFromUsername(username: String): LocalDateTime {

    return LocalDateTime.parse(username, DateTimeFormatter.ofPattern("banee_ishaque_k_dd_MM_yyyy", Locale.getDefault()))
}
