package accountLedgerCli.api.response

data class TransactionResponse(
    val id: String,
    val event_date_time: String,
    val particulars: String,
    val amount: Float,
    val insertion_date_time: String,
    val from_account_name: String,
    val from_account_full_name: String,
    val from_account_id: Int,
    val to_account_name: String,
    val to_account_full_name: String,
    val to_account_id: Int
)
