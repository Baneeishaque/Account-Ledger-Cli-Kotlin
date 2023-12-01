package accountLedgerCli.cli.sub_commands

import account.ledger.library.api.ProjectApiUtils
import accountLedgerCli.enums.CommandLineApiMethodsEnum
import common.utils.library.cli.sub_commands.SubCommandWithUserIdAsArgument
import common.utils.library.utils.ApiUtilsCommon
import io.github.cdimascio.dotenv.Dotenv

class GetAccountsUrl(

    override val isDevelopmentMode: Boolean,
    override val dotEnv: Dotenv

) : SubCommandWithUserIdAsArgument(

    name = CommandLineApiMethodsEnum.GetAccountsUrl.name,
    actionDescription = "Get accounts data URL for a user",
    isDevelopmentMode = isDevelopmentMode,
    dotEnv = dotEnv
) {

    override fun beforeExecuteActions() {}

    override fun furtherActions(userIdLocal: Int) {

        ApiUtilsCommon.printSuccessMessageWithDataForApi(
            textData = ProjectApiUtils.getServerApiMethodSelectUserAccountsFullUrlForUser(
                userId = userIdLocal.toUInt()
            )
        )
    }
}
