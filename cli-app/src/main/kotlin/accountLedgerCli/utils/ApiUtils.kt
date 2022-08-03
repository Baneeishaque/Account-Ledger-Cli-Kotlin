package accountLedgerCli.utils

import accountLedgerCli.api.response.AccountsResponse
import accountLedgerCli.retrofit.data.AccountsDataSource
import accountLedgerCli.to_models.IsOkModel
import kotlinx.coroutines.runBlocking
import accountLedgerCli.to_utils.ApiUtils as CommonApiUtils

internal object ApiUtils {

    @JvmStatic
    internal fun getAccountsFull(userId: UInt): Result<AccountsResponse> {

        //TODO : Change return to AccountsResponse instead of Result<AccountsResponse>
        val getAccountsFullResult: IsOkModel<AccountsResponse> =
            CommonApiUtils.makeApiRequestWithOptionalRetries(apiCallFunction = fun(): Result<AccountsResponse> {
                return runBlocking { AccountsDataSource().selectUserAccountsFull(userId = userId) }
            })
        return if (getAccountsFullResult.isOK) {

            Result.success(value = getAccountsFullResult.data!!)

        } else {

            Result.failure(exception = Exception("Please retry the operation..."))
        }
    }
}
