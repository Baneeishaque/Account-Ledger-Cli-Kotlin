package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.AuthenticationResponse
import accountLedgerCli.api.response.UserResponse
import accountLedgerCli.api.response.UsersResponse
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.enums.BalanceSheetOutputFormatsEnum
import accountLedgerCli.enums.BalanceSheetRefineLevelEnum
import accountLedgerCli.enums.CommandLineApiMethodBalanceSheetOptionsEnum
import accountLedgerCli.models.BalanceSheetDataModel
import accountLedgerCli.models.ChooseUserResult
import accountLedgerCli.models.InsertTransactionResult
import accountLedgerCli.models.UserCredentials
import accountLedgerCli.retrofit.ResponseHolder
import accountLedgerCli.retrofit.data.AuthenticationDataSource
import accountLedgerCli.retrofit.data.UsersDataSource
import accountLedgerCli.to_utils.InputUtils
import accountLedgerCli.utils.UserUtils
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

//import java.nio.file.Paths

class UserOperations {
    companion object {

        @JvmStatic
        internal fun login(

            username: String,
            password: String,
            isNotApiCall: Boolean = true,
            apiMethod: String = "",
            apiMethodOptions: LinkedHashMap<String, Any> = linkedMapOf(),
            fromAccount: AccountResponse,
            viaAccount: AccountResponse,
            toAccount: AccountResponse,
            dateTimeInText: String,
            transactionParticulars: String,
            transactionAmount: Float

        ): InsertTransactionResult {

            if (isNotApiCall) {
//                println("Directory : ${Paths.get("").toAbsolutePath()}")
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
                        when (readLine()!!) {
                            "Y", "" -> {
                                return login(
                                    username = username,
                                    password = password,
                                    fromAccount = fromAccount,
                                    viaAccount = viaAccount,
                                    toAccount = toAccount,
                                    dateTimeInText = dateTimeInText,
                                    transactionParticulars = transactionParticulars,
                                    transactionAmount = transactionAmount
                                )
                            }

                            "N" -> {
                                return InsertTransactionResult(
                                    isSuccess = false,
                                    dateTimeInText = dateTimeInText,
                                    transactionParticulars = transactionParticulars,
                                    transactionAmount = transactionAmount
                                )
                            }

                            else -> invalidOptionMessage()
                        }
                    } while (true)

                } else {

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

                val authenticationResponseResult: AuthenticationResponse =
                    apiResponse.getValue() as AuthenticationResponse

                when (authenticationResponseResult.userCount) {

                    0u -> {
                        if (isNotApiCall) {

                            println("Invalid Credentials...")
                            return InsertTransactionResult(
                                isSuccess = false,
                                dateTimeInText = dateTimeInText,
                                transactionParticulars = transactionParticulars,
                                transactionAmount = transactionAmount
                            )

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

                    1u -> {

                        if (isNotApiCall) {

                            println("Login Success...")
                            return Screens.userScreen(
                                username = user.username,
                                userId = authenticationResponseResult.id,
                                fromAccount = fromAccount,
                                viaAccount = viaAccount,
                                toAccount = toAccount,
                                dateTimeInText = dateTimeInText,
                                transactionParticulars = transactionParticulars,
                                transactionAmount = transactionAmount
                            )

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
                            return InsertTransactionResult(
                                isSuccess = false,
                                dateTimeInText = dateTimeInText,
                                transactionParticulars = transactionParticulars,
                                transactionAmount = transactionAmount
                            )

                        } else {
                            print(
                                Json.encodeToString(
                                    serializer = BalanceSheetDataModel.serializer(),
                                    value = BalanceSheetDataModel(
                                        status = 1,
                                        error = "Server Execution Error, User Count is ${authenticationResponseResult.userCount}"
                                    )
                                )
                            )
                        }
                    }
                }
            }
            return InsertTransactionResult(
                isSuccess = false,
                dateTimeInText = dateTimeInText,
                transactionParticulars = transactionParticulars,
                transactionAmount = transactionAmount
            )
        }

        private fun displayCurrentUser(user: UserCredentials) {
            println("The recognised user is ${user.username}")
        }

        @JvmStatic
        internal fun listUsers(

            fromAccount: AccountResponse,
            viaAccount: AccountResponse,
            toAccount: AccountResponse,
            dateTimeInText: String,
            transactionParticulars: String,
            transactionAmount: Float

        ): InsertTransactionResult {

            val usersDataSource = UsersDataSource()
            println("Contacting Server...")
            val apiResponse: ResponseHolder<UsersResponse>
            runBlocking { apiResponse = usersDataSource.selectUsers() }
//            println("Response : $apiResponse")

            if (apiResponse.isError()) {

                println("Error : ${(apiResponse.getValue() as Exception).localizedMessage}")
                do {
                    print("Retry (Y/N) ? : ")
                    when (readLine()!!) {
                        "Y", "" -> {
                            return listUsers(
                                fromAccount = fromAccount,
                                viaAccount = viaAccount,
                                toAccount = toAccount,
                                dateTimeInText = dateTimeInText,
                                transactionParticulars = transactionParticulars,
                                transactionAmount = transactionAmount,
                            )
                        }

                        "N" -> {
                            return InsertTransactionResult(
                                isSuccess = false,
                                dateTimeInText = dateTimeInText,
                                transactionParticulars = transactionParticulars,
                                transactionAmount = transactionAmount
                            )
                        }

                        else -> invalidOptionMessage()
                    }
                } while (true)
            } else {

                val usersResponse: UsersResponse = apiResponse.getValue() as UsersResponse
                if (usersResponse.status == 1u) {

                    println("No Users...")
                    return InsertTransactionResult(
                        isSuccess = false,
                        dateTimeInText = dateTimeInText,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount
                    )

                } else {

                    val usersMap: LinkedHashMap<UInt, UserResponse> = UserUtils.prepareUsersMap(usersResponse.users)
                    var insertTransactionResult = InsertTransactionResult(
                        isSuccess = false,
                        dateTimeInText = dateTimeInText,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount
                    )
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
                        when (readLine()!!) {
                            "1" -> {
                                balanceSheetOfUser(usersMap = usersMap)
                            }

                            "2" -> {
                                val chooseUserResult: ChooseUserResult = handleUserSelection(
                                    chosenUserId = getValidIndex(
                                        map = usersMap,
                                        itemSpecification = Constants.userText,
                                        items = usersToStringFromLinkedHashMap(usersMap = usersMap)
                                    ), usersMap = usersMap
                                )
                                if (chooseUserResult.isChoosed) {

                                    insertTransactionResult = Screens.userScreen(

                                        username = chooseUserResult.chosenUser!!.username,
                                        userId = chooseUserResult.chosenUser.id,
                                        fromAccount = fromAccount,
                                        viaAccount = viaAccount,
                                        toAccount = toAccount,
                                        dateTimeInText = dateTimeInText,
                                        transactionParticulars = transactionParticulars,
                                        transactionAmount = transactionAmount
                                    )
                                }
                            }

                            "0" -> {
                                return insertTransactionResult
                            }

                            else -> invalidOptionMessage()
                        }
                    } while (true)
                }
            }
        }
    }
}