package accountLedgerCli.cli

import accountLedgerCli.enums.EnvironmentFileEntryEnum
import accountLedgerCli.to_models.EnvironmentFileEntryModel

object EnvironmentalFileEntries {

    private const val accountIdFormalName: String = "Account Index No."
    private const val frequentText: String = "Frequent"

    internal val walletAccountId = EnvironmentFileEntryModel(
        entryName = EnvironmentFileEntryEnum.WALLET_ACCOUNT_ID,
        entryFormalName = "Wallet $accountIdFormalName"
    )

    internal val frequent1AccountId = EnvironmentFileEntryModel(
        entryName = EnvironmentFileEntryEnum.FREQUENT_1_ACCOUNT_ID,
        entryFormalName = "$frequentText 1 $accountIdFormalName"
    )

    internal val frequent2AccountId = EnvironmentFileEntryModel(
        entryName = EnvironmentFileEntryEnum.FREQUENT_2_ACCOUNT_ID,
        entryFormalName = "$frequentText 2 $accountIdFormalName"
    )

    internal val frequent3AccountId = EnvironmentFileEntryModel(
        entryName = EnvironmentFileEntryEnum.FREQUENT_3_ACCOUNT_ID,
        entryFormalName = "$frequentText 3 $accountIdFormalName"
    )

    internal val bankAccountId = EnvironmentFileEntryModel(
        entryName = EnvironmentFileEntryEnum.BANK_ACCOUNT_ID,
        entryFormalName = "Bank $accountIdFormalName"
    )

    internal val isDevelopmentMode = EnvironmentFileEntryModel(entryName = EnvironmentFileEntryEnum.IS_DEVELOPMENT_MODE)
}
