package accountLedgerCli.utils

import accountLedgerCli.to_utils.DateTimeUtils
import java.time.LocalDateTime

object DateUtils {
    internal fun getCurrentDateTimeText(): String {
        return LocalDateTime.now().format(DateTimeUtils.normalDateTimePattern)
    }
}