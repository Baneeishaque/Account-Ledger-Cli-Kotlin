package accountLedgerCli.to_models

import accountLedgerCli.enums.EnvironmentFileEntryEnum

class EnvironmentFileEntryModel(val entryName: EnvironmentFileEntryEnum, val entryFormalName: String? = null)