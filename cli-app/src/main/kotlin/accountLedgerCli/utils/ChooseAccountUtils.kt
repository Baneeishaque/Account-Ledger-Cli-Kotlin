package accountLedgerCli.utils

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.AccountsResponse
import java.util.*

internal class ChooseAccountResult(val chosenAccountId: UInt, val chosenAccount: AccountResponse? = null)

internal object ChooseAccountUtils {

    internal fun chooseAccountById(userId: UInt): ChooseAccountResult {

        var accountIdInput: UInt
        val reader = Scanner(System.`in`)

        while (true) {

            print("Enter To Account ID or 0 to Back : ")

            try {

                accountIdInput = reader.nextInt().toUInt()
                if (accountIdInput == 0u) return AccountUtils.blankChosenAccount

                val apiResponse: Result<AccountsResponse> = ApiUtils.getAccountsFull(userId = userId)
                if (apiResponse.isFailure) {

                    println("Error : ${(apiResponse.exceptionOrNull() as Exception).localizedMessage}")

                    do {
                        print("Retry (Y/N) ? : ")
                        val input: String? = readLine()
                        when (input) {
                            "Y", "" -> {
                                return chooseAccountById(userId = userId)
                            }

                            "N" -> {
                            }

                            else -> println("Invalid option, try again...")
                        }
                    } while (input != "N")

                    return AccountUtils.blankChosenAccount

                } else {

                    val accountsResponseResult: AccountsResponse = apiResponse.getOrNull() as AccountsResponse
                    if (accountsResponseResult.status == 1) {

                        println("No Accounts...")
                        return AccountUtils.blankChosenAccount

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
