package accountLedgerCli.cli.sub_commands

import accountLedgerCli.enums.CommandLineApiMethodsEnum
import account_ledger_library.utils.GistUtilsInteractiveNative
import io.github.cdimascio.dotenv.Dotenv

class GistV4(

    override val isDevelopmentMode: Boolean,
    override val dotEnv: Dotenv

) : GistBase(

    name = CommandLineApiMethodsEnum.GistV4.name,
    actionDescription = "Merge properly formatted Gist Account Ledger Entries to Account Ledger Entries of the Specified User (Version 4), Environment file may exist & contains missing arguments",
    isDevelopmentMode = isDevelopmentMode,
    dotEnv = dotEnv,
    process = GistUtilsInteractiveNative()::processGistIdForDataV4,
    isVersion3 = true
)
