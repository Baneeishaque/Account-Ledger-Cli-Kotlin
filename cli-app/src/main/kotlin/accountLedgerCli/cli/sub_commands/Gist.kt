package accountLedgerCli.cli.sub_commands

import account.ledger.library.enums.CommandLineApiMethodGistArgumentsEnum
import account.ledger.library.enums.CommandLineApiMethodsEnum
import account.ledger.library.enums.EnvironmentFileEntryEnum
import account_ledger_library.constants.Constants
import account_ledger_library.utils.GistUtils
import common.utils.library.cli.sub_commands.SubCommandWithUsernameAndPasswordAsArguments
import common.utils.library.utils.ApiUtils.printMissingArgumentMessageForApi
import io.github.cdimascio.dotenv.Dotenv

class Gist(
    override val isDevelopmentMode: Boolean,
    override val dotEnv: Dotenv
) :
    SubCommandWithUsernameAndPasswordAsArguments(

        name = CommandLineApiMethodsEnum.Gist.name,
        actionDescription = "Merge properly formatted Gist Account Ledger Entries to Account Ledger Entries of the Specified User, , Environment file may exist & contains missing arguments",
        isDevelopmentMode = isDevelopmentMode,
        dotEnv = dotEnv
    ) {

    private val gistId: String? = getOptionalTextArgument(

        fullName = CommandLineApiMethodGistArgumentsEnum.gistId.name,
        description = "Id of the Gist which contains formatted Account Ledger Entries"
    )

    private val userId: String? = getOptionalTextArgument(

        fullName = CommandLineApiMethodGistArgumentsEnum.userId.name,
        description = "Id of the User"
    )

    override fun localBeforeExecuteActions() {

        if (isDevelopmentMode) {
            println("gistId = $gistId")
            println("userId = $userId")
        }
    }

    override fun furtherActions(usernameLocal: String, passwordLocal: String) {

        if (gistId.isNullOrEmpty()) {

            val environmentGistId = dotEnv[EnvironmentFileEntryEnum.GIST_ID.name]
            if (environmentGistId.isNullOrEmpty()) {

                printMissingArgumentMessageForApi(argumentSummary = "Gist ID")

            } else {

                if(userId.isNullOrEmpty()){

                    val environmentUserId = dotEnv[EnvironmentFileEntryEnum.USER_ID.name]
                    if (environmentUserId.isNullOrEmpty()) {

                        printMissingArgumentMessageForApi(argumentSummary = "User ID")

                    }else{

                        GistUtils().processGistIdForData(
                            userName = usernameLocal,
                            userId = environmentUserId.toUInt(),
                            gitHubAccessToken = dotEnv[EnvironmentFileEntryEnum.GITHUB_TOKEN.name]
                                ?: Constants.defaultValueForStringEnvironmentVariables,
                            gistId = environmentGistId,
                            isDevelopmentMode = isDevelopmentMode
                        )

//                        GistUtils().processGistIdForTextData(
//                            userName = usernameLocal,
//                            userId = environmentUserId.toUInt(),
//                            gitHubAccessToken = dotEnv[EnvironmentFileEntryEnum.GITHUB_TOKEN.name]
//                                ?: Constants.defaultValueForStringEnvironmentVariables,
//                            gistId = environmentGistId,
//                            isDevelopmentMode = isDevelopmentMode
//                        )
                    }
                }
            }
        }
    }
}
