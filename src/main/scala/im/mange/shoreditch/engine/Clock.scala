package im.mange.shoreditch.engine

import org.joda.time.{DateTime, LocalDateTime, LocalDate}

//TODO: use the one in little instead
trait Clock2 {
  def localDate: LocalDate
  def localDateTime: LocalDateTime
  def date: LocalDate
  def dateTime: DateTime
}
