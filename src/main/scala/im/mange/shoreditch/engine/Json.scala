package im.mange.shoreditch.engine

import im.mange.little.json.{LittleJodaSerialisers, LittleSerialisers}
import im.mange.shoreditch.api._
import im.mange.shoreditch._
import im.mange.shoreditch.engine.model.TestRunReport
import org.json4s.NoTypeHints
import org.json4s.native.{JsonParser, Serialization}
import org.json4s._
import org.json4s.native.Serialization._
import org.json4s.native.JsonMethods._

object Json {
  private val shoreditchFormats = Serialization.formats(NoTypeHints) ++ LittleSerialisers.all ++ LittleJodaSerialisers.all

  def deserialiseActionResponse(json: String) = {
    implicit val formats = shoreditchFormats
    parse(json).extract[ActionResponse]
  }

  def deserialiseCheckResponse(json: String) = {
    implicit val formats = shoreditchFormats
    parse(json).extract[CheckResponse]
  }

  def deserialiseMetaDataResponse(json: String) = {
    implicit val formats = shoreditchFormats
    parse(json).extract[MetaDataResponse]
  }

  def serialise(r: List[In]) = {
    implicit val formats = shoreditchFormats
    pretty(render(JsonParser.parse(write(r))))
  }

  def serialise(r: TestRunReport) = {
    implicit val formats = shoreditchFormats
    parse(write(r))
  }
}
