package accountLedgerCli.cli.sub_commands

import account.ledger.library.constants.Constants
import account.ledger.library.enums.CommandLineApiMethodGistArgumentsEnum
import account.ledger.library.enums.CommandLineApiMethodsEnum
import account.ledger.library.enums.EnvironmentFileEntryEnum
import accountLedgerCli.cli.App
import account.ledger.library.utils.GistUtils

class Gist(override val isDevelopmentMode: Boolean) : SubCommandWithCommonArguments(
    name = CommandLineApiMethodsEnum.Gist.name,
    actionDescription = "Merge properly formatted Gist Account Ledger Entries to Account Ledger Entries of the Specified User, , Environment file may exist & contains missing arguments",
    isDevelopmentMode = isDevelopmentMode
) {
    private val gistId: String? = getOptionalTextArgument(
        fullName = CommandLineApiMethodGistArgumentsEnum.gistId.name,
        description = "Id of the Gist which contains formatted Account Ledger Entries"
    )

    override fun beforeExecuteActions() {

        if (isDevelopmentMode) {
            println("gistId = $gistId")
        }
    }

    override fun furtherActions(usernameLocal: String, passwordLocal: String) {

        if (gistId.isNullOrEmpty()) {

            val environmentGistId = App.dotenv[EnvironmentFileEntryEnum.GIST_ID.name]
            if (environmentGistId.isNullOrEmpty()) {

                printMissingArgumentMessage(argumentSummary = "Gist ID")

            } else {

                GistUtils.processGistId(
                    userName = usernameLocal,
                    gitHubAccessToken = App.dotenv[EnvironmentFileEntryEnum.GITHUB_TOKEN.name]
                        ?: Constants.defaultValueForStringEnvironmentVariables,
                    gistId = environmentGistId,
                    isDevelopmentMode = isDevelopmentMode
                )
            }
        }
    }
}
