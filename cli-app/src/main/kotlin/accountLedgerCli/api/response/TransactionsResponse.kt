package accountLedgerCli.api.response

internal data class TransactionsResponse(
    
    internal val status: Int,
    internal val transactions: List<TransactionResponse>
)
