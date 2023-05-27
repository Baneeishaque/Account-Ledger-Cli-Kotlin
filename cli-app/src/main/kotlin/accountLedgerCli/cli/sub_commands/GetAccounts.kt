package accountLedgerCli.cli.sub_commands

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.utils.ApiUtils
import account.ledger.library.utils.HandleResponses
import accountLedgerCli.enums.CommandLineApiMethodsEnum
import common.utils.library.cli.sub_commands.SubCommandWithUserIdAsArgument
import common.utils.library.models.IsOkModel
import io.github.cdimascio.dotenv.Dotenv
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class GetAccounts(

    override val isDevelopmentMode: Boolean = false,
    override val dotEnv: Dotenv
) :
    SubCommandWithUserIdAsArgument(
        name = CommandLineApiMethodsEnum.GetAccounts.name,
        actionDescription = "Get accounts data for a user",
        isDevelopmentMode = isDevelopmentMode,
        dotEnv = dotEnv
    ) {

    override fun beforeExecuteActions() {}

    override fun furtherActions(userIdLocal: Int) {

        println(
            Json.encodeToString(
                serializer = IsOkModel.serializer(MapSerializer(UInt.serializer(), AccountResponse.serializer())),
                value = HandleResponses.getUserAccountsMapForSerializer(
                    apiResponse = ApiUtils.getAccountsFull(
                        userId = userIdLocal.toUInt(),
                        isConsoleMode = false,
                        isDevelopmentMode = isDevelopmentMode
                    )
                )
            )
        )
    }
}
