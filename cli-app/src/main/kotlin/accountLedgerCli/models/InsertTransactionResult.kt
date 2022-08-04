package accountLedgerCli.models

import accountLedgerCli.api.response.AccountResponse

internal data class InsertTransactionResult(

    internal val isSuccess: Boolean,
    internal var dateTimeInText: String,
    internal var transactionParticulars: String,
    internal var transactionAmount: Float,
    internal var fromAccount: AccountResponse,
    internal var viaAccount: AccountResponse,
    internal var toAccount: AccountResponse
)
