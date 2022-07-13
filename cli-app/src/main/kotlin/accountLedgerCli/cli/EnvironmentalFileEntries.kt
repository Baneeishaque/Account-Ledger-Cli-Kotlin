package accountLedgerCli.cli

object EnvironmentalFileEntries {

    private const val accountIdFormalName: String = "Account Index No."
    private const val frequentText: String = "Frequent"

    internal val walletAccountId = EnvironmentFileEntry(
        entryName = EnvironmentFileEntryEnum.WALLET_ACCOUNT_ID,
        entryFormalName = "Wallet $accountIdFormalName"
    )

    internal val frequent1AccountId = EnvironmentFileEntry(
        entryName = EnvironmentFileEntryEnum.FREQUENT_1_ACCOUNT_ID,
        entryFormalName = "$frequentText 1 $accountIdFormalName"
    )

    internal val frequent2AccountId = EnvironmentFileEntry(
        entryName = EnvironmentFileEntryEnum.FREQUENT_2_ACCOUNT_ID,
        entryFormalName = "$frequentText 2 $accountIdFormalName"
    )

    internal val frequent3AccountId = EnvironmentFileEntry(
        entryName = EnvironmentFileEntryEnum.FREQUENT_3_ACCOUNT_ID,
        entryFormalName = "$frequentText 3 $accountIdFormalName"
    )

    internal val bankAccountId = EnvironmentFileEntry(
        entryName = EnvironmentFileEntryEnum.BANK_ACCOUNT_ID,
        entryFormalName = "Bank $accountIdFormalName"
    )
}
