package accountLedgerCli.cli

import account.ledger.library.enums.*
import account.ledger.library.models.BalanceSheetDataModel
import kotlinx.cli.*
import kotlinx.serialization.json.Json

@OptIn(ExperimentalCli::class)
class BalanceSheet: Subcommand(
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

            val environmentUsername = App.dotenv[EnvironmentFileEntryEnum.USER_NAME.name]
            val environmentPassword = App.dotenv[EnvironmentFileEntryEnum.PASSWORD.name]

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
            fromAccount = App.fromAccount,
            viaAccount = App.viaAccount,
            toAccount = App.toAccount,
            dateTimeInText = App.dateTimeInText,
            transactionParticulars = App.transactionParticulars,
            transactionAmount = App.transactionAmount,
            isConsoleMode = true,
            isDevelopmentMode = App.isDevelopmentMode
        )
    }
}
