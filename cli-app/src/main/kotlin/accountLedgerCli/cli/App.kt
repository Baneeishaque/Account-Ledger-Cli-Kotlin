package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.enums.*
import accountLedgerCli.models.BalanceSheetDataModel
import accountLedgerCli.models.InsertTransactionResult
import accountLedgerCli.to_utils.*
import accountLedgerCli.utils.AccountUtils
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import kotlinx.cli.*
import kotlinx.serialization.json.Json
import java.nio.file.Paths

class App {
    companion object {

        private var dateTimeInText: String = DateTimeUtils.getCurrentDateTimeText()

        private var fromAccount: AccountResponse = AccountUtils.blankAccount
        private var toAccount: AccountResponse = AccountUtils.blankAccount
        private var viaAccount: AccountResponse = AccountUtils.blankAccount

        private var transactionParticulars: String = ""
        private var transactionAmount: Float = 0F

        @JvmStatic
        internal var userAccountsMap = LinkedHashMap<UInt, AccountResponse>()
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
        @OptIn(ExperimentalCli::class)
        fun main(args: Array<String>) {

            if (args.isEmpty()) {

                do {
                    val identifiedUser: String = dotenv[EnvironmentFileEntryEnum.USER_NAME.name]
                        ?: Constants.defaultValueForStringEnvironmentVariables
                    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                        listOfCommands = listOf(
                            "Account Ledger",
                            "---------------",
                            "The identified user is $identifiedUser",
                            "",
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
                    val choice: String = readLine()!!
                    when (choice) {
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
                                    transactionAmount = transactionAmount
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