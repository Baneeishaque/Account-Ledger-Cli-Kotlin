package accountLedgerCli.models

internal data class InsertTransactionResult(
    internal val isSuccess: Boolean,
    internal val dateTimeInText: String? = null,
    internal val transactionParticulars: String? = null,
    internal val transactionAmount: Float? = null
)
