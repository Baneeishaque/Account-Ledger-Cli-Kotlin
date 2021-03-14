package accountLedgerCli.to_utils

public class CommandLinePrintMenuWithTryPrompt(val commandLinePrintMenu: CommandLinePrintMenuInterface) {
    fun printMenuWithTryPromptFromListOfCommands(listOfCommands: List<String>) {
        commandLinePrintMenu.printMenuFromListOfCommands(listOfCommands = listOfCommands, promptWord = "Try")
    }
}