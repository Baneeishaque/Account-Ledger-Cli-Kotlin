package accountLedgerCli.models

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
internal data class BalanceSheetDataRowModel(
    @Required internal val accountId: UInt,
    @Required internal val accountName: String,
    @Required internal val accountBalance: Float
)
