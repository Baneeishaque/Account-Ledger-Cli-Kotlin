package accountLedgerCli.cli

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.enums.EnvironmentFileEntryEnum
import account.ledger.library.utils.AccountUtils
import account.ledger.library.utils.TransactionForBajajCoinsUtils
import account_ledger_library.constants.ConstantsNative
import account.ledger.library.models.TransactionModel
import common.utils.library.constants.CommonConstants
import common.utils.library.models.IsOkModel
import common.utils.library.utils.EnvironmentFileOperations
import common.utils.library.utils.InputUtilsInteractive
import common.utils.library.utils.IsOkUtils
import common_utils_library.constants.ConstantsCommonNative
import io.github.cdimascio.dotenv.Dotenv

object InsertTransactionForBajajCoins {

    fun generateTransactionsForBajajCoins(

        isSourceTransactionPresent: Boolean = true,
        sourceAccount: AccountResponse,
        secondPartyAccount: AccountResponse,
        eventDateTimeInText: String,
        dotenv: Dotenv,
        userId: UInt,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ): IsOkModel<List<TransactionModel>> {

        val result = IsOkModel<List<TransactionModel>>(isOK = false)

        fun getValidUnsignedIntOrBackForBajajCoinsTransaction(

            dataSpecification: String

        ): IsOkModel<UInt> {

            return InputUtilsInteractive.getValidUnsignedIntOrBackWithPrompt(

                dataSpecification = dataSpecification
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
            dataSpecification = ConstantsNative.BAJAJ_COINS_INCOME_ACCOUNT_ID_TEXT
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

                                return getValidUnsignedIntOrBackForBajajCoinsTransaction(dataSpecification = ConstantsNative.BAJAJ_COINS_INCOME_ACCOUNT_ID_TEXT)
                            }
                        )
                    if (getValidBajajCoinsIncomeAccountResult.isOK) {

                        confirmDataResult = confirmWholeNumberEnvironmentVariableDataForBajajCoinsTransaction(

                            environmentVariableName = EnvironmentFileEntryEnum.BAJAJ_COINS_WALLET_ACCOUNT_ID.name,
                            dataSpecification = ConstantsNative.BAJAJ_COINS_WALLET_ACCOUNT_ID_TEXT
                        )

                        if (confirmDataResult.isOK) {

                            val getValidBajajCoinsWalletAccountResult: IsOkModel<AccountResponse> =
                                AccountUtils.getValidAccountById(

                                    desiredAccount = userAccountsMap[confirmDataResult.data!!],
                                    userAccountsMap = userAccountsMap,
                                    idCorrectionFunction = fun(): IsOkModel<UInt> {

                                        return getValidUnsignedIntOrBackForBajajCoinsTransaction(dataSpecification = ConstantsNative.BAJAJ_COINS_WALLET_ACCOUNT_ID_TEXT)
                                    }
                                )

                            if (getValidBajajCoinsWalletAccountResult.isOK) {

                                confirmDataResult = confirmWholeNumberEnvironmentVariableDataForBajajCoinsTransaction(

                                    environmentVariableName = EnvironmentFileEntryEnum.BAJAJ_COINS_CONVERSION_RATE.name,
                                    dataSpecification = ConstantsNative.BAJAJ_COINS_CONVERSION_RATE_TEXT
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
                                                    result.error =
                                                        "Insufficient balance in ${sourceAccount.name}. Please ensure ${sourceAccount.name} has at least a balance of $amountToSpendForBajajCoinRewards."

                                                } else {

                                                    // Check if (Y * Z) > X and if a/c 2 has (Y * Z) - X amount as balance
                                                    val perTransactionAmountForBajajCoins = getDataResult.data!![1]

                                                    var secondPartyAccountHasDesiredBalance = true
                                                    val secondPartyIsHortInBalanceMessage =
                                                        "Insufficient balance in ${secondPartyAccount.name}. Please ensure ${secondPartyAccount.name} has at least a balance of ${(totalNumberOfTransactionsForBajajCoins * perTransactionAmountForBajajCoins) - amountToSpendForBajajCoinRewards}."

                                                    if ((totalNumberOfTransactionsForBajajCoins * perTransactionAmountForBajajCoins) > amountToSpendForBajajCoinRewards) {

                                                        val secondPartyAccountBalance =
                                                            getAccountBalancesResult.data!![1]
                                                        if (secondPartyAccountBalance < ((totalNumberOfTransactionsForBajajCoins * perTransactionAmountForBajajCoins).toFloat() - amountToSpendForBajajCoinRewards.toFloat())) {

                                                            println(secondPartyIsHortInBalanceMessage)
                                                            secondPartyAccountHasDesiredBalance = false
                                                        }
                                                    }
                                                    if (secondPartyAccountHasDesiredBalance) {

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

                                                        if (isDevelopmentMode) {

                                                            println("transactions = $transactions")
                                                        }
                                                        result.isOK = true
                                                        result.data = transactions

                                                    } else {

                                                        result.error = secondPartyIsHortInBalanceMessage
                                                    }
                                                }
                                            } else {

                                                result.error =
                                                    "${ConstantsNative.accountText} balance calculation error..."
                                            }
                                        } else {

                                            result.error =
                                                CommonConstants.USER_CANCELED_MESSAGE
                                        }
                                    } else {

                                        result.error =
                                            CommonConstants.USER_CANCELED_MESSAGE
                                    }
                                } else {

                                    result.error =
                                        "${ConstantsCommonNative.DATA_CONFIRMATION_ERROR_TEXT} for ${ConstantsNative.BAJAJ_COINS_CONVERSION_RATE_TEXT}"
                                }
                            } else {

                                result.error = "No Valid Bajaj Coins Wallet Account Provided by User"
                            }
                        } else {

                            result.error =
                                "${ConstantsCommonNative.DATA_CONFIRMATION_ERROR_TEXT} for ${ConstantsNative.BAJAJ_COINS_WALLET_ACCOUNT_ID_TEXT}"
                        }
                    } else {

                        result.error = "No Valid Bajaj Coins Income Account Provided by User"
                    }
                }
            )
        } else {

            result.error =
                "${ConstantsCommonNative.DATA_CONFIRMATION_ERROR_TEXT} for ${ConstantsNative.BAJAJ_COINS_INCOME_ACCOUNT_ID_TEXT}"
        }
        return result
    }
}