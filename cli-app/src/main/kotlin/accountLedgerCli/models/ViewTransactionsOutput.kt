package accountLedgerCli.models

internal data class ViewTransactionsOutput(
    internal val output: String,
    internal val addTransactionResult: InsertTransactionResult
)
