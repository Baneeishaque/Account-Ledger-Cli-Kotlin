package accountLedgerCli.to_utils

class CommandLinePrintMenuWithEnterPrompt(private val commandLinePrintMenu: CommandLinePrintMenuInterface) {

    fun printMenuWithEnterPromptFromListOfCommands(listOfCommands: List<String>) {

        commandLinePrintMenu.printMenuFromListOfCommands(listOfCommands = listOfCommands, promptWord = "Enter")
    }
}
