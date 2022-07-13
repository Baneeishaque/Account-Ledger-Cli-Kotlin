package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.UserResponse
import accountLedgerCli.cli.App.Companion.chosenUser
import accountLedgerCli.cli.App.Companion.fromAccount
import accountLedgerCli.cli.App.Companion.toAccount
import accountLedgerCli.cli.App.Companion.viaAccount

internal fun processChildAccountScreenInput(
    userAccountsMap: LinkedHashMap<UInt, AccountResponse>,
    userId: UInt,
    username: String
): String? {

    val choice = readLine()
    when (choice) {
        "1" -> {
            handleFromAccountSelection(
                accountId = chooseAccountByIndex(userAccountsMap = userAccountsMap),
                userAccountsMap = userAccountsMap,
                userId = userId,
                username = username
            )
        }
        "2" -> {
            handleFromAccountSelection(
                accountId = searchAccount(userAccountsMap = userAccountsMap),
                userAccountsMap = userAccountsMap,
                userId = userId,
                username = username
            )
        }
        "3" -> addAccount()
        "0" -> {
        }
        else -> invalidOptionMessage()
    }
    return choice
}

private fun handleFromAccountSelection(
    accountId: UInt,
    userAccountsMap: LinkedHashMap<UInt, AccountResponse>,
    userId: UInt,
    username: String
) {

    if (accountId != 0u) {

        fromAccount = userAccountsMap[accountId]!!
        accountHome(userId = userId, username = username)
    }
}

internal fun handleFromAccountSelection(
    fromAccountId: UInt,
    userAccountsMap: LinkedHashMap<UInt, AccountResponse>
): Boolean {

    if (fromAccountId != 0u) {

        fromAccount = userAccountsMap[fromAccountId]!!
        return true
    }
    return false
}

internal fun handleToAccountSelection(
    depositAccountId: UInt,
    userAccountsMap: LinkedHashMap<UInt, AccountResponse>
): Boolean {

    if (depositAccountId != 0u) {

        toAccount = userAccountsMap[depositAccountId]!!
        return true
    }
    return false
}

internal fun handleUserSelection(
    chosenUserId: UInt,
    usersMap: LinkedHashMap<UInt, UserResponse>
): Boolean {

    if (chosenUserId != 0u) {

        chosenUser = usersMap[chosenUserId]!!
        return true
    }
    return false
}


internal fun handleViaAccountSelection(
    viaAccountId: UInt,
    userAccountsMap: LinkedHashMap<UInt, AccountResponse>
): Boolean {

    if (viaAccountId != 0u) {

        viaAccount = userAccountsMap[viaAccountId]!!
        return true
    }
    return false
}


