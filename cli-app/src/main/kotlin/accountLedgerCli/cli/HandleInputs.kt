package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.UserResponse
import accountLedgerCli.constants.Constants
import accountLedgerCli.models.ChooseUserResult
import accountLedgerCli.models.InsertTransactionResult
import accountLedgerCli.models.ViewTransactionsOutput
import accountLedgerCli.to_utils.ToDoUtils
import accountLedgerCli.to_utils.invalidOptionMessage
import accountLedgerCli.utils.AccountUtils

internal fun processChildAccountScreenInput(

    userAccountsMap: LinkedHashMap<UInt, AccountResponse>,
    userId: UInt,
    username: String,
    fromAccount: AccountResponse,
    viaAccount: AccountResponse,
    toAccount: AccountResponse,
    dateTimeInText: String,
    transactionParticulars: String,
    transactionAmount: Float,
    isConsoleMode: Boolean,
    isDevelopmentMode: Boolean

): ViewTransactionsOutput {

    val choice: String = readLine()!!
    var accountHomeOutput = InsertTransactionResult(

        isSuccess = false,
        dateTimeInText = dateTimeInText,
        transactionParticulars = transactionParticulars,
        transactionAmount = transactionAmount,
        fromAccount = fromAccount,
        viaAccount = viaAccount,
        toAccount = toAccount
    )
    when (choice) {

        "1" -> {
            accountHomeOutput = handleAccountSelection(

                accountId = getValidIndexWithInputPrompt(

                    map = userAccountsMap,
                    itemSpecification = Constants.accountText,
                    items = AccountUtils.userAccountsToStringFromList(accounts = userAccountsMap.values.toList()),
                    backValue = 0u
                ),
                userAccountsMap = userAccountsMap,
                userId = userId,
                username = username,
                fromAccount = fromAccount,
                viaAccount = viaAccount,
                toAccount = toAccount,
                dateTimeInText = dateTimeInText,
                transactionParticulars = transactionParticulars,
                transactionAmount = transactionAmount,
                isConsoleMode = isConsoleMode,
                isDevelopmentMode = isDevelopmentMode
            )
        }

        "2" -> {

            accountHomeOutput = handleAccountSelection(

                accountId = searchAccount(

                    userAccountsMap = userAccountsMap,
                    isDevelopmentMode = isDevelopmentMode
                ),
                userAccountsMap = userAccountsMap,
                userId = userId,
                username = username,
                fromAccount = fromAccount,
                viaAccount = viaAccount,
                toAccount = toAccount,
                dateTimeInText = dateTimeInText,
                transactionParticulars = transactionParticulars,
                transactionAmount = transactionAmount,
                isConsoleMode = isConsoleMode,
                isDevelopmentMode = isDevelopmentMode
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
    fromAccount: AccountResponse,
    viaAccount: AccountResponse,
    toAccount: AccountResponse,
    dateTimeInText: String,
    transactionParticulars: String,
    transactionAmount: Float,
    isConsoleMode: Boolean,
    isDevelopmentMode: Boolean

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
            transactionAmount = transactionAmount,
            isConsoleMode = isConsoleMode,
            isDevelopmentMode = isDevelopmentMode
        )
    }
    return InsertTransactionResult(

        isSuccess = false,
        dateTimeInText = dateTimeInText,
        transactionParticulars = transactionParticulars,
        transactionAmount = transactionAmount,
        fromAccount = fromAccount,
        viaAccount = viaAccount,
        toAccount = toAccount
    )
}

internal fun handleUserSelection(
    chosenUserId: UInt, usersMap: LinkedHashMap<UInt, UserResponse>
): ChooseUserResult {

    if (chosenUserId != 0u) {

        return ChooseUserResult(
            isChosen = true,
            chosenUser = usersMap[chosenUserId]!!
        )
    }
    return ChooseUserResult(isChosen = false)
}