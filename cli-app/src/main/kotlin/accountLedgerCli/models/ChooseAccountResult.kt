package accountLedgerCli.models

import accountLedgerCli.api.response.AccountResponse

internal class ChooseAccountResult(
//    TODO : migrate to isOK model
    internal val chosenAccountId: UInt,
    internal val chosenAccount: AccountResponse? = null
)