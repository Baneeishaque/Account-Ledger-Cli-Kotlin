package accountLedgerCli.api.response

internal data class AccountsResponse(

    internal val status: Int,
    internal val accounts: List<AccountResponse>
)
