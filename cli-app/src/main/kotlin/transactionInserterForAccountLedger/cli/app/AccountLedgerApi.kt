package transactionInserterForAccountLedger.cli.app

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AccountLedgerApi {

    @GET("${AccountLedgerApiConstants.selectUserServerApiMethodName}.${AccountLedgerApiConstants.serverFileExtension}")
    suspend fun selectUser(@Query("username") username: String?,
                           @Query("password") password: String?): Response<AccountLedgerApiLoginResponse>
}
