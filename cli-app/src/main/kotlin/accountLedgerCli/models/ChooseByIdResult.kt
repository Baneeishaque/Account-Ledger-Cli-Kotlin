package accountLedgerCli.models

import accountLedgerCli.to_models.IsOkModel

data class ChooseByIdResult<T>(val isOkWithData: IsOkModel<T>, val id: UInt? = null)
