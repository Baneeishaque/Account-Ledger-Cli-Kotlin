package accountLedgerCli.models

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
internal data class BalanceSheetDataModel(
    @Required internal val status: Int,
    internal val data: List<BalanceSheetDataRowModel>? = null,
    internal val error: String? = null
)
