package transactionInserterForAccountLedger.cli.app

import java.io.IOException
import java.lang.Exception

class UserDataSource {

    private val retrofitClient = AccountLedgerRetrofitClient.retrofitClient

    internal suspend fun selectUser(username: String?,
                                    password: String?): ApiResult<AccountLedgerApiLoginResponse> {

        val loginApiResponse = retrofitClient.selectUser(username = username, password = password)
        if (loginApiResponse.isSuccessful) {
            val loginApiResponseBody = loginApiResponse.body()
            if (loginApiResponseBody != null) {

                return ApiResult.Success(loginApiResponseBody)

            } else {

                return ApiResult.Error(Exception("Invalid Response Body - $loginApiResponseBody"))
            }
        }
        return ApiResult.Error(IOException("Exception Code - ${loginApiResponse.code()}, Message - ${loginApiResponse.message()}"))
    }
}
