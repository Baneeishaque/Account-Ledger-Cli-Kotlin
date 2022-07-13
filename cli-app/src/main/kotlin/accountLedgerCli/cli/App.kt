package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.models.BalanceSheetDataModel
import accountLedgerCli.to_utils.CommandLinePrintMenu
import accountLedgerCli.to_utils.CommandLinePrintMenuWithContinuePrompt
import accountLedgerCli.to_utils.CommandLinePrintMenuWithEnterPrompt
import accountLedgerCli.to_utils.CommandLinePrintMenuWithTryPrompt
import accountLedgerCli.to_utils.DateTimeUtils
import accountLedgerCli.to_utils.ToDoUtils
import accountLedgerCli.utils.AccountUtils
import accountLedgerCli.utils.UserUtils
import io.github.cdimascio.dotenv.dotenv
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.default
import kotlinx.cli.optional
import kotlinx.cli.Subcommand
import kotlinx.serialization.json.Json
import java.nio.file.Paths
import java.time.LocalDateTime

class App {
    companion object {
        @JvmStatic
        internal var dateTimeString = LocalDateTime.now().format(DateTimeUtils.normalDateTimePattern)

        @JvmStatic
        internal var fromAccount = AccountUtils.getBlankAccount()

        @JvmStatic
        internal var viaAccount = AccountUtils.getBlankAccount()

        @JvmStatic
        internal var toAccount = AccountUtils.getBlankAccount()

        @JvmStatic
        internal var transactionParticulars = ""

        @JvmStatic
        internal var transactionAmount = 0F

        @JvmStatic
        internal var chosenUser = UserUtils.getBlankUser()

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
        internal val dotenv = dotenv {
            directory = Paths.get("").toAbsolutePath().toString()
            ignoreIfMissing = true
        }

        @JvmStatic
        @OptIn(ExperimentalCli::class)
        fun main(args: Array<String>) {
            if (args.isEmpty()) {
                do {
                    val identifiedUser: String = dotenv[EnvironmentFileEntryEnum.USER_NAME.name] ?: Constants.defaultValueForStringEnvironmentVariables
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
                    val choice = readLine()
                    when (choice) {
                        "1" -> UserOperations.login(
                            username = if (identifiedUser == Constants.defaultValueForStringEnvironmentVariables) "" else identifiedUser,
                            password = dotenv[EnvironmentFileEntryEnum.PASSWORD.name] ?: ""
                        )

                        "3" -> UserOperations.listUsers()
                        "2", "4", "5" -> ToDoUtils.showTodo()
                        "0" -> println("Thanks...")
                        else -> invalidOptionMessage()
                    }
                } while (choice != "0")

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

                                    UserOperations.login(
                                        username = environmentUsername,
                                        password = environmentPassword,
                                        isNotApiCall = false,
                                        apiMethod = CommandLineApiMethodsEnum.BalanceSheet.name,
                                        apiMethodOptions = linkedMapOf(
                                            CommandLineApiMethodBalanceSheetOptionsEnum.refineLevel.name to refineLevel,
                                            CommandLineApiMethodBalanceSheetOptionsEnum.outputFormat.name to outputFormat
                                        )
                                    )
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

                                UserOperations.login(username = username!!, password = password!!)
                            }
                        }
                    }
                }

                val balanceSheet = BalanceSheet()
                parser.subcommands(balanceSheet)

                parser.parse(args = args)
            }
        }

    }
}