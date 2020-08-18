package accountLedgerCli.to_utils

object ApiUtils {

    internal fun getServerApiMethodAbsoluteUrl(serverApiMethodName: String, serverFileExtension: String): String {

        return "$serverApiMethodName.$serverFileExtension"
    }
}
