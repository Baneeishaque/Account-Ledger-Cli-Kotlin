package accountLedgerCli.api

internal object ApiConstants {

    internal const val serverApiAddress = "http://account-ledger-server.herokuapp.com/http_API/"

    // internal const val serverApiAddress = "http://localhost/account_ledger_server/http_API/"
    internal const val serverFileExtension = "php"

    internal const val selectUserMethod = "select_User"
    internal const val selectUserAccountsV2Method = "select_User_Accounts_v2"
    internal const val selectUserAccountsFull = "select_User_Accounts_full"
    internal const val insertTransaction = "insert_Transaction_v2"
    internal const val selectUserTransactionsV2M = "select_User_Transactions_v2m"
}
