package accountLedgerCli.api

import accountLedgerCli.toUtils.ApiUtils

@Suppress("unused")
object ProjectApiUtils {

    @Suppress("unused")
    fun getServerApiMethodAbsoluteUrl(serverApiMethodName: String): String {

        return ApiUtils.getServerApiMethodAbsoluteUrl(serverApiMethodName, ApiConstants.serverFileExtension)
    }
}
