package transactionInserterForAccountLedger.retrofit.data

import retrofit2.Response
import transactionInserterForAccountLedger.api.response.InsertionResponse
import transactionInserterForAccountLedger.retrofit.ProjectRetrofitClient
import transactionInserterForAccountLedger.retrofit.ResponseHolder
import transactionInserterForAccountLedger.to_utils.MysqlUtils
import java.io.IOException

class TransactionDataSource {

    private val retrofitClient = ProjectRetrofitClient.retrofitClient

    internal suspend fun insertTransaction(userId: Int,
                                           eventDateTimeString: String,
                                           particulars: String?,
                                           amount: Float,
                                           fromAccountId: Int,
                                           toAccountId: Int?): ResponseHolder<InsertionResponse> {

        return processApiResponse(retrofitClient.insertTransaction(userId = userId, eventDateTimeString = MysqlUtils.normalDateTimeStringToMysqlDateTimeString(eventDateTimeString), particulars = particulars, amount = amount, fromAccountId = fromAccountId, toAccountId = toAccountId))
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
}
