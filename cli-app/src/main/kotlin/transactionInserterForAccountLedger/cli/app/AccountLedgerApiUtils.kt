package transactionInserterForAccountLedger.cli.app


object AccountLedgerApiUtils {

    fun getServerApiMethodAbsoluteUrl(serverApiMethodName: String): String {

        return ApiUtils.getServerApiMethodAbsoluteUrl(serverApiMethodName,AccountLedgerApiConstants.serverFileExtension)
    }
}
