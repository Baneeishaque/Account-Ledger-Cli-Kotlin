package accountLedgerCli.utils

import accountLedgerCli.api.response.TransactionResponse
import accountLedgerCli.cli.App
import accountLedgerCli.models.TransactionLedgerInText

internal object TransactionUtils {

    @JvmStatic
    internal fun prepareUserTransactionsMap(transactions: List<TransactionResponse>): LinkedHashMap<UInt, TransactionResponse> {

        val userTransactionsMap = LinkedHashMap<UInt, TransactionResponse>()
        transactions.forEach { currentTransaction: TransactionResponse ->
            userTransactionsMap[currentTransaction.id] = currentTransaction
        }
        return userTransactionsMap
    }

    internal fun userTransactionsToTextFromList(

        transactions: List<TransactionResponse>,
        currentAccountId: UInt,
        isDevelopmentMode: Boolean = App.isDevelopmentMode

    ): String {

        if (isDevelopmentMode) {

            println("transactions = $transactions")
        }
        var currentLedger = TransactionLedgerInText(text = "", balance = 0.0F)
        transactions.forEach { currentTransaction: TransactionResponse ->

            currentLedger = appendToLedger(

                currentTransaction = currentTransaction,
                currentAccountId = currentAccountId,
                currentLedger = currentLedger
            )
        }
        return currentLedger.text
    }

    internal fun userTransactionsToTextFromMap(

        transactionsMap: Map<UInt, TransactionResponse>,
        currentAccountId: UInt,
        isDevelopmentMode: Boolean = App.isDevelopmentMode

    ): String {

        if (isDevelopmentMode) {

            println("transactions = $transactionsMap")
        }
        if (currentAccountId == 0u) {

            var currentTextLedger = ""
            transactionsMap.forEach { currentTransactionEntry: Map.Entry<UInt, TransactionResponse> ->

                currentTextLedger = appendToTextLedger(

                    currentTransaction = currentTransactionEntry.value,
                    currentTextLedger = currentTextLedger
                )
            }
            return currentTextLedger

        } else {

            var currentLedger = TransactionLedgerInText(text = "", balance = 0.0F)
            transactionsMap.forEach { currentTransactionEntry: Map.Entry<UInt, TransactionResponse> ->

                currentLedger = appendToLedger(

                    currentTransaction = currentTransactionEntry.value,
                    currentAccountId = currentAccountId,
                    currentLedger = currentLedger
                )
            }
            return currentLedger.text
        }
    }

    private fun appendToLedger(

        currentTransaction: TransactionResponse,
        currentAccountId: UInt,
        currentLedger: TransactionLedgerInText

    ): TransactionLedgerInText {

        var localCurrentBalance: Float = currentLedger.balance
        val transactionDirection: String
        val secondAccountName: String

        if (currentTransaction.from_account_id == currentAccountId) {

            localCurrentBalance -= currentTransaction.amount
            transactionDirection = "-"
            secondAccountName = currentTransaction.to_account_name

        } else {

            localCurrentBalance += currentTransaction.amount
            transactionDirection = "+"
            secondAccountName = currentTransaction.from_account_name
        }
        return TransactionLedgerInText(

            text = "${currentLedger.text}[${currentTransaction.id}] [${currentTransaction.event_date_time}]\t[${currentTransaction.particulars}]\t[${transactionDirection}${currentTransaction.amount}]\t[${secondAccountName}]\t[${localCurrentBalance}]\n",
            balance = localCurrentBalance
        )
    }

    private fun appendToTextLedger(

        currentTransaction: TransactionResponse,
        currentTextLedger: String

    ): String {

        return "${currentTextLedger}[${currentTransaction.id}] [${currentTransaction.event_date_time}]\t[(${currentTransaction.from_account_full_name}) -> (${currentTransaction.to_account_full_name})]\t[${currentTransaction.particulars}]\t[${currentTransaction.amount}]\n"
    }
}