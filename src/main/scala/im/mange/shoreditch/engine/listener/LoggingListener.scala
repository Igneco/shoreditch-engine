package im.mange.shoreditch.engine.listener

import org.joda.time.{Interval, LocalDateTime}
import im.mange.shoreditch.engine.hipster._
import im.mange.shoreditch.engine.{Clock, ScriptEventListener}
import im.mange.shoreditch.engine.hipster.VersionedService
import im.mange.shoreditch.engine.hipster.Check
import im.mange.shoreditch.engine.hipster.Action
import im.mange.little.date.DateFormatForHumans
import im.mange.little.clock.RealClock

case class LoggingListener()(implicit clock: Clock) extends ScriptEventListener {
  def beforeStarted(script: Script, testId: String) {
    //TODO: should have Test Id
    println("\n### Running: " + testId + " - " + script.name)
  }

  def validated(testRunId: String, versionedServices: List[VersionedService]) {
    val prefix = if (!versionedServices.forall(_.offering.isDefined)) "Validation failed"  else "Validated with"
    println("### " + prefix + ": " + versionedServices.map(v => v.alias + " " + v.offering.fold("Not Available")(_.version) + " (" + v.env.fold("Not Available")(_.toString) + ")").mkString(", ") )
  }

  def started(when: LocalDateTime, script: Script) {
    //TODO: format more nicely
    println("### Started at: " + when)
  }

  def stopped(when: LocalDateTime, script: Script) {
    //TODO: format more nicely
    //TODO: yes and use the jodas duration gubbins
    println("### Stopped at: " + when + ", duration: " + script.duration + " millis")
  }

  def running(step: Step) {}

  def failure(action: Action, failures: List[String]) {
    println("### Failed: " + action + " with: " + failures.headOption.getOrElse("No reason supplied"))
  }

  def success(action: Action) { print(action, None) }
  def failure(check: Check, reasons: List[String]) { print(check, reasons.headOption) }
  def success(check: Check) { print(check, None) }

  private def print(step: Step, context: Option[String]) {
    val took = if (step.isCompleted) Some(new Interval(step.startedAt.get, step.completedAt.get).toPeriod.getMillis + "ms") else None
    println(new DateFormatForHumans(RealClock).timeNow + " " + step.describe + context.fold("")(" - " + _) + took.fold("")(" (" + _ + ")"))
  }
}
