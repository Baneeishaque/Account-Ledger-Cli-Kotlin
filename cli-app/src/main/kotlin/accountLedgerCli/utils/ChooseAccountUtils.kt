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
    internal fun chooseAccountById(userId: UInt, accountType: AccountTypeEnum): ChooseAccountResult {

//        var accountIdInput: UInt
//        val reader = Scanner(System.`in`)
//
//        while (true) {
//
//            print("Enter ${EnumUtils.getEnumNameForPrint(localEnum = accountType)} Account ID or 0 to Back : ")
//
//            try {
//
//                accountIdInput = reader.nextInt().toUInt()
//                if (accountIdInput == 0u) return AccountUtils.blankChosenAccount
//
//                val apiResponse: Result<AccountsResponse> = ApiUtils.getAccountsFull(userId = userId)
//                if (apiResponse.isFailure) {
//
//                    println("Error : ${(apiResponse.exceptionOrNull() as Exception).localizedMessage}")
//
//                    do {
//                        print("Retry (Y/N) ? : ")
//                        when (readLine()!!) {
//                            "Y", "" -> {
//                                return chooseAccountById(userId = userId, accountType = AccountTypeEnum.TO)
//                            }
//
//                            "N" -> {
//                                return AccountUtils.blankChosenAccount
//                            }
//
//                            else -> println("Invalid option, try again...")
//                        }
//                    } while (true)
//
//                } else {
//
//                    val accountsResponseResult: AccountsResponse = apiResponse.getOrNull() as AccountsResponse
//                    if (accountsResponseResult.status == 1u) {
//
//                        println("No Accounts...")
//                        return AccountUtils.blankChosenAccount
//
//                    } else {
//
//                        val userAccountsMap: LinkedHashMap<UInt, AccountResponse> =
//                            AccountUtils.prepareUserAccountsMap(accountsResponseResult.accounts)
//
//                        if (userAccountsMap.containsKey(accountIdInput)) {
//
//                            return ChooseAccountResult(accountIdInput, userAccountsMap[accountIdInput]!!)
//
//                        } else {
//
//                            println("Invalid Account ID...")
//                        }
//                    }
//                }
//            } catch (exception: InputMismatchException) {
//
//                println("Invalid Account ID...")
//            }
//        }

        val chooseByIdResult: ChooseByIdResult<AccountsResponse> = ChooseUtils.chooseById(
            itemSpecification = Constants.accountText,
            apiCallFunction = fun(): Result<AccountsResponse> {
                return ApiUtils.getAccountsFull(userId = userId)
            },
            prefixForPrompt = "${EnumUtils.getEnumNameForPrint(localEnum = accountType)} "
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
