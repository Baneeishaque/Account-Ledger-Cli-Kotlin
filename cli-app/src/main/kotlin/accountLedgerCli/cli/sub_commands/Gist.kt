package accountLedgerCli.cli.sub_commands

import account.ledger.library.enums.CommandLineApiMethodGistArgumentsEnum
import account.ledger.library.enums.CommandLineApiMethodsEnum

class Gist : SubCommandWithCommonArguments(
    name = CommandLineApiMethodsEnum.Gist.name,
    actionDescription = "Merge properly formatted Gist Account Ledger Entries to Account Ledger Entries of the Specified User, , Environment file may exist & contains missing arguments"
) {
    private val gistId: String? = getOptionalTextArgument(
        fullName = CommandLineApiMethodGistArgumentsEnum.gistId.name,
        description = "Id of the Gist which contains formatted Account Ledger Entries"
    )

    override fun beforeExecuteActions() {
        println("gistId = $gistId")
    }

    override fun furtherActions(usernameLocal: String, passwordLocal: String) {

    }
}
