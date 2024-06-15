package accountLedgerCli.cli.sub_commands

import account.ledger.library.api.ProjectApiUtils
import accountLedgerCli.enums.CommandLineApiMethodsEnum
import common.utils.library.cli.sub_commands.SubCommandEnhancedWithUserIdAsArgument
import common.utils.library.utils.ApiUtilsCommon
import common.utils.library.utils.ApiUtilsInteractiveCommon
import io.github.cdimascio.dotenv.Dotenv

class GetAccountsUrl(

    override val isDevelopmentMode: Boolean,
    override val dotEnv: Dotenv

) : SubCommandEnhancedWithUserIdAsArgument(

    name = CommandLineApiMethodsEnum.GetAccountsUrl.name,
    actionDescription = "Get accounts data URL for a user",
    isDevelopmentMode = isDevelopmentMode,
    dotEnv = dotEnv
) {

    override fun beforeExecuteActions() {}

    override fun furtherActions(userIdLocal: Int) {

        ApiUtilsInteractiveCommon.printSuccessMessageWithDataForApi(
            textData = ProjectApiUtils.getServerApiMethodSelectUserAccountsFullUrlForUser(
                userId = userIdLocal.toUInt()
            )
        )
    }
}
