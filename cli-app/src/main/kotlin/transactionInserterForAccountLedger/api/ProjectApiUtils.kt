package transactionInserterForAccountLedger.api

import transactionInserterForAccountLedger.to_utils.ApiUtils

object ProjectApiUtils {

    fun getServerApiMethodAbsoluteUrl(serverApiMethodName: String): String {

        return ApiUtils.getServerApiMethodAbsoluteUrl(serverApiMethodName, ApiConstants.serverFileExtension)
    }
}
