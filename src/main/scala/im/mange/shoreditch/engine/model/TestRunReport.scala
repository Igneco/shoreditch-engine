package im.mange.shoreditch.engine.model

import org.joda.time.DateTime
import im.mange.shoreditch.engine.hipster.Script
import org.joda.time.DateTimeZone._
import im.mange.shoreditch.engine.hipster.VersionedService
import scala.Some

object TestRunReport {
  def create(test: Test, script: Script, services: List[VersionedService]) =
    TestRunReport(
      test.id, script.testRunId.get,
      script.startedAt.map(_.toDateTime(UTC)), script.completedAt.map(_.toDateTime(UTC)),
      services.map(vs => {
        vs.offering match {
          case Some(o) => ServiceSummary(vs.alias, Some(o.env), Some(o.version))
          case None => ServiceSummary(vs.alias, None, None)
        }
      }),
      ScriptSummary(
        test.name,
        script.steps.map(st => StepSummary(st.mangledDescription, st.startedAt, st.completedAt))
      ),
      if (script.successful) Nil else script.abortedBecause.fold(List("?????"))(r => r)
    )
}

//TODO: this is probably what we should send to the comet actor (i.e. what ScriptEventListener should use)
//TODO: indicate missing=true
case class ServiceSummary(alias: String, env: Option[String], version: Option[String])
//TODO: I should hold failures
case class StepSummary(description: String, started: Option[DateTime], completed: Option[DateTime])
case class ScriptSummary(description: String, steps: Seq[StepSummary])

case class TestRunReport(testId: String, testRunId: String, started: Option[DateTime], completed: Option[DateTime],
                         services: List[ServiceSummary], script: ScriptSummary, failures: List[String]) {

  def successful = failures.isEmpty
}
//TODO: add who ran it
//TODO: host these in a service so we can be fetched by test run id

