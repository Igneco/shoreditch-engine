package im.mange.shoreditch.engine.registry

import java.io.File


import scala.io.Source
import scala.reflect.io.Directory
import im.mange.shoreditch.engine.systems.System

//TODO: should probably take a filepath to Sx.txt to later support system sets/switching etc
case class SystemsRegistry(directory: String) {
  private val file = new File(directory + "/systems.txt")

  def load = {
    if (!file.exists()) createSystems(exampleTemplate)
    Source.fromFile(file).getLines().filterNot(l => l.trim.isEmpty || l.startsWith("-")).map(System(_)).toList
  }

  private def createSystems(content: String) {
    Directory(directory).createDirectory(force = true)
    val pw = new java.io.PrintWriter(file)
    try pw.write(content) finally pw.close()
  }

  //Beards bank gets the payment?
  private val exampleTemplate =
    """
      |-----Systems-----
      |---USAGE:
      |---{- to ignore}name,alias,env,url
      |
      |--Local--
      |Hipster Holidays,booking,local,http://localhost:4253/booking
      |Shoreditch Airways,reservations,local,http://localhost:4253/reservations
      |Shoreditch Airways,finance,local,http://localhost:4253/finance
      |Shoreditch Airways,seating,local,http://localhost:4253/seating
      |Shoreditch Airways,ticketing,local,http://localhost:4253/ticketing
    """.stripMargin
}
