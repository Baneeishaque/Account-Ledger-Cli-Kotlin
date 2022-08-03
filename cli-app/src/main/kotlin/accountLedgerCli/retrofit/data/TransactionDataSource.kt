package accountLedgerCli.retrofit.data

import accountLedgerCli.api.response.TransactionManipulationResponse

internal class TransactionDataSource : AppDataSource<TransactionManipulationResponse>() {

    internal suspend fun insertTransaction(

        userId: UInt,
        eventDateTimeString: String,
        particulars: String,
        amount: Float,
        fromAccountId: UInt,
        toAccountId: UInt

    ): Result<TransactionManipulationResponse> {

        return handleApiResponse(
            apiResponse = retrofitClient.insertTransaction(
                userId = userId,
                eventDateTimeString = eventDateTimeString,
                particulars = particulars,
                amount = amount,
                fromAccountId = fromAccountId,
                toAccountId = toAccountId
            )
        )
    }

    internal suspend fun updateTransaction(

        transactionId: UInt,
        eventDateTimeString: String,
        particulars: String,
        amount: Float,
        fromAccountId: UInt,
        toAccountId: UInt

    ): Result<TransactionManipulationResponse> {

        return handleApiResponse(
            apiResponse = retrofitClient.updateTransaction(
                transactionId = transactionId,
                eventDateTimeString = eventDateTimeString,
                particulars = particulars,
                amount = amount,
                fromAccountId = fromAccountId,
                toAccountId = toAccountId
            )
        )

    }

    internal suspend fun deleteTransaction(transactionId: UInt): Result<TransactionManipulationResponse> {

        return handleApiResponse(
            apiResponse = retrofitClient.deleteTransaction(
                transactionId = transactionId
            )
        )
    }
}