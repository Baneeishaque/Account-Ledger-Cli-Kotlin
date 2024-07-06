package accountLedgerCli.cli.sub_commands

import account.ledger.library.enums.EnvironmentFileEntryEnum
import accountLedgerCli.enums.CommandLineApiMethodGistArgumentsEnum
import account_ledger_library.constants.ConstantsNative
import common.utils.library.cli.sub_commands.SubCommandEnhancedWithUsernameAndPasswordAsArguments
import common.utils.library.enums.EnvironmentFileEntryCommonEnum
import common.utils.library.utils.ApiUtilsInteractiveCommon
import io.github.cdimascio.dotenv.Dotenv

abstract class GistBase(

    name: String,
    actionDescription: String,
    isDevelopmentMode: Boolean,
    dotEnv: Dotenv,
    val isVersion3: Boolean = false,
    val process: (String, UInt, String, String, Boolean, Boolean, Boolean) -> Unit
) :
    SubCommandEnhancedWithUsernameAndPasswordAsArguments(

        name = name,
        actionDescription = actionDescription,
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

    override fun additionalBeforeExecuteActions() {

        if (isDevelopmentMode) {

            println("gistId = $gistId")
            println("userId = $userId")
        }
    }

    override fun additionalFurtherActions(usernameLocal: String, passwordLocal: String) {

        if (gistId.isNullOrEmpty()) {

            val environmentGistId: String? = dotEnv[EnvironmentFileEntryEnum.GIST_ID.name]
            if (environmentGistId.isNullOrEmpty()) {

                ApiUtilsInteractiveCommon.printMissingArgumentMessageForApi(argumentSummary = "Gist ID")

            } else {

                if (userId.isNullOrEmpty()) {

                    val environmentUserId: String? = dotEnv[EnvironmentFileEntryCommonEnum.USER_ID.name]
                    if (environmentUserId.isNullOrEmpty()) {

                        ApiUtilsInteractiveCommon.printMissingArgumentMessageForApi(argumentSummary = "User ID")

                    } else {

                        process.invoke(

                            usernameLocal,
                            environmentUserId.toUInt(),
                            dotEnv[EnvironmentFileEntryEnum.GITHUB_TOKEN.name]
                                ?: ConstantsNative.DEFAULT_VALUE_FOR_STRING_ENVIRONMENT_VARIABLES,
                            environmentGistId,
                            isDevelopmentMode,
                            true,
                            isVersion3
                        )
                    }
                }
            }
        }
    }
}
