package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.UserResponse
import accountLedgerCli.constants.Constants
import accountLedgerCli.models.ChooseUserResult
import accountLedgerCli.models.InsertTransactionResult
import accountLedgerCli.models.ViewTransactionsOutput
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

): ViewTransactionsOutput {

    val choice: String = readLine()!!
    var accountHomeOutput = InsertTransactionResult(
        isSuccess = false,
        dateTimeInText = dateTimeInText,
        transactionParticulars = transactionParticulars,
        transactionAmount = transactionAmount
    )
    when (choice) {
        "1" -> {
            accountHomeOutput = handleAccountSelection(
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
            accountHomeOutput = handleAccountSelection(
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
            return ViewTransactionsOutput(output = "3", addTransactionResult = accountHomeOutput)
        }

        "0" -> {
            return ViewTransactionsOutput(output = "0", addTransactionResult = accountHomeOutput)
        }

        else -> {
            invalidOptionMessage()
        }
    }
    return ViewTransactionsOutput(output = choice, addTransactionResult = accountHomeOutput)
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

): InsertTransactionResult {

    if (accountId != 0u) {

        return Screens.accountHome(
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
    return InsertTransactionResult(
        isSuccess = false,
        dateTimeInText = dateTimeInText,
        transactionParticulars = transactionParticulars,
        transactionAmount = transactionAmount
    )
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