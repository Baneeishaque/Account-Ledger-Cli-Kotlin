package accountLedgerCli.utils

import accountLedgerCli.api.response.AccountsResponse
import accountLedgerCli.retrofit.data.AccountsDataSource
import kotlinx.coroutines.runBlocking

internal object ApiUtils {

    internal fun getAccountsFull(userId: UInt): Result<AccountsResponse> {
        val apiResponse: Result<AccountsResponse>
        val userAccountsDataSource = AccountsDataSource()
        println("Contacting Server...")
        runBlocking { apiResponse = userAccountsDataSource.selectUserAccountsFull(userId = userId) }
        // println("Response : $apiResponse")
        return apiResponse
    }
}
