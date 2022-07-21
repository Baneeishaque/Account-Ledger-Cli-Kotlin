package accountLedgerCli.api.response

internal data class TransactionResponse(

    internal val id: UInt,
    internal val event_date_time: String,
    internal val particulars: String,
    internal val amount: Float,
    internal val insertion_date_time: String,
    internal val from_account_name: String,
    internal val from_account_full_name: String,
    internal val from_account_id: UInt,
    internal val to_account_name: String,
    internal val to_account_full_name: String,
    internal val to_account_id: UInt
)
