package accountLedgerCli.cli

import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.cli.App.Companion.commandLinePrintMenuWithTryPrompt
import accountLedgerCli.cli.App.Companion.dateTimeString
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

    val accountIdInput: String = readLine()!!
    if (accountIdInput == "0") return 0u

    val inputForIndex = InputUtils.getValidInt(

        accountIdInput,
        "Invalid $itemSpecification Index...\nEnter $itemSpecification Index, or O to back : ${itemSpecification.first()}"
    )
    if (inputForIndex == 0u) {

        return 0u

    } else {

        if (map.containsKey(inputForIndex)) {

            return inputForIndex

        } else {

            commandLinePrintMenuWithTryPrompt.printMenuWithTryPromptFromListOfCommands(
                listOf("Invalid $itemSpecification Index, Try again ? (Y/N) : ")
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
                        listOf("Invalid Entry...")
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

//TODO : Enum for purpose
internal fun chooseDepositTop(userId: UInt): Boolean {

    return handleAccountsApiResponse(apiResponse = getAccounts(userId = userId), purpose = "To")
}

internal fun chooseDepositFull(userId: UInt): Boolean {

    return handleAccountsApiResponse(apiResponse = ApiUtils.getAccountsFull(userId = userId), purpose = "To")
}

internal fun chooseFromAccountTop(userId: UInt): Boolean {

    return handleAccountsApiResponse(apiResponse = getAccounts(userId = userId), purpose = "From")
}

internal fun chooseFromAccountFull(userId: UInt): Boolean {

    return handleAccountsApiResponse(apiResponse = ApiUtils.getAccountsFull(userId), purpose = "From")
}

internal fun chooseViaAccountFull(userId: UInt): Boolean {

    return handleAccountsApiResponse(apiResponse = ApiUtils.getAccountsFull(userId), purpose = "Via.")
}

internal fun enterDateWithTime(transactionTypeEnum: TransactionTypeEnum): String {

    print(
        "$dateTimeString Correct? (Y/N), D+Tr to increase 1 Day with Time Reset, D+ to increase 1 Day, D2+Tr to increase 2 Days with Time Reset, D2+ to increase 2 Days,${if (transactionTypeEnum == TransactionTypeEnum.VIA) " Ex12 to exchange From & Via A/Cs, Ex23 to exchange Via & To A/Cs, Ex13 to exchange From & To A/Cs" else " Ex to exchange From & To A/Cs"} or B to Back : "
    )
    when (readLine()) {
        "Y", "" -> {

            return dateTimeString
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

            if (transactionTypeEnum == TransactionTypeEnum.VIA) {
                invalidOptionMessage()
                return enterDateWithTime(transactionTypeEnum = transactionTypeEnum)
            }
            return "Ex"
        }

        "Ex12" -> {

            if (transactionTypeEnum != TransactionTypeEnum.VIA) {
                invalidOptionMessage()
                return enterDateWithTime(transactionTypeEnum = transactionTypeEnum)
            }
            return "Ex12"
        }

        "Ex23" -> {

            if (transactionTypeEnum != TransactionTypeEnum.VIA) {
                invalidOptionMessage()
                return enterDateWithTime(transactionTypeEnum = transactionTypeEnum)
            }
            return "Ex23"
        }

        "Ex13" -> {

            if (transactionTypeEnum != TransactionTypeEnum.VIA) {
                invalidOptionMessage()
                return enterDateWithTime(transactionTypeEnum = transactionTypeEnum)
            }
            return "Ex13"
        }

        "B" -> {

            return "B"
        }

        else -> {

            invalidOptionMessage()
            return enterDateWithTime(transactionTypeEnum = transactionTypeEnum)
        }
    }
}
