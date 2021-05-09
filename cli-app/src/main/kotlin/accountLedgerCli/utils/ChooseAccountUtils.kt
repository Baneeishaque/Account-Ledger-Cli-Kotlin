package accountLedgerCli.utils

import accountLedgerCli.utils.ApiUtils
import accountLedgerCli.utils.AccountUtils
import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.AccountsResponse

import java.util.Scanner
import java.util.InputMismatchException

internal class ChooseAccountResult(val choosedAccountId:Int, val choosedAccount:AccountResponse)

internal object ChooseAccountUtils {

    internal fun chooseAccountById(userId: Int): ChooseAccountResult {

        var accountIdInput:Int
        val reader = Scanner(System.`in`)

        while(true) {

            print("Enter To Account ID or 0 to Back : ")
            
            try {

                accountIdInput = reader.nextInt()
                if (accountIdInput == 0) return ChooseAccountResult(0,AccountUtils.getBlankAccount())

                val apiResponse = ApiUtils.getAccountsFull(userId = userId);
                if (apiResponse.isError()) {

                    println("Error : ${(apiResponse.getValue() as Exception).localizedMessage}")

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
                } 
                else {

                    val accountsResponseResult = apiResponse.getValue() as AccountsResponse
                    if (accountsResponseResult.status == 1) {

                        println("No Accounts...")
                        return ChooseAccountResult(0,AccountUtils.getBlankAccount())
                        
                    } else {

                        val userAccountsMap:LinkedHashMap<Int, AccountResponse> = AccountUtils.prepareUserAccountsMap(accountsResponseResult.accounts)

                        if (userAccountsMap.containsKey(accountIdInput)) {

                            return ChooseAccountResult(accountIdInput,userAccountsMap[accountIdInput]!!)
                        }
                        else{

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
