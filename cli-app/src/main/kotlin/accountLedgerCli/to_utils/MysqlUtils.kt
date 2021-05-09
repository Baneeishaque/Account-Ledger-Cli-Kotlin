package accountLedgerCli.to_utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object MysqlUtils {

    val mysqlDateTimePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")!!

    fun normalDateTimeStringToMysqlDateTimeString(normalDateTimeString: String): String {

        return LocalDateTime.parse(normalDateTimeString, DateTimeUtils.normalPattern).format(mysqlDateTimePattern)
    }
}
