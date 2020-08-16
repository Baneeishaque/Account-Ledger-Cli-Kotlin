package transactionInserterForAccountLedger.api

import transactionInserterForAccountLedger.to_utils.ApiUtils

@Suppress("unused")
object ProjectApiUtils {

    @Suppress("unused")
    fun getServerApiMethodAbsoluteUrl(serverApiMethodName: String): String {

        return ApiUtils.getServerApiMethodAbsoluteUrl(serverApiMethodName, ApiConstants.serverFileExtension)
    }
}
