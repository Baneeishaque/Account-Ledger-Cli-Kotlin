package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.UserResponse

internal fun userAccountsToStringFromLinkedHashMapLimitedTo10(
    userAccountsMap: LinkedHashMap<UInt, AccountResponse>
): String {

    return userAccountsToStringFromListPair(getLast10ItemsFromLinkedHashMap(userAccountsMap))
}

private fun userAccountsToStringFromListPair(
    userAccountsList: List<Pair<UInt, AccountResponse>>
): String {

    var result = ""
    userAccountsList.forEach { accountEntry -> result += "A${accountEntry.first} - ${accountEntry.second.fullName}\n" }
    return result
}

internal fun userAccountsToStringFromLinkedHashMap(
    userAccountsMap: LinkedHashMap<UInt, AccountResponse>
): String {

    var result = ""
    userAccountsMap.forEach { account -> result += "A${account.key} - ${account.value.fullName}\n" }
    return result
}

internal fun userAccountsToStringFromList(accounts: List<AccountResponse>): String {

    var result = ""
    accounts.forEach { account -> result += "A${account.id} - ${account.name}\n" }
    return result
}

internal fun usersToStringFromLinkedHashMap(
    usersMap: LinkedHashMap<UInt, UserResponse>
): String {

    var result = ""
    usersMap.forEach { user -> result += "U${user.key} - ${user.value.username}\n" }
    return result
}

