package accountLedgerCli.utils

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.cli.App
import accountLedgerCli.cli.Screens
import accountLedgerCli.constants.Constants
import accountLedgerCli.models.AccountFrequencyModel
import accountLedgerCli.models.ChooseAccountResult
import accountLedgerCli.models.FrequencyOfAccountsModel
import accountLedgerCli.to_models.IsOkModel
import accountLedgerCli.to_utils.JsonFileUtils
import accountLedgerCli.to_constants.Constants as CommonConstants

internal object AccountUtils {

    @JvmStatic
    internal val blankAccount = AccountResponse(

        id = 0u,
        fullName = "",
        name = "",
        parentAccountId = 0u,
        accountType = "",
        notes = "",
        commodityType = "",
        commodityValue = "",
        ownerId = 0u,
        taxable = "",
        placeHolder = ""
    )


    @JvmStatic
    internal fun prepareUserAccountsMap(accounts: List<AccountResponse>): LinkedHashMap<UInt, AccountResponse> {

        val userAccountsMap = LinkedHashMap<UInt, AccountResponse>()
        accounts.forEach { currentAccount -> userAccountsMap[currentAccount.id] = currentAccount }
        return userAccountsMap
    }

    @JvmStatic
    internal val blankChosenAccount = ChooseAccountResult(chosenAccountId = 0u)

    //TODO : Write List to String, then rewrite userAccountsToStringFromList, usersToStringFromLinkedHashMap, userAccountsToStringFromLinkedHashMap & userAccountsToStringFromListPair

    internal fun userAccountsToStringFromList(accounts: List<AccountResponse>): String {

        var result = ""
        accounts.forEach { account -> result += "${Constants.accountText.first()}${account.id} - ${account.fullName}\n" }
        return result
    }

    @JvmStatic
    internal fun getFrequentlyUsedTop10Accounts(userId: UInt): String {

        return getFrequentlyUsedTopXAccounts(userId = userId, x=10)
    }

    @JvmStatic
    internal fun getFrequentlyUsedTop20Accounts(userId: UInt): String {

        return getFrequentlyUsedTopXAccounts(userId = userId, x=20)
    }

    @JvmStatic
    internal fun getFrequentlyUsedTop30Accounts(userId: UInt): String {

        return getFrequentlyUsedTopXAccounts(userId = userId, x=30)
    }

    @JvmStatic
    internal fun getFrequentlyUsedTopXAccounts(userId: UInt, x:Int): String {

        var result = ""

        val readFrequencyOfAccountsFileResult: IsOkModel<FrequencyOfAccountsModel> =
            JsonFileUtils.readJsonFile(
                fileName = Constants.frequencyOfAccountsFileName,
                isDevelopmentMode = App.isDevelopmentMode
            )
        if (readFrequencyOfAccountsFileResult.isOK) {

            Screens.getAccountFrequenciesForUser(

                frequencyOfAccounts = readFrequencyOfAccountsFileResult.data!!,
                userId = userId

            )?.sortedByDescending { accountFrequency: AccountFrequencyModel ->

                accountFrequency.countOfRepetition

            }?.take(n = x)

                ?.forEach { accountFrequency: AccountFrequencyModel ->

                    result += "${accountFrequency.accountID} : ${accountFrequency.accountName} [${accountFrequency.countOfRepetition}]\n"
                }
        }
        return if (result.isEmpty()) {

            CommonConstants.dashedLineSeparator

        } else {

            CommonConstants.dashedLineSeparator + "\n" + result + CommonConstants.dashedLineSeparator
        }
    }
}
