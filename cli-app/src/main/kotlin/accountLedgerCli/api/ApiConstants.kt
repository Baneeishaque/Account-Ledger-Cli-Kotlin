package accountLedgerCli.api

internal object ApiConstants {

    internal const val serverApiAddress = "https://account-ledger-server.herokuapp.com/http_API/"

    // internal const val serverApiAddress = "http://localhost/account_ledger_server/http_API/"
    internal const val serverFileExtension = "php"

    internal const val selectUserMethod = "select_User"
    internal const val selectUsersMethod = "getUsers"
    internal const val selectUserAccountsV2Method = "select_User_Accounts_v2"
    internal const val selectUserAccountsFullMethod = "select_User_Accounts_full"
    internal const val insertTransactionMethod = "insert_Transaction_v2"
    internal const val selectUserTransactionsV2MMethod = "select_User_Transactions_v2m"
    internal const val selectUserTransactionsAfterSpecifiedDateMethod = "select_User_Transactions_After_Specified_Date"
    internal const val updateTransactionMethod = "update_Transaction_v2"
}
