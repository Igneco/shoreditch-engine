package im.mange.shoreditch.engine

import net.liftweb.json._
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import net.liftweb.json.TypeInfo

//TODO: I think little may already have this ... if not migrate it
class DateTimeSerializer extends Serializer[DateTime] {
  private val pattern = ISODateTimeFormat.dateTime()
  private val TheClass = classOf[DateTime]

  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), DateTime] = {
    case (TypeInfo(TheClass, _), json) ⇒ json match {
      case JString(value) ⇒ pattern.parseDateTime(value)
      case x ⇒ throw new MappingException("Can't convert " + x + " to DateTime")
    }
  }

  def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case x: DateTime ⇒ JString(pattern.print(x))
  }
}
