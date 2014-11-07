package im.mange.shoreditch.hipster

import org.joda.time.DateTime
import im.mange.shoreditch.engine.Clock

trait Step {
  //TODO: should be S-prefixed
  val id: Long
  val description: String
  val requiredSystem: String
  val me: String
  //TODO: should this be LocalDateTime?
  private var started: Option[DateTime] = None
  private var completed: Option[DateTime] = None
  var script: Script = _
  def isCompleted = completed.isDefined
  def markCompleted()(implicit clock: Clock) { completed = Some(clock.dateTime) }
  def completedAt = completed
  def startedAt = started
  def mangledDescription: String
  def describe: String
  def start()(implicit clock: Clock) { started = Some(clock.dateTime) }
}
