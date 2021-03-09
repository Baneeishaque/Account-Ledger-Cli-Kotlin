package accountLedgerCli.toUtils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object MysqlUtils {

    private val mysqlDateTimePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")!!

    internal fun normalDateTimeStringToMysqlDateTimeString(normalDateTimeString: String): String {

        return LocalDateTime.parse(normalDateTimeString, DateTimeUtils.normalPattern).format(mysqlDateTimePattern)
    }
}
