package accountLedgerCli.to_utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ofPattern

object DateTimeUtils {

    val normalDateTimePattern = ofPattern("dd/MM/yyyy HH:mm:ss")!!
    val normalDatePattern = ofPattern("dd/MM/yyyy")!!

    //TODO : extract into function
    private const val resetHour = 9
    private const val resetMinute = 0
    private const val resetSecond = 0

    fun addDaysToDateTimeString(
        dateTimeString: String,
        days: Int
    ): String {

        return addDaysToDateTimeStringAsDateTime(
            dateTimeString = dateTimeString,
            days = days
        ).format(normalDateTimePattern)
    }

    fun addDaysToDateTimeStringAsDateTime(
        dateTimeString: String,
        days: Int
    ): LocalDateTime {

        return LocalDateTime.parse(
            dateTimeString,
            normalDateTimePattern
        ).plusDays(days.toLong())
    }

    fun add1DayToDateTimeInText(dateTimeInText: String): String {

        return addDaysToDateTimeString(
            dateTimeString = dateTimeInText,
            days = 1
        )
    }

    fun add1DayToDateTimeStringAsDateTime(dateTimeString: String): LocalDateTime {

        return addDaysToDateTimeStringAsDateTime(
            dateTimeString = dateTimeString,
            days = 1
        )
    }

    fun add2DaysToDateTimeString(dateTimeInText: String): String {

        return addDaysToDateTimeString(
            dateTimeString = dateTimeInText,
            days = 2
        )
    }

    fun add2DaysToDateTimeStringAsDateTime(dateTimeString: String): LocalDateTime {

        return addDaysToDateTimeStringAsDateTime(
            dateTimeString = dateTimeString,
            days = 2
        )
    }

    fun add1DayWith9ClockTimeToDateTimeInText(dateTimeInText: String): String {

        return add1DayWith9ClockTimeToDateTimeStringAsDateTime(dateTimeString = dateTimeInText).format(normalDateTimePattern)
    }

    fun add1DayWith9ClockTimeToDateTimeStringAsDateTime(dateTimeString: String): LocalDateTime {

        return add1DayToDateTimeStringAsDateTime(dateTimeString = dateTimeString).withHour(resetHour)
            .withMinute(resetMinute).withSecond(resetSecond)
    }

    fun add2DaysWith9ClockTimeToDateTimeStringAsDateTime(dateTimeString: String): LocalDateTime {

        return add2DaysToDateTimeStringAsDateTime(dateTimeString = dateTimeString).withHour(resetHour)
            .withMinute(resetMinute).withSecond(resetSecond)
    }

    fun add2DaysWith9ClockTimeToDateTimeInText(dateTimeInText: String): String {

        return add2DaysWith9ClockTimeToDateTimeStringAsDateTime(dateTimeString = dateTimeInText).format(normalDateTimePattern)
    }
}
