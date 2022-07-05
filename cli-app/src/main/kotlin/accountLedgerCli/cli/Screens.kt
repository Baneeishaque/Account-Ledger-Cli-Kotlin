package accountLedgerCli.cli

internal fun userScreen(username: String, userId: Int) {

//    println("Env. Variables : ${UserOperations.dotenv.entries()}")
    do {
        commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
            listOf(
                "\nUser : $username",
                "1 - List Accounts : Top Levels",
                "2 - Insert Quick Transaction On : Wallet",
                "3 - Insert Quick Transaction On : Wallet To : ${UserOperations.dotenv["FREQUENT_1_ACCOUNT_NAME"] ?: "N/A"}",
                "4 - Insert Quick Transaction On : Wallet To : ${UserOperations.dotenv["FREQUENT_2_ACCOUNT_NAME"] ?: "N/A"}",
                "5 - Insert Quick Transaction On : Wallet To : ${UserOperations.dotenv["FREQUENT_3_ACCOUNT_NAME"] ?: "N/A"}",
                "6 - Insert Quick Transaction On : Bank : ${UserOperations.dotenv["BANK_ACCOUNT_NAME"] ?: "N/A"}",
                "7 - Insert Quick Transaction On : Bank : ${UserOperations.dotenv["BANK_ACCOUNT_NAME"] ?: "N/A"} To : $${UserOperations.dotenv["FREQUENT_1_ACCOUNT_NAME"] ?: "N/A"}",
                "8 - Insert Quick Transaction On : Bank : ${UserOperations.dotenv["BANK_ACCOUNT_NAME"] ?: "N/A"} To : $${UserOperations.dotenv["FREQUENT_2_ACCOUNT_NAME"] ?: "N/A"}",
                "9 - Insert Quick Transaction On : Bank : ${UserOperations.dotenv["BANK_ACCOUNT_NAME"] ?: "N/A"} To : $${UserOperations.dotenv["FREQUENT_3_ACCOUNT_NAME"] ?: "N/A"}",
                "10 - Insert Quick Transaction On : $${UserOperations.dotenv["FREQUENT_1_ACCOUNT_NAME"] ?: "N/A"}",
                "11 - Insert Quick Transaction On : $${UserOperations.dotenv["FREQUENT_2_ACCOUNT_NAME"] ?: "N/A"}",
                "12 - Insert Quick Transaction On : $${UserOperations.dotenv["FREQUENT_3_ACCOUNT_NAME"] ?: "N/A"}",
                "13 - List Accounts : Full Names",
                "14 - Import Transactions To : Bank : ${UserOperations.dotenv["BANK_ACCOUNT_NAME"] ?: "N/A"} From CSV",
                "15 - Import Transactions To : Bank : ${UserOperations.dotenv["BANK_ACCOUNT_NAME"] ?: "N/A"} From XLX",
                "16 - Check A/Cs affected after a specified date",
                "17 - View Transactions of a specific A/C",
                "0 - Logout",
                "",
                "Enter Your Choice : "
            )
        )
        val choice = readLine()
        when (choice) {
            "1" -> listAccountsTop(username = username, userId = userId)
            "2" -> insertQuickTransactionWallet(userId = userId, username = username)
            "3" -> insertQuickTransactionWalletToFrequent1(userId = userId, username = username)
            "4" -> insertQuickTransactionWalletToFrequent2(userId = userId, username = username)
            "5" -> insertQuickTransactionWalletToFrequent3(userId = userId, username = username)
            "6" -> insertQuickTransactionBank(userId = userId, username = username)
            "7" -> insertQuickTransactionBankToFrequent1(userId = userId, username = username)
            "8" -> insertQuickTransactionBankToFrequent2(userId = userId, username = username)
            "9" -> insertQuickTransactionBankToFrequent3(userId = userId, username = username)
            "10" -> insertQuickTransactionFrequent1(userId = userId, username = username)
            "11" -> insertQuickTransactionFrequent2(userId = userId, username = username)
            "12" -> insertQuickTransactionFrequent3(userId = userId, username = username)
            "13" -> listAccountsFull(username = username, userId = userId)
            "14" -> importBankFromCsv()
            "15" -> importBankFromXlx()
            "16" -> checkAccountsAffectedAfterSpecifiedDate(userId = userId, username = username)
            "17" -> viewTransactionsOfSpecificAccount(userId = userId, username = username)
            "0" -> {}
            else -> invalidOptionMessage()
        }
    } while (choice != "0")
}


internal fun accountHome(userId: Int, username: String) {

    do {
        commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
            listOf(
                "\nUser : $username",
                "Account - ${fromAccount.fullName}",
                "1 - View Transactions",
                "2 - Add Transaction",
                "3 - View Child Accounts",
                "4 - Add Via. Transaction",
                "5 - Add Two Way Transaction",
                "0 - Back",
                "",
                "Enter Your Choice : "
            )
        )
        val choiceInput = readLine()
        when (choiceInput) {
            "1" ->
                viewTransactions(
                    userId = userId,
                    username = username,
                    accountId = fromAccount.id,
                    accountFullName = fromAccount.fullName
                )

            "2" -> addTransaction(userId = userId, username = username, transactionType = TransactionType.NORMAL)
            "3" ->
                viewChildAccounts(
                    userId = userId,
                    username = username,
                )

            "4" -> addTransaction(userId = userId, username = username, transactionType = TransactionType.VIA)
            "5" -> addTransaction(userId = userId, username = username, transactionType = TransactionType.TWO_WAY)
            "0" -> {}
            else -> invalidOptionMessage()
        }
    } while (choiceInput != "0")
}