package accountLedgerCli.retrofit.data

import accountLedgerCli.api.response.AccountsResponse

internal class AccountsDataSource : AppDataSource<AccountsResponse>() {

    internal suspend fun selectUserAccounts(

        userId: UInt,
        parentAccountId: UInt = 0u

    ): Result<AccountsResponse> {

        return handleApiResponse(

            apiResponse = retrofitClient.selectUserAccounts(

                userId = userId,
                parentAccountId = parentAccountId
            )
        )
    }

    internal suspend fun selectUserAccountsFull(userId: UInt): Result<AccountsResponse> {

        return handleApiResponse(

            apiResponse = retrofitClient.selectUserAccountsFull(userId = userId)
        )
    }
}