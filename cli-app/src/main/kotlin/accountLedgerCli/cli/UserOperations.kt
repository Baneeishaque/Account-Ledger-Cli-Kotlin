package accountLedgerCli.cli

import accountLedgerCli.api.response.AuthenticationResponse
import accountLedgerCli.api.response.UsersResponse
import accountLedgerCli.cli.App.Companion.chosenUser
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.models.BalanceSheetDataModel
import accountLedgerCli.retrofit.ResponseHolder
import accountLedgerCli.retrofit.data.AuthenticationDataSource
import accountLedgerCli.retrofit.data.UsersDataSource
import accountLedgerCli.to_utils.InputUtils
import accountLedgerCli.utils.UserUtils
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

class UserOperations {
    companion object {

        @JvmStatic
        internal fun login(
            username: String,
            password: String,
            isNotApiCall: Boolean = true,
            apiMethod: String = "",
            apiMethodOptions: LinkedHashMap<String, Any> = linkedMapOf()
        ) {

            if (isNotApiCall) {
//            println("Directory : ${Paths.get("").toAbsolutePath()}")
                println("\nAccount Ledger Authentication")
                println("--------------------------------")
            }

            var user: UserCredentials
            if (username.isEmpty() || password.isEmpty()) {

                user = InputUtils.getUserCredentials()

            } else {

                user = UserCredentials(
                    username = username,
                    passcode = password
                )

                if (isNotApiCall) {
                    do {
                        displayCurrentUser(user)
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
            }

            val authenticationDataSource = AuthenticationDataSource()
            if (isNotApiCall) {
                println("Contacting Server...")
            }
            val apiResponse: ResponseHolder<AuthenticationResponse>
            runBlocking {
                apiResponse =
                    authenticationDataSource.authenticateUser(username = user.username, password = user.passcode)
            }
            // println("Response : $apiResponse")
            if (apiResponse.isError()) {

                if (isNotApiCall) {
                    println("Error : ${(apiResponse.getValue() as Exception).localizedMessage}")
                    do {
                        print("Retry (Y/N) ? : ")
                        val input = readLine()
                        when (input) {
                            "Y", "" -> {
                                login(username = username, password = password)
                                return
                            }

                            "N" -> {
                            }

                            else -> invalidOptionMessage()
                        }
                    } while (input != "N")

                } else {

//                    println("API Call Error")
                    print(
                        Json.encodeToString(
                            serializer = BalanceSheetDataModel.serializer(),
                            value = BalanceSheetDataModel(
                                status = 1,
                                error = "Error : ${(apiResponse.getValue() as Exception).localizedMessage}"
                            )
                        )
                    )
                }
            } else {

                val authenticationResponseResult = apiResponse.getValue() as AuthenticationResponse
                when (authenticationResponseResult.userCount) {
                    0 -> {
                        if (isNotApiCall) {
                            println("Invalid Credentials...")
                        } else {
                            print(
                                Json.encodeToString(
                                    serializer = BalanceSheetDataModel.serializer(),
                                    value = BalanceSheetDataModel(
                                        status = 1,
                                        error = "Invalid Credentials"
                                    )
                                )
                            )
                        }
                    }

                    1 -> {

                        if (isNotApiCall) {
                            println("Login Success...")
                            userScreen(username = user.username, userId = authenticationResponseResult.id)
                        } else {
                            when (apiMethod) {
                                "BalanceSheet" -> {
                                    if (apiMethodOptions.containsKey(CommandLineApiMethodBalanceSheetOptionsEnum.refineLevel.name)) {
                                        val refineryLevel =
                                            apiMethodOptions[CommandLineApiMethodBalanceSheetOptionsEnum.refineLevel.name]
                                        if (refineryLevel is BalanceSheetRefineLevelEnum) {
                                            if (apiMethodOptions.containsKey(CommandLineApiMethodBalanceSheetOptionsEnum.outputFormat.name)) {
                                                if (apiMethodOptions[CommandLineApiMethodBalanceSheetOptionsEnum.outputFormat.name] is BalanceSheetOutputFormatsEnum) {
                                                    printBalanceSheetOfUser(
                                                        currentUserName = username,
                                                        currentUserId = authenticationResponseResult.id,
                                                        refineLevel = refineryLevel,
                                                        isNotApiCall = false
                                                    )
                                                } else {
//                                                    println("Output Format is Invalid")
                                                    print(
                                                        Json.encodeToString(
                                                            serializer = BalanceSheetDataModel.serializer(),
                                                            value = BalanceSheetDataModel(
                                                                status = 1,
                                                                error = "Invalid Output Format"
                                                            )
                                                        )
                                                    )
                                                }
                                            } else {
//                                                println("Output Format is Missing")
                                                print(
                                                    Json.encodeToString(
                                                        serializer = BalanceSheetDataModel.serializer(),
                                                        value = BalanceSheetDataModel(
                                                            status = 1,
                                                            error = "Missing Output Format of the Balance Sheet Ledger"
                                                        )
                                                    )
                                                )
                                            }
                                        } else {
//                                            println("Refinery Level is Invalid")
                                            print(
                                                Json.encodeToString(
                                                    serializer = BalanceSheetDataModel.serializer(),
                                                    value = BalanceSheetDataModel(
                                                        status = 1,
                                                        error = "Invalid Refinery Level"
                                                    )
                                                )
                                            )
                                        }
                                    } else {
//                                        println("Refinery Level is Missing")
                                        print(
                                            Json.encodeToString(
                                                serializer = BalanceSheetDataModel.serializer(),
                                                value = BalanceSheetDataModel(
                                                    status = 1,
                                                    error = "Missing Refinery Level of the Balance Sheet Ledger"
                                                )
                                            )
                                        )
                                    }
                                }

                                else -> {
//                                    println("Invalid API Method")
                                    print(
                                        Json.encodeToString(
                                            serializer = BalanceSheetDataModel.serializer(),
                                            value = BalanceSheetDataModel(
                                                status = 1,
                                                error = "Invalid API Method Reference"
                                            )
                                        )
                                    )
                                }
                            }
                        }
                    }

                    else -> {
                        if (isNotApiCall) {
                            println("Server Execution Error...")
                        } else {
                            print(
                                Json.encodeToString(
                                    serializer = BalanceSheetDataModel.serializer(),
                                    value = BalanceSheetDataModel(
                                        status = 1,
                                        error = "Server Execution Error"
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }

        private fun displayCurrentUser(user: UserCredentials) {
            println("The recognised user is ${user.username}")
        }

        @JvmStatic
        internal fun listUsers() {

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
                                "2 - User Home",
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

                            "2" -> {
                                if (handleUserSelection(
                                        chosenUserId = chooseUserByIndex(usersMap = usersMap), usersMap = usersMap
                                    )
                                ) {
                                    userScreen(username = chosenUser.username, userId = chosenUser.id)
                                }
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