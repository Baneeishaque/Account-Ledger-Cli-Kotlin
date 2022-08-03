package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.constants.Constants
import accountLedgerCli.enums.BalanceSheetRefineLevelEnum
import accountLedgerCli.enums.EnvironmentFileEntryEnum
import accountLedgerCli.enums.TransactionTypeEnum
import accountLedgerCli.models.AccountFrequencyModel
import accountLedgerCli.models.FrequencyOfAccountsModel
import accountLedgerCli.models.InsertTransactionResult
import accountLedgerCli.models.UserModel
import accountLedgerCli.to_utils.EnumUtils
import accountLedgerCli.to_utils.ToDoUtils
import accountLedgerCli.utils.AccountUtils
import accountLedgerCli.utils.ApiUtils

object Screens {
    internal fun userScreen(

        username: String,
        userId: UInt,
        fromAccount: AccountResponse,
        viaAccount: AccountResponse,
        toAccount: AccountResponse,
        dateTimeInText: String,
        transactionParticulars: String,
        transactionAmount: Float

    ): InsertTransactionResult {

//        println("Env. Variables : ${App.dotenv.entries()}")

        var insertTransactionResult = InsertTransactionResult(
            isSuccess = false,
            dateTimeInText = dateTimeInText,
            transactionParticulars = transactionParticulars,
            transactionAmount = transactionAmount
        )
        do {
            commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                listOf(
                    "\nUser : $username",
                    AccountUtils.getFrequentlyUsedTop10Accounts(userId = userId),
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
            when (readLine()!!) {
                "1" -> {
                    insertTransactionResult = HandleResponses.handleAccountsResponseAndPrintMenu(

                        apiResponse = getAccounts(userId = userId),
                        username = username,
                        userId = userId,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = insertTransactionResult.dateTimeInText,
                        transactionParticulars = insertTransactionResult.transactionParticulars,
                        transactionAmount = insertTransactionResult.transactionAmount
                    )
                }

                "2" -> {
                    insertTransactionResult = InsertOperations.openSpecifiedAccountHome(

                        account = InsertOperations.walletAccount,
                        userId = userId,
                        username = username,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = insertTransactionResult.dateTimeInText,
                        transactionParticulars = insertTransactionResult.transactionParticulars,
                        transactionAmount = insertTransactionResult.transactionAmount
                    )
                }

                "3" -> {
                    insertTransactionResult = InsertOperations.insertQuickTransactionFromAccount1toAccount2(

                        account1 = InsertOperations.walletAccount,
                        account2 = InsertOperations.frequent1Account,
                        userId = userId,
                        username = username,
                        viaAccount = viaAccount,
                        dateTimeInText = insertTransactionResult.dateTimeInText,
                        transactionParticulars = insertTransactionResult.transactionParticulars,
                        transactionAmount = insertTransactionResult.transactionAmount
                    )
                }

                "4" -> {
                    insertTransactionResult = InsertOperations.insertQuickTransactionFromAccount1toAccount2(

                        account1 = InsertOperations.walletAccount,
                        account2 = InsertOperations.frequent2Account,
                        userId = userId,
                        username = username,
                        viaAccount = viaAccount,
                        dateTimeInText = insertTransactionResult.dateTimeInText,
                        transactionParticulars = insertTransactionResult.transactionParticulars,
                        transactionAmount = insertTransactionResult.transactionAmount
                    )
                }

                "5" -> {
                    insertTransactionResult = InsertOperations.insertQuickTransactionFromAccount1toAccount2(

                        account1 = InsertOperations.walletAccount,
                        account2 = InsertOperations.frequent3Account,
                        userId = userId,
                        username = username,
                        viaAccount = viaAccount,
                        dateTimeInText = insertTransactionResult.dateTimeInText,
                        transactionParticulars = insertTransactionResult.transactionParticulars,
                        transactionAmount = insertTransactionResult.transactionAmount
                    )
                }

                "6" -> {
                    insertTransactionResult = InsertOperations.openSpecifiedAccountHome(

                        account = InsertOperations.bankAccount,
                        userId = userId,
                        username = username,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = insertTransactionResult.dateTimeInText,
                        transactionParticulars = insertTransactionResult.transactionParticulars,
                        transactionAmount = insertTransactionResult.transactionAmount
                    )
                }

                "7" -> {
                    insertTransactionResult = InsertOperations.insertQuickTransactionFromAccount1toAccount2(

                        account1 = InsertOperations.bankAccount,
                        account2 = InsertOperations.frequent1Account,
                        userId = userId,
                        username = username,
                        viaAccount = viaAccount,
                        dateTimeInText = insertTransactionResult.dateTimeInText,
                        transactionParticulars = insertTransactionResult.transactionParticulars,
                        transactionAmount = insertTransactionResult.transactionAmount
                    )
                }

                "8" -> {
                    insertTransactionResult = InsertOperations.insertQuickTransactionFromAccount1toAccount2(

                        account1 = InsertOperations.bankAccount,
                        account2 = InsertOperations.frequent2Account,
                        userId = userId,
                        username = username,
                        viaAccount = viaAccount,
                        dateTimeInText = insertTransactionResult.dateTimeInText,
                        transactionParticulars = insertTransactionResult.transactionParticulars,
                        transactionAmount = insertTransactionResult.transactionAmount
                    )
                }

                "9" -> {
                    insertTransactionResult = InsertOperations.insertQuickTransactionFromAccount1toAccount2(

                        account1 = InsertOperations.frequent1Account,
                        account2 = InsertOperations.frequent3Account,
                        userId = userId,
                        username = username,
                        viaAccount = viaAccount,
                        dateTimeInText = insertTransactionResult.dateTimeInText,
                        transactionParticulars = insertTransactionResult.transactionParticulars,
                        transactionAmount = insertTransactionResult.transactionAmount
                    )
                }

                "10" -> {

                    insertTransactionResult = InsertOperations.openSpecifiedAccountHome(

                        account = InsertOperations.walletAccount,
                        userId = userId,
                        username = username,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = insertTransactionResult.dateTimeInText,
                        transactionParticulars = insertTransactionResult.transactionParticulars,
                        transactionAmount = insertTransactionResult.transactionAmount
                    )
                }

                "11" -> {
                    insertTransactionResult = InsertOperations.openSpecifiedAccountHome(

                        account = InsertOperations.frequent2Account,
                        userId = userId,
                        username = username,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = insertTransactionResult.dateTimeInText,
                        transactionParticulars = insertTransactionResult.transactionParticulars,
                        transactionAmount = insertTransactionResult.transactionAmount
                    )
                }

                "12" -> {
                    insertTransactionResult = InsertOperations.openSpecifiedAccountHome(

                        account = InsertOperations.frequent3Account,
                        userId = userId,
                        username = username,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = insertTransactionResult.dateTimeInText,
                        transactionParticulars = insertTransactionResult.transactionParticulars,
                        transactionAmount = insertTransactionResult.transactionAmount
                    )
                }

                "13" -> {
                    insertTransactionResult = HandleResponses.handleAccountsResponseAndPrintMenu(

                        apiResponse = ApiUtils.getAccountsFull(userId = userId),
                        username = username,
                        userId = userId,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = insertTransactionResult.dateTimeInText,
                        transactionParticulars = insertTransactionResult.transactionParticulars,
                        transactionAmount = insertTransactionResult.transactionAmount
                    )
                }

                "14" -> {
                    ToDoUtils.showTodo()
                }

                "15" -> {
                    ToDoUtils.showTodo()
                }

                "16" -> {
                    checkAccountsAffectedAfterSpecifiedDate(
                        userId = userId,
                        username = username,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = insertTransactionResult.dateTimeInText,
                        transactionParticulars = insertTransactionResult.transactionParticulars,
                        transactionAmount = insertTransactionResult.transactionAmount
                    )
                }

                "17" -> {
                    viewTransactionsOfSpecificAccount(
                        userId = userId,
                        username = username,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = insertTransactionResult.dateTimeInText,
                        transactionParticulars = insertTransactionResult.transactionParticulars,
                        transactionAmount = insertTransactionResult.transactionAmount
                    )
                }

                "18" -> {
                    printBalanceSheetOfUser(
                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.ALL
                    )
                }

                "19" -> {
                    printBalanceSheetOfUser(
                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_OPEN_BALANCES
                    )
                }

                "20" -> {
                    printBalanceSheetOfUser(
                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_MISC_INCOMES
                    )
                }

                "21" -> {
                    printBalanceSheetOfUser(
                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_INVESTMENT_RETURNS
                    )
                }

                "22" -> {
                    printBalanceSheetOfUser(
                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_FAMILY_ACCOUNTS
                    )
                }

                "23" -> {
                    printBalanceSheetOfUser(
                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_EXPENSE_ACCOUNTS
                    )
                }

                "0" -> {
                    return insertTransactionResult
                }

                else -> {
                    invalidOptionMessage()
                }
            }
        } while (true)
    }



    internal fun getAccountFrequenciesForUser(

        frequencyOfAccounts: FrequencyOfAccountsModel,
        userId: UInt

    ): List<AccountFrequencyModel>? {

        return frequencyOfAccounts.users.find { user: UserModel -> user.id == userId }?.accountFrequencies
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

    ): InsertTransactionResult {

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

            var viewTransactionsInsertTransactionResult = InsertTransactionResult(
                isSuccess = false,
                dateTimeInText = dateTimeInText,
                transactionParticulars = transactionParticulars,
                transactionAmount = transactionAmount
            )

            when (readLine()!!) {

                "1" -> {
                    viewTransactionsInsertTransactionResult = viewTransactions(

                        userId = userId,
                        username = username,
                        accountId = fromAccount.id,
                        accountFullName = fromAccount.fullName,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = viewTransactionsInsertTransactionResult.dateTimeInText,
                        transactionParticulars = viewTransactionsInsertTransactionResult.transactionParticulars,
                        transactionAmount = viewTransactionsInsertTransactionResult.transactionAmount

                    ).addTransactionResult
                }

                "2" -> {
                    viewTransactionsInsertTransactionResult = InsertOperations.addTransaction(

                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.NORMAL,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = viewTransactionsInsertTransactionResult.dateTimeInText,
                        transactionParticulars = viewTransactionsInsertTransactionResult.transactionParticulars,
                        transactionAmount = viewTransactionsInsertTransactionResult.transactionAmount
                    )
                }

                "3" -> {
                    viewTransactionsInsertTransactionResult = viewChildAccounts(
                        username = username,
                        userId = userId,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = viewTransactionsInsertTransactionResult.dateTimeInText,
                        transactionParticulars = viewTransactionsInsertTransactionResult.transactionParticulars,
                        transactionAmount = viewTransactionsInsertTransactionResult.transactionAmount
                    )
                }

                "4" -> {
                    viewTransactionsInsertTransactionResult = InsertOperations.addTransaction(
                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.VIA,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = viewTransactionsInsertTransactionResult.dateTimeInText,
                        transactionParticulars = viewTransactionsInsertTransactionResult.transactionParticulars,
                        transactionAmount = viewTransactionsInsertTransactionResult.transactionAmount
                    )
                }

                "5" -> {
                    viewTransactionsInsertTransactionResult = InsertOperations.addTransaction(
                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.TWO_WAY,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount,
                        dateTimeInText = viewTransactionsInsertTransactionResult.dateTimeInText,
                        transactionParticulars = viewTransactionsInsertTransactionResult.transactionParticulars,
                        transactionAmount = viewTransactionsInsertTransactionResult.transactionAmount
                    )
                }

                "0" -> {
                    return viewTransactionsInsertTransactionResult
                }

                else -> {
                    invalidOptionMessage()
                }
            }
        } while (true)
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
            "Transaction Type : ${
                EnumUtils.getEnumNameForPrint(localEnum = transactionType)
            }",
            "From Account - ${fromAccount.id} : ${fromAccount.fullName}"
        )
        if (transactionType == TransactionTypeEnum.VIA) {
            menuItems = menuItems + listOf("Via. Account - ${viaAccount.id} : ${viaAccount.fullName}")
        }
        menuItems = menuItems + listOf("To Account - ${toAccount.id} : ${toAccount.fullName}")
        return menuItems
    }
}