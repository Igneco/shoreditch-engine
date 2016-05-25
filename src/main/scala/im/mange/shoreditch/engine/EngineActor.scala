package im.mange.shoreditch.engine

import im.mange.shoreditch.engine.hipster.{Step, Check, Action, Script}
import io.shaka.http.TrustAllSslCertificates
import im.mange.shoreditch.api.{CheckResponse, ActionResponse}
import akka.actor.Actor

//TODO: make this a capturing actor so we can see what goes on
//TODO: delegate to an registered impl .. so we can multi use it ... notify pattern
//TODO: ultimately if this is standable, akka actors probably better tha lift actors, but whatever
class EngineActor extends Actor {
  //TODO: probably not the best place for this
  TrustAllSslCertificates

  //TODO: this really needs to be injected somehow ...
  private val clock = RealClock

  def receive = {
//  protected def messageHandler: PartialFunction[Any, Unit] = {
    case s:Script => doScript(s)
    case a:Action => doAction(a)
    case a:Check => doCheck(a)
    //TODO: should the responses be sent back here too .. that way more fine grained event reporting for ui etc
    //TODO: could then use futures
    case x => println("wasnt expecting: " + x) //UnexpectedItemInTheBaggingArea
  }

  //TODO: exception handling
  //TODO: put the queuing in the Script
  private def doScript(script: Script) {
    val validationErrors = script.validate()
    if (validationErrors.isEmpty) {
      script.start()
      if (!script.isCompleted) queue(script.nextStep)
    } else {
      script.abort("Systems could not be validated:" :: validationErrors.toList)
    }
  }

  //TODO: exception handling
  //TODO: put the queuing in the Script
  private def doAction(action: Action) {
    try {
      action.start()(clock)
      action.script.running(action)
      action.script.update(action, action.run(action.script.debug))
    }
    catch {
      case e: Exception => {
        e.printStackTrace()
        action.script.update(action, ActionResponse(List(e.getMessage), None))
        action.script.abort(List("Action aborted due to:" + e.getMessage))
      }
    }

    if (!action.script.isCompleted) queue(action.script.nextStep) else action.script.stop()
  }

  //TODO: exception handling
  //TODO: put the queuing in the Script
  private def doCheck(check: Check) {
    try {
      check.start()(clock)
      check.script.running(check)
      check.script.update(check, check.run(check.script.debug))
    }
    catch {
      //TODO: we should probably do wthat action does above ... still thinking whats best ...
      case e: Exception => {
        e.printStackTrace()
        check.script.update(check, CheckResponse(List(e.getMessage)))
        check.script.abort(List("Check aborted due to:" + e.getMessage))
      }
    }

    if (!check.script.isCompleted) queue(check.script.nextStep) else check.script.stop()

    //TODO: should probably bail after x attempts/time
    //if (check.maxCount < 5) self ! check.copy(maxCount = check.maxCount + 1)
  }

  private def queue(message: Step) {
//    val thisInstance = this

    val waitTime = message match {
      case Check(_, _, failed) => 500 * failed
      case _ => 500
    }

    new Thread(new Runnable() {
      override def run() {
        Thread.sleep(waitTime)
        //    self ! message
        self ! message
        //    println("queued: " + message)
      }
    }).start()
  }
}
