package accountLedgerCli.retrofit.data

import accountLedgerCli.api.response.TransactionsResponse
import accountLedgerCli.retrofit.ProjectRetrofitClient
import accountLedgerCli.retrofit.ResponseHolder
import retrofit2.Response
import java.io.IOException

class TransactionsDataSource {

    private val retrofitClient = ProjectRetrofitClient.retrofitClient

    internal suspend fun selectUserTransactions(
        userId: Int,
        accountId: Int
    ): ResponseHolder<TransactionsResponse> {
        return try {

            processApiResponse(retrofitClient.selectUserTransactionsV2M(userId = userId, accountId = accountId))

        } catch (exception: java.lang.Exception) {

            ResponseHolder.Error(Exception("Exception - ${exception.localizedMessage}"))
        }
    }

    //    TODO : Rewrite as general function for all responses
    private fun processApiResponse(apiResponse: Response<TransactionsResponse>): ResponseHolder<TransactionsResponse> {

        if (apiResponse.isSuccessful) {
            val userTransactionsApiResponseBody = apiResponse.body()
            return if (userTransactionsApiResponseBody != null) {

                ResponseHolder.Success(userTransactionsApiResponseBody)

            } else {

                ResponseHolder.Error(Exception("Invalid Response Body - $userTransactionsApiResponseBody"))
            }
        }
        return ResponseHolder.Error(IOException("Exception Code - ${apiResponse.code()}, Message - ${apiResponse.message()}"))
    }
}
