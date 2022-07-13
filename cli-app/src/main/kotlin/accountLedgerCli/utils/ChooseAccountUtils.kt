package accountLedgerCli.utils

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.AccountsResponse
import java.util.*

internal class ChooseAccountResult(val chosenAccountId: UInt, val chosenAccount: AccountResponse)

internal object ChooseAccountUtils {

    internal fun chooseAccountById(userId: UInt): ChooseAccountResult {

        var accountIdInput: UInt
        val reader = Scanner(System.`in`)

        while (true) {

            print("Enter To Account ID or 0 to Back : ")

            try {

                accountIdInput = reader.nextInt().toUInt()
                if (accountIdInput == 0u) return ChooseAccountResult(0u, AccountUtils.getBlankAccount())

                val apiResponse = ApiUtils.getAccountsFull(userId = userId)
                if (apiResponse.isFailure) {

                    println("Error : ${(apiResponse.exceptionOrNull() as Exception).localizedMessage}")

                    //        do {
                    //            print("Retry (Y/N) ? : ")
                    //            val input = readLine()
                    //            when (input) {
                    //                "Y", "" -> {
                    //                    login()
                    //                    return
                    //                }
                    //                "N" -> {
                    //                }
                    //                else -> println("Invalid option, try again...")
                    //            }
                    //        } while (input != "N")
                } else {

                    val accountsResponseResult = apiResponse.getOrNull() as AccountsResponse
                    if (accountsResponseResult.status == 1) {

                        println("No Accounts...")
                        return ChooseAccountResult(0u, AccountUtils.getBlankAccount())

                    } else {

                        val userAccountsMap: LinkedHashMap<UInt, AccountResponse> =
                            AccountUtils.prepareUserAccountsMap(accountsResponseResult.accounts)

                        if (userAccountsMap.containsKey(accountIdInput)) {

                            return ChooseAccountResult(accountIdInput, userAccountsMap[accountIdInput]!!)
                        } else {

                            println("Invalid Account ID...")
                        }
                    }
                }
            } catch (exception: InputMismatchException) {

                println("Invalid Account ID...")
            }
        }
    }
}
