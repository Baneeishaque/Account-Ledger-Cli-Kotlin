package accountLedgerCli.utils

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.constants.Constants
import account.ledger.library.models.AccountFrequencyModel
import account.ledger.library.models.ChooseAccountResult
import account.ledger.library.models.FrequencyOfAccountsModel
import accountLedgerCli.cli.Screens
import common.utils.library.models.IsOkModel
import common.utils.library.utils.JsonFileUtils
import common.utils.library.constants.Constants as CommonConstants

object AccountUtils {

    @JvmStatic
    val blankAccount = AccountResponse(

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
    fun prepareUserAccountsMap(accounts: List<AccountResponse>): LinkedHashMap<UInt, AccountResponse> {

        val userAccountsMap = LinkedHashMap<UInt, AccountResponse>()
        accounts.forEach { currentAccount -> userAccountsMap[currentAccount.id] = currentAccount }
        return userAccountsMap
    }

    @JvmStatic
    internal val blankChosenAccount = ChooseAccountResult(chosenAccountId = 0u)

    //TODO : Write List to String, then rewrite userAccountsToStringFromList, usersToStringFromLinkedHashMap, userAccountsToStringFromLinkedHashMap & userAccountsToStringFromListPair

    fun userAccountsToStringFromList(accounts: List<AccountResponse>): String {

        var result = ""
        accounts.forEach { account -> result += "${Constants.accountText.first()}${account.id} - ${account.fullName}\n" }
        return result
    }

    @JvmStatic
    fun getFrequentlyUsedTop10Accounts(userId: UInt, isDevelopmentMode: Boolean): String {

        return getFrequentlyUsedTopXAccounts(userId = userId, x = 10, isDevelopmentMode = isDevelopmentMode)
    }

    @JvmStatic
    internal fun getFrequentlyUsedTop20Accounts(userId: UInt, isDevelopmentMode: Boolean): String {

        return getFrequentlyUsedTopXAccounts(userId = userId, x = 20, isDevelopmentMode = isDevelopmentMode)
    }

    @JvmStatic
    internal fun getFrequentlyUsedTop30Accounts(userId: UInt, isDevelopmentMode: Boolean): String {

        return getFrequentlyUsedTopXAccounts(userId = userId, x = 30, isDevelopmentMode = isDevelopmentMode)
    }

    @JvmStatic
    fun getFrequentlyUsedTop40Accounts(userId: UInt, isDevelopmentMode: Boolean): String {

        return getFrequentlyUsedTopXAccounts(userId = userId, x = 40, isDevelopmentMode = isDevelopmentMode)
    }

    @JvmStatic
    internal fun getFrequentlyUsedTopXAccounts(userId: UInt, x: Int, isDevelopmentMode:Boolean): String {

        var result = ""

        val readFrequencyOfAccountsFileResult: IsOkModel<FrequencyOfAccountsModel> =
            JsonFileUtils.readJsonFile(
                fileName = Constants.frequencyOfAccountsFileName,
                isDevelopmentMode = isDevelopmentMode
            )
        if (readFrequencyOfAccountsFileResult.isOK) {

            Screens.getAccountFrequenciesForUser(

                frequencyOfAccounts = readFrequencyOfAccountsFileResult.data!!,
                userId = userId

            )?.sortedByDescending { accountFrequency: AccountFrequencyModel ->

                accountFrequency.countOfRepetition

            }?.take(n = x)
            ?.sortedBy { accountFrequency: AccountFrequencyModel ->

                accountFrequency.accountName

            }?.forEach { accountFrequency: AccountFrequencyModel ->

                result += "${accountFrequency.accountID} : ${accountFrequency.accountName} [${accountFrequency.countOfRepetition}]\n"
            }
        }
        if(isDevelopmentMode){
            println("result = $result")
        }
        return if (result.isEmpty()) {

            CommonConstants.dashedLineSeparator

        } else {

            CommonConstants.dashedLineSeparator + "\n" + result + CommonConstants.dashedLineSeparator
        }
    }
}
