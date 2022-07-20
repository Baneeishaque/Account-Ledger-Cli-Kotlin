package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.UserResponse
import accountLedgerCli.cli.App.Companion.chosenUser
import accountLedgerCli.to_utils.ToDoUtils

internal fun processChildAccountScreenInput(

    userAccountsMap: LinkedHashMap<UInt, AccountResponse>,
    userId: UInt,
    username: String,
    viaAccount: AccountResponse,
    toAccount: AccountResponse

): String {

    val choice: String = readLine()!!
    when (choice) {
        "1" -> {
            handleAccountSelection(
                accountId = getValidIndex(
                    map = userAccountsMap,
                    itemSpecification = Constants.accountText,
                    items = userAccountsToStringFromLinkedHashMap(userAccountsMap = userAccountsMap)
                ),
                userAccountsMap = userAccountsMap,
                userId = userId,
                username = username,
                viaAccount = viaAccount,
                toAccount = toAccount
            )
        }

        "2" -> {
            handleAccountSelection(
                accountId = searchAccount(userAccountsMap = userAccountsMap),
                userAccountsMap = userAccountsMap,
                userId = userId,
                username = username,
                viaAccount = viaAccount,
                toAccount = toAccount
            )
        }

        "3" -> {

            ToDoUtils.showTodo()
        }

        "0" -> {
        }

        else -> {
            invalidOptionMessage()
        }
    }
    return choice
}

private fun handleAccountSelection(

    accountId: UInt,
    userAccountsMap: LinkedHashMap<UInt, AccountResponse>,
    userId: UInt,
    username: String,
    viaAccount: AccountResponse,
    toAccount: AccountResponse

) {

    if (accountId != 0u) {

        Screens.accountHome(
            userId = userId,
            username = username,
            fromAccount = userAccountsMap[accountId]!!,
            viaAccount = viaAccount,
            toAccount = toAccount
        )
    }
}

internal fun handleUserSelection(
    chosenUserId: UInt, usersMap: LinkedHashMap<UInt, UserResponse>
): Boolean {

    if (chosenUserId != 0u) {

        chosenUser = usersMap[chosenUserId]!!
        return true
    }
    return false
}