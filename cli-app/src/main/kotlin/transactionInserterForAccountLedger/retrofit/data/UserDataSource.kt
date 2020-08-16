package transactionInserterForAccountLedger.retrofit.data

import retrofit2.Response
import transactionInserterForAccountLedger.api.response.LoginResponse
import transactionInserterForAccountLedger.retrofit.ResponseHolder
import transactionInserterForAccountLedger.retrofit.ProjectRetrofitClient
import java.io.IOException
import java.lang.Exception

class UserDataSource {

    private val retrofitClient = ProjectRetrofitClient.retrofitClient

    internal suspend fun selectUser(username: String?,
                                    password: String?): ResponseHolder<LoginResponse> {

        return processApiResponse(retrofitClient.selectUser(username = username, password = password))
    }

    //    TODO : Rewrite as general function for all responses
    private fun processApiResponse(apiResponse: Response<LoginResponse>): ResponseHolder<LoginResponse> {

        if (apiResponse.isSuccessful) {

            val loginApiResponseBody = apiResponse.body()
            return if (loginApiResponseBody != null) {

                ResponseHolder.Success(loginApiResponseBody)

            } else {

                ResponseHolder.Error(Exception("Invalid Response Body - $loginApiResponseBody"))
            }
        }
        return ResponseHolder.Error(IOException("Exception Code - ${apiResponse.code()}, Message - ${apiResponse.message()}"))
    }
}
