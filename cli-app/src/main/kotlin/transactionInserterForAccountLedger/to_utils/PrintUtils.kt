package transactionInserterForAccountLedger.to_utils

object PrintUtils {

    fun printMenu(listOfCommands: List<String>) {

        listOfCommands.forEach { command ->

            if (command.contains("Enter", ignoreCase = true)) {

                print(command)

            } else {

                println(command)
            }
        }
    }
}
