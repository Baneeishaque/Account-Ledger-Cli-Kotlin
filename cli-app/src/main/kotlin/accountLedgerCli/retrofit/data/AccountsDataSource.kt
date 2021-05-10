package accountLedgerCli.retrofit.data

import accountLedgerCli.api.response.AccountsResponse
import accountLedgerCli.retrofit.ProjectRetrofitClient
import accountLedgerCli.retrofit.ResponseHolder
import retrofit2.Response
import java.io.IOException

class AccountsDataSource {

    private val retrofitClient = ProjectRetrofitClient.retrofitClient

    internal suspend fun selectUserAccounts(
        userId: Int,
        parentAccountId: Int? = 0
    ): ResponseHolder<AccountsResponse> {

        return try {

            processApiResponse(retrofitClient.selectUserAccounts(userId = userId, parentAccountId = parentAccountId))

        } catch (exception: java.lang.Exception) {

            ResponseHolder.Error(Exception("Exception - ${exception.localizedMessage}"))
        }
    }

    internal suspend fun selectUserAccountsFull(userId: Int): ResponseHolder<AccountsResponse> {

        return try {

            processApiResponse(retrofitClient.selectUserAccountsFull(userId = userId))

        } catch (exception: java.lang.Exception) {

            ResponseHolder.Error(Exception("Exception - ${exception.localizedMessage}"))
        }
    }
}

//    TODO : Rewrite as general function for all responses
private fun processApiResponse(apiResponse: Response<AccountsResponse>): ResponseHolder<AccountsResponse> {

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