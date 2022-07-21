package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.UserResponse
import accountLedgerCli.models.ChooseAccountResult
import accountLedgerCli.models.ChooseUserResult
import accountLedgerCli.to_utils.ToDoUtils

internal fun processChildAccountScreenInput(

    userAccountsMap: LinkedHashMap<UInt, AccountResponse>,
    userId: UInt,
    username: String,
    viaAccount: AccountResponse,
    toAccount: AccountResponse,
    dateTimeInText: String,
    transactionParticulars: String,
    transactionAmount: Float

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
                toAccount = toAccount,
                dateTimeInText = dateTimeInText,
                transactionParticulars = transactionParticulars,
                transactionAmount = transactionAmount
            )
        }

        "2" -> {
            handleAccountSelection(
                accountId = searchAccount(userAccountsMap = userAccountsMap),
                userAccountsMap = userAccountsMap,
                userId = userId,
                username = username,
                viaAccount = viaAccount,
                toAccount = toAccount,
                dateTimeInText = dateTimeInText,
                transactionParticulars = transactionParticulars,
                transactionAmount = transactionAmount
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
    toAccount: AccountResponse,
    dateTimeInText: String,
    transactionParticulars: String,
    transactionAmount: Float

) {

    if (accountId != 0u) {

        Screens.accountHome(
            userId = userId,
            username = username,
            fromAccount = userAccountsMap[accountId]!!,
            viaAccount = viaAccount,
            toAccount = toAccount,
            dateTimeInText = dateTimeInText,
            transactionParticulars = transactionParticulars,
            transactionAmount = transactionAmount
        )
    }
}

internal fun handleUserSelection(
    chosenUserId: UInt, usersMap: LinkedHashMap<UInt, UserResponse>
): ChooseUserResult {

    if (chosenUserId != 0u) {

        return ChooseUserResult(
            isChoosed = true,
            chosenUser = usersMap[chosenUserId]!!
        )
    }
    return ChooseUserResult(isChoosed = false)
}