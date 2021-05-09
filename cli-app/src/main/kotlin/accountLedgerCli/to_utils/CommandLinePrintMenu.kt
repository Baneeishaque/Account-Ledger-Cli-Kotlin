package accountLedgerCli.to_utils

class CommandLinePrintMenu : CommandLinePrintMenuInterface {

    override fun printMenuFromListOfCommands(listOfCommands: List<String>, promptWord: String) {

        listOfCommands.forEach { command ->

            if (command.contains(promptWord, ignoreCase = true)) {

                print(command)

            } else {

                println(command)
            }
        }
    }
}
