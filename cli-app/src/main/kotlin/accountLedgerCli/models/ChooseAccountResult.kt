package accountLedgerCli.models

import accountLedgerCli.api.response.AccountResponse

internal class ChooseAccountResult(
    internal val chosenAccountId: UInt,
    internal val chosenAccount: AccountResponse? = null
)