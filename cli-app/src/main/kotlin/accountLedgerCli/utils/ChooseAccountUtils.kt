package accountLedgerCli.utils

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.AccountsResponse
import accountLedgerCli.constants.Constants
import accountLedgerCli.enums.AccountTypeEnum
import accountLedgerCli.models.ChooseAccountResult
import accountLedgerCli.models.ChooseByIdResult
import accountLedgerCli.to_utils.EnumUtils

internal object ChooseAccountUtils {

    @JvmStatic
    internal fun chooseAccountById(

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
                    AccountUtils.prepareUserAccountsMap(chooseByIdResult.isOkWithData.data.accounts)

                if (userAccountsMap.containsKey(chooseByIdResult.id!!)) {

                    return ChooseAccountResult(chooseByIdResult.id, userAccountsMap[chooseByIdResult.id]!!)

                } else {

                    println("Invalid Account ID...")
                }
            }
        }
        return AccountUtils.blankChosenAccount
    }
}
