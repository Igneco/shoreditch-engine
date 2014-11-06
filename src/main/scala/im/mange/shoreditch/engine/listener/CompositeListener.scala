package im.mange.shoreditch.engine.listener

import org.joda.time.LocalDateTime
import im.mange.shoreditch.hipster._
import im.mange.shoreditch.engine.ScriptEventListener
import im.mange.shoreditch.hipster.VersionedService
import im.mange.shoreditch.hipster.Check
import im.mange.shoreditch.hipster.Action

case class CompositeListener(listeners: List[ScriptEventListener]) extends ScriptEventListener {
  override def beforeStarted(script: Script) { listeners.foreach(_.beforeStarted(script)) }
  def validated(testRunId: String, versionedServices: List[VersionedService]) { listeners.foreach(_.validated(testRunId, versionedServices)) }
  def started(when: LocalDateTime, script: Script) { listeners.foreach(_.started(when, script)) }
  def stopped(when: LocalDateTime, script: Script) { listeners.foreach(_.stopped(when, script)) }
  def running(step: Step) { listeners.foreach(_.running(step)) }
  def failure(action: Action, reasons: List[String]) { listeners.foreach(_.failure(action, reasons)) }
  def success(action: Action) { listeners.foreach(_.success(action)) }
  def failure(check: Check, reasons: List[String]) { listeners.foreach(_.failure(check, reasons)) }
  def success(check: Check) { listeners.foreach(_.success(check)) }
}