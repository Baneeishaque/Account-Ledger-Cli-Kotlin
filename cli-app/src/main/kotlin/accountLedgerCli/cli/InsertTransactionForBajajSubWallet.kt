package accountLedgerCli.cli

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.enums.EnvironmentFileEntryEnum
import account.ledger.library.models.TransactionModel
import account.ledger.library.to_common_utils.AccountLedgerLibInteractiveUtils
import account.ledger.library.utils.AccountUtils
import account.ledger.library.utils.TransactionForBajajWalletUtils
import account_ledger_library.constants.ConstantsNative
import common.utils.library.constants.CommonConstants
import common.utils.library.models.IsOkModel
import common.utils.library.utils.DateTimeUtils
import common.utils.library.utils.EnvironmentFileOperations
import common.utils.library.utils.InputUtilsInteractive
import common.utils.library.utils.InteractiveUtils
import common.utils.library.utils.IsOkUtils
import common_utils_library.constants.ConstantsCommonNative
import io.github.cdimascio.dotenv.Dotenv
import java.time.LocalDateTime

object InsertTransactionForBajajSubWallet {

    fun generateTransactionsForBajajSubWallet(

        isFundingTransactionPresent: Boolean = true,
        sourceAccount: AccountResponse,
        secondPartyAccount: AccountResponse,
        eventDateTimeInText: String,
        dotenv: Dotenv,
        userId: UInt,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ): IsOkModel<List<TransactionModel>> {

        val result = IsOkModel<List<TransactionModel>>(isOK = false)

        var confirmDataResult: IsOkModel<UInt> =
            EnvironmentFileOperations.confirmWholeNumberEnvironmentVariableDataOrBackWithPrompt(

                dotenv = dotenv,
                environmentVariableName = EnvironmentFileEntryEnum.BAJAJ_SUB_WALLET_INCOME_ACCOUNT_ID.name,
                dataSpecification = ConstantsNative.BAJAJ_SUB_WALLET_INCOME_ACCOUNT_ID_TEXT
            )

        if (confirmDataResult.isOK) {

            AccountUtils.processUserAccountsMap(

                userId = userId,
                isConsoleMode = isConsoleMode,
                isDevelopmentMode = isDevelopmentMode,
                successActions = fun(userAccountsMap: LinkedHashMap<UInt, AccountResponse>) {

                    val getValidBajajSubWalletIncomeAccountResult: IsOkModel<AccountResponse> =
                        AccountUtils.getValidAccountById(

                            desiredAccount = userAccountsMap[confirmDataResult.data!!],
                            userAccountsMap = userAccountsMap,
                            idCorrectionFunction = fun(): IsOkModel<UInt> {

                                return InputUtilsInteractive.getValidUnsignedIntOrBackWithPrompt(

                                    dataSpecification = ConstantsNative.BAJAJ_SUB_WALLET_INCOME_ACCOUNT_ID_TEXT
                                )
                            }
                        )
                    if (getValidBajajSubWalletIncomeAccountResult.isOK) {

                        confirmDataResult =
                            EnvironmentFileOperations.confirmWholeNumberEnvironmentVariableDataOrBackWithPrompt(

                                dotenv = dotenv,
                                environmentVariableName = EnvironmentFileEntryEnum.BAJAJ_SUB_WALLET_ACCOUNT_ID.name,
                                dataSpecification = ConstantsNative.BAJAJ_SUB_WALLET_ACCOUNT_ID_TEXT
                            )

                        if (confirmDataResult.isOK) {

                            val getValidBajajSubWalletAccountResult: IsOkModel<AccountResponse> =
                                AccountUtils.getValidAccountById(

                                    desiredAccount = userAccountsMap[confirmDataResult.data!!],
                                    userAccountsMap = userAccountsMap,
                                    idCorrectionFunction = fun(): IsOkModel<UInt> {

                                        return InputUtilsInteractive.getValidUnsignedIntOrBackWithPrompt(

                                            dataSpecification = ConstantsNative.BAJAJ_SUB_WALLET_ACCOUNT_ID_TEXT
                                        )
                                    }
                                )

                            if (getValidBajajSubWalletAccountResult.isOK) {

                                val amountToSpendForBajajSubWalletRewardText =
                                    "Amount To Spend for Bajaj Sub Wallet Rewarding Transactions"
                                val amountToSpendForBajajSubWalletRewardResult: IsOkModel<UInt> =
                                    if (isFundingTransactionPresent) {

                                        InputUtilsInteractive.getValidUnsignedIntOrBackWithPrompt(

                                            dataSpecification = amountToSpendForBajajSubWalletRewardText
                                        )
                                    } else {
                                        IsOkModel<UInt>(

                                            isOK = true,
                                            data = 0u
                                        )
                                    }

                                if (amountToSpendForBajajSubWalletRewardResult.isOK) {

                                    val getDataResult: IsOkModel<List<UInt>> =
                                        IsOkUtils.checkListOfOkModels(
                                            isOkModels = listOf(

                                                InputUtilsInteractive.getValidUnsignedIntOrBackWithPrompt(

                                                    dataSpecification = "Amount of each Transaction for Bajaj Sub Wallet Reward"
                                                ),
                                                InputUtilsInteractive.getValidUnsignedIntOrBackWithPrompt(

                                                    dataSpecification = "Count of Sub Wallet Rewarding Transactions"
                                                )
                                            )
                                        )
                                    if (getDataResult.isOK) {

                                        val countOfCoinRewardingTransactions: UInt = getDataResult.data!!.last()
                                        val getDataLists: IsOkModel<List<List<UInt>>> = IsOkUtils.checkListOfOkModels(
                                            isOkModels = listOf(

                                                InputUtilsInteractive.getMultipleValidUnsignedIntOrBackWithPrompt(

                                                    dataSpecification = "List of Bajaj Sub Wallet Rewarding Transactions",
                                                    count = countOfCoinRewardingTransactions
                                                ),
                                                InputUtilsInteractive.getMultipleValidUnsignedIntOrBackWithPrompt(

                                                    dataSpecification = "List of Bajaj Sub Wallet Rewards",
                                                    count = countOfCoinRewardingTransactions
                                                )
                                            )
                                        )

                                        if (getDataLists.isOK) {

                                            val listOfBajajSubWalletRewardingTransactionIndexes: List<UInt> =
                                                getDataLists.data!!.first()
                                            val totalNumberOfTransactionsForBajajSubWalletReward: UInt =
                                                listOfBajajSubWalletRewardingTransactionIndexes.last()

                                            val fundingPartyAccountBalance: IsOkModel<Float> =
                                                if (isFundingTransactionPresent) {
                                                    AccountUtils.getAccountBalance(

                                                        userId = userId,
                                                        desiredAccountId = secondPartyAccount.id,
                                                        isDevelopmentMode = isDevelopmentMode
                                                    )
                                                } else {
                                                    IsOkModel(

                                                        isOK = true,
                                                        data = 0F
                                                    )
                                                }

                                            val eventDateTime: IsOkModel<LocalDateTime> =
                                                DateTimeUtils.normalDateTimeInTextToDateTime(normalDateTimeInText = eventDateTimeInText)
                                            if (eventDateTime.isOK) {

                                                val getAccountBalancesResult: IsOkModel<List<Float>> =
                                                    IsOkUtils.checkListOfOkModels(

                                                        isOkModels = listOf(

                                                            fundingPartyAccountBalance,
                                                            AccountUtils.getAccountBalance(

                                                                userId = userId,
                                                                desiredAccountId = sourceAccount.id,
                                                                isDevelopmentMode = isDevelopmentMode,
                                                                upToDateTime = eventDateTime.data!!
                                                            )
                                                        )
                                                    )

                                                if (getAccountBalancesResult.isOK) {

                                                    // Check if a/c 1 has at least amountToSpendForBajajSubWalletRewards.
                                                    val amountToSpendForBajajSubWalletReward: UInt =
                                                        amountToSpendForBajajSubWalletRewardResult.data!!
                                                    if (isFundingTransactionPresent && (fundingPartyAccountBalance.data!! < amountToSpendForBajajSubWalletReward.toFloat())
                                                    ) {
                                                        result.error =
                                                            "Insufficient balance in ${secondPartyAccount.name}. Please ensure ${secondPartyAccount.name} has at least a balance of $amountToSpendForBajajSubWalletReward."

                                                    } else {

                                                        // Check (totalNumberOfTransactionsForBajajSubWalletReward * perTransactionAmountForBajajSubWalletReward) > amountToSpendForBajajSubWalletRewards.
                                                        // Check a/c 2 has ((totalNumberOfTransactionsForBajajSubWalletReward * perTransactionAmountForBajajSubWalletReward) - amountToSpendForBajajSubWalletRewards) amount as balance.
                                                        val perTransactionAmountForBajajSubWalletReward: UInt =
                                                            getDataResult.data!!.first()

                                                        var sourceHasEnoughBalance = true
                                                        val totalAmountOfBajajSubWalletRewardTransactions: UInt =
                                                            totalNumberOfTransactionsForBajajSubWalletReward * perTransactionAmountForBajajSubWalletReward
                                                        val sourceShortInBalanceMessage =
                                                            "Insufficient balance in ${sourceAccount.name}. Please ensure ${sourceAccount.name} has at least a balance of ${totalAmountOfBajajSubWalletRewardTransactions - amountToSpendForBajajSubWalletReward}."

                                                        val secondPartyAccountBalance: Float =
                                                            getAccountBalancesResult.data!!.last()

                                                        val effectiveTotalAmountOfBajajSubWalletRewardTransactions: Float =
                                                            totalAmountOfBajajSubWalletRewardTransactions.toFloat() - amountToSpendForBajajSubWalletReward.toFloat()

                                                        if (secondPartyAccountBalance < effectiveTotalAmountOfBajajSubWalletRewardTransactions) {

                                                            sourceHasEnoughBalance = false
                                                        }

                                                        if (sourceHasEnoughBalance) {

                                                            // Prepare transactions
                                                            val listOfBajajSubWalletRewards: List<UInt> =
                                                                getDataLists.data!!.last()
                                                            val transactions: List<TransactionModel> =
                                                                TransactionForBajajWalletUtils.prepareTransactions(

                                                                    isSourceTransactionPresent = isFundingTransactionPresent,
                                                                    sourceAccount = sourceAccount,
                                                                    secondPartyAccount = secondPartyAccount,
                                                                    bajajSubWalletIncomeAccount = getValidBajajSubWalletIncomeAccountResult.data!!,
                                                                    bajajSubWalletAccount = getValidBajajSubWalletAccountResult.data!!,
                                                                    amountToSpendForBajajSubWalletReward = amountToSpendForBajajSubWalletReward,
                                                                    perTransactionAmountForBajajSubWalletReward = perTransactionAmountForBajajSubWalletReward,
                                                                    totalNumberOfTransactionsForBajajSubWalletReward = totalNumberOfTransactionsForBajajSubWalletReward,
                                                                    listOfBajajSubWalletRewardingTransactionIndexes = listOfBajajSubWalletRewardingTransactionIndexes,
                                                                    listOfBajajSubWalletRewards = listOfBajajSubWalletRewards,
                                                                    eventDateTimeInText = eventDateTimeInText
                                                                )

                                                            if (isDevelopmentMode) {

                                                                println("transactions = $transactions")
                                                            }
                                                            result.isOK = true
                                                            result.data = transactions

                                                        } else {

                                                            result.error = sourceShortInBalanceMessage
                                                        }
                                                    }
                                                } else {

                                                    result.error =
                                                        "${ConstantsNative.accountText} balance calculation error..."
                                                }
                                            } else {

                                                result.error =
                                                    DateTimeUtils.constructDateErrorMessage(message = eventDateTime.error!!)
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
                                        InteractiveUtils.generateInvalidInputMessage(inputSpecifier = amountToSpendForBajajSubWalletRewardText)
                                }

                            } else {

                                result.error =
                                    InteractiveUtils.generateInvalidInputMessage(inputSpecifier = ConstantsNative.BAJAJ_SUB_WALLET_ACCOUNT_ID_TEXT)
                            }
                        } else {

                            result.error =
                                AccountLedgerLibInteractiveUtils.generateDataConfirmationErrorMessage(dataSpecifier = ConstantsNative.BAJAJ_SUB_WALLET_ACCOUNT_ID_TEXT)
                        }
                    } else {

                        result.error =
                            InteractiveUtils.generateInvalidInputMessage(inputSpecifier = ConstantsNative.BAJAJ_SUB_WALLET_INCOME_ACCOUNT_ID_TEXT)
                    }
                }
            )
        } else {

            result.error =
                AccountLedgerLibInteractiveUtils.generateDataConfirmationErrorMessage(dataSpecifier = ConstantsNative.BAJAJ_SUB_WALLET_INCOME_ACCOUNT_ID_TEXT)
        }
        return result
    }
}
