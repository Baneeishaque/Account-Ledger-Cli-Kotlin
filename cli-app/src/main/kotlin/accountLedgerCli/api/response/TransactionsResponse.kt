package accountLedgerCli.api.response

internal data class TransactionsResponse(

    internal val status: UInt,
    internal var transactions: List<TransactionResponse>
)
