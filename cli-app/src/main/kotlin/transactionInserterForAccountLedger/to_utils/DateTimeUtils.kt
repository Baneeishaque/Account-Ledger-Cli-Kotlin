package transactionInserterForAccountLedger.to_utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ofPattern

object DateTimeUtils {

    internal val normalPattern = ofPattern("dd/MM/yyyy HH:mm:ss")!!
    @Suppress("MemberVisibilityCanBePrivate")
    internal const val resetHour = 9
    @Suppress("MemberVisibilityCanBePrivate")
    internal const val resetMinute = 0
    @Suppress("MemberVisibilityCanBePrivate")
    internal const val resetSecond = 0

    @Suppress("MemberVisibilityCanBePrivate")
    internal fun addDaysToDateTimeString(
            dateTimeString: String,
            days: Int
    ): String {

        return addDaysToDateTimeStringAsDateTime(
                dateTimeString = dateTimeString,
                days = days
        ).format(normalPattern)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    internal fun addDaysToDateTimeStringAsDateTime(
            dateTimeString: String,
            days: Int
    ): LocalDateTime {

        return LocalDateTime.parse(
                dateTimeString,
                normalPattern
        ).plusDays(days.toLong())
    }

    internal fun add1DayToDateTimeString(dateTimeString: String): String {

        return addDaysToDateTimeString(
                dateTimeString = dateTimeString,
                days = 1
        )
    }

    @Suppress("MemberVisibilityCanBePrivate")
    internal fun add1DayToDateTimeStringAsDateTime(dateTimeString: String): LocalDateTime {

        return addDaysToDateTimeStringAsDateTime(
                dateTimeString = dateTimeString,
                days = 1
        )
    }

    internal fun add2DaysToDateTimeString(dateTimeString: String): String {

        return addDaysToDateTimeString(
                dateTimeString = dateTimeString,
                days = 2
        )
    }

    @Suppress("MemberVisibilityCanBePrivate")
    internal fun add2DaysToDateTimeStringAsDateTime(dateTimeString: String): LocalDateTime {

        return addDaysToDateTimeStringAsDateTime(
                dateTimeString = dateTimeString,
                days = 2
        )
    }

    internal fun add1DayWith9ClockTimeToDateTimeString(dateTimeString: String): String {

        return add1DayWith9ClockTimeToDateTimeStringAsDateTime(dateTimeString = dateTimeString).format(normalPattern)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    internal fun add1DayWith9ClockTimeToDateTimeStringAsDateTime(dateTimeString: String): LocalDateTime {

        return add1DayToDateTimeStringAsDateTime(dateTimeString = dateTimeString).withHour(resetHour).withMinute(resetMinute).withSecond(resetSecond)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    internal fun add2DaysWith9ClockTimeToDateTimeStringAsDateTime(dateTimeString: String): LocalDateTime {

        return add2DaysToDateTimeStringAsDateTime(dateTimeString = dateTimeString).withHour(resetHour).withMinute(resetMinute).withSecond(resetSecond)
    }

    internal fun add2DaysWith9ClockTimeToDateTimeString(dateTimeString: String): String {

        return add2DaysWith9ClockTimeToDateTimeStringAsDateTime(dateTimeString = dateTimeString).format(normalPattern)
    }
}
