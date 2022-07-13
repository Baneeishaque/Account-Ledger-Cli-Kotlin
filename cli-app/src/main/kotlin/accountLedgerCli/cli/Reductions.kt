package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse

internal fun getLast10ItemsFromLinkedHashMap(userAccountsMap: LinkedHashMap<UInt, AccountResponse>): List<Pair<UInt, AccountResponse>> {

    if (userAccountsMap.size > 10) {

        return userAccountsMap.toList().takeLast(10)
    }
    return userAccountsMap.toList()
}
