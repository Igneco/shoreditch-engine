package im.mange.shoreditch.engine.hipster

import org.joda.time.{DateTime, LocalDateTime}
import im.mange.shoreditch.api.{CheckResponse, ActionResponse}
import im.mange.shoreditch.engine.hipster
import im.mange.shoreditch.engine.{Clock, systems, ScriptEventListener}
import im.mange.shoreditch.engine.services.{Service, Services}
import im.mange.shoreditch.engine.systems.System
import im.mange.shoreditch.engine.registry.TestRunIdCounter

//TODO: nextStep ... unless failed
//TODO: hasFailed ...
//TODO: store plan vs actual ...?
//TODO: store last return value
//(probably a map of let x = y) ... context
//TODO: action will need to know it's script
//TODO: I think Script should have a run() that takes the services ...
//and construct it with it's queue (aka actor)
//TODO: everything needs to sad-casing
//TODO: two phase parsing on script -> xlines -> Steps
//TODO: have a validate method before starting (both script and services)
//TODO: consider longer sleeps for checks that have taken too long ...
//TODO@ tidy up the naming completed/incomplete etc
//TODO: need a ScriptBook (aka Suite) .. and time how long that takes

//TODO: do we need a ScriptRunner which hold the services and listeners and coordinaters the test/testpac
//TODO: should testRunId be a case class of the test id and the run id ...
//TODO: we need an if (debug)
object Script {
  def parse(engineEventListener: ScriptEventListener, services: Services, lines: Seq[String], name: String)(implicit clock: Clock) = {
    val script = hipster.Script(engineEventListener, services, Nil, name)
    val stepFactory = StepFactory()
    val steps = lines.map(l => {
      val step = stepFactory.create(l)
      step.script = script
      step
    })
    script.steps = steps
    script
  }
}

case class AvailableServices(system: System, services: Seq[Service])
case class AvailableSystems(alias: String, systems: Seq[System])
//TODO: this is not a good name anymore
case class VersionedService(alias: String, env: Option[String], offering: Option[ServiceOffering])
case class ServiceOffering(env: String, version: String, validated: Boolean)

//TODO: surely some of these vars can be private at least
//TODO: store the testRunId = TestRunIdCounter.next
case class Script(engineEventListener: ScriptEventListener, private val services: Services, var steps: Seq[Step] = Nil, name: String)(implicit clock: Clock) {
  //TODO: provide methods for getting/checking vars/aborting, so we can hide these again
  var context = Map[String, String]()
  private var aborted: Option[String] = None
  private var started: Option[LocalDateTime] = None
  private var stopped: Option[LocalDateTime] = None
  private var validatedServices: Option[List[VersionedService]] = None

  //TODO: this is all a bit nasty ... it's like the script has a different mode ... RunningScript or something
  var testRunId: Option[String] = None

  def hasStarted = started.isDefined
  def isCompleted = isAborted || incompleteSteps.isEmpty
  def nextStep = incompleteSteps.head
  def isCurrentlyRunningStep(s: Step) = hasStarted && !isCompleted && nextStep == s
  def startedAt = started
  def completedAt = stopped
  def abortedBecause = aborted
  def versionedServices = validatedServices

  private def isAborted = aborted.isDefined

  //TODO: .... clearly there is a state pattern in here ....

  def systemUrlFor(serviceKey: String) = {
    if (!services.discovered.contains(serviceKey)) println("Cant find: " + serviceKey + " in " + services.discovered.keys)
    services.discovered(serviceKey)
  }

  def duration = {
    //TODO: get the joda's in
    if (started.isEmpty) 0L
    //TODO: should be using the clock()
    else if (stopped.isEmpty) new LocalDateTime().toDate.getTime - started.get.toDate.getTime
    else stopped.get.toDate.getTime - started.get.toDate.getTime
  }
  
  def successful = isCompleted && !isAborted && hasStarted

  def validate() = {
    val debug = false
    val requiredSystemAliases = steps.map(_.requiredSystem).distinct.sorted
//    println("### requiredSystemAliases: " + requiredSystemAliases)

//    val availableSystems = services.systems.map(s => AvailableSystems(s.alias, services.systems.filter(_.alias == s.alias)))

//    val availableSystems = requiredSystemAliases.map(
//      rsa => AvailableSystems(rsa, services.systems.filter(_.alias == rsa))
//    )
//    println("### availableSystems: " + availableSystems.map(_.alias))

    //TODO: fail if not matching ...
//    println("### got all: " + availableSystems.map(_.alias).toSet.subsetOf(requiredSystemAliases.toSet))

    val availableSystemsForRequiredAliases = requiredSystemAliases.map(s => AvailableSystems(s, services.systems.filter(_.alias == s)))

//    println("### requiredSystems: " + availableSystemsForRequiredAliases)

    val availableServices = availableSystemsForRequiredAliases.map(as => {
//      println("### as: " + as)
      as.systems.map(sys => AvailableServices(sys, services.raw.filter(_.system.alias == as.alias)))
    }
    ).flatten

//    println("### availableServices: " + availableServices)
//    println("### availableServices: " + availableServices.map(_.system.alias))

    val requiredVersionedServices = availableServices.map(as =>
//      as match {
//        case Some(s) => VersionedService(as.system, s.system.env, s.metaData.version, true)
//        case None => VersionedService(as.system, s.system.env, s.metaData.version, false)
//      }
      //TODO: clearly more work required here for multiple system implementations
      VersionedService(as.system.alias, Some(as.system.env), Some(ServiceOffering(as.system.env, as.services.headOption.fold("Not Available")(_.metaData.version), !as.services.isEmpty)))
    ).toList

    //TODO: we must also validate every single action and check ...

    //validation: failures []
    //services for steps ...
    steps.map(s => {
      //system is the the thing under test
      //which systems can run my step
      //(1) do we have all the required systems
      val availableSystems = services.systems.filter(_.alias == s.requiredSystem)
      if (debug) println("> step " + s.id + " needs " + s.requiredSystem + " we found candidate " + availableSystems.map(_.env))

      //(2) which of the systems available can run this step
      availableSystems.map(as => {
        if (debug) println(s"does ${as.alias} ${as.env} support " + s.me + " - ")
        if (debug) println(services.discovered)
        if (debug) println(services.raw)
      })
    })

    val missingSystemAliases = requiredSystemAliases.toSet.diff(requiredVersionedServices.map(_.alias).toSet).toList
    val missingVersionServices = missingSystemAliases.map(a => VersionedService(a, None, None))
    val allVersionedServices = requiredVersionedServices ++ missingVersionServices
    engineEventListener.validated(testRunId.get, allVersionedServices)
    //TODO: this is simply shameful
    validatedServices = Some(allVersionedServices)
    allVersionedServices.filterNot(_.offering.fold(false)(_.validated)).isEmpty
  }

  //TODO: passing the testId is nasty, the script should know it anyway
  def beforeRun(testId: String) {
    testRunId = Some(TestRunIdCounter.next)
    engineEventListener.beforeStarted(this, testId)
  }
  
  //TODO: blow up if trying to start a previously run script
  //TODO: should engineEventListener be passed in here instead
  def start(/*services: Map[String, String]*/) {
    //TODO: still needed?
    //TODO: should be using the clock()
    started = Some(new LocalDateTime())
    engineEventListener.started(started.get, this)
  }

  def stop() {
    //TODO: still needed?
    //TODO: should be using the clock()
    stopped = Some(new LocalDateTime())
    //TODO: or could pass duration ...

    //TODO: this is annoying because for user aborts we get an update too many ...
    //... but to correctly report exceptions in actions we need it ...
    /*if (!isAborted)*/ engineEventListener.stopped(stopped.get, this)
  }

  //TODO: we should this everywhere we do an abort and hide the member access
  def abort(reason: String) {
    aborted = Some(reason)
    //TODO: still needed?
    //TODO: should be using the clock()
    stopped = Some(new LocalDateTime())
    //TODO: or could pass duration ...
    //TODO: need engineEventListener.aborted(reason)
    engineEventListener.stopped(stopped.get, this)
  }

  def running(step: Step) {
    if (!isAborted) engineEventListener.running(step)
  }

  def update(action: Action, response: ActionResponse) = {
//    println("### Script.update: " + response)
    response match {
      case ActionResponse(Nil, None) => {
        action.markCompleted()
        if (!isAborted) engineEventListener.success(action)
      }
      case ActionResponse(Nil, Some(returnValue)) => {
        action.markCompleted()
        action.returnValue.map(v => context = context.updated(v, returnValue))
        if (!isAborted) engineEventListener.success(action)
      }
      case ActionResponse(failures, _) => {
        if (!isAborted) engineEventListener.failure(action, failures)
        aborted = Some(failures.head)
      }
      //TODO case _
    }
  }

  def update(check: Check, response: CheckResponse) = {
    response match {
      case CheckResponse(Nil) => {
        check.markCompleted()
        if (!isAborted) engineEventListener.success(check)
      }
      case CheckResponse(failures) => {
        check.failedAttempts = check.failedAttempts + 1
        if (!isAborted) engineEventListener.failure(check, failures)
      }
      //TODO case _
    }
  }

  //def describe = "☐" + " "

  private def incompleteSteps = steps.filterNot(_.isCompleted)
}
