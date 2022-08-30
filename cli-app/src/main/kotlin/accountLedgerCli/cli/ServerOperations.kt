package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountsResponse
import accountLedgerCli.api.response.TransactionsResponse
import accountLedgerCli.retrofit.data.AccountsDataSource
import accountLedgerCli.retrofit.data.TransactionsDataSource
import accountLedgerCli.to_utils.ApiUtils
import kotlinx.coroutines.runBlocking

internal fun getAccounts(

    userId: UInt,
    parentAccountId: UInt = 0u,
    isConsoleMode: Boolean,
    isDevelopmentMode: Boolean

): Result<AccountsResponse> {

    return ApiUtils.getResultFromApiRequestWithOptionalRetries(apiCallFunction = fun(): Result<AccountsResponse> {

        return runBlocking {

            AccountsDataSource().selectUserAccounts(

                userId = userId,
                parentAccountId = parentAccountId
            )
        }
    }, isConsoleMode = isConsoleMode, isDevelopmentMode = isDevelopmentMode)
}

internal fun getUserTransactionsForAnAccount(

    userId: UInt,
    accountId: UInt,
    isNotFromBalanceSheet: Boolean = true,
    isDevelopmentMode: Boolean

): Result<TransactionsResponse> {

    return ApiUtils.getResultFromApiRequestWithOptionalRetries(

        apiCallFunction = fun(): Result<TransactionsResponse> {

            return runBlocking {

                TransactionsDataSource().selectUserTransactions(

                    userId = userId,
                    accountId = accountId
                )
            }
        },
        isConsoleMode = isNotFromBalanceSheet,
        isDevelopmentMode = isDevelopmentMode
    )
}
