package accountLedgerCli.cli

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.cli.EnvironmentalFileEntries
import account.ledger.library.constants.Constants
import account.ledger.library.enums.EnvironmentFileEntryEnum
import account.ledger.library.models.InsertTransactionResult
import account.ledger.library.models.Root
import accountLedgerCli.cli.sub_commands.BalanceSheet
import accountLedgerCli.utils.AccountUtils
import common.utils.library.utils.*
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.cli.ArgParser
import kotlinx.cli.ExperimentalCli
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.nio.file.Paths
import common.utils.library.constants.Constants as CommonConstants

class App {
    companion object {

        var dateTimeInText: String = DateTimeUtils.getCurrentDateTimeText()

        var fromAccount: AccountResponse = AccountUtils.blankAccount
        var toAccount: AccountResponse = AccountUtils.blankAccount
        var viaAccount: AccountResponse = AccountUtils.blankAccount

        var transactionParticulars: String = ""
        var transactionAmount: Float = 0F

        private val commandLinePrintMenu = CommandLinePrintMenu()

        @JvmStatic
        val commandLinePrintMenuWithEnterPrompt =
            CommandLinePrintMenuWithEnterPrompt(commandLinePrintMenu)

        @JvmStatic
        internal val commandLinePrintMenuWithTryPrompt =
            CommandLinePrintMenuWithTryPrompt(commandLinePrintMenu)

        @JvmStatic
        internal val commandLinePrintMenuWithContinuePrompt =
            CommandLinePrintMenuWithContinuePrompt(commandLinePrintMenu)

        @JvmStatic
        val dotenv: Dotenv = dotenv {
            directory = Paths.get("").toAbsolutePath().toString()
            ignoreIfMissing = true
        }

        @JvmStatic
        val isDevelopmentMode: Boolean =
            EnvironmentFileOperations.getEnvironmentVariableValueForBooleanWithDefaultValue(
                dotenv = dotenv,
                environmentVariableName = EnvironmentalFileEntries.isDevelopmentMode.entryName.name,
                defaultValue = false
            ).value!!

        @OptIn(ExperimentalCli::class)
        @JvmStatic
        fun main(args: Array<String>) {

            if (args.isEmpty()) {

                do {
                    val identifiedUser: String = dotenv[EnvironmentFileEntryEnum.USER_NAME.name]
                        ?: Constants.defaultValueForStringEnvironmentVariables
                    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                        listOfCommands = listOf(
                            "Account Ledger",
                            CommonConstants.dashedLineSeparator,
                            "The identified user is $identifiedUser",
                            "",
                            "1 : Login",
                            "2 : Registration",
                            "3 : List Users",
                            "4 : Balance Sheet for an User",
                            "5 : Balance Sheet for all Users",
                            "0 : Exit",
                            "",
                            CommonConstants.dashedLineSeparator,
                            "Gist : Merge A/C Ledger from Gist",
                            "",
                            "Enter Your Choice : "
                        )
                    )
                    when (readln()) {
                        "1", "" -> {
                            // "1" -> {

                            processInsertTransactionResult(
                                insertTransactionResult = UserOperations.login(

                                    username = if (identifiedUser == Constants.defaultValueForStringEnvironmentVariables) "" else identifiedUser,
                                    password = dotenv[EnvironmentFileEntryEnum.PASSWORD.name] ?: "",
                                    fromAccount = fromAccount,
                                    viaAccount = viaAccount,
                                    toAccount = toAccount,
                                    dateTimeInText = dateTimeInText,
                                    transactionParticulars = transactionParticulars,
                                    transactionAmount = transactionAmount,
                                    isConsoleMode = true,
                                    isDevelopmentMode = isDevelopmentMode
                                )
                            )
                        }

                        "3" -> {

                            processInsertTransactionResult(
                                insertTransactionResult = UserOperations.listUsers(
                                    fromAccount = fromAccount,
                                    viaAccount = viaAccount,
                                    toAccount = toAccount,
                                    dateTimeInText = dateTimeInText,
                                    transactionParticulars = transactionParticulars,
                                    transactionAmount = transactionAmount,
                                    isConsoleMode = true,
                                    isDevelopmentMode = isDevelopmentMode
                                )
                            )
                        }

                        "2", "4", "5" -> {
                            ToDoUtils.showTodo()
                        }

                        "0" -> {
                            println("Thanks...")
                            return
                        }

                        "Gist" -> {
                            // "Gist", "" -> {

                            runBlocking {

                                // TODO : Refactor to HttpClient
                                HttpClient {
                                    expectSuccess = true
                                    install(Logging) {

                                        logger = Logger.DEFAULT
                                        level = if (isDevelopmentMode) LogLevel.ALL else LogLevel.NONE
                                    }
                                    install(Auth) {
                                        bearer {
                                            BearerTokens(
                                                accessToken = dotenv[EnvironmentFileEntryEnum.GITHUB_TOKEN.name]
                                                    ?: Constants.defaultValueForStringEnvironmentVariables,
                                                refreshToken = ""
                                            )
                                        }
                                    }
                                    install(ContentNegotiation) {
                                        json(Json {
                                            prettyPrint = true
                                            ignoreUnknownKeys = true
                                        })
                                    }
                                }.use { client ->

                                    // TODO: inline use of serialization
                                    // TODO : Refactor get gistResponse
                                    val gistResponse: Root =
                                        client.get("https://api.github.com/gists/${dotenv[EnvironmentFileEntryEnum.GIST_ID.name] ?: Constants.defaultValueForStringEnvironmentVariables}") {
                                            onDownload { bytesSentTotal, contentLength ->

                                                if (isDevelopmentMode) {

                                                    println("Received $bytesSentTotal bytes from $contentLength")
                                                }
                                            }
                                        }.body()
                                    val gistContent = gistResponse.files.mainTxt.content
                                    val gistContentLines: List<String> = gistContent.lines()
                                    if (isDevelopmentMode) {
                                        // println("Gist : $gistResponse")
                                        println("Gist Contents")
                                        // println(gistContent)
                                        gistContentLines.forEach { println(it) }
                                        println(CommonConstants.dashedLineSeparator)
                                    }

                                    // TODO : Parse Gist Contents
                                    // var isWalletHeaderFound: Boolean = false
                                    val accountHeaderIdentifier: String = Constants.accountHeaderIdentifier
                                    var currentAccountId = 0u
                                    val processedLedger: LinkedHashMap<UInt, MutableList<String>> = LinkedHashMap()
                                    var isPreviousLineIsNotAccountHeader = false

                                    var i = 1u
                                    gistContentLines.forEach { line: String ->

//                                        println(line)

                                        if (line.contains(other = accountHeaderIdentifier)) {

//                                            println("$i : $line")

                                            val accountName = line.replace(
                                                regex = accountHeaderIdentifier.toRegex(),
                                                replacement = ""
                                            ).trim()
                                            if (accountName == Constants.walletAccountHeaderIdentifier) {

                                                // TODO : set currentAccountId from environment variable
                                                currentAccountId = 6u
                                            }
                                            // TODO : check for custom bank name
                                            else if (accountName == Constants.bankAccountHeaderIdentifier) {

//                                                      TODO : set currentAccountId from environment variable
                                                currentAccountId = 11u
                                            }
//                                            println(message = "currentAccountId = $currentAccountId")
                                            isPreviousLineIsNotAccountHeader = false

                                        } else {

                                            if (line.isNotEmpty()) {

//                                                println(line)
                                                if (!line.contains(other = Constants.accountHeaderUnderlineCharacter)) {

//                                                    println(line)
                                                    addLineToCurrentAccountLedger(
                                                        ledgerToProcess = processedLedger,
                                                        desiredAccountId = currentAccountId,
                                                        desiredLine = line
                                                    )
                                                } else {

                                                    if (isPreviousLineIsNotAccountHeader) {

//                                                        println("$i : $line")
//                                                        println(line)
                                                        addLineToCurrentAccountLedger(
                                                            ledgerToProcess = processedLedger,
                                                            desiredAccountId = currentAccountId,
                                                            desiredLine = line
                                                        )
                                                    }
                                                }
                                            }
                                            isPreviousLineIsNotAccountHeader = true
                                        }
                                        i++
                                    }

                                    //TODO : Refactor Process Ledger
                                    processedLedger.forEach { (localCurrentAccountId: UInt, currentAccountLedgerLines: List<String>) ->

                                        println("currentAccountId = $localCurrentAccountId")
                                        currentAccountLedgerLines.forEach { ledgerLine: String ->

//                                            println(ledgerLine)
                                            val indexOfFirstSpace: Int = ledgerLine.indexOf(char = ' ')
                                            var dateOrAmount = ""
                                            if (indexOfFirstSpace != -1) {

                                                dateOrAmount = ledgerLine.substring(
                                                    startIndex = 0,
                                                    endIndex = indexOfFirstSpace
                                                )
                                            } else if (!ledgerLine.contains(other = Constants.dateUnderlineCharacter)) {

                                                dateOrAmount = ledgerLine.trim()
                                            }
                                            if (dateOrAmount.isNotEmpty()) {

                                                println(dateOrAmount)
                                            }
                                        }
                                    }
                                }
                            }
                            return
                        }
                        else -> {
                            invalidOptionMessage()
                        }
                    }
                } while (true)

            } else {

                val parser = ArgParser(programName = "Account-Ledger-Cli", strictSubcommandOptionsOrder = true)

                val balanceSheet = BalanceSheet()
                parser.subcommands(balanceSheet)

                parser.parse(args = args)
            }
        }

        private fun processInsertTransactionResult(insertTransactionResult: InsertTransactionResult) {

            dateTimeInText = insertTransactionResult.dateTimeInText
            transactionParticulars = insertTransactionResult.transactionParticulars
            transactionAmount = insertTransactionResult.transactionAmount
        }

        private fun addLineToCurrentAccountLedger(
            ledgerToProcess: LinkedHashMap<UInt, MutableList<String>>,
            desiredAccountId: UInt,
            desiredLine: String
        ) {
            val currentAccountLedgerLines: MutableList<String> =
                ledgerToProcess.getOrDefault(key = desiredAccountId, defaultValue = mutableListOf())
            currentAccountLedgerLines.add(element = desiredLine)
            ledgerToProcess[desiredAccountId] = currentAccountLedgerLines
        }
    }
}
