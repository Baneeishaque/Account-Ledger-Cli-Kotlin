package accountLedgerCli.utils

import accountLedgerCli.api.response.TransactionResponse
import accountLedgerCli.models.AppendToTransactionLedgerResult

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
        currentAccountId: UInt

    ): String {

        //    println("transactions = $transactions")

        var currentLedger = AppendToTransactionLedgerResult(text = "", balance = 0.0F)
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
        currentAccountId: UInt

    ): String {

        //    println("transactions = $transactions")

        var currentLedger = AppendToTransactionLedgerResult(text = "", balance = 0.0F)
        transactionsMap.forEach { currentTransactionEntry: Map.Entry<UInt, TransactionResponse> ->

            currentLedger = appendToLedger(

                currentTransaction = currentTransactionEntry.value,
                currentAccountId = currentAccountId,
                currentLedger = currentLedger
            )
        }
        return currentLedger.text
    }

    private fun appendToLedger(

        currentTransaction: TransactionResponse,
        currentAccountId: UInt,
        currentLedger: AppendToTransactionLedgerResult

    ): AppendToTransactionLedgerResult {

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
        return AppendToTransactionLedgerResult(

            text = "${currentLedger.text}[${currentTransaction.id}] ${currentTransaction.event_date_time}\t${currentTransaction.particulars}\t${transactionDirection}${currentTransaction.amount}\t${secondAccountName}\t${localCurrentBalance}\n",
            balance = localCurrentBalance
        )
    }
}