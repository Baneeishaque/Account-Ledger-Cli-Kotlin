package accountLedgerCli.cli

import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithTryPrompt
import accountLedgerCli.enums.AccountTypeEnum
import accountLedgerCli.enums.HandleAccountsApiResponseResult
import accountLedgerCli.enums.TransactionTypeEnum
import accountLedgerCli.to_utils.InputUtils
import accountLedgerCli.utils.ApiUtils

internal fun <T> getValidIndex(

    map: LinkedHashMap<UInt, T>,
    itemSpecification: String,
    items: String

): UInt {

    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
        listOf(
            "\n${itemSpecification}s",
            items,
            "Enter $itemSpecification Index, or O to back : ${itemSpecification.first()}"
        )
    )

    val idInput: String = readLine()!!
    if (idInput == "0") return 0u

    val inputForIndex: UInt = InputUtils.getValidInt(
        inputString = idInput,
        invalidMessage = "Invalid $itemSpecification Index...\nEnter $itemSpecification Index, or O to back : ${itemSpecification.first()}"
    )
    if (inputForIndex == 0u) {

        return 0u

    } else {

        if (map.containsKey(inputForIndex)) {

            return inputForIndex

        } else {

            commandLinePrintMenuWithTryPrompt.printMenuWithTryPromptFromListOfCommands(
                listOfCommands = listOf("Invalid $itemSpecification Index, Try again ? (Y/N) : ")
            )
            return when (readLine()) {

                "Y", "" -> {

                    getValidIndex(

                        map = map,
                        itemSpecification = itemSpecification,
                        items = items
                    )
                }

                "N" -> {
                    0u
                }

                else -> {

                    commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                        listOfCommands = listOf("Invalid Entry...")
                    )
                    getValidIndex(

                        map = map,
                        itemSpecification = itemSpecification,
                        items = items
                    )
                }
            }
        }
    }
}

internal fun chooseDepositTop(userId: UInt): HandleAccountsApiResponseResult {

    return HandleResponses.handleAccountsApiResponse(
        apiResponse = getAccounts(userId = userId),
        purpose = AccountTypeEnum.TO
    )
}

internal fun chooseDepositFull(userId: UInt): HandleAccountsApiResponseResult {

    return HandleResponses.handleAccountsApiResponse(
        apiResponse = ApiUtils.getAccountsFull(userId = userId),
        purpose = AccountTypeEnum.TO
    )
}

internal fun chooseWithdrawTop(userId: UInt): HandleAccountsApiResponseResult {

    return HandleResponses.handleAccountsApiResponse(
        apiResponse = getAccounts(userId = userId),
        purpose = AccountTypeEnum.FROM
    )
}

internal fun chooseWithdrawFull(userId: UInt): HandleAccountsApiResponseResult {

    return HandleResponses.handleAccountsApiResponse(
        apiResponse = ApiUtils.getAccountsFull(userId),
        purpose = AccountTypeEnum.FROM
    )
}

internal fun chooseViaTop(userId: UInt): HandleAccountsApiResponseResult {

    return HandleResponses.handleAccountsApiResponse(
        apiResponse = getAccounts(userId = userId),
        purpose = AccountTypeEnum.VIA
    )
}

internal fun chooseViaFull(userId: UInt): HandleAccountsApiResponseResult {

    return HandleResponses.handleAccountsApiResponse(
        apiResponse = ApiUtils.getAccountsFull(userId),
        purpose = AccountTypeEnum.VIA
    )
}

internal fun enterDateWithTime(
    dateTimeInText: String,
    transactionType: TransactionTypeEnum
): String {

    print(
        "$dateTimeInText Correct? (Y/N), D+Tr to increase 1 Day with Time Reset, D+ to increase 1 Day, D2+Tr to increase 2 Days with Time Reset, D2+ to increase 2 Days,${if (transactionType == TransactionTypeEnum.VIA) " Ex12 to exchange From & Via A/Cs, Ex23 to exchange Via & To A/Cs, Ex13 to exchange From & To A/Cs" else " Ex to exchange From & To A/Cs"} or B to Back : "
    )
    when (readLine()) {
        "Y", "" -> {

            return dateTimeInText
        }

        "N" -> {

            return InputUtils.getValidDateTimeInNormalPattern()
        }

        "D+Tr" -> {

            return "D+Tr"
        }

        "D+" -> {

            return "D+"
        }

        "D2+Tr" -> {

            return "D2+Tr"
        }

        "D2+" -> {

            return "D2+"
        }

        "Ex" -> {

            if (transactionType == TransactionTypeEnum.VIA) {

                return retryEnterDateWithTimeOnInvalidEntry(transactionType, dateTimeInText)
            }
            return "Ex"
        }

        "Ex12" -> {

            if (transactionType != TransactionTypeEnum.VIA) {

                return retryEnterDateWithTimeOnInvalidEntry(transactionType, dateTimeInText)
            }
            return "Ex12"
        }

        "Ex23" -> {

            if (transactionType != TransactionTypeEnum.VIA) {

                return retryEnterDateWithTimeOnInvalidEntry(transactionType, dateTimeInText)
            }
            return "Ex23"
        }

        "Ex13" -> {

            if (transactionType != TransactionTypeEnum.VIA) {

                return retryEnterDateWithTimeOnInvalidEntry(transactionType, dateTimeInText)
            }
            return "Ex13"
        }

        "B" -> {

            return "B"
        }

        else -> {

            return retryEnterDateWithTimeOnInvalidEntry(transactionType, dateTimeInText)
        }
    }
}

private fun retryEnterDateWithTimeOnInvalidEntry(transactionType: TransactionTypeEnum, dateTimeInText: String): String {

    invalidOptionMessage()
    return enterDateWithTime(transactionType = transactionType, dateTimeInText = dateTimeInText)
}
