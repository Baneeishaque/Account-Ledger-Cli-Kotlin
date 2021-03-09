package accountLedgerCli.toUtils.printMenu.commandLinePrintMenu.commandLinePrintMenuDefinedSeperatorAndDefinedPrompt

import accountLedgerCli.toUtils.printMenu.commandLinePrintMenu.CommandLinePrintMenuInterface

public interface CommandLinePrintMenuDefinedSeperatorDefinedPromptInterface: CommandLinePrintMenuInterface {
    val titleSeperator: Char
    val promptWord: String
    fun printMenuWithDefinedSeperatorAndDefinedPromptFromListOfCommands(title: String, listOfCommands: List<String>) {
        printMenuFromListOfCommands(title, titleSeperator, listOfCommands, promptWord)
    }
}