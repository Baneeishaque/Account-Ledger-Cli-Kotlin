package accountLedgerCli.toUtils.printMenu

public interface PrintMenuInterface {
    fun printMenuFromListOfCommands(title: String, titleSeperator: Char, listOfCommands: List<String>, promptWord: String) {}
}