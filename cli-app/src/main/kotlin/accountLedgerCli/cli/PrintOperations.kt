package accountLedgerCli.cli

import accountLedgerCli.api.response.TransactionResponse

internal fun printAccountLedger(transactions: List<TransactionResponse>, currentAccountId: Int): String {

    println("transactions = [${transactions}]")

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
            "${currentTransaction.event_date_time}\t${currentTransaction.particulars}\t${transactionDirection}${currentTransaction.amount}\t${secondAccountName}\t${currentBalance}\n"
    }
    return outPut
}
