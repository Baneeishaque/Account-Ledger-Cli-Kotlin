package accountLedgerCli.utils

import account_ledger_library.constants.ConstantsNative
import account_ledger_library.models.AccountLedgerGistDateLedgerModel
import account_ledger_library.models.AccountLedgerGistTransactionModel
import account_ledger_library.utils.GistUtils

object GistUtilsInteractive {

    @JvmStatic
    internal fun processGistIdInteractive(

        userName: String,
        userId: UInt,
        gitHubAccessToken: String,
        gistId: String,
        isDevelopmentMode: Boolean
    ) {

        if (gistId == ConstantsNative.defaultValueForStringEnvironmentVariables) {

            println("Missing Gist ID in environment file")

        } else {

            val accountLedgerGist = GistUtils().processGistIdForData(
                userName = userName,
                userId = userId,
                gitHubAccessToken = gitHubAccessToken,
                gistId = gistId,
                isDevelopmentMode = isDevelopmentMode,
                isApiCall = false
            )
            accountLedgerGist.accountLedgerPages.forEach { (currentAccountId: UInt, accountLedgerGistDateLedgers: LinkedHashMap<String, AccountLedgerGistDateLedgerModel>) ->

                println("Current Account ID => $currentAccountId")
                accountLedgerGistDateLedgers.forEach { accountLedgerGistDateLedger: Map.Entry<String, AccountLedgerGistDateLedgerModel> ->

                    println("${accountLedgerGistDateLedger.key} - Initial Balance => ${accountLedgerGistDateLedger.value.initialBalanceOnDate}")
                    accountLedgerGistDateLedger.value.transactionsOnDate.forEach { accountLedgerGistTransaction: AccountLedgerGistTransactionModel ->

                        println("${accountLedgerGistDateLedger.key} - ${accountLedgerGistTransaction.transactionParticulars} ~ ${accountLedgerGistTransaction.transactionAmount}")
                    }
                    println("${accountLedgerGistDateLedger.key} - Final Balance => ${accountLedgerGistDateLedger.value.finalBalanceOnDate}")
                }
            }
        }
    }
}
