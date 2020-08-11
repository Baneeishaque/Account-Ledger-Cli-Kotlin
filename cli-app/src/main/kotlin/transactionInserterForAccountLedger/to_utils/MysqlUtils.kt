package transactionInserterForAccountLedger.to_utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object MysqlUtils {

    val dateTimePattern= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")!!

    fun normalDateTimeToMysqlDateTime(dateTime: String): String {

        return LocalDateTime.parse(dateTime, DateTimeUtils.normalPattern).format(dateTimePattern)
    }
}
