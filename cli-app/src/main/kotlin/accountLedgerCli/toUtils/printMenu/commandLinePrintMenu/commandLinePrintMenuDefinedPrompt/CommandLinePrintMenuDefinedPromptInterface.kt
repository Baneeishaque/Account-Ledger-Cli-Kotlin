package accountLedgerCli.toUtils.printMenu.commandLinePrintMenu.commandLinePrintMenuDefinedPrompt

import accountLedgerCli.toUtils.printMenu.commandLinePrintMenu.CommandLinePrintMenuInterface

public interface CommandLinePrintMenuDefinedPromptInterface: CommandLinePrintMenuInterface {
    val promptWord: String
    fun printMenuDefinedPromptFromListOfCommands(title: String, titleSeperator: Char,listOfCommands: List<String>) {
        printMenuFromListOfCommands(title, titleSeperator, listOfCommands, promptWord)
    }
}