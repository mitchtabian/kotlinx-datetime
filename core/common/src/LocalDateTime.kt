/*
 * Copyright 2019-2020 JetBrains s.r.o.
 * Use of this source code is governed by the Apache 2.0 License that can be found in the LICENSE.txt file.
 */

package kotlinx.datetime

import kotlinx.datetime.serializers.LocalDateTimeIso8601Serializer
import kotlinx.serialization.Serializable

@Serializable(with = LocalDateTimeIso8601Serializer::class)
public expect class LocalDateTime : Comparable<LocalDateTime> {
    public companion object {

        /**
         * Parses a string that represents a date/time value in ISO-8601 format including date and time components
         * but without any time zone component and returns the parsed [LocalDateTime] value.
         *
         * Examples of date/time in ISO-8601 format:
         * - `2020-08-30T18:43`
         * - `2020-08-30T18:43:00`
         * - `2020-08-30T18:43:00.500`
         * - `2020-08-30T18:43:00.123456789`
         *
         * @throws IllegalArgumentException if the text cannot be parsed or the boundaries of [LocalDateTime] are
         * exceeded.
         */
        public fun parse(isoString: String): LocalDateTime

        internal val MIN: LocalDateTime
        internal val MAX: LocalDateTime
    }

    /**
     * Constructs a [LocalDateTime] instance from the given date and time components.
     *
     * The components [monthNumber] and [dayOfMonth] are 1-based.
     *
     * The supported ranges of components:
     * - [year] the range is platform dependent, but at least is enough to represent dates of all instants between
     *          [Instant.DISTANT_PAST] and [Instant.DISTANT_FUTURE]
     * - [monthNumber] `1..12`
     * - [dayOfMonth] `1..31`, the upper bound can be less, depending on the month
     * - [hour] `0..23`
     * - [minute] `0..59`
     * - [second] `0..59`
     * - [nanosecond] `0..999_999_999`
     *
     * @throws IllegalArgumentException if any parameter is out of range, or if [dayOfMonth] is invalid for the given [monthNumber] and
     * [year].
     */
    public constructor(year: Int, monthNumber: Int, dayOfMonth: Int, hour: Int, minute: Int, second: Int = 0, nanosecond: Int = 0)

    /**
     * Constructs a [LocalDateTime] instance from the given date and time components.
     *
     * The supported ranges of components:
     * - [year] the range is platform dependent, but at least is enough to represent dates of all instants between
     *          [Instant.DISTANT_PAST] and [Instant.DISTANT_FUTURE]
     * - [month] all values of the [Month] enum
     * - [dayOfMonth] `1..31`, the upper bound can be less, depending on the month
     * - [hour] `0..23`
     * - [minute] `0..59`
     * - [second] `0..59`
     * - [nanosecond] `0..999_999_999`
     *
     * @throws IllegalArgumentException if any parameter is out of range, or if [dayOfMonth] is invalid for the given [month] and
     * [year].
     */
    public constructor(year: Int, month: Month, dayOfMonth: Int, hour: Int, minute: Int, second: Int = 0, nanosecond: Int = 0)

    /** Returns the year component of the date. */
    public val year: Int
    /** Returns the number-of-month (1..12) component of the date. */
    public val monthNumber: Int
    /** Returns the month ([Month]) component of the date. */
    public val month: Month
    /** Returns the day-of-month component of the date. */
    public val dayOfMonth: Int
    /** Returns the day-of-week component of the date. */
    public val dayOfWeek: DayOfWeek
    /** Returns the day-of-year component of the date. */
    public val dayOfYear: Int
    /** Returns the hour-of-day time component of this date/time value. */
    public val hour: Int
    /** Returns the minute-of-hour time component of this date/time value. */
    public val minute: Int
    /** Returns the second-of-minute time component of this date/time value. */
    public val second: Int
    /** Returns the nanosecond-of-second time component of this date/time value. */
    public val nanosecond: Int

    /** Returns the date part of this date/time value. */
    public val date: LocalDate

    /**
     * Compares `this` date/time value with the [other] date/time value.
     * Returns zero if this value is equal to the other,
     * a negative number if this value represents earlier civil time than the other,
     * and a positive number if this value represents later civil time than the other.
     */
    public override operator fun compareTo(other: LocalDateTime): Int

    /**
     * Converts this date/time value to the ISO-8601 string representation.
     *
     * @see LocalDateTime.parse
     */
    public override fun toString(): String
}

/**
 * Converts this string representing a date/time value in ISO-8601 format including date and time components
 * but without any time zone component to a [LocalDateTime] value.
 *
 * See [LocalDateTime.parse] for examples of date/time string representations.
 *
 * @throws IllegalArgumentException if the text cannot be parsed or the boundaries of [LocalDateTime] are exceeded.
 */
public fun String.toLocalDateTime(): LocalDateTime = LocalDateTime.parse(this)

/**
 * Convert a LocalDatetime into a more readable String version.
 *
 * YESTERDAY format: "Yesterday at 3pm"
 * TODAY format: "Today at 1am"
 * TOMORROW format: "Tomorrow at 7pm"
 * Everything else: "January 19"
 */
public fun LocalDateTime.humanizeLocalDateTime(): String {
    val sb = StringBuilder()
    date?.run {
        val hour = if(this.hour > 12){
            (this.hour - 12).toString() + "pm"
        }
        else{
            if(this.hour != 0) this.hour.toString() + "am" else "midnight"
        }
        val today = now()
        val tomorrow = Clock.System.now().plus(1, DateTimeUnit.DAY, TimeZone.UTC).toLocalDateTime(TimeZone.UTC)
        if(this.date == today.date){
            sb.append("Today at $hour")
        }
        else if (this.date == tomorrow.date){
            sb.append("Tomorrow at $hour")
        }
        else{
            sb.append(this.date.month.name.lowercase() + " ${this.date.dayOfMonth}")
        }
    }?: sb.append("Unknown")
    return sb.toString()
}