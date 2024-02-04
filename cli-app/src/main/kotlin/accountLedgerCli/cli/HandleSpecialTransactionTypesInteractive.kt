package accountLedgerCli.cli

import account.ledger.library.models.ChooseSpecialTransactionTypeResultModel
import account.ledger.library.models.SpecialTransactionTypeModel
import account.ledger.library.utils.SpecialTransactionTypeUtils
import account_ledger_library.constants.ConstantsNative
import common.utils.library.constants.CommonConstants
import common.utils.library.utils.InteractiveUtils

object HandleSpecialTransactionTypesInteractive {

    internal fun chooseSpecialTransactionType(

        specialTransactionTypes: List<SpecialTransactionTypeModel>,
        isDevelopmentMode: Boolean

    ): ChooseSpecialTransactionTypeResultModel {

        do {
            App.commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                listOf(
                    "\nSpecial Transaction Types",
                    SpecialTransactionTypeUtils.specialTransactionTypesToTextFromList(

                        specialTransactionTypes = specialTransactionTypes
                    ),
                    "1 - Choose Special Transaction Type - By Index Number",
                    "2 - Search Special Transaction Type - By Part Of Name",
                    "0 - Back",
                    "",
                    "Enter Your Choice : "
                )
            )
            when (readln()) {
                "1" -> {
                    return handleSpecialTransactionTypesWithZeroAsBackValue(

                        selectedSpecialTransactionTypeIndex = InputOperations.getValidIndexFromCollectionWithSelectionPromptAndZeroAsBack(

                            list = specialTransactionTypes,
                            itemSpecification = ConstantsNative.SPECIAL_TRANSACTION_TYPE_TEXT,
                            items = SpecialTransactionTypeUtils.specialTransactionTypesToTextFromList(
                                specialTransactionTypes = specialTransactionTypes
                            )
                        ),
                        specialTransactionTypes = specialTransactionTypes
                    )
                }

                "2" -> {
                    return handleSpecialTransactionTypesWithZeroAsBackValue(

                        selectedSpecialTransactionTypeIndex = searchInSpecialTransactionTypes(

                            specialTransactionTypes = specialTransactionTypes,
                            isDevelopmentMode = isDevelopmentMode
                        ),
                        specialTransactionTypes = specialTransactionTypes
                    )
                }

                "0" -> {
                    return ChooseSpecialTransactionTypeResultModel(isSpecialTransactionTypeSelected = false)
                }

                else -> InteractiveUtils.invalidOptionMessage()
            }
        } while (true)
    }

    private fun handleSpecialTransactionTypesWithZeroAsBackValue(

        selectedSpecialTransactionTypeIndex: UInt,
        specialTransactionTypes: List<SpecialTransactionTypeModel>

    ): ChooseSpecialTransactionTypeResultModel {

        if (selectedSpecialTransactionTypeIndex != 0u) {

            return ChooseSpecialTransactionTypeResultModel(

                isSpecialTransactionTypeSelected = true,
                selectedSpecialTransactionType = specialTransactionTypes[selectedSpecialTransactionTypeIndex.toInt()]
            )
        }
        return ChooseSpecialTransactionTypeResultModel(isSpecialTransactionTypeSelected = false)
    }

    private fun searchInSpecialTransactionTypes(

        specialTransactionTypes: List<SpecialTransactionTypeModel>,
        isDevelopmentMode: Boolean

    ): UInt {

        App.commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
            listOf("\nEnter Search Key : ")
        )
        val searchKeyInput: String = readln()

        if (isDevelopmentMode) {

            println(
                "List to Search\n${CommonConstants.dashedLineSeparator}\n${
                    SpecialTransactionTypeUtils.specialTransactionTypesToTextFromList(

                        specialTransactionTypes = specialTransactionTypes
                    )
                }"
            )
            println("searchKey = $searchKeyInput")
        }

        val searchResult: MutableList<SpecialTransactionTypeModel> = mutableListOf()
        specialTransactionTypes.forEach { specialTransactionType: SpecialTransactionTypeModel ->

            if (specialTransactionType.indicator.contains(other = searchKeyInput, ignoreCase = true)) {

                searchResult.add(specialTransactionType)
            }
        }

        if (searchResult.isEmpty()) {

            do {
                App.commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                    listOf(
                        "No Matches....",
                        "1 - Try Again",
                        "0 - Back",
                        "",
                        "Enter Your Choice : "
                    )
                )

                val input: String = readln()

                if (input == "1") return searchInSpecialTransactionTypes(

                    specialTransactionTypes = specialTransactionTypes,
                    isDevelopmentMode = isDevelopmentMode
                )
                else if (input != "0") InteractiveUtils.invalidOptionMessage()

            } while (input != "0")

        } else {

            do {
                App.commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                    listOf(
                        "\nSearch Results",
                        SpecialTransactionTypeUtils.specialTransactionTypesToTextFromList(specialTransactionTypes = searchResult),
                        "1 - Choose Special Transaction Type - By Index Number",
                        "0 - Back",
                        "",
                        "Enter Your Choice : "
                    )
                )
                val input: String = readln()
                if (input == "1") {

                    return InputOperations.getValidIndexFromCollectionWithSelectionPromptAndZeroAsBack(

                        list = searchResult,
                        itemSpecification = ConstantsNative.SPECIAL_TRANSACTION_TYPE_TEXT,
                        items = SpecialTransactionTypeUtils.specialTransactionTypesToTextFromList(

                            specialTransactionTypes = searchResult
                        )
                    )
                } else if (input != "0") {

                    InteractiveUtils.invalidOptionMessage()
                }
            } while (input != "0")
        }
        return 0u
    }
}