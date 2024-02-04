package accountLedgerCli.cli

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.constants.EnvironmentalFileEntries
import account.ledger.library.enums.EnvironmentFileEntryEnum
import account.ledger.library.models.InsertTransactionResult
import account.ledger.library.utils.AccountUtils
import accountLedgerCli.cli.sub_commands.*
import accountLedgerCli.utils.GistUtilsInteractive
import account_ledger_library.constants.ConstantsNative
import common.utils.library.constants.CommonConstants
import common.utils.library.enums.EnvironmentFileEntryCommonEnum
import common.utils.library.utils.*
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import kotlinx.cli.ArgParser
import kotlinx.cli.ExperimentalCli
import java.nio.file.Paths

class App {
    companion object {

        var dateTimeInText: String = DateTimeUtils.getCurrentNormalDateTimeInText()

        var fromAccount: AccountResponse = AccountUtils.blankAccount
        var toAccount: AccountResponse = AccountUtils.blankAccount
        var viaAccount: AccountResponse = AccountUtils.blankAccount

        var transactionParticulars: String = ""
        var transactionAmount: Float = 0F

        @JvmStatic
        internal val commandLinePrintMenu = CommandLinePrintMenu()

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
        internal val commandLinePrintMenuWithBackPrompt =
            CommandLinePrintMenuWithBackPrompt(commandLinePrintMenu)

        @JvmStatic
        var dotEnv: Dotenv = reloadDotEnv()

        @JvmStatic
        internal fun reloadDotEnv() = dotenv {
            directory = Paths.get("").toAbsolutePath().toString()
            ignoreIfMissing = true
        }

        @JvmStatic
        val isDevelopmentMode: Boolean =
            EnvironmentFileOperations.getEnvironmentVariableValueForBooleanWithDefaultValue(
                dotEnv = dotEnv,
                environmentVariableName = EnvironmentalFileEntries.isDevelopmentMode.entryName.name,
                defaultValue = false
            ).value!!

        @OptIn(ExperimentalCli::class)
        @JvmStatic
        fun main(args: Array<String>) {

            if (args.isEmpty()) {

                do {
                    val identifiedUser: String = dotEnv[EnvironmentFileEntryCommonEnum.USER_NAME.name]
                        ?: ConstantsNative.defaultValueForStringEnvironmentVariables
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

                            processInsertTransactionResult(
                                insertTransactionResult = UserOperationsInterActiveWithApiService.login(

                                    username = if (identifiedUser == ConstantsNative.defaultValueForStringEnvironmentVariables) "" else identifiedUser,
                                    password = dotEnv[EnvironmentFileEntryCommonEnum.PASSWORD.name] ?: "",
                                    fromAccount = fromAccount,
                                    viaAccount = viaAccount,
                                    toAccount = toAccount,
                                    dateTimeInText = dateTimeInText,
                                    transactionParticulars = transactionParticulars,
                                    transactionAmount = transactionAmount,
                                    isConsoleMode = true,
                                    isDevelopmentMode = isDevelopmentMode,
                                    dotEnv = dotEnv
                                )
                            )
                        }

                        "3" -> {

                            processInsertTransactionResult(
                                insertTransactionResult = UserOperationsInterActiveWithApiService.listUsers(
                                    fromAccount = fromAccount,
                                    viaAccount = viaAccount,
                                    toAccount = toAccount,
                                    dateTimeInText = dateTimeInText,
                                    transactionParticulars = transactionParticulars,
                                    transactionAmount = transactionAmount,
                                    isConsoleMode = true,
                                    isDevelopmentMode = isDevelopmentMode,
                                    dotEnv = dotEnv
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
                            GistUtilsInteractive.processGistIdInteractive(
                                userName = identifiedUser,
                                userId = if (dotEnv[EnvironmentFileEntryCommonEnum.USER_ID.name] == null) ConstantsNative.defaultValueForIntegerEnvironmentVariables.toUInt() else dotEnv[EnvironmentFileEntryCommonEnum.USER_ID.name].toUInt(),
                                gitHubAccessToken = dotEnv[EnvironmentFileEntryEnum.GITHUB_TOKEN.name]
                                    ?: ConstantsNative.defaultValueForStringEnvironmentVariables,
                                gistId = dotEnv[EnvironmentFileEntryEnum.GIST_ID.name]
                                    ?: ConstantsNative.defaultValueForStringEnvironmentVariables,
                                isDevelopmentMode = isDevelopmentMode
                            )
                            return
                        }

                        else -> {
                            InteractiveUtils.invalidOptionMessage()
                        }
                    }
                } while (true)

            } else {

                val parser = ArgParser(programName = "Account-Ledger-Cli", strictSubcommandOptionsOrder = true)
                parser.subcommands(
                    BalanceSheet(
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    ),
                    Gist(
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    ),
                    InsertTransaction(isDevelopmentMode = isDevelopmentMode),
                    GetAccounts(
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    ),
                    GetAccountsUrl(
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    ),
                    GistV2(
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    ),
                    ViewTransactionsOfAnAccount(

                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    )
                )
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
