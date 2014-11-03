package im.mange.shoreditch.hipster

import org.joda.time.DateTime
import im.mange.shoreditch.engine.Clock

trait Step {
  val id: Long
  val description: String
  val requiredSystem: String
  val me: String
  //TODO: should this be LocalDateTime?
  private var completed: Option[DateTime] = None
  var script: Script = _
  def isCompleted = completed.isDefined
  def markCompleted()(implicit clock: Clock) { completed = Some(clock.dateTime) }
  def completedAt = completed
  def mangledDescription: String
  def describe: String
}
