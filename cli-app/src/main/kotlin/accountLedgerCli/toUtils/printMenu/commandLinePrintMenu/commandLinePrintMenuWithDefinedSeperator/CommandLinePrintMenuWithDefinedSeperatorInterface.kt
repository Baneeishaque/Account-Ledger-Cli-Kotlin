package accountLedgerCli.toUtils.printMenu.commandLinePrintMenu.commandLinePrintMenuWithDefinedSeperator

import accountLedgerCli.toUtils.printMenu.commandLinePrintMenu.CommandLinePrintMenuInterface

public interface CommandLinePrintMenuWithDefinedSeperatorInterface: CommandLinePrintMenuInterface {
    val titleSeperator: Char
    fun printMenuWithDefinedSeperatorFromListOfCommands(title: String, listOfCommands: List<String>, promptWord: String) {
        printMenuFromListOfCommands(title, titleSeperator, listOfCommands, promptWord)
    }
}