package im.mange.shoreditch.engine.listener

import im.mange.shoreditch.engine.{Filepath, Json, ScriptEventListener}
import im.mange.shoreditch.engine.hipster._
import org.joda.time.{DateTime, LocalDateTime}
import net.liftweb.json._
import org.joda.time.DateTimeZone._
import im.mange.shoreditch.engine.hipster.VersionedService
import im.mange.shoreditch.engine.hipster.Action
import im.mange.shoreditch.engine.hipster.Check
import scala.reflect.io.Directory
import im.mange.shoreditch.engine.model.{TestRunReport, Test}

//TODO: allow rendering as text/html

case class TestRunReportListener(test: Test, outputDirectory: String) extends ScriptEventListener {
  private var services: List[VersionedService] = Nil

  override def beforeStarted(script: Script, testId: String) {}
  override def success(check: Check) { writeReport(check.script/*, Nil*/) }
  override def failure(check: Check, reasons: List[String]) { writeReport(check.script/*, reasons*/) }
  override def success(action: Action) { writeReport(action.script/*, Nil*/) }
  override def failure(action: Action, reasons: List[String]) { writeReport(action.script/*, reasons*/) }
  override def running(step: Step) {}
  override def started(when: LocalDateTime, script: Script) { writeReport(script/*, Nil*/) }

  //TODO: we should know why we failed.. can probably fish it out form the first !completed step
  //TODO: also handle aborted by user ... aborted should be an option instead of a boolean perhaps ...
  override def stopped(when: LocalDateTime, script: Script) {
    writeReport(script)
  }

  private def writeReport(script: Script) {
    val jsonAst = Json.serialise(TestRunReport.create(test, script, services))

    //TODO: we should delegate to the TestRunRegistry for this ...
    val directory = Directory(outputDirectory)
    if (!directory.exists) directory.createDirectory(force = true)
    Filepath(outputDirectory + "/" + script.testRunId.get + ".json").write(pretty(render(jsonAst)))
  }

  override def validated(testRunId: String, versionedServices: List[VersionedService]) {
    services = versionedServices
  }
}