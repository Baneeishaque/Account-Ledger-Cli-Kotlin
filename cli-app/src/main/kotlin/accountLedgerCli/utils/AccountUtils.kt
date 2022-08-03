package accountLedgerCli.utils

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.constants.Constants
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

    internal fun userAccountsToStringFromListPair(

        userAccountsList: List<Pair<UInt, AccountResponse>>

    ): String {

        var result = ""
        userAccountsList.forEach { accountEntry -> result += "${Constants.accountText}${accountEntry.first} - ${accountEntry.second.fullName}\n" }
        return result
    }

    internal fun userAccountsToStringFromLinkedHashMap(

        userAccountsMap: LinkedHashMap<UInt, AccountResponse>

    ): String {

        var result = ""
        userAccountsMap.forEach { account -> result += "${Constants.accountText}${account.key} - ${account.value.fullName}\n" }
        return result
    }

    //TODO : Write List to String, then rewrite userAccountsToStringFromList, usersToStringFromLinkedHashMap, userAccountsToStringFromLinkedHashMap & userAccountsToStringFromListPair

    internal fun userAccountsToStringFromList(accounts: List<AccountResponse>): String {

        var result = ""
        accounts.forEach { account -> result += "${Constants.accountText}${account.id} - ${account.name}\n" }
        return result
    }

}
