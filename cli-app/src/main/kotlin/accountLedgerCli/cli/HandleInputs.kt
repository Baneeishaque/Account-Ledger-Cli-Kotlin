package accountLedgerCli.cli

import account.ledger.library.api.response.AccountResponse
import account_ledger_library.constants.ConstantsNative
import account.ledger.library.models.InsertTransactionResult
import account.ledger.library.models.ViewTransactionsOutput
import account.ledger.library.utils.AccountUtils
import common.utils.library.utils.ErrorUtilsInteractive
import common.utils.library.utils.ListUtilsInteractive
import common.utils.library.utils.ToDoUtilsInteractive
import io.github.cdimascio.dotenv.Dotenv

fun processChildAccountScreenInput(

    userAccountsMap: LinkedHashMap<UInt, AccountResponse>,
    userId: UInt,
    username: String,
    fromAccount: AccountResponse,
    viaAccount: AccountResponse,
    toAccount: AccountResponse,
    dateTimeInText: String,
    transactionParticulars: String,
    transactionAmount: Float,
    isDevelopmentMode: Boolean,
    dotEnv: Dotenv

): ViewTransactionsOutput {

    val choice: String = readln()
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

                accountId = ListUtilsInteractive.getValidIndexFromCollectionWithSelectionPromptAndZeroAsBack(

                    map = userAccountsMap,
                    itemSpecification = ConstantsNative.ACCOUNT_TEXT,
                    items = AccountUtils.userAccountsToStringFromList(accounts = userAccountsMap.values.toList())
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
                isDevelopmentMode = isDevelopmentMode,
                dotEnv = dotEnv
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
                isDevelopmentMode = isDevelopmentMode,
                dotEnv = dotEnv
            )
        }

        "3" -> {

            ToDoUtilsInteractive.showTodo()
            return ViewTransactionsOutput(output = "3", addTransactionResult = accountHomeOutput)
        }

        "0" -> {
            return ViewTransactionsOutput(output = "0", addTransactionResult = accountHomeOutput)
        }

        else -> {
            ErrorUtilsInteractive.printInvalidOptionMessage()
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
    isDevelopmentMode: Boolean,
    dotEnv: Dotenv

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
            isDevelopmentMode = isDevelopmentMode,
            dotEnv = dotEnv
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
