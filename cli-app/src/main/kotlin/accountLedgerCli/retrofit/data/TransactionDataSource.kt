package accountLedgerCli.retrofit.data

import accountLedgerCli.api.response.InsertionResponse

internal class TransactionDataSource : AppDataSource<InsertionResponse>() {

    internal suspend fun insertTransaction(

        userId: UInt,
        eventDateTimeString: String,
        particulars: String?,
        amount: Float,
        fromAccountId: UInt,
        toAccountId: UInt?

    ): Result<InsertionResponse> {

        return try {

            processApiResponse(
                apiResponse = retrofitClient.insertTransaction(
                    userId = userId,
                    eventDateTimeString = eventDateTimeString,
                    particulars = particulars,
                    amount = amount,
                    fromAccountId = fromAccountId,
                    toAccountId = toAccountId
                )
            )

        } catch (exception: java.lang.Exception) {

            Result.failure(exception = Exception("Exception - ${exception.localizedMessage}"))
        }
    }
}