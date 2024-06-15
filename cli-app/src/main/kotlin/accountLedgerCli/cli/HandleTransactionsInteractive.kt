package accountLedgerCli.cli

import account.ledger.library.api.response.TransactionResponse
import account.ledger.library.models.ChooseTransactionResultModel
import account.ledger.library.utils.TransactionUtils.transactionsToTextFromList
import account_ledger_library.constants.ConstantsNative
import common.utils.library.constants.ConstantsCommon
import common.utils.library.utils.ErrorUtilsInteractive
import common.utils.library.utils.ListUtilsInteractive

object HandleTransactionsInteractive {

    fun chooseTransaction(

        transactions: List<TransactionResponse>,
        isDevelopmentMode: Boolean

    ): ChooseTransactionResultModel {

        do {
            App.commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(

                listOf(
                    "\nChoose a Transaction",
                    transactionsToTextFromList(transactions),
                    "1 - Choose Transaction - By Index Number",
                    "2 - Search Transactions - By Part Of Particulars",
                    "0 - Back",
                    "",
                    "Enter Your Choice : "
                )
            )
            when (readln()) {
                "1" -> {
                    return handleTransactionsWithZeroAsBackValue(

                        selectedTransactionIndex = ListUtilsInteractive.getValidIndexFromCollectionWithSelectionPromptAndZeroAsBack(

                            list = transactions,
                            itemSpecification = ConstantsNative.TRANSACTION_TEXT,
                            items = transactionsToTextFromList(transactions)
                        ),
                        transactions = transactions
                    )
                }

                "2" -> {
                    return handleTransactionsWithZeroAsBackValue(

                        selectedTransactionIndex = searchInTransactions(

                            transactions = transactions,
                            isDevelopmentMode = isDevelopmentMode
                        ),
                        transactions = transactions
                    )
                }

                "0" -> {
                    return ChooseTransactionResultModel(isTransactionSelected = false)
                }

                else -> ErrorUtilsInteractive.printInvalidOptionMessage()
            }
        } while (true)
    }

    private fun handleTransactionsWithZeroAsBackValue(

        selectedTransactionIndex: UInt,
        transactions: List<TransactionResponse>

    ): ChooseTransactionResultModel {

        if (selectedTransactionIndex != 0u) {

            return ChooseTransactionResultModel(

                isTransactionSelected = true,
                selectedTransaction = transactions[selectedTransactionIndex.toInt()]
            )
        }
        return ChooseTransactionResultModel(isTransactionSelected = false)
    }

    private fun searchInTransactions(

        transactions: List<TransactionResponse>,
        isDevelopmentMode: Boolean

    ): UInt {

        App.commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(

            listOf("\nEnter Search Key (Part of Particulars) : ")
        )
        val searchKeyInput: String = readln()

        if (isDevelopmentMode) {

            println(
                "List to Search\n${ConstantsCommon.dashedLineSeparator}\n${

                    transactionsToTextFromList(transactions = transactions)
                }"
            )
            println("searchKey = $searchKeyInput")
        }

        val searchResult: MutableList<TransactionResponse> = mutableListOf()
        transactions.forEach { transaction: TransactionResponse ->

            if (transaction.particulars.contains(other = searchKeyInput, ignoreCase = true)) {

                searchResult.add(transaction)
            }
        }

        if (searchResult.isEmpty()) {

            do {

                App.commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(

                    listOf(

                        "No Matches....",
                        "1 - Try Again",
                        "0 - Back",
                        "",
                        "Enter Your Choice : "
                    )
                )

                val input: String = readln()

                if (input == "1") return searchInTransactions(

                    transactions = transactions,
                    isDevelopmentMode = isDevelopmentMode
                )
                else if (input != "0") ErrorUtilsInteractive.printInvalidOptionMessage()

            } while (input != "0")

        } else {

            do {

                App.commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(

                    listOf(

                        "\nSearch Results",
                        transactionsToTextFromList(transactions = searchResult),
                        "1 - Choose Transaction - By Index Number",
                        "0 - Back",
                        "",
                        "Enter Your Choice : "
                    )
                )

                val input: String = readln()

                if (input == "1") {

                    return ListUtilsInteractive.getValidIndexFromCollectionWithSelectionPromptAndZeroAsBack(

                        list = searchResult,
                        itemSpecification = ConstantsNative.TRANSACTION_TEXT,
                        items = transactionsToTextFromList(transactions = searchResult)
                    )
                } else if (input != "0") {

                    ErrorUtilsInteractive.printInvalidOptionMessage()
                }
            } while (input != "0")
        }
        return 0u
    }
}
