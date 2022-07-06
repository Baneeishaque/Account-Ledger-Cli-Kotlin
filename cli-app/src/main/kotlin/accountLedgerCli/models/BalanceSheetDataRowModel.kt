package accountLedgerCli.models

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class BalanceSheetDataRowModel(
    @Required val accountId: Int,
    @Required val accountName: String,
    @Required val accountBalance: Float
)
