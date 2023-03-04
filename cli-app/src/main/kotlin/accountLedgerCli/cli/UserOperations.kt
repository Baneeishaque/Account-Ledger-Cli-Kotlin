package accountLedgerCli.cli

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.api.response.AuthenticationResponse
import account.ledger.library.api.response.UserResponse
import account.ledger.library.api.response.UsersResponse
import account.ledger.library.constants.Constants
import account.ledger.library.enums.BalanceSheetOutputFormatsEnum
import account.ledger.library.enums.BalanceSheetRefineLevelEnum
import account.ledger.library.enums.CommandLineApiMethodBalanceSheetOptionsEnum
import account.ledger.library.models.*
import account.ledger.library.retrofit.ResponseHolder
import account.ledger.library.retrofit.data.AuthenticationDataSource
import account.ledger.library.retrofit.data.UsersDataSource
import account.ledger.library.utils.UserUtils
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import common.utils.library.utils.invalidOptionMessage
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import common.utils.library.constants.Constants as CommonConstants

import java.nio.file.Paths

class UserOperations {
    companion object {

        @JvmStatic
        fun login(

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
                        when (readlnOrNull().toString()) {
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
                        when (readln()) {
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
                            serializer = CommonDataModel.serializer(Unit.serializer()),
                            value = CommonDataModel(
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
                                    serializer = CommonDataModel.serializer(Unit.serializer()),
                                    value = CommonDataModel(
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
                                                            serializer = CommonDataModel.serializer(Unit.serializer()),
                                                            value = CommonDataModel(
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
                                                        serializer = CommonDataModel.serializer(Unit.serializer()),
                                                        value = CommonDataModel(
                                                            status = 1,
                                                            error = "Missing Output Format of the Balance Sheet Ledger"
                                                        )
                                                    )
                                                )
                                            }
                                        } else {
                                            print(
                                                Json.encodeToString(
                                                    serializer = CommonDataModel.serializer(Unit.serializer()),
                                                    value = CommonDataModel(
                                                        status = 1,
                                                        error = "Invalid Refinery Level"
                                                    )
                                                )
                                            )
                                        }
                                    } else {
                                        print(
                                            Json.encodeToString(
                                                serializer = CommonDataModel.serializer(Unit.serializer()),
                                                value = CommonDataModel(
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
                                            serializer = CommonDataModel.serializer(Unit.serializer()),
                                            value = CommonDataModel(
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
                                    serializer = CommonDataModel.serializer(Unit.serializer()),
                                    value = CommonDataModel(
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
        fun listUsers(

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
                    when (readln()) {
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
                        when (readln()) {
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
                                        userId = chooseUserResult.chosenUser!!.id,
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