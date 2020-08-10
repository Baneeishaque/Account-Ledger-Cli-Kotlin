package transactionInserterForAccountLedger.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import transactionInserterForAccountLedger.api.response.AccountsResponse
import transactionInserterForAccountLedger.api.response.LoginResponse

interface Api {

    @GET("${ApiConstants.selectUserMethod}.${ApiConstants.serverFileExtension}")
    suspend fun selectUser(@Query("username") username: String?,
                           @Query("password") password: String?): Response<LoginResponse>

    @GET("${ApiConstants.selectUserAccountsV2Method}.${ApiConstants.serverFileExtension}")
    suspend fun selectUserAccounts(@Query("user_id") userId: Int?,
                                   @Query("parent_account_id") parentAccountId: Int?): Response<AccountsResponse>

}
