package accountLedgerCli.cli.sub_commands

import account.ledger.library.enums.CommandLineApiMethodInsertTransactionArgumentsEnum
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand

@OptIn(ExperimentalCli::class)
class InsertTransaction(

    name: String,
    actionDescription: String,
    isDevelopmentMode: Boolean
) :
    Subcommand(
        name = name,
        actionDescription = actionDescription,
    ) {
    private val userId: Int by argument(
        type = ArgType.Int,
        fullName = CommandLineApiMethodInsertTransactionArgumentsEnum.userId.name,
        description = "User ID of the User"
    )

    override fun execute() {

    }
}