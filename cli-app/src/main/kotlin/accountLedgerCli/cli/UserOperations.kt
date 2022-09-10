package accountLedgerCli.cli

import accountLedgerCli.api.response.AccountResponse
import accountLedgerCli.api.response.AuthenticationResponse
import accountLedgerCli.api.response.UserResponse
import accountLedgerCli.api.response.UsersResponse
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.constants.Constants
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
import accountLedgerCli.to_utils.invalidOptionMessage
import accountLedgerCli.utils.UserUtils
import accountLedgerCli.to_constants.Constants as CommonConstants
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

import java.nio.file.Paths

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
            transactionAmount: Float,
            isConsoleMode: Boolean,
            isDevelopmentMode: Boolean

        ): InsertTransactionResult {

            if (isNotApiCall) {

                if (isDevelopmentMode) {

                    println("Directory : ${Paths.get("").toAbsolutePath()}")
                }
                println("\nAccount Ledger Authentication")
                println(CommonConstants.dashedLineSeparator)
            }

            var user: UserCredentials
            if (username.isEmpty() || password.isEmpty()) {

                user = UserUtils.getUserCredentials()

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
                                user = UserUtils.getUserCredentials()
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
                                    transactionAmount = transactionAmount,
                                    isConsoleMode = isConsoleMode,
                                    isDevelopmentMode = isDevelopmentMode
                                )
                            }

                            "N" -> {
                                return InsertTransactionResult(
                                    isSuccess = false,
                                    dateTimeInText = dateTimeInText,
                                    transactionParticulars = transactionParticulars,
                                    transactionAmount = transactionAmount,
                                    fromAccount = fromAccount,
                                    viaAccount = viaAccount,
                                    toAccount = toAccount
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
                                transactionAmount = transactionAmount,
                                fromAccount = fromAccount,
                                viaAccount = viaAccount,
                                toAccount = toAccount
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
                                transactionAmount = transactionAmount,
                                isConsoleMode = isConsoleMode,
                                isDevelopmentMode = isDevelopmentMode
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
                                                        isNotApiCall = false,
                                                        isConsoleMode = isConsoleMode,
                                                        isDevelopmentMode = isDevelopmentMode
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
                                transactionAmount = transactionAmount,
                                fromAccount = fromAccount,
                                viaAccount = viaAccount,
                                toAccount = toAccount
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
                transactionAmount = transactionAmount,
                fromAccount = fromAccount,
                viaAccount = viaAccount,
                toAccount = toAccount
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
            transactionAmount: Float,
            isConsoleMode: Boolean,
            isDevelopmentMode: Boolean

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
                                isConsoleMode = isConsoleMode,
                                isDevelopmentMode = isDevelopmentMode
                            )
                        }

                        "N" -> {
                            return InsertTransactionResult(
                                isSuccess = false,
                                dateTimeInText = dateTimeInText,
                                transactionParticulars = transactionParticulars,
                                transactionAmount = transactionAmount,
                                fromAccount = fromAccount,
                                viaAccount = viaAccount,
                                toAccount = toAccount
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
                        transactionAmount = transactionAmount,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount
                    )

                } else {

                    val usersMap: LinkedHashMap<UInt, UserResponse> = UserUtils.prepareUsersMap(usersResponse.users)
                    var insertTransactionResult = InsertTransactionResult(
                        isSuccess = false,
                        dateTimeInText = dateTimeInText,
                        transactionParticulars = transactionParticulars,
                        transactionAmount = transactionAmount,
                        fromAccount = fromAccount,
                        viaAccount = viaAccount,
                        toAccount = toAccount
                    )
                    do {
                        commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                            listOf(
                                "\nUsers",
                                UserUtils.usersToStringFromLinkedHashMap(
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
                                balanceSheetOfUser(

                                    usersMap = usersMap,
                                    isConsoleMode = isConsoleMode,
                                    isDevelopmentMode = isDevelopmentMode
                                )
                            }

                            "2" -> {
                                val chooseUserResult: ChooseUserResult = handleUserSelection(
                                    chosenUserId = getValidIndexWithInputPrompt(

                                        map = usersMap,
                                        itemSpecification = Constants.userText,
                                        items = UserUtils.usersToStringFromLinkedHashMap(usersMap = usersMap),
                                        backValue = 0u

                                    ), usersMap = usersMap
                                )
                                if (chooseUserResult.isChosen) {

                                    insertTransactionResult = Screens.userScreen(

                                        username = chooseUserResult.chosenUser!!.username,
                                        userId = chooseUserResult.chosenUser.id,
                                        fromAccount = fromAccount,
                                        viaAccount = viaAccount,
                                        toAccount = toAccount,
                                        dateTimeInText = dateTimeInText,
                                        transactionParticulars = transactionParticulars,
                                        transactionAmount = transactionAmount,
                                        isConsoleMode = isConsoleMode,
                                        isDevelopmentMode = isDevelopmentMode
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