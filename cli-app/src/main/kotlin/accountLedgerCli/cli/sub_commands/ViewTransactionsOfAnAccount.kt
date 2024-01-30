package accountLedgerCli.cli.sub_commands

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.enums.EnvironmentFileEntryEnum
import account.ledger.library.models.InsertTransactionResult
import account.ledger.library.utils.ApiUtils
import account.ledger.library.utils.HandleResponses
import accountLedgerCli.cli.App
import accountLedgerCli.cli.TransactionViews.viewTransactionsForAnAccount
import accountLedgerCli.enums.CommandLineApiMethodInsertTransactionArgumentsEnum
import accountLedgerCli.enums.CommandLineApiMethodViewTransactionsOfAnAccountArgumentsEnum
import accountLedgerCli.enums.CommandLineApiMethodsEnum
import common.utils.library.cli.sub_commands.SubCommandWithUserIdAndUsernameAsArguments
import common.utils.library.models.IsOkModel
import common.utils.library.utils.ApiUtilsCommon
import io.github.cdimascio.dotenv.Dotenv
import kotlinx.cli.ArgType
import kotlinx.cli.optional

class ViewTransactionsOfAnAccount(

    override val isDevelopmentMode: Boolean = false,
    override val dotEnv: Dotenv

) : SubCommandWithUserIdAndUsernameAsArguments(

    name = CommandLineApiMethodsEnum.ViewTransactionsOfAnAccount.name,
    actionDescription = "View transactions of an account",
    isDevelopmentMode = isDevelopmentMode,
    dotEnv = dotEnv
) {
    private val accountIdDescription: String = "ID of the Account - the transactions of that account will return"
    private var accountId: Int? by argument(

        type = ArgType.Int,
        fullName = CommandLineApiMethodViewTransactionsOfAnAccountArgumentsEnum.AccountId.name,
        description = accountIdDescription

    ).optional()

    override fun additionalBeforeExecuteActions() {}

    override fun additionalActionsWithUserIdAndUsername(userIdLocal: Int, usernameLocal: String) {

        if (accountId == null) {

            try {

                accountId = dotEnv[EnvironmentFileEntryEnum.WALLET_ACCOUNT_ID.name].toInt()
                if (accountId!! <= 0) {

                    ApiUtilsCommon.printNegativeNumberOrZeroArgumentValueMessageForApi(

                        argumentSummary = accountIdDescription
                    )
                } else {

                    operationsAfterAccountId(userIdLocal, usernameLocal)
                }
            } catch (exception: NumberFormatException) {

                ApiUtilsCommon.printInvalidArgumentValueMessageForApi(

                    argumentSummary = accountIdDescription
                )
            }
        } else {

            operationsAfterAccountId(userIdLocal, usernameLocal)
        }
    }

    private fun operationsAfterAccountId(userIdLocal: Int, usernameLocal: String) {

        val getUserAccountsMapResult: IsOkModel<LinkedHashMap<UInt, AccountResponse>> =
            HandleResponses.getUserAccountsMap(

                apiResponse = ApiUtils.getAccountsFull(

                    userId = userIdLocal.toUInt(),
                    isConsoleMode = true,
                    isDevelopmentMode = isDevelopmentMode
                )
            )

        if (getUserAccountsMapResult.isOK) {

            val accountsMap = getUserAccountsMapResult.data
            if (accountsMap != null) {

                val account = accountsMap[accountId!!.toUInt()]
                if (account != null) {

                    viewTransactionsForAnAccount(

                        userId = userIdLocal.toUInt(),
                        username = usernameLocal,
                        accountId = accountId!!.toUInt(),
                        accountFullName = account.fullName,
                        previousTransactionData = InsertTransactionResult(

                            isSuccess = false,
                            dateTimeInText = App.dateTimeInText,
                            transactionParticulars = App.transactionParticulars,
                            transactionAmount = App.transactionAmount,
                            fromAccount = App.fromAccount,
                            viaAccount = App.viaAccount,
                            toAccount = App.toAccount
                        ),
                        fromAccount = App.fromAccount,
                        isConsoleMode = true,
                        isNotApiCall = false,
                        isDevelopmentMode = isDevelopmentMode
                    )
                } else {
                    ApiUtilsCommon.printErrorMessageForApi(errorMessage = "Account with ID $accountId not found")
                }
            } else {

                ApiUtilsCommon.printErrorMessageForApi(errorMessage = "No accounts found for user with ID $userIdLocal")
            }
        } else {

            ApiUtilsCommon.printErrorMessageForApi(errorMessage = "Error : ${getUserAccountsMapResult.error}")
        }
    }
}
