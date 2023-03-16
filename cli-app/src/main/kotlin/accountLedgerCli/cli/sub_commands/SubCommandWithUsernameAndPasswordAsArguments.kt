package accountLedgerCli.cli.sub_commands

import account.ledger.library.enums.CommandLineApiMethodCommonArgumentsEnum
import account.ledger.library.enums.EnvironmentFileEntryEnum
import accountLedgerCli.cli.App
import common.utils.library.utils.ApiUtils.printMissingArgumentMessageForApi
import kotlinx.cli.ExperimentalCli

abstract class SubCommandWithUsernameAndPasswordAsArguments(

    name: String,
    actionDescription: String,
    override val isDevelopmentMode: Boolean
) :
    SubCommandWithUsernameAsArgument(

        name = name,
        actionDescription = actionDescription,
        isDevelopmentMode = isDevelopmentMode
    ) {

    val password: String? = getOptionalTextArgument(
        fullName = CommandLineApiMethodCommonArgumentsEnum.Password.name,
        description = "Password of the User"
    )

    abstract fun localBeforeExecuteActions()

    override fun beforeExecuteActions() {

        if (isDevelopmentMode) {

            println("userName = $username")
        }
        localBeforeExecuteActions()
    }

    override fun furtherActions(usernameLocal: String) {

        if (password.isNullOrEmpty()) {

            val environmentPasscode = App.dotenv[EnvironmentFileEntryEnum.PASSWORD.name]
            if (environmentPasscode.isNullOrEmpty()) {

                printMissingArgumentMessageForApi(argumentSummary = "password of the user")

            } else {

                furtherActions(
                    usernameLocal = usernameLocal,
                    passwordLocal = environmentPasscode
                )
            }

        } else {

            furtherActions(
                usernameLocal = usernameLocal,
                passwordLocal = password,
            )
        }
    }

    abstract fun furtherActions(usernameLocal: String, passwordLocal: String)
}