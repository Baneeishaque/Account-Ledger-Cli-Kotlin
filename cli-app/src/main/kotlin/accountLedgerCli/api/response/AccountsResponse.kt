package accountLedgerCli.api.response

data class AccountsResponse(

    val status: Int,
    val accounts: List<AccountResponse>
)
