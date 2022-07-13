package accountLedgerCli.cli

import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.cli.App.Companion.fromAccount

internal fun userScreen(username: String, userId: UInt) {

//    println("Env. Variables : ${UserOperations.dotenv.entries()}")
    do {
        commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
            listOf(
                "\nUser : $username",
                "1 - List Accounts : Top Levels",
                "2 - Insert Quick Transaction On : Wallet",
                "3 - Insert Quick Transaction On : Wallet To : ${
                    getEnvironmentVariableValueForUserScreen(environmentVariableName = "FREQUENT_1_ACCOUNT_NAME")
                }",
                "4 - Insert Quick Transaction On : Wallet To : ${
                    getEnvironmentVariableValueForUserScreen(environmentVariableName = "FREQUENT_2_ACCOUNT_NAME")
                }",
                "5 - Insert Quick Transaction On : Wallet To : ${
                    getEnvironmentVariableValueForUserScreen(environmentVariableName = "FREQUENT_3_ACCOUNT_NAME")
                }",
                "6 - Insert Quick Transaction On : Bank : ${
                    getEnvironmentVariableValueForUserScreen(environmentVariableName = "BANK_ACCOUNT_NAME")
                }",
                "7 - Insert Quick Transaction On : Bank : ${
                    getEnvironmentVariableValueForUserScreen(environmentVariableName = "BANK_ACCOUNT_NAME")
                } To : ${
                    getEnvironmentVariableValueForUserScreen(environmentVariableName = "FREQUENT_1_ACCOUNT_NAME")
                }",
                "8 - Insert Quick Transaction On : Bank : ${
                    getEnvironmentVariableValueForUserScreen(environmentVariableName = "BANK_ACCOUNT_NAME")
                } To : ${
                    getEnvironmentVariableValueForUserScreen(environmentVariableName = "FREQUENT_2_ACCOUNT_NAME")
                }",
                "9 - Insert Quick Transaction On : Bank : ${
                    getEnvironmentVariableValueForUserScreen(environmentVariableName = "BANK_ACCOUNT_NAME")
                } To : ${
                    getEnvironmentVariableValueForUserScreen(environmentVariableName = "FREQUENT_3_ACCOUNT_NAME")
                }",
                "10 - Insert Quick Transaction On : ${
                    getEnvironmentVariableValueForUserScreen(environmentVariableName = "FREQUENT_1_ACCOUNT_NAME")
                }",
                "11 - Insert Quick Transaction On : ${
                    getEnvironmentVariableValueForUserScreen(environmentVariableName = "FREQUENT_2_ACCOUNT_NAME")
                }",
                "12 - Insert Quick Transaction On : ${
                    getEnvironmentVariableValueForUserScreen(environmentVariableName = "FREQUENT_3_ACCOUNT_NAME")
                }",
                "13 - List Accounts : Full Names",
                "14 - Import Transactions To : Bank : ${
                    getEnvironmentVariableValueForUserScreen(environmentVariableName = "BANK_ACCOUNT_NAME")
                } From CSV",
                "15 - Import Transactions To : Bank : ${
                    getEnvironmentVariableValueForUserScreen(environmentVariableName = "BANK_ACCOUNT_NAME")
                } From XLX",
                "16 - Check A/Cs affected after a specified date",
                "17 - View Transactions of a specific A/C",
                "18 - View Balance Sheet Ledger (All)",
                "19 - View Balance Sheet Ledger (Excluding Open Balances)",
                "20 - View Balance Sheet Ledger (Excluding Open Balances & Misc. Incomes)",
                "21 - View Balance Sheet Ledger (Excluding Open Balances, Misc. Incomes & Investment Returns)",
                "22 - View Balance Sheet Ledger (Excluding Open Balances, Misc. Incomes, Investment Returns & Family Accounts)",
                "23 - View Balance Sheet Ledger (Excluding Open Balances, Misc. Incomes, Investment Returns, Family & Expense Accounts)",
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
            "18" -> printBalanceSheetOfUser(
                currentUserName = username,
                currentUserId = userId,
                refineLevel = BalanceSheetRefineLevelEnum.ALL
            )

            "19" -> printBalanceSheetOfUser(
                currentUserName = username,
                currentUserId = userId,
                refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_OPEN_BALANCES
            )

            "20" -> printBalanceSheetOfUser(
                currentUserName = username,
                currentUserId = userId,
                refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_MISC_INCOMES
            )

            "21" -> printBalanceSheetOfUser(
                currentUserName = username,
                currentUserId = userId,
                refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_INVESTMENT_RETURNS
            )

            "22" -> printBalanceSheetOfUser(
                currentUserName = username,
                currentUserId = userId,
                refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_FAMILY_ACCOUNTS
            )

            "23" -> printBalanceSheetOfUser(
                currentUserName = username,
                currentUserId = userId,
                refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_EXPENSE_ACCOUNTS
            )

            "0" -> {}
            else -> invalidOptionMessage()
        }
    } while (choice != "0")
}

private fun getEnvironmentVariableValueForUserScreen(environmentVariableName: String) =
    EnvironmentFileOperations.getEnvironmentVariableValueForTextWithDefaultValue(
        dotenv = App.dotenv,
        environmentVariableName = environmentVariableName,
        defaultValue = Constants.defaultValueForStringEnvironmentVariables
    )

internal fun accountHome(userId: UInt, username: String) {

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
        val choiceInput:String? = readLine()
        when (choiceInput) {
            "1" ->
                viewTransactions(
                    userId = userId,
                    username = username,
                    accountId = fromAccount.id,
                    accountFullName = fromAccount.fullName
                )

            "2" -> addTransaction(
                userId = userId,
                username = username,
                transactionTypeEnum = TransactionTypeEnum.NORMAL
            )

            "3" ->
                viewChildAccounts(
                    userId = userId,
                    username = username,
                )

            "4" -> addTransaction(userId = userId, username = username, transactionTypeEnum = TransactionTypeEnum.VIA)
            "5" -> addTransaction(
                userId = userId,
                username = username,
                transactionTypeEnum = TransactionTypeEnum.TWO_WAY
            )

            "0" -> {}
            else -> invalidOptionMessage()
        }
    } while (choiceInput != "0")
}