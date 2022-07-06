package accountLedgerCli.models

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class BalanceSheetDataModel(
    @Required val status: Int,
    val data: List<BalanceSheetDataRowModel>? = null,
    val error: String? = null
)
