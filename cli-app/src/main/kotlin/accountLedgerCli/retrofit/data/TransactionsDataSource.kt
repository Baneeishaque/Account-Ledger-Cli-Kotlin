package accountLedgerCli.retrofit.data

import accountLedgerCli.api.response.TransactionsResponse

internal class TransactionsDataSource : AppDataSource<TransactionsResponse>() {

    internal suspend fun selectUserTransactions(

        userId: UInt,
        accountId: UInt

    ): Result<TransactionsResponse> {

        return handleApiResponse(

            apiResponse = retrofitClient.selectUserTransactionsV2M(

                userId = userId,
                accountId = accountId
            )
        )
    }

    internal suspend fun selectUserTransactionsAfterSpecifiedDate(

        userId: UInt,
        specifiedDate: String

    ): Result<TransactionsResponse> {

        return handleApiResponse(

            apiResponse = retrofitClient.selectUserTransactionsAfterSpecifiedDate(

                userId = userId,
                specifiedDate = specifiedDate
            )
        )
    }
}