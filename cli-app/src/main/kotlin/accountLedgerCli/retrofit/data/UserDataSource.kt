package accountLedgerCli.retrofit.data

import retrofit2.Response
import accountLedgerCli.api.response.LoginResponse
import accountLedgerCli.retrofit.ResponseHolder
import accountLedgerCli.retrofit.ProjectRetrofitClient
import java.io.IOException
import java.lang.Exception

class UserDataSource {

    private val retrofitClient = ProjectRetrofitClient.retrofitClient

    internal suspend fun selectUser(username: String?,
                                    password: String?): ResponseHolder<LoginResponse> {
        return try {

            processApiResponse(retrofitClient.selectUser(username = username, password = password))

        } catch (exception: Exception) {

            ResponseHolder.Error(Exception("Exception - ${exception.localizedMessage}"))
        }
    }
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
