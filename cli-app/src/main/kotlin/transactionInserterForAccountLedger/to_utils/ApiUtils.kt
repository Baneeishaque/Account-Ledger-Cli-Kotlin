package transactionInserterForAccountLedger.to_utils

object ApiUtils {

    fun getServerApiMethodAbsoluteUrl(serverApiMethodName: String, serverFileExtension: String): String {

        return "$serverApiMethodName.$serverFileExtension"
    }
}
