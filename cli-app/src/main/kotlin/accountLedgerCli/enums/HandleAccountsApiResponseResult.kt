package accountLedgerCli.enums

import accountLedgerCli.api.response.AccountResponse

internal data class HandleAccountsApiResponseResult(
    internal val isAccountIdSelected: Boolean,
    internal val selectedAccount: AccountResponse? = null
)