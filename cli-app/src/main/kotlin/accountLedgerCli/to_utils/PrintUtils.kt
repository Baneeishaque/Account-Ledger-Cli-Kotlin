package accountLedgerCli.to_utils

object PrintUtils {

    internal fun printMenu(listOfCommands: List<String>, promptWord: String = "Enter") {

        listOfCommands.forEach { command ->

            if (command.contains(promptWord, ignoreCase = true)) {

                print(command)

            } else {

                println(command)
            }
        }
    }

    internal fun printMenuWithTryPrompt(listOfCommands: List<String>) {

        printMenu(listOfCommands = listOfCommands, promptWord = "Try")
    }
}
