package accountLedgerCli.utils

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.api.response.AccountsResponse
import account_ledger_library.constants.ConstantsNative
import account.ledger.library.enums.AccountTypeEnum
import account.ledger.library.models.ChooseAccountResult
import common.utils.library.models.ChooseByIdResult
import account.ledger.library.utils.AccountUtils
import account.ledger.library.utils.ApiUtilsInteractive
import common.utils.library.utils.ChooseUtilsInteractive
import common.utils.library.utils.EnumUtils

object ChooseAccountUtilsInteractive {

    @JvmStatic
    fun chooseAccountById(

        userId: UInt,
        accountType: AccountTypeEnum,
        isDevelopmentMode: Boolean

    ): ChooseAccountResult {

        val chooseByIdResult: ChooseByIdResult<AccountsResponse> = ChooseUtilsInteractive.chooseById(

            itemSpecification = ConstantsNative.ACCOUNT_TEXT,
            apiCallFunction = fun(): Result<AccountsResponse> {

                return ApiUtilsInteractive.getAccountsFull(

                    userId = userId,
                    isConsoleMode = true,
                    isDevelopmentMode = isDevelopmentMode
                )
            },
            prefixForPrompt = "${EnumUtils.getEnumNameForPrint(localEnum = accountType)} ",
            isConsoleMode = true,
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
