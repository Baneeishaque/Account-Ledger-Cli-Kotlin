package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.constants.Constants
import accountLedgerCli.enums.*
import accountLedgerCli.models.BalanceSheetDataModel
import accountLedgerCli.models.InsertTransactionResult
import accountLedgerCli.to_utils.*
import accountLedgerCli.utils.AccountUtils
import accountLedgerCli.to_constants.Constants as CommonConstants
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import kotlinx.cli.*
import kotlinx.serialization.json.Json
import java.nio.file.Paths
import io.ktor.client.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.*
import kotlinx.coroutines.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import accountLedgerCli.models.Root
import io.ktor.client.call.*

class App {
    companion object {

        private var dateTimeInText: String = DateTimeUtils.getCurrentDateTimeText()

        private var fromAccount: AccountResponse = AccountUtils.blankAccount
        private var toAccount: AccountResponse = AccountUtils.blankAccount
        private var viaAccount: AccountResponse = AccountUtils.blankAccount

        private var transactionParticulars: String = ""
        private var transactionAmount: Float = 0F

        private val commandLinePrintMenu = CommandLinePrintMenu()

        @JvmStatic
        internal val commandLinePrintMenuWithEnterPrompt =
            CommandLinePrintMenuWithEnterPrompt(commandLinePrintMenu)

        @JvmStatic
        internal val commandLinePrintMenuWithTryPrompt =
            CommandLinePrintMenuWithTryPrompt(commandLinePrintMenu)

        @JvmStatic
        internal val commandLinePrintMenuWithContinuePrompt =
            CommandLinePrintMenuWithContinuePrompt(commandLinePrintMenu)

        @JvmStatic
        internal val dotenv: Dotenv = dotenv {
            directory = Paths.get("").toAbsolutePath().toString()
            ignoreIfMissing = true
        }

        @JvmStatic
        internal val isDevelopmentMode: Boolean =
            EnvironmentFileOperations.getEnvironmentVariableValueForBooleanWithDefaultValue(
                dotenv = dotenv,
                environmentVariableName = EnvironmentalFileEntries.isDevelopmentMode.entryName.name,
                defaultValue = false
            ).value!!

        @JvmStatic
        @OptIn(ExperimentalCli::class)
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
                    when (readLine()!!) {
                        // "1", "" -> {
                        "1" -> {

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

                        // "Gist" -> {
                        "Gist", "" -> {

                            runBlocking {

                                HttpClient() {
                                    expectSuccess = true
                                    install(Logging) {

                                        logger = Logger.DEFAULT
                                        level = LogLevel.ALL
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

                                    val gistResponse: Root =
                                        client.get("https://api.github.com/gists/${dotenv[EnvironmentFileEntryEnum.GIST_ID.name] ?: Constants.defaultValueForStringEnvironmentVariables}") {
                                            onDownload { bytesSentTotal, contentLength ->
                                                println("Received $bytesSentTotal bytes from $contentLength")
                                            }
                                        }.body()
                                    val gistContent = gistResponse.files.mainTxt.content
                                    if(isDevelopmentMode){
                                        // println("Gist : $gistResponse")
                                        println("Gist Content : \n$gistContent")
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

                class BalanceSheet : Subcommand(
                    name = CommandLineApiMethodsEnum.BalanceSheet.name,
                    actionDescription = "Provides Balance Sheet Ledger of the Specified User, Currently in JSON format, Default Balance Sheet Refine Level is [Excluding Open Balances, Misc. Incomes, Investment Returns, Family & Expense Accounts], Environment file must exist & contain proper values"
                ) {
                    val username by argument(
                        type = ArgType.String,
                        fullName = CommandLineApiMethodBalanceSheetArgumentsEnum.Username.name,
                        description = "Username of the User"
                    ).optional()
                    val password by argument(
                        type = ArgType.String,
                        fullName = CommandLineApiMethodBalanceSheetArgumentsEnum.Password.name,
                        description = "Password of the User"
                    ).optional()

                    val refineLevel by option(
                        type = ArgType.Choice<BalanceSheetRefineLevelEnum> { it.name.lowercase() },
                        fullName = CommandLineApiMethodBalanceSheetOptionsEnum.refineLevel.name,
                        shortName = "r",
                        description = "Refine Level of the Balance Sheet Ledger"
                    ).default(BalanceSheetRefineLevelEnum.WITHOUT_EXPENSE_ACCOUNTS)
                    val outputFormat by option(
                        type = ArgType.Choice<BalanceSheetOutputFormatsEnum> { it.name.lowercase() },
                        fullName = CommandLineApiMethodBalanceSheetOptionsEnum.outputFormat.name,
                        shortName = "o",
                        description = "Output Format of the Balance Sheet Ledger"
                    ).default(BalanceSheetOutputFormatsEnum.JSON)

                    override fun execute() {

//                        println("userName = $username")
//                        println("passWord = $password")
//                        println("refineLevel = $refineLevel")
//                        println("outputFormat = $outputFormat")

                        if (username.isNullOrEmpty()) {

                            val environmentUsername = dotenv[EnvironmentFileEntryEnum.USER_NAME.name]
                            val environmentPassword = dotenv[EnvironmentFileEntryEnum.PASSWORD.name]

                            if (environmentUsername.isNullOrEmpty()) {

//                                println("Please provide username of the user")
                                print(
                                    Json.encodeToString(
                                        serializer = BalanceSheetDataModel.serializer(),
                                        value = BalanceSheetDataModel(
                                            status = 1,
                                            error = "Missing username of the user"
                                        )
                                    )
                                )

                            } else {

                                if (environmentPassword.isNullOrEmpty()) {

//                                    println("Please provide password of the user (environment file)")
                                    print(
                                        Json.encodeToString(
                                            serializer = BalanceSheetDataModel.serializer(),
                                            value = BalanceSheetDataModel(
                                                status = 1,
                                                error = "Missing password of the user (environment file)"
                                            )
                                        )
                                    )

                                } else {

                                    invokeUserLoginByApi(environmentUsername, environmentPassword)
                                }
                            }
                        } else {

                            if (password.isNullOrEmpty()) {

//                                println("Please provide password of the user (command line)")
                                print(
                                    Json.encodeToString(
                                        serializer = BalanceSheetDataModel.serializer(),
                                        value = BalanceSheetDataModel(
                                            status = 1,
                                            error = "Missing password of the user (command line)"
                                        )
                                    )
                                )

                            } else {

                                invokeUserLoginByApi(username!!, password!!)
                            }
                        }
                    }

                    private fun invokeUserLoginByApi(usernameLocal: String, passwordLocal: String) {

                        UserOperations.login(
                            username = usernameLocal,
                            password = passwordLocal,
                            isNotApiCall = false,
                            apiMethod = CommandLineApiMethodsEnum.BalanceSheet.name,
                            apiMethodOptions = linkedMapOf(
                                CommandLineApiMethodBalanceSheetOptionsEnum.refineLevel.name to refineLevel,
                                CommandLineApiMethodBalanceSheetOptionsEnum.outputFormat.name to outputFormat
                            ),
                            fromAccount = fromAccount,
                            viaAccount = viaAccount,
                            toAccount = toAccount,
                            dateTimeInText = dateTimeInText,
                            transactionParticulars = transactionParticulars,
                            transactionAmount = transactionAmount,
                            isConsoleMode = true,
                            isDevelopmentMode = isDevelopmentMode
                        )
                    }
                }

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
    }
}