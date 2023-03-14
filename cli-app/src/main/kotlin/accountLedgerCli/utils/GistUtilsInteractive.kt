package accountLedgerCli.utils

import account.ledger.library.constants.Constants
import account.ledger.library.models.AccountLedgerGistDateLedgerModel
import account.ledger.library.models.AccountLedgerGistTransactionModel
import account.ledger.library.utils.GistUtils

object GistUtilsInteractive {

    @JvmStatic
    internal fun processGistIdInteractive(

        userName: String,
        gitHubAccessToken: String,
        gistId: String,
        isDevelopmentMode: Boolean
    ) {

        if (gistId == Constants.defaultValueForStringEnvironmentVariables) {

            println("Missing Gist ID in environment file")

        } else {

            val accountLedgerGist = GistUtils.processGistId(
                userName = userName,
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