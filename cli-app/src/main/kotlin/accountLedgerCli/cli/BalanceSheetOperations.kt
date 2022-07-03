package accountLedgerCli.cli

import accountLedgerCli.api.response.TransactionResponse
import accountLedgerCli.api.response.TransactionsResponse
import accountLedgerCli.api.response.UserResponse
import accountLedgerCli.retrofit.ResponseHolder
import accountLedgerCli.retrofit.data.TransactionsDataSource
import accountLedgerCli.to_utils.DateTimeUtils
import accountLedgerCli.to_utils.MysqlUtils
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.LinkedHashMap

internal fun balanceSheetOfUser(usersMap: LinkedHashMap<Int, UserResponse>) {
    if (handleUserSelection(
            chosenUserId = chooseUserByIndex(usersMap = usersMap),
            usersMap = usersMap
        )
    ) {

        val transactionsDataSource = TransactionsDataSource()
        println("Contacting Server...")
        val apiResponse2: ResponseHolder<TransactionsResponse>
        val specifiedDate = MysqlUtils.normalDateStringToMysqlDateString(
            normalDateString = getUserInitialTransactionDateFromUsername(username = chosenUser.username).minusDays(
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