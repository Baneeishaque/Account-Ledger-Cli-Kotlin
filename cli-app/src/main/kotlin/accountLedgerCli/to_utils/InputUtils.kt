package accountLedgerCli.to_utils

import accountLedgerCli.cli.App
import accountLedgerCli.models.UserCredentials
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

object InputUtils {

    @JvmStatic
    fun getValidFloat(inputString: String, invalidMessage: String): Float {

        return try {
            inputString.toFloat()

        } catch (exception: NumberFormatException) {

            println(invalidMessage)
            getValidFloat(readLine()!!, invalidMessage)
        }
    }

    @JvmStatic
    fun getValidInt(inputString: String, invalidMessage: String): UInt {

        return try {
            inputString.toUInt()

        } catch (exception: NumberFormatException) {

            App.commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                listOf(invalidMessage)
            )
            getValidInt(readLine()!!, invalidMessage)
        }
    }

    @JvmStatic
    fun getValidDateTimeInNormalPattern(): String {

        // TODO : Implement Back
        print("Enter Time (DD/MM/YYYY HH:MM:SS) : ")
        return try {

            LocalDateTime.parse(readLine(), DateTimeUtils.normalDateTimePattern)
                .format(DateTimeUtils.normalDateTimePattern)

        } catch (e: DateTimeParseException) {

            println("Invalid Date...")
            getValidDateTimeInNormalPattern()
        }
    }

    @JvmStatic
    fun getValidDateInNormalPattern(): String {

        // TODO : Implement Back
        print("Enter Date (DD/MM/YYYY) : ")
        return try {

            LocalDate.parse(readLine(), DateTimeUtils.normalDatePattern)
                .format(DateTimeUtils.normalDatePattern)

        } catch (e: DateTimeParseException) {

            println("Invalid Date...")
            getValidDateInNormalPattern()
        }
    }
}
