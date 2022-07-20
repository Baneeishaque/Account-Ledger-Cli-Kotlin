package accountLedgerCli.to_utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object MysqlUtils {

    val mysqlDateTimePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")!!
    val mysqlDatePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")!!

    //TODO : Replace with data class
    fun normalDateTimeStringToMysqlDateTimeString(normalDateTimeString: String): Pair<Boolean, String> {

        try {

            val result = LocalDateTime.parse(normalDateTimeString, DateTimeUtils.normalDateTimePattern)
                .format(mysqlDateTimePattern)
            return Pair(true, result)

        } catch (e: DateTimeParseException) {

            return Pair(false, e.localizedMessage)
        }
    }

    fun normalDateStringToMysqlDateString(normalDateString: String): Pair<Boolean, String> {

        try {

            val result = LocalDate.parse(normalDateString, DateTimeUtils.normalDatePattern).format(mysqlDatePattern)
            return Pair(true, result)

        } catch (e: DateTimeParseException) {

//            println("Something went wrong...")
            return Pair(false, e.localizedMessage)
        }
    }

    //TODO : Normal Date to MySQL Date String
    //TODO : Normal DateTime to MySQL DateTime String
}
