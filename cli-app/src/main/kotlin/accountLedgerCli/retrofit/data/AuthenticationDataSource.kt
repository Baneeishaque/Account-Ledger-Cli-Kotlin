package accountLedgerCli.retrofit.data

import accountLedgerCli.api.response.AuthenticationResponse
import accountLedgerCli.retrofit.ProjectRetrofitClient
import accountLedgerCli.retrofit.ResponseHolder
import retrofit2.Response
import java.io.IOException

internal class AuthenticationDataSource {

    private val retrofitClient = ProjectRetrofitClient.retrofitClient

    internal suspend fun authenticateUser(
        username: String?,
        password: String?
    ): ResponseHolder<AuthenticationResponse> {
        return try {

            processApiResponse(retrofitClient.authenticateUser(username = username, password = password))

        } catch (exception: Exception) {

            ResponseHolder.Error(Exception("Exception - ${exception.localizedMessage}"))
        }
    }
}

//    TODO : Rewrite as general function for all responses
private fun processApiResponse(apiResponse: Response<AuthenticationResponse>): ResponseHolder<AuthenticationResponse> {

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
