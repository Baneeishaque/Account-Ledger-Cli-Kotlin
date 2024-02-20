package accountLedgerCli.cli

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.enums.EnvironmentFileEntryEnum
import account.ledger.library.utils.AccountUtils
import account.ledger.library.utils.TransactionForBajajCoinsUtils
import account_ledger_library.constants.ConstantsNative
import common.utils.library.models.IsOkModel
import common.utils.library.utils.EnvironmentFileOperations
import common.utils.library.utils.InputUtilsInteractive
import common.utils.library.utils.IsOkUtils
import io.github.cdimascio.dotenv.Dotenv

object InsertTransactionForBajajCoins {

    fun addTransactionForBajajCoins(

        isSourceTransactionPresent: Boolean = true,
        sourceAccount: AccountResponse,
        secondPartyAccount: AccountResponse,
        eventDateTimeInText: String,
        dotenv: Dotenv,
        userId: UInt,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean
    ) {
        fun getValidUnsignedIntOrBackForBajajCoinsTransaction(

            dataSpecification: String

        ): IsOkModel<UInt> {

            return InputUtilsInteractive.getValidUnsignedIntOrBack(

                inputText = readlnOrNull().toString(),
                invalidMessage = "Please Enter Correct $dataSpecification"
            )
        }

        fun confirmWholeNumberEnvironmentVariableDataForBajajCoinsTransaction(

            environmentVariableName: String,
            dataSpecification: String

        ): IsOkModel<UInt> {

            return EnvironmentFileOperations.confirmWholeNumberEnvironmentVariableData(

                dotenv = dotenv,
                environmentVariableName = environmentVariableName,
                dataCorrectionOperation = fun(): IsOkModel<UInt> {

                    return getValidUnsignedIntOrBackForBajajCoinsTransaction(dataSpecification = dataSpecification)
                },
                dataSpecification = dataSpecification
            )
        }

        var confirmDataResult: IsOkModel<UInt> = confirmWholeNumberEnvironmentVariableDataForBajajCoinsTransaction(

            environmentVariableName = EnvironmentFileEntryEnum.BAJAJ_COINS_INCOME_ACCOUNT_ID.name,
            dataSpecification = ConstantsNative.BAJAJ_COINS_INCOME_ACCOUNT_ID
        )

        if (confirmDataResult.isOK) {

            AccountUtils.processUserAccountsMap(

                userId = userId,
                isConsoleMode = isConsoleMode,
                isDevelopmentMode = isDevelopmentMode,
                successActions = fun(userAccountsMap: LinkedHashMap<UInt, AccountResponse>) {

                    val getValidBajajCoinsIncomeAccountResult: IsOkModel<AccountResponse> =
                        AccountUtils.getValidAccountById(

                            desiredAccount = userAccountsMap[confirmDataResult.data!!],
                            userAccountsMap = userAccountsMap,
                            idCorrectionFunction = fun(): IsOkModel<UInt> {

                                return getValidUnsignedIntOrBackForBajajCoinsTransaction(dataSpecification = ConstantsNative.BAJAJ_COINS_INCOME_ACCOUNT_ID)
                            }
                        )
                    if (getValidBajajCoinsIncomeAccountResult.isOK) {

                        confirmDataResult = confirmWholeNumberEnvironmentVariableDataForBajajCoinsTransaction(

                            environmentVariableName = EnvironmentFileEntryEnum.BAJAJ_COINS_WALLET_ACCOUNT_ID.name,
                            dataSpecification = ConstantsNative.BAJAJ_COINS_WALLET_ACCOUNT_ID
                        )

                        if (confirmDataResult.isOK) {

                            val getValidBajajCoinsWalletAccountResult: IsOkModel<AccountResponse> =
                                AccountUtils.getValidAccountById(

                                    desiredAccount = userAccountsMap[confirmDataResult.data!!],
                                    userAccountsMap = userAccountsMap,
                                    idCorrectionFunction = fun(): IsOkModel<UInt> {

                                        return getValidUnsignedIntOrBackForBajajCoinsTransaction(dataSpecification = ConstantsNative.BAJAJ_COINS_WALLET_ACCOUNT_ID)
                                    }
                                )

                            if (getValidBajajCoinsWalletAccountResult.isOK) {

                                confirmDataResult = confirmWholeNumberEnvironmentVariableDataForBajajCoinsTransaction(

                                    environmentVariableName = EnvironmentFileEntryEnum.BAJAJ_COINS_CONVERSION_RATE.name,
                                    dataSpecification = ConstantsNative.BAJAJ_COINS_CONVERSION_RATE
                                )
                                if (confirmDataResult.isOK) {

                                    val getDataResult: IsOkModel<List<UInt>> =
                                        IsOkUtils.checkListOfOkModels(
                                            isOkModels = listOf(

                                                InputUtilsInteractive.getValidUnsignedIntOrBackWithPrompt(

                                                    dataSpecification = "Amount To Spend for Bajaj Coin Rewarding Transactions"
                                                ),
                                                InputUtilsInteractive.getValidUnsignedIntOrBackWithPrompt(

                                                    dataSpecification = "Transaction Amount for Bajaj Coin Rewards"
                                                ),
                                                InputUtilsInteractive.getValidUnsignedIntOrBackWithPrompt(

                                                    dataSpecification = "Count of Rewarding Transactions"
                                                )
                                            )
                                        )
                                    if (getDataResult.isOK) {

                                        val countOfCoinRewardingTransactions = getDataResult.data!!.last()
                                        val getDataLists = IsOkUtils.checkListOfOkModels(
                                            isOkModels = listOf(

                                                InputUtilsInteractive.getMultipleValidUnsignedIntOrBackWithPrompt(

                                                    dataSpecification = "List of Bajaj Coin Rewarding Transactions",
                                                    count = countOfCoinRewardingTransactions
                                                ),
                                                InputUtilsInteractive.getMultipleValidUnsignedIntOrBackWithPrompt(

                                                    dataSpecification = "List of Bajaj Coin Rewards",
                                                    count = countOfCoinRewardingTransactions
                                                )
                                            )
                                        )

                                        if (getDataLists.isOK) {

                                            val listOfCoinRewardingTransactionIndexes = getDataLists.data!!.first()
                                            val totalNumberOfTransactionsForBajajCoins =
                                                listOfCoinRewardingTransactionIndexes.last()

                                            val bajajCoinConversionRate = confirmDataResult.data!!
                                            val getAccountBalancesResult = IsOkUtils.checkListOfOkModels(

                                                isOkModels = listOf(

                                                    AccountUtils.getAccountBalance(

                                                        userId = userId,
                                                        desiredAccountId = sourceAccount.id,
                                                        isDevelopmentMode = isDevelopmentMode
                                                    ),
                                                    AccountUtils.getAccountBalance(

                                                        userId = userId,
                                                        desiredAccountId = secondPartyAccount.id,
                                                        isDevelopmentMode = isDevelopmentMode
                                                    ),
                                                    AccountUtils.getAccountBalance(

                                                        userId = userId,
                                                        desiredAccountId = getValidBajajCoinsWalletAccountResult.data!!.id,
                                                        isDevelopmentMode = isDevelopmentMode
                                                    )
                                                )
                                            )

                                            if (getAccountBalancesResult.isOK) {

                                                val balanceOfBajajCoinsWalletAccount =
                                                    getAccountBalancesResult.data!!.last()

                                                // Check if a/c 1 has at least balance of X
                                                val amountToSpendForBajajCoinRewards = getDataResult.data!!.first()
                                                val balanceOfSourceAccount = getAccountBalancesResult.data!!.first()
                                                if (balanceOfSourceAccount < amountToSpendForBajajCoinRewards
                                                        .toFloat()
                                                ) {
                                                    println("Insufficient balance in ${sourceAccount.name}. Please ensure ${sourceAccount.name} has at least a balance of $amountToSpendForBajajCoinRewards.")
                                                } else {

                                                    // Check if (Y * Z) > X and if a/c 2 has (Y * Z) - X amount as balance
                                                    val perTransactionAmountForBajajCoins = getDataResult.data!![1]
                                                    if ((totalNumberOfTransactionsForBajajCoins * perTransactionAmountForBajajCoins) > amountToSpendForBajajCoinRewards) {

                                                        val secondPartyAccountBalance =
                                                            getAccountBalancesResult.data!![1]
                                                        if (secondPartyAccountBalance < ((totalNumberOfTransactionsForBajajCoins * perTransactionAmountForBajajCoins).toFloat() - amountToSpendForBajajCoinRewards.toFloat())) {
                                                            println("Insufficient balance in ${secondPartyAccount.name}. Please ensure ${secondPartyAccount.name} has at least a balance of ${(totalNumberOfTransactionsForBajajCoins * perTransactionAmountForBajajCoins) - amountToSpendForBajajCoinRewards}.")
                                                        }
                                                    } else {

                                                        // Prepare transactions
                                                        val listOfRewardedCoins = getDataLists.data!!.last()
                                                        val transactions =
                                                            TransactionForBajajCoinsUtils.prepareTransactions(

                                                                isSourceTransactionPresent = isSourceTransactionPresent,
                                                                sourceAccount = sourceAccount,
                                                                secondPartyAccount = secondPartyAccount,
                                                                bajajCoinsIncomeAccount = getValidBajajCoinsIncomeAccountResult.data!!,
                                                                bajajCoinsCollectionAccount = getValidBajajCoinsWalletAccountResult.data!!,
                                                                amountToSpendForBajajCoinRewards = amountToSpendForBajajCoinRewards,
                                                                perTransactionAmountForBajajCoins = perTransactionAmountForBajajCoins,
                                                                totalNumberOfTransactionsForBajajCoins = totalNumberOfTransactionsForBajajCoins,
                                                                listOfCoinRewardingTransactionIndexes = listOfCoinRewardingTransactionIndexes,
                                                                listOfRewardedCoins = listOfRewardedCoins,
                                                                bajajCoinsCollectionAccountBalance = balanceOfBajajCoinsWalletAccount,
                                                                eventDateTimeInText = eventDateTimeInText,
                                                                bajajCoinConversionRate = bajajCoinConversionRate
                                                            )

                                                        println("transactions = $transactions")

//                                                        // Confirm transactions with user
//                                                        val confirmed = confirmTransactions(transactions)
//
//                                                        // If user confirmed, insert transactions to server
//                                                        if (confirmed) {
//                                                            insertTransactionsToServer(transactions)
//                                                        }

                                                    }
                                                }
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}