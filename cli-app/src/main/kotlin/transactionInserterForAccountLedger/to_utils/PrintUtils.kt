package transactionInserterForAccountLedger.to_utils

object PrintUtils {

    fun printMenu(listOfCommands: List<String>) {

        listOfCommands.forEach { command ->
            println(command)
        }
    }
}
