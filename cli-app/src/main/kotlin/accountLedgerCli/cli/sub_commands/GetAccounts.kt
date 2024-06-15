package accountLedgerCli.cli.sub_commands

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.utils.ApiUtilsInteractive
import account.ledger.library.utils.HandleResponsesInteractiveLibrary
import accountLedgerCli.enums.CommandLineApiMethodsEnum
import common.utils.library.cli.sub_commands.SubCommandEnhancedWithUserIdAsArgument
import common.utils.library.models.IsOkModel
import io.github.cdimascio.dotenv.Dotenv
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class GetAccounts(

    override val isDevelopmentMode: Boolean = false,
    override val dotEnv: Dotenv
) :
    SubCommandEnhancedWithUserIdAsArgument(
        name = CommandLineApiMethodsEnum.GetAccounts.name,
        actionDescription = "Get accounts data for a user",
        isDevelopmentMode = isDevelopmentMode,
        dotEnv = dotEnv
    ) {

    override fun beforeExecuteActions() {}

    override fun furtherActions(userIdLocal: Int) {

        println(
            Json.encodeToString(
                serializer = IsOkModel.serializer(typeSerial0 = ListSerializer(elementSerializer = AccountResponse.serializer())),
                value = HandleResponsesInteractiveLibrary.getUserAccountsMapForSerializer(
                    apiResponse = ApiUtilsInteractive.getAccountsFull(
                        userId = userIdLocal.toUInt(),
                        isConsoleMode = false,
                        isDevelopmentMode = isDevelopmentMode
                    )
                )
            )
        )
    }
}
