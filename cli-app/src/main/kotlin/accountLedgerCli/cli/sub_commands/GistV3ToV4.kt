package accountLedgerCli.cli.sub_commands

import accountLedgerCli.enums.CommandLineApiMethodsEnum
import account_ledger_library.utils.GistUtilsInteractiveNative
import io.github.cdimascio.dotenv.Dotenv

class GistV3ToV4(

    override val isDevelopmentMode: Boolean,
    override val dotEnv: Dotenv

) : GistBase(

    name = CommandLineApiMethodsEnum.GistV3ToV4.name,
    actionDescription = "Convert Gist Account Ledger Entries from V3 to v4, Environment file may exist & contains missing arguments",
    isDevelopmentMode = isDevelopmentMode,
    dotEnv = dotEnv,
    process = GistUtilsInteractiveNative()::processGistIdForDataV3ToV4,
    isVersion3 = true
)
