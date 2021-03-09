package accountLedgerCli.toUtils.printMenu.commandLinePrintMenu

import accountLedgerCli.toUtils.printMenu.PrintMenuInterface

public interface CommandLinePrintMenuInterface: PrintMenuInterface {
    override fun printMenuFromListOfCommands(title: String, titleSeperator: Char, listOfCommands: List<String>, promptWord: String) {
        print(title+"\n")
        for (i in 1..(title.length+1)) {
            print(titleSeperator)
        }
        listOfCommands.forEach { command ->
            if (command.contains(promptWord, ignoreCase = true)) {
                print(command)
            } else {
                println(command)
            }
        }
    }
}