package accountLedgerCli.models

import accountLedgerCli.api.response.UserResponse

internal data class ChooseUserResult(internal val isChosen: Boolean, internal val chosenUser: UserResponse? = null)