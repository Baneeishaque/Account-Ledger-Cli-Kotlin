package accountLedgerCli.api

import accountLedgerCli.api.response.AccountsResponse
import accountLedgerCli.api.response.InsertionResponse
import accountLedgerCli.api.response.LoginResponse
import accountLedgerCli.api.response.TransactionsResponse
import retrofit2.Response
import retrofit2.http.*

internal interface Api {

    @GET("${ApiConstants.selectUserMethod}.${ApiConstants.serverFileExtension}")
    suspend fun selectUser(
        @Query("username") username: String?,
        @Query("password") password: String?
    ): Response<LoginResponse>

    @GET("${ApiConstants.selectUserAccountsV2Method}.${ApiConstants.serverFileExtension}")
    suspend fun selectUserAccounts(
        @Query("user_id") userId: Int?,
        @Query("parent_account_id") parentAccountId: Int?
    ): Response<AccountsResponse>

    @GET("${ApiConstants.selectUserAccountsFull}.${ApiConstants.serverFileExtension}")
    suspend fun selectUserAccountsFull(@Query("user_id") userId: Int?): Response<AccountsResponse>

    @GET("${ApiConstants.selectUserTransactionsV2M}.${ApiConstants.serverFileExtension}")
    suspend fun selectUserTransactionsV2M(
        @Query("user_id") userId: Int,
        @Query("account_id") accountId: Int
    ): Response<TransactionsResponse>

    @FormUrlEncoded
    @POST("${ApiConstants.insertTransaction}.${ApiConstants.serverFileExtension}")
    suspend fun insertTransaction(
        @Field("event_date_time") eventDateTimeString: String,
        @Field("user_id") userId: Int?,
        @Field("particulars") particulars: String?,
        @Field("amount") amount: Float,
        @Field("from_account_id") fromAccountId: Int,
        @Field("to_account_id") toAccountId: Int?
    ): Response<InsertionResponse>

    @GET("${ApiConstants.selectUserTransactionsAfterSpecifiedDate}.${ApiConstants.serverFileExtension}")
    suspend fun selectUserTransactionsAfterSpecifiedDate(
        @Query("user_id") userId: Int,
        @Query("specified_date") specifiedDate: String
    ): Response<TransactionsResponse>
}
