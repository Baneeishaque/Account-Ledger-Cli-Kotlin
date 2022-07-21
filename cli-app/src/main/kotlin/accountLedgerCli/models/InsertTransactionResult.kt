package accountLedgerCli.models

internal data class InsertTransactionResult(
    internal val isSuccess: Boolean,
    internal val dateTimeInText: String,
    internal val transactionParticulars: String,
    internal val transactionAmount: Float
)
