package im.mange.shoreditch.engine

import org.joda.time.{DateTime, LocalDateTime, LocalDate}

//TODO: use th eone in little instead
trait Clock {
  def localDate: LocalDate
  def localDateTime: LocalDateTime
  def date: LocalDate
  def dateTime: DateTime
}
