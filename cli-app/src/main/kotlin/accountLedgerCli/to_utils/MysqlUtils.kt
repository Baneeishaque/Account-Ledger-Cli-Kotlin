package accountLedgerCli.to_utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object MysqlUtils {

    @JvmStatic
    val mysqlDateTimePattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")!!

    @JvmStatic
    val mysqlDatePattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")!!

    //TODO : Replace with data class
    @JvmStatic
    fun normalDateTimeStringToMysqlDateTimeString(normalDateTimeString: String): Pair<Boolean, String> {

        return try {

            val result: String = LocalDateTime.parse(normalDateTimeString, DateTimeUtils.normalDateTimePattern)
                .format(mysqlDateTimePattern)
            Pair(true, result)

        } catch (e: DateTimeParseException) {

            Pair(false, e.localizedMessage)
        }
    }

    @JvmStatic
    fun normalDateStringToMysqlDateString(normalDateString: String): Pair<Boolean, String> {

        return try {

            val result: String =
                LocalDate.parse(normalDateString, DateTimeUtils.normalDatePattern).format(mysqlDatePattern)
            Pair(true, result)

        } catch (e: DateTimeParseException) {

            //            println("Something went wrong...")
            Pair(false, e.localizedMessage)
        }
    }

    //TODO : Normal Date to MySQL Date String
    //TODO : Normal DateTime to MySQL DateTime String
}
