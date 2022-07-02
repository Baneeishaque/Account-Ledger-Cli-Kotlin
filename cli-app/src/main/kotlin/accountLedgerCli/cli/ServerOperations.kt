package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountsResponse
import accountLedgerCli.api.response.TransactionsResponse
import accountLedgerCli.retrofit.ResponseHolder
import accountLedgerCli.retrofit.data.AccountsDataSource
import accountLedgerCli.retrofit.data.TransactionsDataSource
import kotlinx.coroutines.runBlocking

internal fun getAccounts(userId: Int, parentAccountId: Int = 0): ResponseHolder<AccountsResponse> {

    val apiResponse: ResponseHolder<AccountsResponse>
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

internal fun getUserTransactions(userId: Int, accountId: Int): ResponseHolder<TransactionsResponse> {

    val apiResponse: ResponseHolder<TransactionsResponse>
    val userTransactionsDataSource = TransactionsDataSource()
    println("Contacting Server...")
    runBlocking {
        apiResponse =
            userTransactionsDataSource.selectUserTransactions(
                userId = userId, accountId = accountId
            )
    }
    println("Response : $apiResponse")
    return apiResponse
}
