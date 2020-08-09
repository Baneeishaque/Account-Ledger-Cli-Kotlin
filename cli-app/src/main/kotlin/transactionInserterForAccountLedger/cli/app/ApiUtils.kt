package transactionInserterForAccountLedger.cli.app

object ApiUtils {

    fun getServerApiMethodAbsoluteUrl(serverApiMethodName: String, serverFileExtension: String): String {

        return "$serverApiMethodName.$serverFileExtension"
    }
}
