package accountLedgerCli.cli

import account.ledger.library.enums.AccountTypeEnum
import account.ledger.library.enums.HandleAccountsApiResponseResult
import account.ledger.library.enums.TransactionTypeEnum
import account.ledger.library.operations.ServerOperations
import account.ledger.library.utils.ApiUtils
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithBackPrompt
import account_ledger_library.constants.ConstantsNative
import common.utils.library.utils.InputUtilsInteractive
import common.utils.library.utils.InteractiveUtils

object InputOperations {

    @JvmStatic
    internal fun chooseDepositTop(

        userId: UInt,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ): HandleAccountsApiResponseResult {

        return HandleResponsesInteractive.handleAccountsApiResponse(

            apiResponse = ServerOperations.getAccounts(

                userId = userId,
                isConsoleMode = isConsoleMode,
                isDevelopmentMode = isDevelopmentMode
            ),
            purpose = AccountTypeEnum.TO,
            isDevelopmentMode = isDevelopmentMode
        )
    }

    @JvmStatic
    internal fun chooseDepositFull(

        userId: UInt,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ): HandleAccountsApiResponseResult {

        return HandleResponsesInteractive.handleAccountsApiResponse(

            apiResponse = ApiUtils.getAccountsFull(

                userId = userId,
                isConsoleMode = isConsoleMode,
                isDevelopmentMode = isDevelopmentMode
            ),
            purpose = AccountTypeEnum.TO,
            isDevelopmentMode = isDevelopmentMode
        )
    }

    @JvmStatic
    internal fun chooseWithdrawTop(

        userId: UInt,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ): HandleAccountsApiResponseResult {

        return HandleResponsesInteractive.handleAccountsApiResponse(

            apiResponse = ServerOperations.getAccounts(

                userId = userId,
                isConsoleMode = isConsoleMode,
                isDevelopmentMode = isDevelopmentMode
            ),
            purpose = AccountTypeEnum.FROM,
            isDevelopmentMode = isDevelopmentMode
        )
    }

    @JvmStatic
    internal fun chooseWithdrawFull(
        userId: UInt,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean
    ): HandleAccountsApiResponseResult {

        return HandleResponsesInteractive.handleAccountsApiResponse(

            apiResponse = ApiUtils.getAccountsFull(

                userId = userId,
                isConsoleMode = isConsoleMode,
                isDevelopmentMode = isDevelopmentMode
            ),
            purpose = AccountTypeEnum.FROM,
            isDevelopmentMode = isDevelopmentMode
        )
    }

    @JvmStatic
    internal fun chooseViaTop(

        userId: UInt,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ): HandleAccountsApiResponseResult {

        return HandleResponsesInteractive.handleAccountsApiResponse(

            apiResponse = ServerOperations.getAccounts(

                userId = userId,
                isConsoleMode = isConsoleMode,
                isDevelopmentMode = isDevelopmentMode
            ),
            purpose = AccountTypeEnum.VIA,
            isDevelopmentMode = isDevelopmentMode
        )
    }

    @JvmStatic
    internal fun chooseViaFull(

        userId: UInt,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ): HandleAccountsApiResponseResult {

        return HandleResponsesInteractive.handleAccountsApiResponse(

            apiResponse = ApiUtils.getAccountsFull(

                userId = userId,
                isConsoleMode = isConsoleMode,
                isDevelopmentMode = isDevelopmentMode
            ),
            purpose = AccountTypeEnum.VIA,
            isDevelopmentMode = isDevelopmentMode
        )
    }

    @JvmStatic
    internal fun enterDateWithTime(

        promptCommands: List<String>,
        dateTimeInText: String,
        transactionType: TransactionTypeEnum,
        isNotFromSplitTransaction: Boolean

    ): String {

        commandLinePrintMenuWithBackPrompt.printMenuWithBackPromptFromListOfCommands(

            listOfCommands = promptCommands +

                    "Event Time : $dateTimeInText Correct? (Y/N)," +

                    (if ((transactionType == TransactionTypeEnum.VIA) || (transactionType == TransactionTypeEnum.CYCLIC_VIA)) {

                        "\tEx12 to exchange From & Via A/Cs," +
                                "\n\tEx23 to exchange Via & To A/Cs," +
                                "\n\tEx13 to exchange From & To A/Cs,"
                    } else {

                        "\tEx to exchange From & To A/Cs,"
                    }) +

                    (if (isNotFromSplitTransaction) "\tS to Split Transactions," else "") +

                    "\tTr{0-23}*:*{0-59}*:*{0-59}* to Reset Time to {0-23}*:*{0-59}*:*{0-59}* ," +

                    "\tD{d}{+,-} to +/- d Days," +
                    "\tH{d}{+,-} to +/- d Hours," +
                    "\tM{d}{+,-} to +/- d Minutes," +
                    "\tS{d}{+,-} to +/- d Seconds," +

                    // TODO : Option for Complete Back
                    "\tB to Back : "
        )

        when (val userInput = readlnOrNull()) {

            "Y", "" -> {

                return dateTimeInText
            }

            "N" -> {

                return InputUtilsInteractive.getValidDateTimeInNormalPattern()
            }

            "Ex" -> {

                if ((transactionType == TransactionTypeEnum.VIA) || (transactionType == TransactionTypeEnum.CYCLIC_VIA)) {

                    return retryEnterDateWithTimeOnInvalidEntry(

                        transactionType = transactionType,
                        dateTimeInText = dateTimeInText,
                        isFromSplitTransaction = isNotFromSplitTransaction,
                        promptCommands = promptCommands
                    )
                }
                return "Ex"
            }

            "Ex12" -> {

                if (transactionType != TransactionTypeEnum.VIA) {

                    return retryEnterDateWithTimeOnInvalidEntry(

                        transactionType = transactionType,
                        dateTimeInText = dateTimeInText,
                        isFromSplitTransaction = isNotFromSplitTransaction,
                        promptCommands = promptCommands
                    )
                }
                return "Ex12"
            }

            "Ex23" -> {

                if (transactionType != TransactionTypeEnum.VIA) {

                    return retryEnterDateWithTimeOnInvalidEntry(

                        transactionType = transactionType,
                        dateTimeInText = dateTimeInText,
                        isFromSplitTransaction = isNotFromSplitTransaction,
                        promptCommands = promptCommands
                    )
                }
                return "Ex23"
            }

            "Ex13" -> {

                if (transactionType != TransactionTypeEnum.VIA) {

                    return retryEnterDateWithTimeOnInvalidEntry(

                        transactionType = transactionType,
                        dateTimeInText = dateTimeInText,
                        isFromSplitTransaction = isNotFromSplitTransaction,
                        promptCommands = promptCommands
                    )
                }
                return "Ex13"
            }

            "S" -> {

                if (isNotFromSplitTransaction) {

                    return "S"
                }
            }

            "B" -> {

                return "B"
            }

            else -> {

                if (ConstantsNative.timeResetPatternRegex.matchEntire(input = userInput!!) != null) {

                    return userInput
                }
                if (ConstantsNative.hourIncrementOrDecrementPatternRegex.matchEntire(input = userInput) != null) {

                    return userInput
                }
                if (ConstantsNative.minuteIncrementOrDecrementPatternRegex.matchEntire(input = userInput) != null) {

                    return userInput
                }
                if (ConstantsNative.secondIncrementOrDecrementPatternRegex.matchEntire(input = userInput) != null) {

                    return userInput
                }
                if (ConstantsNative.dayIncrementOrDecrementPatternRegex.matchEntire(input = userInput) != null) {

                    return userInput
                }
                if (ConstantsNative.dayIncrementOrDecrementWithTimeResetPatternRegex.matchEntire(input = userInput) != null) {

                    return userInput
                }
            }
        }
        return retryEnterDateWithTimeOnInvalidEntry(

            transactionType = transactionType,
            dateTimeInText = dateTimeInText,
            isFromSplitTransaction = isNotFromSplitTransaction,
            promptCommands = promptCommands
        )
    }

    private fun retryEnterDateWithTimeOnInvalidEntry(

        promptCommands: List<String>,
        transactionType: TransactionTypeEnum,
        dateTimeInText: String,
        isFromSplitTransaction: Boolean

    ): String {

        InteractiveUtils.invalidOptionMessage()
        return enterDateWithTime(

            promptCommands = promptCommands,
            dateTimeInText = dateTimeInText,
            transactionType = transactionType,
            isNotFromSplitTransaction = isFromSplitTransaction
        )
    }
}