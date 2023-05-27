package accountLedgerCli.cli.sub_commands

import account.ledger.library.enums.CommandLineApiMethodInsertTransactionArgumentsEnum
import account.ledger.library.enums.CommandLineApiMethodsEnum
import account.ledger.library.operations.InsertOperations
import common.utils.library.utils.ApiUtils.printErrorMessageForApi
import common.utils.library.utils.ApiUtils.printInvalidArgumentValueMessageForApi
import common.utils.library.utils.ApiUtils.printNegativeDoubleArgumentValueMessageForApi
import common.utils.library.utils.ApiUtils.printNegativeNumberArgumentValueMessageForApi
import common.utils.library.utils.ApiUtils.printSuccessMessageForApi
import common.utils.library.utils.DateTimeUtils
import common.utils.library.utils.DateTimeUtils.normalDateTimePatternAsText
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand

@OptIn(ExperimentalCli::class)
class InsertTransaction(

    val isDevelopmentMode: Boolean = false
) :
    Subcommand(
        name = CommandLineApiMethodsEnum.InsertTransaction.name,
        actionDescription = "Inserts a single transaction to the database",
    ) {

    private val userIdDescription: String = "User Id of the User"
    private val userId: Int by argument(

        type = ArgType.Int,
        fullName = CommandLineApiMethodInsertTransactionArgumentsEnum.UserId.name,
        description = userIdDescription
    )

    private val eventDateTimeDescription: String = "Timestamp of the Transaction"
    private val eventDateTime: String by argument(

        type = ArgType.String,
        fullName = CommandLineApiMethodInsertTransactionArgumentsEnum.EventDateTime.name,
        description = eventDateTimeDescription
    )

    private val particularsDescription: String = "Particulars of the Transaction"
    private val particulars: String by argument(

        type = ArgType.String,
        fullName = CommandLineApiMethodInsertTransactionArgumentsEnum.Particulars.name,
        description = particularsDescription
    )

    private val amountDescription = "Amount of the Transaction"
    private val amount: Double by argument(

        type = ArgType.Double,
        fullName = CommandLineApiMethodInsertTransactionArgumentsEnum.Amount.name,
        description = amountDescription
    )

    private val fromAccountIdDescription = "From Account Id of the Transaction"
    private val fromAccountId: Int by argument(

        type = ArgType.Int,
        fullName = CommandLineApiMethodInsertTransactionArgumentsEnum.FromAccountId.name,
        description = fromAccountIdDescription
    )

    private val toAccountIdDescription = "To Account Id of the Transaction"
    private val toAccountId: Int by argument(

        type = ArgType.Int,
        fullName = CommandLineApiMethodInsertTransactionArgumentsEnum.ToAccountId.name,
        description = toAccountIdDescription
    )

    override fun execute() {

        if (userId <= 0) {

            printNegativeNumberArgumentValueMessageForApi(

                argumentSummary = userIdDescription,
                searchedInEnvironmentFile = false
            )

        } else {

            if (DateTimeUtils.normalDateTimeInTextToDateTime(normalDateTimeInText = eventDateTime).isOK) {

                if (particulars.isEmpty()) {

                    printInvalidArgumentValueMessageForApi(

                        argumentSummary = particularsDescription,
                        searchedInEnvironmentFile = false
                    )

                } else {

                    if (amount <= 0) {

                        printNegativeDoubleArgumentValueMessageForApi(

                            argumentSummary = amountDescription,
                            searchedInEnvironmentFile = false
                        )

                    } else {

                        if (fromAccountId <= 0) {

                            printNegativeNumberArgumentValueMessageForApi(

                                argumentSummary = fromAccountIdDescription,
                                searchedInEnvironmentFile = false
                            )

                        } else {

                            if (toAccountId <= 0) {

                                printNegativeNumberArgumentValueMessageForApi(

                                    argumentSummary = toAccountIdDescription,
                                    searchedInEnvironmentFile = false
                                )

                            } else {

                                if (InsertOperations.insertTransaction(

                                        userId = userId.toUInt(),
                                        eventDateTime = eventDateTime,
                                        particulars = particulars,
                                        amount = amount.toFloat(),
                                        fromAccountId = fromAccountId.toUInt(),
                                        toAccountId = toAccountId.toUInt(),
                                        isDevelopmentMode = isDevelopmentMode
                                    )
                                ) {

                                    printSuccessMessageForApi()

                                } else {

                                    printErrorMessageForApi()
                                }
                            }
                        }
                    }
                }
            } else {

                printErrorMessageForApi(errorMessage = "EventDateTime Must be in <$normalDateTimePatternAsText> pattern")
            }
        }
    }
}
