package accountLedgerCli.to_utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

object InputUtils {

    @JvmStatic
    fun getValidFloat(inputText: String, constructInvalidMessage: (String) -> String): Float {

        return try {
            inputText.toFloat()

        } catch (exception: NumberFormatException) {

            print(constructInvalidMessage.invoke(inputText))
            getValidFloat(inputText = readLine()!!, constructInvalidMessage = constructInvalidMessage)
        }
    }

    @JvmStatic
    fun getValidUnsignedInt(inputText: String, invalidMessage: String): UInt {

        return try {

            inputText.toUInt()

        } catch (exception: NumberFormatException) {

            print(message = invalidMessage)
            getValidUnsignedInt(inputText = readLine()!!, invalidMessage = invalidMessage)
        }
    }

    @JvmStatic
    fun getGreaterUnsignedInt(inputUInt: UInt, thresholdValue: UInt, constructInvalidMessage: (UInt) -> String): UInt {

        if (inputUInt > thresholdValue) {

            return inputUInt

        } else {

            print(message = constructInvalidMessage.invoke(inputUInt))
            return getGreaterUnsignedInt(

                inputUInt = getValidUnsignedInt(

                    inputText = readLine()!!,
                    invalidMessage = "Please Enter Valid Unsigned Integer"
                ),
                thresholdValue = thresholdValue,
                constructInvalidMessage = constructInvalidMessage
            )
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
