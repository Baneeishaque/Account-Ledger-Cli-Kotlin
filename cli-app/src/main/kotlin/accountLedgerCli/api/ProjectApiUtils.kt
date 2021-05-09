package accountLedgerCli.api

import accountLedgerCli.to_utils.ApiUtils

internal object ProjectApiUtils {

    internal fun getServerApiMethodAbsoluteUrl(serverApiMethodName: String): String {

        return ApiUtils.getServerApiMethodAbsoluteUrl(serverApiMethodName, ApiConstants.serverFileExtension)
    }
}
