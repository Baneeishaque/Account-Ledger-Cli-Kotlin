package transactionInserterForAccountLedger.api

import retrofit2.Response
import retrofit2.http.*
import transactionInserterForAccountLedger.api.response.AccountsResponse
import transactionInserterForAccountLedger.api.response.InsertionResponse
import transactionInserterForAccountLedger.api.response.LoginResponse

interface Api {

    @GET("${ApiConstants.selectUserMethod}.${ApiConstants.serverFileExtension}")
    suspend fun selectUser(@Query("username") username: String?,
                           @Query("password") password: String?): Response<LoginResponse>

    @GET("${ApiConstants.selectUserAccountsV2Method}.${ApiConstants.serverFileExtension}")
    suspend fun selectUserAccounts(@Query("user_id") userId: Int?,
                                   @Query("parent_account_id") parentAccountId: Int?): Response<AccountsResponse>

    @GET("${ApiConstants.selectUserAccountsFull}.${ApiConstants.serverFileExtension}")
    suspend fun selectUserAccountsFull(@Query("user_id") userId: Int?): Response<AccountsResponse>

    @FormUrlEncoded
    @POST("${ApiConstants.insertTransaction}.${ApiConstants.serverFileExtension}")
    suspend fun insertTransaction(@Field("event_date_time") eventDateTime: String,
                                  @Field("user_id") userId: Int?,
                                  @Field("particulars") particulars: String?,
                                  @Field("amount") amount: Float,
                                  @Field("from_account_id") fromAccountId: Int,
                                  @Field("to_account_id") toAccountId: Int?): Response<InsertionResponse>
}
