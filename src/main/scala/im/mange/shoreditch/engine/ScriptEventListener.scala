package im.mange.shoreditch.engine

import org.joda.time.LocalDateTime
import im.mange.shoreditch.hipster._
import im.mange.shoreditch.hipster.Action
import im.mange.shoreditch.hipster.Check

//TODO: we should notify if the script ultimately passed or failed (i.e was aborted after n attempts
trait ScriptEventListener {
 //TODO: not sure about script in here, find a better way ...
 // .. maybe some lightweight ScriptState thing

 def beforeStarted(script: Script): Unit
 def validated(testRunId: String, versionedServices: List[VersionedService]): Unit
 def started(when: LocalDateTime, script: Script): Unit
 def stopped(when: LocalDateTime, script: Script): Unit
 def running(step: Step): Unit

 //TODO: do we need all this versions, can it not just be based on Step?
 def failure(action: Action, reasons: List[String]): Unit
 def success(action: Action): Unit
 def failure(check: Check, reasons: List[String]): Unit
 def success(check: Check): Unit
}