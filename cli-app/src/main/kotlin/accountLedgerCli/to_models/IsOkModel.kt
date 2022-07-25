package accountLedgerCli.to_models

data class IsOkModel<T>(val isOK: Boolean, val data: T? = null)