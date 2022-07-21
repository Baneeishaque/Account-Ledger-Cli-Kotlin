package accountLedgerCli.retrofit.data

import accountLedgerCli.api.response.AccountsResponse

internal class AccountsDataSource : AppDataSource<AccountsResponse>() {

    internal suspend fun selectUserAccounts(

        userId: UInt,
        parentAccountId: UInt = 0u

    ): Result<AccountsResponse> {

        return executeApiRequest(

            apiRequest = retrofitClient.selectUserAccounts(

                userId = userId,
                parentAccountId = parentAccountId
            )
        )
    }

    internal suspend fun selectUserAccountsFull(userId: UInt): Result<AccountsResponse> {

        return executeApiRequest(

            apiRequest = retrofitClient.selectUserAccountsFull(userId = userId)
        )
    }
}