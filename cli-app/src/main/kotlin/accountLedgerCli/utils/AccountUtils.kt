package accountLedgerCli.utils

import accountLedgerCli.api.response.AccountResponse

internal object AccountUtils {

    internal fun getBlankAccount(): AccountResponse {

        return AccountResponse(
            id = 0u,
            fullName = "",
            name = "",
            parentAccountId = 0,
            accountType = "",
            notes = "",
            commodityType = "",
            commodityValue = "",
            ownerId = 0,
            taxable = "",
            placeHolder = ""
        )
    }

    internal fun prepareUserAccountsMap(accounts: List<AccountResponse>): LinkedHashMap<UInt, AccountResponse> {

        val userAccountsMap = LinkedHashMap<UInt, AccountResponse>()
        accounts.forEach { currentAccount -> userAccountsMap[currentAccount.id] = currentAccount }
        return userAccountsMap
    }
}
