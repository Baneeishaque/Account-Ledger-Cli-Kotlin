package accountLedgerCli.to_utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ofPattern

object DateTimeUtils {

    @JvmStatic
    val normalDateTimePattern: DateTimeFormatter = ofPattern("dd/MM/yyyy HH:mm:ss")!!

    @JvmStatic
    val normalDatePattern: DateTimeFormatter = ofPattern("dd/MM/yyyy")!!

    //TODO : extract into function
    private const val resetHour = 9
    private const val resetMinute = 0
    private const val resetSecond = 0

    @JvmStatic
    fun addDaysToDateTimeString(

        dateTimeString: String,
        days: Int

    ): String {

        return addDaysToDateTimeStringAsDateTime(

            dateTimeString = dateTimeString,
            days = days

        ).format(normalDateTimePattern)
    }

    @JvmStatic
    fun add5MinutesToDateTimeString(

        dateTimeInText: String

    ): String {

        return addMinutesToDateTimeString(

            dateTimeString = dateTimeInText,
            minutes = 5
        )
    }

    @JvmStatic
    fun addMinutesToDateTimeString(

        dateTimeString: String,
        minutes: Int

    ): String {

        return addMinutesToDateTimeStringAsDateTime(

            dateTimeString = dateTimeString,
            minutes = minutes

        ).format(normalDateTimePattern)
    }

    @JvmStatic
    fun addDaysToDateTimeStringAsDateTime(

        dateTimeString: String,
        days: Int

    ): LocalDateTime {

        return LocalDateTime.parse(
            dateTimeString,
            normalDateTimePattern
        ).plusDays(days.toLong())
    }

    @JvmStatic
    fun addMinutesToDateTimeStringAsDateTime(

        dateTimeString: String,
        minutes: Int

    ): LocalDateTime {

        return LocalDateTime.parse(
            dateTimeString, normalDateTimePattern
        ).plusMinutes(minutes.toLong())
    }

    @JvmStatic
    fun subtractSecondsFromMySqlDateTimeTextAsDateTime(

        dateTimeText: String,
        seconds: Int

    ): LocalDateTime {

        return LocalDateTime.parse(
            dateTimeText, MysqlUtils.mysqlDateTimePattern
        ).minusSeconds(seconds.toLong())
    }

    @JvmStatic
    fun subtractSecondsFromMySqlDateTimeText(

        dateTimeText: String,
        seconds: Int

    ): String {

        return subtractSecondsFromMySqlDateTimeTextAsDateTime(

            dateTimeText = dateTimeText,
            seconds = seconds

        ).format(MysqlUtils.mysqlDateTimePattern)
    }

    @JvmStatic
    fun subtract1SecondFromMySqlDateTimeText(

        dateTimeText: String

    ): String {

        return subtractSecondsFromMySqlDateTimeText(

            dateTimeText = dateTimeText,
            seconds = 1
        )
    }

    @JvmStatic
    fun add1DayToDateTimeInText(dateTimeInText: String): String {

        return addDaysToDateTimeString(
            dateTimeString = dateTimeInText,
            days = 1
        )
    }

    @JvmStatic
    fun add1DayToDateTimeStringAsDateTime(dateTimeString: String): LocalDateTime {

        return addDaysToDateTimeStringAsDateTime(
            dateTimeString = dateTimeString,
            days = 1
        )
    }

    @JvmStatic
    fun add2DaysToDateTimeString(dateTimeInText: String): String {

        return addDaysToDateTimeString(
            dateTimeString = dateTimeInText,
            days = 2
        )
    }

    @JvmStatic
    fun add2DaysToDateTimeStringAsDateTime(dateTimeString: String): LocalDateTime {

        return addDaysToDateTimeStringAsDateTime(
            dateTimeString = dateTimeString,
            days = 2
        )
    }

    @JvmStatic
    fun add1DayWith9ClockTimeToDateTimeInText(dateTimeInText: String): String {

        return add1DayWith9ClockTimeToDateTimeStringAsDateTime(dateTimeString = dateTimeInText).format(
            normalDateTimePattern
        )
    }

    @JvmStatic
    fun add1DayWith9ClockTimeToDateTimeStringAsDateTime(dateTimeString: String): LocalDateTime {

        return add1DayToDateTimeStringAsDateTime(dateTimeString = dateTimeString).withHour(resetHour)
            .withMinute(resetMinute).withSecond(resetSecond)
    }

    @JvmStatic
    fun add2DaysWith9ClockTimeToDateTimeStringAsDateTime(dateTimeString: String): LocalDateTime {

        return add2DaysToDateTimeStringAsDateTime(dateTimeString = dateTimeString).withHour(resetHour)
            .withMinute(resetMinute).withSecond(resetSecond)
    }

    @JvmStatic
    fun add2DaysWith9ClockTimeToDateTimeInText(dateTimeInText: String): String {

        return add2DaysWith9ClockTimeToDateTimeStringAsDateTime(dateTimeString = dateTimeInText).format(
            normalDateTimePattern
        )
    }

    @JvmStatic
    fun getCurrentDateTimeText(): String {

        return LocalDateTime.now().format(normalDateTimePattern)
    }
}
