package im.mange.shoreditch.engine

import net.liftweb.json._
import net.liftweb.json.Serialization._
import net.liftweb.json.{JsonParser, NoTypeHints, Serialization}
import im.mange.shoreditch.api.MetaDataResponse
import im.mange.shoreditch.api.ActionResponse
import im.mange.shoreditch.api.In
import im.mange.shoreditch.api.CheckResponse
import im.mange.shoreditch.engine.model.TestRunReport

//TODO: probably better to have: CheckResponseJson.x etc
object Json {
  private val shoreditchFormats = Serialization.formats(NoTypeHints) + new DateTimeSerializer
  
  //TODO: we don't technically need this in the public api
  def deserialiseActionResponse(json: String) = {
    implicit val formats = shoreditchFormats
    parse(json).extract[ActionResponse]
  }

  //TODO: we don't technically need this in the public api
  def deserialiseCheckResponse(json: String) = {
    implicit val formats = shoreditchFormats
    parse(json).extract[CheckResponse]
  }

  //TODO: we don't technically need this in the public api
  def deserialiseMetaDataResponse(json: String) = {
    implicit val formats = shoreditchFormats
    parse(json).extract[MetaDataResponse]
  }

  def serialise(r: List[In]) = {
    implicit val formats = shoreditchFormats
    val res = pretty(render(JsonParser.parse(write(r))))
    println(res)
    res
  }

  //TODO: we don't technically need this in the public api
  def serialise(r: TestRunReport) = {
    implicit val formats = shoreditchFormats
    JsonParser.parse(write(r))
  }
}
