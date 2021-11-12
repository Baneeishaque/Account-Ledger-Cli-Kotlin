package accountLedgerCli.retrofit.data

import accountLedgerCli.api.response.InsertionResponse
import accountLedgerCli.retrofit.ProjectRetrofitClient
import accountLedgerCli.retrofit.ResponseHolder
import accountLedgerCli.to_utils.MysqlUtils
import retrofit2.Response
import java.io.IOException

class TransactionDataSource {

    private val retrofitClient = ProjectRetrofitClient.retrofitClient

    internal suspend fun insertTransaction(
        userId: Int,
        eventDateTimeString: String,
        particulars: String?,
        amount: Float,
        fromAccountId: Int,
        toAccountId: Int?
    ): ResponseHolder<InsertionResponse> {
        return try {

            processApiResponse(
                retrofitClient.insertTransaction(
                    userId = userId,
                    eventDateTimeString = eventDateTimeString,
                    particulars = particulars,
                    amount = amount,
                    fromAccountId = fromAccountId,
                    toAccountId = toAccountId
                )
            )

        } catch (exception: java.lang.Exception) {

            ResponseHolder.Error(Exception("Exception - ${exception.localizedMessage}"))
        }
    }
}

//    TODO : Rewrite as general function for all responses
private fun processApiResponse(apiResponse: Response<InsertionResponse>): ResponseHolder<InsertionResponse> {

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