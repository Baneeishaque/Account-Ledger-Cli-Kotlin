package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.enums.BalanceSheetRefineLevelEnum
import accountLedgerCli.enums.EnvironmentFileEntryEnum
import accountLedgerCli.enums.TransactionTypeEnum
import accountLedgerCli.utils.ApiUtils

object Screens {
    internal fun userScreen(
        username: String,
        userId: UInt,
        fromAccount:AccountResponse,
        viaAccount: AccountResponse,
        toAccount: AccountResponse,
        dateTimeInText: String,
        transactionParticulars: String,
        transactionAmount: Float
    ) {

//        println("Env. Variables : ${App.dotenv.entries()}")
        do {
            commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                listOf(
                    "\nUser : $username",
                    "1 - List Accounts : Top Levels",
                    "2 - Insert Quick Transaction On : Wallet",
                    "3 - Insert Quick Transaction On : Wallet To : ${
                        getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_1_ACCOUNT_NAME.name)
                    }",
                    "4 - Insert Quick Transaction On : Wallet To : ${
                        getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_2_ACCOUNT_NAME.name)
                    }",
                    "5 - Insert Quick Transaction On : Wallet To : ${
                        getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_3_ACCOUNT_NAME.name)
                    }",
                    "6 - Insert Quick Transaction On : Bank : ${
                        getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.BANK_ACCOUNT_NAME.name)
                    }",
                    "7 - Insert Quick Transaction On : Bank : ${
                        getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.BANK_ACCOUNT_NAME.name)
                    } To : ${
                        getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_1_ACCOUNT_NAME.name)
                    }",
                    "8 - Insert Quick Transaction On : Bank : ${
                        getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.BANK_ACCOUNT_NAME.name)
                    } To : ${
                        getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_2_ACCOUNT_NAME.name)
                    }",
                    "9 - Insert Quick Transaction On : Bank : ${
                        getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.BANK_ACCOUNT_NAME.name)
                    } To : ${
                        getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_3_ACCOUNT_NAME.name)
                    }",
                    "10 - Insert Quick Transaction On : ${
                        getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_1_ACCOUNT_NAME.name)
                    }",
                    "11 - Insert Quick Transaction On : ${
                        getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_2_ACCOUNT_NAME.name)
                    }",
                    "12 - Insert Quick Transaction On : ${
                        getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_3_ACCOUNT_NAME.name)
                    }",
                    "13 - List Accounts : Full Names",
                    "14 - Import Transactions To : Bank : ${
                        getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.BANK_ACCOUNT_NAME.name)
                    } From CSV",
                    "15 - Import Transactions To : Bank : ${
                        getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.BANK_ACCOUNT_NAME.name)
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
            val choice: String? = readLine()
            when (choice) {
                "1" -> handleAccountsResponseAndPrintMenu(
                    apiResponse = getAccounts(userId = userId),
                    username = username,
                    userId = userId,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )

                "2" -> InsertOperations.openSpecifiedAccountHome(
                    account = InsertOperations.walletAccount,
                    userId = userId,
                    userAccountsMapLocal = App.userAccountsMap,
                    username = username,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )

                "3" -> InsertOperations.insertQuickTransactionFromAccount1toAccount2(
                    account1 = InsertOperations.walletAccount,
                    account2 = InsertOperations.frequent1Account,
                    userId = userId,
                    userAccountsMapLocal = App.userAccountsMap,
                    username = username,
                    viaAccount = viaAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )

                "4" -> InsertOperations.insertQuickTransactionFromAccount1toAccount2(
                    account1 = InsertOperations.walletAccount,
                    account2 = InsertOperations.frequent2Account,
                    userId = userId,
                    userAccountsMapLocal = App.userAccountsMap,
                    username = username,
                    viaAccount = viaAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )

                "5" -> InsertOperations.insertQuickTransactionFromAccount1toAccount2(
                    account1 = InsertOperations.walletAccount,
                    account2 = InsertOperations.frequent3Account,
                    userId = userId,
                    userAccountsMapLocal = App.userAccountsMap,
                    username = username,
                    viaAccount = viaAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )

                "6" -> InsertOperations.openSpecifiedAccountHome(
                    account = InsertOperations.bankAccount,
                    userId = userId,
                    userAccountsMapLocal = App.userAccountsMap,
                    username = username,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )

                "7" -> InsertOperations.insertQuickTransactionFromAccount1toAccount2(
                    account1 = InsertOperations.bankAccount,
                    account2 = InsertOperations.frequent1Account,
                    userId = userId,
                    userAccountsMapLocal = App.userAccountsMap,
                    username = username,
                    viaAccount = viaAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )

                "8" -> InsertOperations.insertQuickTransactionFromAccount1toAccount2(
                    account1 = InsertOperations.bankAccount,
                    account2 = InsertOperations.frequent2Account,
                    userId = userId,
                    userAccountsMapLocal = App.userAccountsMap,
                    username = username,
                    viaAccount = viaAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )

                "9" -> InsertOperations.insertQuickTransactionFromAccount1toAccount2(
                    account1 = InsertOperations.frequent1Account,
                    account2 = InsertOperations.frequent3Account,
                    userId = userId,
                    userAccountsMapLocal = App.userAccountsMap,
                    username = username,
                    viaAccount = viaAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )

                "10" -> InsertOperations.openSpecifiedAccountHome(
                    account = InsertOperations.walletAccount,
                    userId = userId,
                    userAccountsMapLocal = App.userAccountsMap,
                    username = username,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )

                "11" -> InsertOperations.openSpecifiedAccountHome(
                    account = InsertOperations.frequent2Account,
                    userId = userId,
                    userAccountsMapLocal = App.userAccountsMap,
                    username = username,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )

                "12" -> InsertOperations.openSpecifiedAccountHome(
                    account = InsertOperations.frequent3Account,
                    userId = userId,
                    userAccountsMapLocal = App.userAccountsMap,
                    username = username,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )

                "13" -> handleAccountsResponseAndPrintMenu(
                    apiResponse = ApiUtils.getAccountsFull(userId = userId),
                    username = username,
                    userId = userId,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )

                "14" -> importBankFromCsv()
                "15" -> importBankFromXlx()
                "16" -> checkAccountsAffectedAfterSpecifiedDate(
                    userId = userId,
                    username = username,
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )

                "17" -> viewTransactionsOfSpecificAccount(
                    userId = userId,
                    username = username,
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )

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

    internal fun accountHome(
        userId: UInt,
        username: String,
        fromAccount: AccountResponse,
        viaAccount: AccountResponse,
        toAccount: AccountResponse,
        dateTimeInText: String,
        transactionParticulars: String,
        transactionAmount: Float
    ) {

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
            val choiceInput: String? = readLine()
            when (choiceInput) {
                "1" -> viewTransactions(
                    userId = userId,
                    username = username,
                    accountId = fromAccount.id,
                    accountFullName = fromAccount.fullName,
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )

                "2" -> InsertOperations.addTransaction(
                    userId = userId,
                    username = username,
                    transactionType = TransactionTypeEnum.NORMAL,
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )

                "3" -> viewChildAccounts(
                    username = username,
                    userId = userId,
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )

                "4" -> InsertOperations.addTransaction(
                    userId = userId,
                    username = username,
                    transactionType = TransactionTypeEnum.VIA,
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )

                "5" -> InsertOperations.addTransaction(
                    userId = userId,
                    username = username,
                    transactionType = TransactionTypeEnum.TWO_WAY,
                    fromAccount = fromAccount,
                    viaAccount = viaAccount,
                    toAccount = toAccount,
                    dateTimeInText = dateTimeInText,
                    transactionParticulars = transactionParticulars,
                    transactionAmount = transactionAmount
                )

                "0" -> {}
                else -> invalidOptionMessage()
            }
        } while (choiceInput != "0")
    }

    internal fun getUserWithCurrentAccountSelectionsAsText(

        username: String,
        fromAccount: AccountResponse,
        viaAccount: AccountResponse,
        toAccount: AccountResponse,
        transactionType: TransactionTypeEnum

    ): List<String> {

        var menuItems: List<String> = listOf(
            "\nUser : $username",
            "Transaction Type : $transactionType",
            "From Account - ${fromAccount.id} : ${fromAccount.fullName}"
        )
        if (transactionType == TransactionTypeEnum.VIA) {
            menuItems = menuItems + listOf("Via. Account - ${viaAccount.id} : ${viaAccount.fullName}")
        }
        menuItems = menuItems + listOf("To Account - ${toAccount.id} : ${toAccount.fullName}")
        return menuItems
    }
}