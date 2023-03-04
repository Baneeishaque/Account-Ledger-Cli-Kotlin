package accountLedgerCli.cli.sub_commands

import account.ledger.library.enums.CommandLineApiMethodCommonArgumentsEnum
import account.ledger.library.enums.EnvironmentFileEntryEnum
import account.ledger.library.models.CommonDataModel
import accountLedgerCli.cli.App
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.cli.optional
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

@OptIn(ExperimentalCli::class)
abstract class SubCommandWithCommonArguments(
    name: String,
    actionDescription: String
) :
    Subcommand(
        name = name,
        actionDescription = actionDescription
    ) {

    val username: String? = getOptionalTextArgument(
        fullName = CommandLineApiMethodCommonArgumentsEnum.Username.name,
        description = "Username of the User"
    )

    val password: String? = getOptionalTextArgument(
        fullName = CommandLineApiMethodCommonArgumentsEnum.Password.name,
        description = "Password of the User"
    )

    override fun execute() {

        beforeExecuteActions()

        println("userName = $username")
        println("passWord = $password")

        if (username.isNullOrEmpty()) {

            val environmentUsername = App.dotenv[EnvironmentFileEntryEnum.USER_NAME.name]
            if (environmentUsername.isNullOrEmpty()) {

                printMissingArgumentMessage(message ="username of the user" )

            } else {

                checkForPasscode<Any?>(username = environmentUsername)
            }
        } else {

            checkForPasscode<Any?>(username = username)
        }
    }

    abstract fun beforeExecuteActions()

    private fun <T> checkForPasscode(username: String) {

        if (password.isNullOrEmpty()) {

            val environmentPasscode = App.dotenv[EnvironmentFileEntryEnum.PASSWORD.name]
            if (environmentPasscode.isNullOrEmpty()) {

                printMissingArgumentMessage(message ="password of the user" )

            } else {

                furtherActions(
                    usernameLocal = username,
                    passwordLocal = environmentPasscode
                )
            }

        } else {

            furtherActions(
                usernameLocal = username,
                passwordLocal = password,
            )
        }
    }

    abstract fun furtherActions(usernameLocal: String, passwordLocal: String)

    fun getOptionalTextArgument(fullName: String, description: String): String? {

        val optionalTextArgument: String? by argument(

            type = ArgType.String,
            fullName = fullName,
            description = description

        ).optional()

        return optionalTextArgument
    }

    private fun printMissingArgumentMessage(
        message: String
    ) {

        print(
            Json.encodeToString(
                serializer = CommonDataModel.serializer(Unit.serializer()),
                value = CommonDataModel(
                    status = 1,
                    error = "Missing $message in command line arguments & environment file"
                )
            )
        )
    }
}