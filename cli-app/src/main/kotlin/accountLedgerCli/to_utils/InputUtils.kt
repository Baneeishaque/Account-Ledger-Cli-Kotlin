package accountLedgerCli.to_utils

import accountLedgerCli.cli.UserCredentials
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

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

    fun getValidDateTimeInNormalPattern(): String {

        // TODO : Implement Back
        print("Enter Time (DD/MM/YYYY HH:MM:SS) : ")
        try {

            return LocalDateTime.parse(readLine(), DateTimeUtils.normalDateTimePattern)
                .format(DateTimeUtils.normalDateTimePattern)

        } catch (e: DateTimeParseException) {

            println("Invalid Date...")
            return getValidDateTimeInNormalPattern()
        }
    }

    fun getValidDateInNormalPattern(): String {

        // TODO : Implement Back
        print("Enter Date (DD/MM/YYYY) : ")
        try {

            return LocalDate.parse(readLine(), DateTimeUtils.normalDatePattern)
                .format(DateTimeUtils.normalDatePattern)

        } catch (e: DateTimeParseException) {

            println("Invalid Date...")
            return getValidDateInNormalPattern()
        }
    }

    internal fun getUserCredentials(): UserCredentials {
        var user = UserCredentials("", "");
        print("Enter Your Username : ")
        user.username = readLine().toString()
        print("Enter Your Password : ")
        user.passcode = readLine().toString()
        return user
    }
}
