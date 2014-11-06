package im.mange.shoreditch.engine.listener

import im.mange.shoreditch.engine.{Filepath, Json, ScriptEventListener}
import im.mange.shoreditch.hipster._
import org.joda.time.{DateTime, LocalDateTime}
import net.liftweb.json._
import org.joda.time.DateTimeZone._
import im.mange.shoreditch.hipster.VersionedService
import im.mange.shoreditch.hipster.Action
import im.mange.shoreditch.hipster.Check
import im.mange.shoreditch.engine.registry.Test
import scala.reflect.io.Directory

//TODO: allow rendering as text/html

//TODO: this is probably what we should send to the comet actor (i.e. what ScriptEventListener should use)
//TODO: indicate missing=true
case class ServiceSummary(alias: String, env: Option[String], version: Option[String])
//TODO: I should hold failures
case class StepSummary(description: String, completed: Option[DateTime])
case class ScriptSummary(description: String, steps: Seq[StepSummary])
case class TestRunReport(testId: Long, testRunId: Long, started: Option[DateTime], completed: Option[DateTime],
                        services: List[ServiceSummary], script: ScriptSummary, failures: List[String])
//TODO: add who ran it
//TODO: host these in a service so we can be fetched by test run id

//next up TestSuite and TestSuiteRun ....

case class TestRunReportListener(test: Test, outputDirectory: String = "registry/testruns") extends ScriptEventListener {
  private var services: List[VersionedService] = Nil

  override def beforeStarted(script: Script) {}
  override def success(check: Check) { writeReport(check.script, Nil) }
  override def failure(check: Check, reasons: List[String]) { writeReport(check.script, reasons) }
  override def success(action: Action) { writeReport(action.script, Nil) }
  override def failure(action: Action, reasons: List[String]) { writeReport(action.script, reasons) }
  override def running(step: Step) {}
  override def started(when: LocalDateTime, script: Script) { writeReport(script, Nil) }

  //TODO: we should know why we failed.. can probably fish it out form the first !completed step
  //TODO: also handle aborted by user ... aborted should be an option instead of a boolean perhaps ...
  override def stopped(when: LocalDateTime, script: Script) {
    writeReport(script, List(script.abortedBecause.getOrElse("?????")))
  }

  private def writeReport(script: Script, reasons: List[String]) {
    val jsonAst = Json.serialise(TestRunReport(
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
        script.steps.map(st => StepSummary(st.mangledDescription, st.completedAt))
      ),
      if (script.successful) Nil else reasons
    ))

    //TODO: we should delegate to the TestRunRegistry for this ...
    val directory = Directory(outputDirectory)
    if (!directory.exists) directory.createDirectory(force = true)
    Filepath(outputDirectory + "/TR" + script.testRunId.get + ".json").write(pretty(render(jsonAst)))
  }

  override def validated(testRunId: Long, versionedServices: List[VersionedService]) {
    services = versionedServices
  }
}