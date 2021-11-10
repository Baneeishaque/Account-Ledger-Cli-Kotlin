package accountLedgerCli.to_utils

object InputUtils {

    fun getValidFloat(inputString: String, invalidMessage: String): Float {

        try {
            return inputString.toFloat()

        } catch (exception: NumberFormatException) {

            println(invalidMessage)
            return getValidFloat(readLine()!!, invalidMessage)
        }
    }

    fun getValidInt(inputString: String, invalidMessage: String): Int {

        try {
            return inputString.toInt()

        } catch (exception: NumberFormatException) {

            println(invalidMessage)
            return getValidInt(readLine()!!, invalidMessage)
        }
    }
}
