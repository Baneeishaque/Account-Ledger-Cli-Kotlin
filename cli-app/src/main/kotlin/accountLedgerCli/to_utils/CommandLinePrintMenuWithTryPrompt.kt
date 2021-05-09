package accountLedgerCli.to_utils

class CommandLinePrintMenuWithTryPrompt(val commandLinePrintMenu: CommandLinePrintMenuInterface) {

    fun printMenuWithTryPromptFromListOfCommands(listOfCommands: List<String>) {

        commandLinePrintMenu.printMenuFromListOfCommands(listOfCommands = listOfCommands, promptWord = "Try")
    }
}
