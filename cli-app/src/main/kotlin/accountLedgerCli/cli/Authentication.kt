package accountLedgerCli.cli

import accountLedgerCli.api.response.AuthenticationResponse
import accountLedgerCli.retrofit.ResponseHolder
import accountLedgerCli.retrofit.data.AuthenticationDataSource
import accountLedgerCli.to_utils.InputUtils
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.runBlocking
import java.nio.file.Paths

internal fun login() {

//    println("Directory : ${Paths.get("").toAbsolutePath()}")
    println("\nAccount Ledger Authentication")
    println("--------------------------------")

    val dotenv = dotenv {
        directory = Paths.get("").toAbsolutePath().toString()
        ignoreIfMissing = true
    }
    var user = UserCredentials(dotenv["USER_NAME"] ?: "", dotenv["PASSWORD"] ?: "")
    if (user.username.isEmpty() || user.passcode.isEmpty()) {

        user = InputUtils.getUserCredentials()

    } else {
        do {
            println("The recognised user is ${user.username}")
            print("Do you want to continue (Y/N) : ")
            when (readLine().toString()) {
                "Y", "" -> {
                    break
                }
                "N" -> {
                    user = InputUtils.getUserCredentials()
                }
                else -> invalidOptionMessage()
            }

        } while (true)
    }

    val authenticationDataSource = AuthenticationDataSource()
    println("Contacting Server...")
    val apiResponse: ResponseHolder<AuthenticationResponse>
    runBlocking { apiResponse = authenticationDataSource.authenticateUser(username = user.username, password = user.passcode) }
    // println("Response : $apiResponse")
    if (apiResponse.isError()) {

        println("Error : ${(apiResponse.getValue() as Exception).localizedMessage}")
        do {
            print("Retry (Y/N) ? : ")
            val input = readLine()
            when (input) {
                "Y", "" -> {
                    login()
                }
                "N" -> {
                }
                else -> invalidOptionMessage()
            }
        } while (input != "N")
    } else {

        val authenticationResponseResult = apiResponse.getValue() as AuthenticationResponse
        when (authenticationResponseResult.userCount) {
            0 -> println("Invalid Credentials...")
            1 -> {

                println("Login Success...")
                userScreen(username = user.username, userId = authenticationResponseResult.id)
            }
            else -> println("Server Execution Error...")
        }
    }
}
