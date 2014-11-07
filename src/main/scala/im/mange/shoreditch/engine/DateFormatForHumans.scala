package im.mange.shoreditch.engine

import org.joda.time.DateTimeZone._
import org.joda.time.format.DateTimeFormat._
import org.joda.time.format.PeriodFormatterBuilder
import org.joda.time.{Interval, LocalDateTime, Period}

object DateFormatForHumans {
  private val standardTimeFormat = forPattern("HH:mm:ss").withZone(UTC)
  private val standardDateTimeFormat = forPattern("HH:mm:ss EEE dd MMM yyyy").withZone(UTC)
//  private val todayDateTimeFormat = forPattern("HH:mm:ss 'Today'").withZone(UTC)
  private val todayDateTimeFormat = forPattern("HH:mm:ss").withZone(UTC)
  private val thisYearDateTimeFormat = forPattern("HH:mm:ss EEE dd MMM").withZone(UTC)

  private val periodFormat = new PeriodFormatterBuilder()
    .appendHours()
    .appendSuffix("h")
    .appendSeparator(", ")
    .printZeroRarelyLast()
    .appendMinutes()
    .appendSuffix("m")
    .appendSeparator(", ")
    .appendSeconds()
    .appendSuffix("s")
    .toFormatter()

  def format(when: LocalDateTime)(implicit clock: Clock) = formatFor(when).print(when)
  //TODO: terrible name
  def ago(when: LocalDateTime)(implicit clock: Clock) = periodFormat.print(new Interval(when.toDateTime, today.toDateTime).toPeriod)
  def ago(period: Period) = periodFormat.print(period)
  def timeNow(implicit clock: Clock) = standardTimeFormat.print(today)
  def formattedNow(implicit clock: Clock) = format(today)

  private def formatFor(when: LocalDateTime)(implicit clock: Clock) = {
    if (isToday(when)) todayDateTimeFormat
    else if (isThisYear(when)) thisYearDateTimeFormat
    else standardDateTimeFormat
  }

  private def isToday(when: LocalDateTime)(implicit clock: Clock) = isSameDay(when, today)
  private def isThisYear(when: LocalDateTime)(implicit clock: Clock) = when.isAfter(today.minusYears(1))

  private def isSameDay(when: LocalDateTime, as: LocalDateTime) =
    when.getYear == as.getYear &&
      when.getMonthOfYear == as.getMonthOfYear &&
      when.getDayOfMonth == as.getDayOfMonth

  private def today(implicit clock: Clock) = clock.localDateTime
}