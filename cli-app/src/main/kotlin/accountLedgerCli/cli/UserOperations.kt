package accountLedgerCli.cli

import accountLedgerCli.api.response.AuthenticationResponse
import accountLedgerCli.api.response.UsersResponse
import accountLedgerCli.retrofit.ResponseHolder
import accountLedgerCli.retrofit.data.AuthenticationDataSource
import accountLedgerCli.retrofit.data.UsersDataSource
import accountLedgerCli.to_utils.InputUtils
import accountLedgerCli.to_utils.ToDoUtils
import accountLedgerCli.utils.UserUtils
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.runBlocking
import java.nio.file.Paths

class UserOperations {
    companion object {

        @JvmStatic
        val dotenv = dotenv {
            directory = Paths.get("").toAbsolutePath().toString()
            ignoreIfMissing = true
        }

        @JvmStatic
        fun login() {

//            println("Directory : ${Paths.get("").toAbsolutePath()}")
            println("\nAccount Ledger Authentication")
            println("--------------------------------")

//            val dotenv = dotenv {
//                directory = Paths.get("").toAbsolutePath().toString()
//                ignoreIfMissing = true
//            }
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
            runBlocking {
                apiResponse =
                    authenticationDataSource.authenticateUser(username = user.username, password = user.passcode)
            }
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

        @JvmStatic
        fun register() {

            ToDoUtils.showTodo()
        }

        @JvmStatic
        fun listUsers() {

            val usersDataSource = UsersDataSource()
            println("Contacting Server...")
            val apiResponse: ResponseHolder<UsersResponse>
            runBlocking { apiResponse = usersDataSource.selectUsers() }
//            println("Response : $apiResponse")
            if (apiResponse.isError()) {

                println("Error : ${(apiResponse.getValue() as Exception).localizedMessage}")
                do {
                    print("Retry (Y/N) ? : ")
                    val input = readLine()
                    when (input) {
                        "Y", "" -> {
                            listUsers()
                        }

                        "N" -> {
                        }

                        else -> invalidOptionMessage()
                    }
                } while (input != "N")
            } else {

                val usersResponse = apiResponse.getValue() as UsersResponse
                if (usersResponse.status == 1) {

                    println("No Users...")

                } else {

                    val usersMap = UserUtils.prepareUsersMap(usersResponse.users)
                    do {
                        commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                            listOf(
                                "\nUsers",
                                usersToStringFromLinkedHashMap(
                                    usersMap = usersMap
                                ),
                                "1 - Balance Sheet for an User",
                                "0 - Back",
                                "",
                                "Enter Your Choice : "
                            )
                        )
                        val choice = readLine()
                        when (choice) {
                            "1" -> {
                                balanceSheetOfUser(usersMap = usersMap)
                            }

                            "0" -> {
                            }

                            else -> invalidOptionMessage()
                        }
                    } while (choice != "0")
                }
            }
        }
    }
}