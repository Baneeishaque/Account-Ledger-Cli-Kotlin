package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.UserResponse

internal fun processChildAccountScreenInput(
    userAccountsMap: LinkedHashMap<Int, AccountResponse>,
    userId: Int,
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
    accountId: Int,
    userAccountsMap: LinkedHashMap<Int, AccountResponse>,
    userId: Int,
    username: String
) {

    if (accountId != 0) {

        fromAccount = userAccountsMap[accountId]!!
        accountHome(userId = userId, username = username)
    }
}

internal fun handleFromAccountSelection(
    fromAccountId: Int,
    userAccountsMap: LinkedHashMap<Int, AccountResponse>
): Boolean {

    if (fromAccountId != 0) {

        fromAccount = userAccountsMap[fromAccountId]!!
        return true
    }
    return false
}

internal fun handleToAccountSelection(
    depositAccountId: Int,
    userAccountsMap: LinkedHashMap<Int, AccountResponse>
): Boolean {

    if (depositAccountId != 0) {

        toAccount = userAccountsMap[depositAccountId]!!
        return true
    }
    return false
}

internal fun handleUserSelection(
    chosenUserId: Int,
    usersMap: LinkedHashMap<Int, UserResponse>
): Boolean {

    if (chosenUserId != 0) {

        chosenUser = usersMap[chosenUserId]!!
        return true
    }
    return false
}


internal fun handleViaAccountSelection(
    viaAccountId: Int,
    userAccountsMap: LinkedHashMap<Int, AccountResponse>
): Boolean {

    if (viaAccountId != 0) {

        viaAccount = userAccountsMap[viaAccountId]!!
        return true
    }
    return false
}


