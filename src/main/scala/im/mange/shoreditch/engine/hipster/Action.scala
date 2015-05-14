package im.mange.shoreditch.engine.hipster

import im.mange.shoreditch.api.{In, ActionResponse}
import io.shaka.http.ContentType.APPLICATION_JSON
import io.shaka.http.Http._
import io.shaka.http.Request.POST
import im.mange.shoreditch.engine.Json

//TODO
//content-type
//method
//payload
case class Action(id: Long, description: String) extends Step {
  //TODO: parse everything out early - and model it properly .. ActionLine, CheckLine etc
  val returnValue = if (description.contains("<=")) Some(description.split("<=").head.trim) else None
//  private val method = description.split("<=").last.split(" ").map(_.trim).filterNot(_.isEmpty)

  private val method = {
    var m = description
    if (description.contains("=>")) m = description.split("=>").head
    m.split("<=").last.split(" ").map(_.trim).filterNot(_.isEmpty)
  }

  val inApp = method.last.split(":").last //TODO: this is a bit nasty, should split into the three distinct bits and return to :
  val pure = method.init.mkString(" ")
  val requiredSystem = inApp

  private val maybeParams = description.split("=>")
  private val params = if (maybeParams.size == 2) maybeParams.lastOption else None

  val in = params.fold(List.empty[In])(ps => {
    //      println("ps:" + ps)
    ps.trim.split(" ").map(p => {
      val bits = p.trim.split(":")
      //        println("bits:" + bits)
      In(bits(0), Some(bits(1)), Nil)
    }).toList
  })

  //  println("so far so good")

  var mangledIn: Option[List[In]] = None
  val me = "action/" + method.init.mkString("/")

  def run: ActionResponse = {
//    println(in)
//    println(method.toList)

//    println(me)
    val serviceKey = inApp + "/" + me
//    println(serviceKey)
    val requestUrl = this.script.systemUrlFor(serviceKey) + "/" + me
//    println(request)

    mangledIn = Some(in.map(i => {
      val rawValue = i.value.get.trim
      val mangledValue = if (rawValue.startsWith("@")) script.context.getOrElse(rawValue, rawValue) else rawValue
      i.copy(value = Some(mangledValue))
    }))

    val json = Json.serialise(mangledIn.get)
    val request = POST(requestUrl).contentType(APPLICATION_JSON.value).entity(json)

    GoldenRetriever.doRunRun(request) match {
      case Left(e) => ActionResponse(List(e.getMessage), None)
      case Right(r) => {
        val response = r.entityAsString
        try {
          Json.deserialiseActionResponse(response)
        }
        catch {
          case e: Exception => ActionResponse(e.getMessage :: response.split("\n").toList, None)
        }
      }
    }
  }

  //☐
  def describe = (if (isCompleted) "☑" else "☒") + " - " + mangledDescription

  def mangledDescription = {
    val paramBit = if (in.isEmpty) "" else " => " + mangledIn.getOrElse(in).map(i => i.name + ":" + i.value.get).mkString(" ")
    val rv = if (replacedReturnValue.trim.isEmpty) "" else replacedReturnValue + " <= "
    rv + method.mkString(" ") + paramBit
  }

  def replacedReturnValue = returnValue.fold("")(v => script.context.getOrElse(v, v))
}
