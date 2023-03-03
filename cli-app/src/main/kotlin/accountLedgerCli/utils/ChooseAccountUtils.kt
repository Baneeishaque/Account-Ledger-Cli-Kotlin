package accountLedgerCli.utils

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.api.response.AccountsResponse
import account.ledger.library.constants.Constants
import account.ledger.library.enums.AccountTypeEnum
import account.ledger.library.models.ChooseAccountResult
import account.ledger.library.models.ChooseByIdResult
import account.ledger.library.utils.ApiUtils
import account.ledger.library.utils.ChooseUtils
import common.utils.library.utils.EnumUtils

object ChooseAccountUtils {

    @JvmStatic
    fun chooseAccountById(

        userId: UInt,
        accountType: AccountTypeEnum,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ): ChooseAccountResult {

        val chooseByIdResult: ChooseByIdResult<AccountsResponse> = ChooseUtils.chooseById(

            itemSpecification = Constants.accountText,
            apiCallFunction = fun(): Result<AccountsResponse> {

                return ApiUtils.getAccountsFull(

                    userId = userId,
                    isConsoleMode = isConsoleMode,
                    isDevelopmentMode = isDevelopmentMode
                )
            },
            prefixForPrompt = "${EnumUtils.getEnumNameForPrint(localEnum = accountType)} ",
            isConsoleMode = isConsoleMode,
            isDevelopmentMode = isDevelopmentMode
        )
        if (chooseByIdResult.isOkWithData.isOK) {

            if (chooseByIdResult.isOkWithData.data!!.status == 1u) {

                println("No Accounts...")

            } else {

                val userAccountsMap: LinkedHashMap<UInt, AccountResponse> =
                    AccountUtils.prepareUserAccountsMap(chooseByIdResult.isOkWithData.data!!.accounts)

                if (userAccountsMap.containsKey(chooseByIdResult.id!!)) {

                    return ChooseAccountResult(chooseByIdResult.id!!, userAccountsMap[chooseByIdResult.id]!!)

                } else {

                    println("Invalid Account ID...")
                }
            }
        }
        return AccountUtils.blankChosenAccount
    }
}
