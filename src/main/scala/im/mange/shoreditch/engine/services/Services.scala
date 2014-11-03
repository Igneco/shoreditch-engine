package im.mange.shoreditch.engine.services

import scala.collection.immutable.HashMap
import im.mange.shoreditch.engine.systems.System
import im.mange.shoreditch.engine.{Json, HttpClient}

//TODO: should be a refresh or discover method or something ...
case class Services(systems: Seq[System]) {
  private val supportedServiceTypes = List("action", "check")

  var raw = Seq[Service]()
  var discovered = new HashMap[String, String]()

  //TODO: ultimately .par this up
  //TODO: probably need to make env be part of the service key too (and populate from run-with
  //TODO: blow up if service key already exists
  raw = systems.map(system => {
    try {
      val metaData = Json.deserialiseMetaDataResponse(HttpClient.unsafeGet(system.url + "/metadata"))
      println("### " + system.alias + " " + metaData.version + " - actions: " + metaData.actions.size + ", checks: " + metaData.checks.size)

      metaData.actions.map(service => {
        val fixedUpServices = service.url.split("/").dropWhile(!supportedServiceTypes.contains(_))
        val normalisedService = fixedUpServices.mkString("/")
        val aliasedService = system.alias + "/" + normalisedService
        discovered = discovered.updated(aliasedService, system.url)
      })

      metaData.checks.map(service => {
        val fixedUpServices = service.url.split("/").dropWhile(!supportedServiceTypes.contains(_))
        val normalisedService = fixedUpServices.map(p => if (p.startsWith("@")) "@?" else p).mkString("/")
        val aliasedService = system.alias + "/" + normalisedService
        //        println("check: " + service + " -> " + aliasedService)
        discovered = discovered.updated(aliasedService, system.url)
      })

      Some(Service(system, metaData))
    } catch {
      case _: Exception => println("### error discovering: " + system.url); None
    }
  }).flatten

  println("\n### All discovered: \n" + discovered.toString().split(", ").mkString("\n"))
}
