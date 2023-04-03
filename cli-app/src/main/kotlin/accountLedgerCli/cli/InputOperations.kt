package accountLedgerCli.cli

import account.ledger.library.constants.Constants
import account.ledger.library.enums.AccountTypeEnum
import account.ledger.library.enums.HandleAccountsApiResponseResult
import account.ledger.library.enums.TransactionTypeEnum
import account.ledger.library.operations.getAccounts
import account.ledger.library.utils.ApiUtils
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithBackPrompt
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithTryPrompt
import common.utils.library.utils.InputUtils
import common.utils.library.utils.InteractiveUtils

fun <T> getValidIndexWithInputPrompt(

    map: Map<UInt, T>,
    itemSpecification: String,
    items: String,
    itemSpecificationPrefix: String = "",
    backValue: UInt

): UInt {

    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(

        listOfCommands = listOf(
            "\n${itemSpecification}s",
            items,
            "Enter $itemSpecificationPrefix$itemSpecification Index, or $backValue to back : ${itemSpecification.first()}"
        )
    )

    val idInput: String = readln()
    if (idInput == backValue.toString()) return backValue

    return getValidIndex(

        inputForIndex = InputUtils.getValidUnsignedInt(

            inputText = idInput,
            invalidMessage = "Invalid $itemSpecification Index...\nEnter $itemSpecification Index, or $backValue to back : ${itemSpecification.first()}"
        ),
        map = map,
        itemSpecification = itemSpecification,
        items = items,
        backValue = backValue
    )
}

internal fun <T> getValidIndexOrBack(

    userInputForIndex: String,
    map: Map<UInt, T>,
    itemSpecification: String,
    items: String,
    backValue: UInt

): UInt {

    return if (userInputForIndex == backValue.toString()) {

        backValue

    } else {

        val inputForIndex: UInt = InputUtils.getValidUnsignedInt(

            inputText = userInputForIndex,
            invalidMessage = "Invalid $itemSpecification Index...\nEnter $itemSpecification Index, or $backValue to back : ${itemSpecification.first()}"
        )
        getValidIndex(map, inputForIndex, itemSpecification, items, backValue)
    }
}

private fun <T> getValidIndex(

    map: Map<UInt, T>,
    inputForIndex: UInt,
    itemSpecification: String,
    items: String,
    backValue: UInt

): UInt {

    if (map.containsKey(inputForIndex)) {

        return inputForIndex

    } else {

        commandLinePrintMenuWithTryPrompt.printMenuWithTryPromptFromListOfCommands(

            listOfCommands = listOf("Invalid $itemSpecification Index, Try again ? (Y/N) : ")
        )
        return when (readlnOrNull()) {

            "Y", "" -> {

                getValidIndexWithInputPrompt(

                    map = map,
                    itemSpecification = itemSpecification,
                    items = items,
                    backValue = backValue
                )
            }

            "N" -> {

                backValue
            }

            else -> {

                commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                    listOfCommands = listOf("Invalid Entry...")
                )
                getValidIndexWithInputPrompt(

                    map = map,
                    itemSpecification = itemSpecification,
                    items = items,
                    backValue = backValue
                )
            }
        }
    }
}

internal fun chooseDepositTop(

    userId: UInt,
    isConsoleMode: Boolean,
    isDevelopmentMode: Boolean

): HandleAccountsApiResponseResult {

    return HandleResponses.handleAccountsApiResponse(

        apiResponse = getAccounts(

            userId = userId,
            isConsoleMode = isConsoleMode,
            isDevelopmentMode = isDevelopmentMode
        ),
        purpose = AccountTypeEnum.TO,
        isDevelopmentMode = isDevelopmentMode
    )
}

internal fun chooseDepositFull(

    userId: UInt,
    isConsoleMode: Boolean,
    isDevelopmentMode: Boolean

): HandleAccountsApiResponseResult {

    return HandleResponses.handleAccountsApiResponse(

        apiResponse = ApiUtils.getAccountsFull(

            userId = userId,
            isConsoleMode = isConsoleMode,
            isDevelopmentMode = isDevelopmentMode
        ),
        purpose = AccountTypeEnum.TO,
        isDevelopmentMode = isDevelopmentMode
    )
}

internal fun chooseWithdrawTop(

    userId: UInt,
    isConsoleMode: Boolean,
    isDevelopmentMode: Boolean

): HandleAccountsApiResponseResult {

    return HandleResponses.handleAccountsApiResponse(

        apiResponse = getAccounts(

            userId = userId,
            isConsoleMode = isConsoleMode,
            isDevelopmentMode = isDevelopmentMode
        ),
        purpose = AccountTypeEnum.FROM,
        isDevelopmentMode = isDevelopmentMode
    )
}

internal fun chooseWithdrawFull(
    userId: UInt,
    isConsoleMode: Boolean,
    isDevelopmentMode: Boolean
): HandleAccountsApiResponseResult {

    return HandleResponses.handleAccountsApiResponse(

        apiResponse = ApiUtils.getAccountsFull(

            userId = userId,
            isConsoleMode = isConsoleMode,
            isDevelopmentMode = isDevelopmentMode
        ),
        purpose = AccountTypeEnum.FROM,
        isDevelopmentMode = isDevelopmentMode
    )
}

internal fun chooseViaTop(

    userId: UInt,
    isConsoleMode: Boolean,
    isDevelopmentMode: Boolean

): HandleAccountsApiResponseResult {

    return HandleResponses.handleAccountsApiResponse(

        apiResponse = getAccounts(

            userId = userId,
            isConsoleMode = isConsoleMode,
            isDevelopmentMode = isDevelopmentMode
        ),
        purpose = AccountTypeEnum.VIA,
        isDevelopmentMode = isDevelopmentMode
    )
}

internal fun chooseViaFull(

    userId: UInt,
    isConsoleMode: Boolean,
    isDevelopmentMode: Boolean

): HandleAccountsApiResponseResult {

    return HandleResponses.handleAccountsApiResponse(

        apiResponse = ApiUtils.getAccountsFull(

            userId = userId,
            isConsoleMode = isConsoleMode,
            isDevelopmentMode = isDevelopmentMode
        ),
        purpose = AccountTypeEnum.VIA,
        isDevelopmentMode = isDevelopmentMode
    )
}

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

            return InputUtils.getValidDateTimeInNormalPattern()
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

            if (Constants.timeResetPatternRegex.matchEntire(input = userInput!!) != null) {

                return userInput
            }
            if (Constants.hourIncrementOrDecrementPatternRegex.matchEntire(input = userInput) != null) {

                return userInput
            }
            if (Constants.minuteIncrementOrDecrementPatternRegex.matchEntire(input = userInput) != null) {

                return userInput
            }
            if (Constants.secondIncrementOrDecrementPatternRegex.matchEntire(input = userInput) != null) {

                return userInput
            }
            if (Constants.dayIncrementOrDecrementPatternRegex.matchEntire(input = userInput) != null) {

                return userInput
            }
            if (Constants.dayIncrementOrDecrementWithTimeResetPatternRegex.matchEntire(input = userInput) != null) {

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
