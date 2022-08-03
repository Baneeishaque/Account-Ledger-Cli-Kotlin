package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountsResponse
import accountLedgerCli.api.response.TransactionsResponse
import accountLedgerCli.retrofit.ResponseHolder
import accountLedgerCli.retrofit.data.AccountsDataSource
import accountLedgerCli.retrofit.data.TransactionsDataSource
import kotlinx.coroutines.runBlocking

internal fun getAccounts(userId: UInt, parentAccountId: UInt = 0u): Result<AccountsResponse> {

    // TODO : Use new API Utils
    val apiResponse: Result<AccountsResponse>
    val userAccountsDataSource = AccountsDataSource()
    println("Contacting Server...")
    runBlocking {
        apiResponse =
            userAccountsDataSource.selectUserAccounts(
                userId = userId, parentAccountId = parentAccountId
            )
    }
    //    println("Response : $apiResponse")
    return apiResponse
}

internal fun getUserTransactions(

    userId: UInt,
    accountId: UInt,
    isNotFromBalanceSheet: Boolean = true

): ResponseHolder<TransactionsResponse> {

    // TODO : Use new API Utils
    val apiResponse: ResponseHolder<TransactionsResponse>
    val userTransactionsDataSource = TransactionsDataSource()
    if (isNotFromBalanceSheet) {
        println("Contacting Server...")
    }
    runBlocking {
        apiResponse =
            userTransactionsDataSource.selectUserTransactions(
                userId = userId, accountId = accountId
            )
    }
//    println("Response : $apiResponse")
    return apiResponse
}
