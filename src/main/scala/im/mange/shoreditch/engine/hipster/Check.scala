package im.mange.shoreditch.engine.hipster

import im.mange.little.LittleClient
import im.mange.shoreditch.api.CheckResponse
import im.mange.shoreditch.engine.Json
import io.shaka.http.Request.GET

//TODO:
//countout
//timeout
case class Check(id: Long, description: String, var failedAttempts: Int = 0) extends Step {
  private val isParamaterless = !description.contains("=>")

  private val method = (if (isParamaterless) description else description.split("=>").head).split(" ")
  val rawParams = if (isParamaterless) Array.empty[String] else description.split("=>").last.split(" ").map(_.trim).filterNot(_.isEmpty)
  val pure = method.init.mkString(" ")
  val in = method.last.split(":").last

  val requiredSystem = in
  val me = method.init.mkString("/")

  def run: CheckResponse = {

    //    val me = in + method.split(" ").filterNot(_.trim().startsWith("@")).mkString("/")

    //    println("### rawParams: " + rawParams.toList)
    //TODO: or args? which is more usery
    val params = rawParams.map(p => if (p.trim.startsWith("@")) script.context.getOrElse(p, throw new RuntimeException("no value for: " + p)) else p)
    //    val params = rawParams.map(p => script.context.getOrElse(p, "123"))
    //TODO: ick probably need to encode these, or POST instead

    val requestUrl = this.script.systemUrlFor(in + "/" + me + "/" + rawParams.map(rp => "@?").mkString("/")) + "/" + me + "/" + params.mkString("/")
    /*if (debug) */ //println("### " + this + " = " + request)

    LittleClient.doRunRun(GET(requestUrl)) match {
      case Left(e) => CheckResponse(List(e.getMessage))
      case Right(r) => {
        val response = r.entityAsString
        try {
          Json.deserialiseCheckResponse(response)
        }
        catch {
          case e: Exception => CheckResponse(e.getMessage :: response.split("\n").toList)
        }
      }
    }
  }

  //  returnValue.fold("")(v => script.context.getOrElse(v, v)) + " <= " +
  def describe = (if (isCompleted) "☑" else "☒") + " - " + mangledDescription
  def mangledDescription = method.mkString(" ") + " => " + replacedParams.mkString(" ")

  def replacedParams = rawParams.map(p => script.context.getOrElse(p, p))
}
