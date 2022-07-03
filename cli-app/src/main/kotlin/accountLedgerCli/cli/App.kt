package accountLedgerCli.cli

import accountLedgerCli.api.response.*
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