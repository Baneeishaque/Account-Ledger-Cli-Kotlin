package accountLedgerCli.to_utils

public class CommandLinePrintMenuWithContinuePrompt(private val commandLinePrintMenu: CommandLinePrintMenuInterface) {
    fun printMenuWithContinuePromptFromListOfCommands(listOfCommands: List<String>) {
        commandLinePrintMenu.printMenuFromListOfCommands(listOfCommands = listOfCommands, promptWord = "Continue")
    }
}