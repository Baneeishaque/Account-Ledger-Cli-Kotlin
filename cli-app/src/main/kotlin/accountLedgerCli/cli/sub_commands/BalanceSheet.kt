package accountLedgerCli.cli.sub_commands

import account.ledger.library.enums.BalanceSheetOutputFormatsEnum
import account.ledger.library.enums.BalanceSheetRefineLevelEnum
import account.ledger.library.enums.CommandLineApiMethodBalanceSheetOptionsEnum
import account.ledger.library.enums.CommandLineApiMethodsEnum
import accountLedgerCli.cli.App
import accountLedgerCli.cli.UserOperationsInterActiveWithApiService
import common.utils.library.cli.sub_commands.SubCommandWithUsernameAndPasswordAsArguments
import io.github.cdimascio.dotenv.Dotenv
import kotlinx.cli.ArgType
import kotlinx.cli.default

class BalanceSheet(

    override val isDevelopmentMode: Boolean,
    override val dotEnv: Dotenv

) : SubCommandWithUsernameAndPasswordAsArguments(

    name = CommandLineApiMethodsEnum.BalanceSheet.name,
    actionDescription = "Provides Balance Sheet Ledger of the Specified User, Currently in JSON format, Default Balance Sheet Refine Level is [Excluding Open Balances, Misc. Incomes, Investment Returns, Family & Expense Accounts], Environment file may exist & contains missing arguments",
    isDevelopmentMode = isDevelopmentMode,
    dotEnv = dotEnv
) {

    private val refineLevel: BalanceSheetRefineLevelEnum by option(

        type = ArgType.Choice<BalanceSheetRefineLevelEnum> { it.name.lowercase() },
        fullName = CommandLineApiMethodBalanceSheetOptionsEnum.refineLevel.name,
        shortName = "r",
        description = "Refine Level of the Balance Sheet Ledger"

    ).default(BalanceSheetRefineLevelEnum.WITHOUT_EXPENSE_ACCOUNTS)

    private val outputFormat: BalanceSheetOutputFormatsEnum by option(

        type = ArgType.Choice<BalanceSheetOutputFormatsEnum> { it.name.lowercase() },
        fullName = CommandLineApiMethodBalanceSheetOptionsEnum.outputFormat.name,
        shortName = "o",
        description = "Output Format of the Balance Sheet Ledger"

    ).default(BalanceSheetOutputFormatsEnum.JSON)

    override fun localBeforeExecuteActions() {

        println("userName = $username")
        println("passWord = $password")
        println("refineLevel = $refineLevel")
        println("outputFormat = $outputFormat")
    }

    override fun furtherActions(usernameLocal: String, passwordLocal: String) {

        invokeUserLoginByApi(

            usernameLocal = usernameLocal,
            passwordLocal = passwordLocal,
        )
    }

    private fun invokeUserLoginByApi(usernameLocal: String, passwordLocal: String) {

        UserOperationsInterActiveWithApiService.login(

            username = usernameLocal,
            password = passwordLocal,
            isNotApiCall = false,
            apiMethod = CommandLineApiMethodsEnum.BalanceSheet.name,
            apiMethodOptions = linkedMapOf(

                CommandLineApiMethodBalanceSheetOptionsEnum.refineLevel.name to refineLevel,
                CommandLineApiMethodBalanceSheetOptionsEnum.outputFormat.name to outputFormat
            ),
            fromAccount = App.fromAccount,
            viaAccount = App.viaAccount,
            toAccount = App.toAccount,
            dateTimeInText = App.dateTimeInText,
            transactionParticulars = App.transactionParticulars,
            transactionAmount = App.transactionAmount,
            isConsoleMode = true,
            isDevelopmentMode = isDevelopmentMode,
            dotEnv = dotEnv
        )
    }
}
