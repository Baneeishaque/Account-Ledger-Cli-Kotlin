package accountLedgerCli.api.response

data class TransactionsResponse(
    val status: Int,
    val transactions: List<TransactionResponse>
)
