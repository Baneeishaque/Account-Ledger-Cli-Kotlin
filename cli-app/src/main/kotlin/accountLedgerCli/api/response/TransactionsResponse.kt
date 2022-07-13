package accountLedgerCli.api.response

internal data class TransactionsResponse(

    internal val status: UInt,
    internal val transactions: List<TransactionResponse>
)
