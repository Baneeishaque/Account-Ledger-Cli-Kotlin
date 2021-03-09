package accountLedgerCli.toUtils

object ApiUtils {

    internal fun getServerApiMethodAbsoluteUrl(serverApiMethodName: String, serverFileExtension: String): String {

        return "$serverApiMethodName.$serverFileExtension"
    }
}
