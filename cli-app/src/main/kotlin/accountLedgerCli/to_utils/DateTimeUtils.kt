package accountLedgerCli.to_utils

import accountLedgerCli.to_models.IsOkModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ofPattern
import java.time.format.DateTimeParseException

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
    fun addDaysToDateTimeText(

        dateTimeText: String,
        days: Int

    ): String {

        return addDaysToDateTimeTextAsDateTime(

            dateTimeText = dateTimeText,
            days = days

        ).format(normalDateTimePattern)
    }

    @JvmStatic
    fun subtractDaysToDateTimeText(

        dateTimeText: String,
        days: Int

    ): String {

        return subtractDaysToDateTimeTextAsDateTime(

            dateTimeText = dateTimeText,
            days = days

        ).format(normalDateTimePattern)
    }

    @JvmStatic
    fun add5MinutesToDateTimeInText(

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
    fun addDaysToDateTimeTextAsDateTime(

        dateTimeText: String,
        days: Int

    ): LocalDateTime {

        return LocalDateTime.parse(
            dateTimeText,
            normalDateTimePattern
        ).plusDays(days.toLong())
    }

    @JvmStatic
    fun subtractDaysToDateTimeTextAsDateTime(

        dateTimeText: String,
        days: Int

    ): LocalDateTime {

        return LocalDateTime.parse(
            dateTimeText,
            normalDateTimePattern
        ).minusDays(days.toLong())
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

        return addDaysToDateTimeText(

            dateTimeText = dateTimeInText,
            days = 1
        )
    }

    @JvmStatic
    fun subtract1DayToDateTimeInText(dateTimeInText: String): String {

        return subtractDaysToDateTimeText(

            dateTimeText = dateTimeInText,
            days = 1
        )
    }

    @JvmStatic
    fun add1DayToDateTimeStringAsDateTime(dateTimeText: String): LocalDateTime {

        return addDaysToDateTimeTextAsDateTime(

            dateTimeText = dateTimeText,
            days = 1
        )
    }

    @JvmStatic
    fun add2DaysToDateTimeInText(dateTimeInText: String): String {

        return addDaysToDateTimeText(

            dateTimeText = dateTimeInText,
            days = 2
        )
    }

    @JvmStatic
    fun subtract2DaysToDateTimeInText(dateTimeInText: String): String {

        return subtractDaysToDateTimeText(

            dateTimeText = dateTimeInText,
            days = 2
        )
    }

    @JvmStatic
    fun add2DaysToDateTimeStringAsDateTime(dateTimeText: String): LocalDateTime {

        return addDaysToDateTimeTextAsDateTime(

            dateTimeText = dateTimeText,
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

        return add1DayToDateTimeStringAsDateTime(dateTimeText = dateTimeString).withHour(resetHour)
            .withMinute(resetMinute).withSecond(resetSecond)
    }

    @JvmStatic
    fun add2DaysWith9ClockTimeToDateTimeStringAsDateTime(dateTimeString: String): LocalDateTime {

        return add2DaysToDateTimeStringAsDateTime(dateTimeText = dateTimeString).withHour(resetHour)
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

    fun normalDateTimeTextToDateTime(

        normalDateTimeText: String,
        conversionSuccessActions: () -> Unit = fun() {},
        conversionFailureActions: () -> Unit = fun() {}

    ): IsOkModel<LocalDateTime> {

        return dateTimeTextToDateTime(dateTimeText = normalDateTimeText, dateTimeTextPattern = normalDateTimePattern)
    }

    fun dateTimeTextToDateTime(

        dateTimeText: String,
        dateTimeTextPattern: DateTimeFormatter,
        conversionSuccessActions: () -> Unit = fun() {},
        conversionFailureActions: () -> Unit = fun() {}

    ): IsOkModel<LocalDateTime> {

        return try {

            val result: LocalDateTime = LocalDateTime.parse(dateTimeText, dateTimeTextPattern)
            conversionSuccessActions.invoke()
            IsOkModel(isOK = true, data = result)

        } catch (e: DateTimeParseException) {

            println("Date Error : ${e.localizedMessage}")
            conversionFailureActions.invoke()
            IsOkModel(isOK = false)
        }
    }
}
