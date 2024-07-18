package accountLedgerCli.cli

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.constants.EnvironmentalFileEntries
import account.ledger.library.enums.EnvironmentFileEntryEnum
import account.ledger.library.models.InsertTransactionResult
import account.ledger.library.utils.AccountUtils
import accountLedgerCli.cli.sub_commands.*
import accountLedgerCli.utils.GistUtilsInteractiveCli
import account_ledger_library.constants.ConstantsNative
import common.utils.library.constants.ConstantsCommon
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
            EnvironmentFileOperationsInteractive.getEnvironmentVariableValueForBooleanWithDefaultValueInteractive(

                dotEnv = dotEnv,
                environmentVariableName = EnvironmentFileEntryEnum.IS_DEVELOPMENT_MODE.name,
                defaultValue = false

            ).value!!

        internal val walletAccount: EnvironmentVariableForWholeNumber = EnvironmentFileOperationsInteractive.getEnvironmentVariableValueForWholeNumberInteractive(

            environmentVariableName = EnvironmentalFileEntries.walletAccountId.entry.name,
            environmentVariableFormalName = EnvironmentalFileEntries.walletAccountId.formalName,
            dotEnv = dotEnv
        )

        internal val frequent1Account: EnvironmentVariableForWholeNumber = EnvironmentFileOperationsInteractive.getEnvironmentVariableValueForWholeNumberInteractive(
            environmentVariableName = EnvironmentalFileEntries.frequent1AccountId.entry.name,
            environmentVariableFormalName = EnvironmentalFileEntries.frequent1AccountId.formalName,
            dotEnv = dotEnv
        )

        internal val frequent2Account: EnvironmentVariableForWholeNumber = EnvironmentFileOperationsInteractive.getEnvironmentVariableValueForWholeNumberInteractive(
            environmentVariableName = EnvironmentalFileEntries.frequent2AccountId.entry.name,
            environmentVariableFormalName = EnvironmentalFileEntries.frequent2AccountId.formalName,
            dotEnv = dotEnv
        )

        internal val frequent3Account: EnvironmentVariableForWholeNumber = EnvironmentFileOperationsInteractive.getEnvironmentVariableValueForWholeNumberInteractive(
            environmentVariableName = EnvironmentalFileEntries.frequent3AccountId.entry.name,
            environmentVariableFormalName = EnvironmentalFileEntries.frequent3AccountId.formalName,
            dotEnv = dotEnv
        )

        internal val bankAccount: EnvironmentVariableForWholeNumber = EnvironmentFileOperationsInteractive.getEnvironmentVariableValueForWholeNumberInteractive(
            environmentVariableName = EnvironmentalFileEntries.bankAccountId.entry.name,
            environmentVariableFormalName = EnvironmentalFileEntries.bankAccountId.formalName,
            dotEnv = dotEnv
        )

        @OptIn(ExperimentalCli::class)
        @JvmStatic
        fun main(args: Array<String>) {

            if (args.isEmpty()) {

                do {
                    val identifiedUser: String = dotEnv[EnvironmentFileEntryCommonEnum.USER_NAME.name]
                        ?: ConstantsNative.DEFAULT_VALUE_FOR_STRING_ENVIRONMENT_VARIABLES
                    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                        listOfCommands = listOf(
                            "Account Ledger",
                            ConstantsCommon.dashedLineSeparator,
                            "The identified user is $identifiedUser",
                            "",
                            "1 : Login",
                            "2 : Registration",
                            "3 : List Users",
                            "4 : Balance Sheet for an User",
                            "5 : Balance Sheet for all Users",
                            "0 : Exit",
                            "",
                            ConstantsCommon.dashedLineSeparator,
                            "Gist : Merge A/C Ledger from Gist",
                            "",
                            "Enter Your Choice : "
                        )
                    )
                    when (readln()) {
                        "1", "" -> {

                            processInsertTransactionResult(
                                insertTransactionResult = UserOperationsInterActiveWithApiService.login(

                                    username = if (identifiedUser == ConstantsNative.DEFAULT_VALUE_FOR_STRING_ENVIRONMENT_VARIABLES) "" else identifiedUser,
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
                  ToDoUtilsInteractive.showTodo()
                        }

                        "0" -> {
                            println("Thanks...")
                            return
                        }

                        // "Gist", "" -> {
                        "Gist" -> {
                            GistUtilsInteractiveCli.processGistIdInteractive(
                                userName = identifiedUser,
                                userId = if (dotEnv[EnvironmentFileEntryCommonEnum.USER_ID.name] == null) ConstantsNative.DEFAULT_VALUE_FOR_INTEGER_ENVIRONMENT_VARIABLES.toUInt() else dotEnv[EnvironmentFileEntryCommonEnum.USER_ID.name].toUInt(),
                                gitHubAccessToken = dotEnv[EnvironmentFileEntryEnum.GITHUB_TOKEN.name]
                                    ?: ConstantsNative.DEFAULT_VALUE_FOR_STRING_ENVIRONMENT_VARIABLES,
                                gistId = dotEnv[EnvironmentFileEntryEnum.GIST_ID.name]
                                    ?: ConstantsNative.DEFAULT_VALUE_FOR_STRING_ENVIRONMENT_VARIABLES,
                                isDevelopmentMode = isDevelopmentMode
                            )
                            return
                        }

                        else -> {
                            ErrorUtilsInteractive.printInvalidOptionMessage()
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
                    ),
                    GistV3(
                        isDevelopmentMode = isDevelopmentMode,
                        dotEnv = dotEnv
                    ),
                    GistV3ToV4(
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
