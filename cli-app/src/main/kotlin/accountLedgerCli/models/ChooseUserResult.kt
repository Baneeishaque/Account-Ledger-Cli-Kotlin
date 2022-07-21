package accountLedgerCli.models

import accountLedgerCli.api.response.UserResponse

internal class ChooseUserResult(internal val isChoosed: Boolean, internal val chosenUser: UserResponse? = null)