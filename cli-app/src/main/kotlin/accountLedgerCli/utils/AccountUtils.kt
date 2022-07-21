package accountLedgerCli.utils

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.models.ChooseAccountResult

internal object AccountUtils {

    @JvmStatic
    internal val blankAccount = AccountResponse(
        id = 0u,
        fullName = "",
        name = "",
        parentAccountId = 0u,
        accountType = "",
        notes = "",
        commodityType = "",
        commodityValue = "",
        ownerId = 0u,
        taxable = "",
        placeHolder = ""
    )


    @JvmStatic
    internal fun prepareUserAccountsMap(accounts: List<AccountResponse>): LinkedHashMap<UInt, AccountResponse> {

        val userAccountsMap = LinkedHashMap<UInt, AccountResponse>()
        accounts.forEach { currentAccount -> userAccountsMap[currentAccount.id] = currentAccount }
        return userAccountsMap
    }

    @JvmStatic
    internal val blankChosenAccount = ChooseAccountResult(chosenAccountId = 0u)
}
