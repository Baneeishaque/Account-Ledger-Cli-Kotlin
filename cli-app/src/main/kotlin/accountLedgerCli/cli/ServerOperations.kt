package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountsResponse
import accountLedgerCli.api.response.TransactionsResponse
import accountLedgerCli.retrofit.data.AccountsDataSource
import accountLedgerCli.retrofit.data.TransactionsDataSource
import accountLedgerCli.to_utils.ApiUtils
import kotlinx.coroutines.runBlocking

internal fun getAccounts(

    userId: UInt,
    parentAccountId: UInt = 0u

): Result<AccountsResponse> {

    return ApiUtils.getResultFromApiRequestWithOptionalRetries(apiCallFunction = fun(): Result<AccountsResponse> {

        return runBlocking {

            AccountsDataSource().selectUserAccounts(

                userId = userId,
                parentAccountId = parentAccountId
            )
        }
    })
}

internal fun getUserTransactions(

    userId: UInt,
    accountId: UInt,
    isNotFromBalanceSheet: Boolean = true

): Result<TransactionsResponse> {

    return ApiUtils.getResultFromApiRequestWithOptionalRetries(apiCallFunction = fun(): Result<TransactionsResponse> {

        return runBlocking {

            TransactionsDataSource().selectUserTransactions(

                userId = userId,
                accountId = accountId
            )
        }
    }, isConsoleMode = isNotFromBalanceSheet)
}
