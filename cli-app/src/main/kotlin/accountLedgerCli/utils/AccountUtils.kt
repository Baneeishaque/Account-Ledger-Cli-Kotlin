package accountLedgerCli.utils

import accountLedgerCli.api.response.AccountResponse

internal object AccountUtils {

    internal fun getBlankAccount(): AccountResponse {

        return AccountResponse(
            id = 0,
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

    internal fun prepareUserAccountsMap(accounts: List<AccountResponse>): LinkedHashMap<Int, AccountResponse> {

        val userAccountsMap = LinkedHashMap<Int, AccountResponse>()
        accounts.forEach { currentAccount -> userAccountsMap[currentAccount.id] = currentAccount }
        return userAccountsMap
    }
}
