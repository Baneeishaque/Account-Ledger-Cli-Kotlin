package accountLedgerCli.utils

import accountLedgerCli.api.response.TransactionResponse

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

        var outPut = ""
        var currentBalance = 0.0F
        var transactionDirection: String
        var secondAccountName: String
        transactions.forEach { currentTransaction: TransactionResponse ->
            if (currentTransaction.from_account_id == currentAccountId) {

                currentBalance -= currentTransaction.amount
                transactionDirection = "-"
                secondAccountName = currentTransaction.to_account_name

            } else {

                currentBalance += currentTransaction.amount
                transactionDirection = ""
                secondAccountName = currentTransaction.from_account_name
            }
            outPut +=
                "[${currentTransaction.id}] ${currentTransaction.event_date_time}\t${currentTransaction.particulars}\t${transactionDirection}${currentTransaction.amount}\t${secondAccountName}\t${currentBalance}\n"
        }
        return outPut
    }
}