package accountLedgerCli.cli.sub_commands

import accountLedgerCli.enums.CommandLineApiMethodGistArgumentsEnum
import accountLedgerCli.enums.CommandLineApiMethodsEnum
import account.ledger.library.enums.EnvironmentFileEntryEnum
import account_ledger_library.constants.ConstantsNative
import account_ledger_library.utils.GistUtils
import common.utils.library.cli.sub_commands.SubCommandWithUsernameAndPasswordAsArguments
import common.utils.library.enums.EnvironmentFileEntryCommonEnum
import common.utils.library.utils.ApiUtilsCommon
import io.github.cdimascio.dotenv.Dotenv

class Gist(
    override val isDevelopmentMode: Boolean,
    override val dotEnv: Dotenv
) :
    SubCommandWithUsernameAndPasswordAsArguments(

        name = CommandLineApiMethodsEnum.Gist.name,
        actionDescription = "Merge properly formatted Gist Account Ledger Entries to Account Ledger Entries of the Specified User, Environment file may exist & contains missing arguments",
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

                ApiUtilsCommon.printMissingArgumentMessageForApi(argumentSummary = "Gist ID")

            } else {

                if (userId.isNullOrEmpty()) {

                    val environmentUserId = dotEnv[EnvironmentFileEntryCommonEnum.USER_ID.name]
                    if (environmentUserId.isNullOrEmpty()) {

                        ApiUtilsCommon.printMissingArgumentMessageForApi(argumentSummary = "User ID")

                    } else {

                        GistUtils().processGistIdForData(
                            userName = usernameLocal,
                            userId = environmentUserId.toUInt(),
                            gitHubAccessToken = dotEnv[EnvironmentFileEntryEnum.GITHUB_TOKEN.name]
                                ?: ConstantsNative.defaultValueForStringEnvironmentVariables,
                            gistId = environmentGistId,
                            isDevelopmentMode = isDevelopmentMode
                        )
                    }
                }
            }
        }
    }
}
