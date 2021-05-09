package accountLedgerCli.to_utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ofPattern

object DateTimeUtils {

    val normalPattern = ofPattern("dd/MM/yyyy HH:mm:ss")!!

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
        ).format(normalPattern)
    }

    fun addDaysToDateTimeStringAsDateTime(
        dateTimeString: String,
        days: Int
    ): LocalDateTime {

        return LocalDateTime.parse(
            dateTimeString,
            normalPattern
        ).plusDays(days.toLong())
    }

    fun add1DayToDateTimeString(dateTimeString: String): String {

        return addDaysToDateTimeString(
            dateTimeString = dateTimeString,
            days = 1
        )
    }

    fun add1DayToDateTimeStringAsDateTime(dateTimeString: String): LocalDateTime {

        return addDaysToDateTimeStringAsDateTime(
            dateTimeString = dateTimeString,
            days = 1
        )
    }

    fun add2DaysToDateTimeString(dateTimeString: String): String {

        return addDaysToDateTimeString(
            dateTimeString = dateTimeString,
            days = 2
        )
    }

    fun add2DaysToDateTimeStringAsDateTime(dateTimeString: String): LocalDateTime {

        return addDaysToDateTimeStringAsDateTime(
            dateTimeString = dateTimeString,
            days = 2
        )
    }

    fun add1DayWith9ClockTimeToDateTimeString(dateTimeString: String): String {

        return add1DayWith9ClockTimeToDateTimeStringAsDateTime(dateTimeString = dateTimeString).format(normalPattern)
    }

    fun add1DayWith9ClockTimeToDateTimeStringAsDateTime(dateTimeString: String): LocalDateTime {

        return add1DayToDateTimeStringAsDateTime(dateTimeString = dateTimeString).withHour(resetHour)
            .withMinute(resetMinute).withSecond(resetSecond)
    }

    fun add2DaysWith9ClockTimeToDateTimeStringAsDateTime(dateTimeString: String): LocalDateTime {

        return add2DaysToDateTimeStringAsDateTime(dateTimeString = dateTimeString).withHour(resetHour)
            .withMinute(resetMinute).withSecond(resetSecond)
    }

    fun add2DaysWith9ClockTimeToDateTimeString(dateTimeString: String): String {

        return add2DaysWith9ClockTimeToDateTimeStringAsDateTime(dateTimeString = dateTimeString).format(normalPattern)
    }
}
