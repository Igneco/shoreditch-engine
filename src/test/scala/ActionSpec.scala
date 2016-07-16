import im.mange.shoreditch.In
import im.mange.shoreditch.engine.hipster.{Action, Check}
import org.scalatest.{MustMatchers, WordSpec}

class ActionSpec extends WordSpec with MustMatchers {

  "simple action" in {
    val action = Action(1, "method in:barry")
    action.pure mustEqual "method"
    action.inApp mustEqual "barry"
    action.in mustEqual Nil
    action.me mustEqual "action/method"
    action.returnValue mustEqual None
  }

  "simple action with arg and return value" in {
    val action = Action(1, "@id <= method in:barry => argName:argValue")
    action.pure mustEqual "method"
    action.inApp mustEqual "barry"
    action.in mustEqual List(In("argName", Some("argValue")))
    action.me mustEqual "action/method"
    action.returnValue mustEqual Some("@id")
  }

  "simple action with multiple args" in {
    val action = Action(1, "method in:barry => argName1:argValue1 argName1:argValue1")
    action.pure mustEqual "method"
    action.inApp mustEqual "barry"
    action.in mustEqual List(In("argName1", Some("argValue1")), In("argName1", Some("argValue1")))
    action.me mustEqual "action/method"
    action.returnValue mustEqual None
  }

  "simple action with multiple method bits" in {
    val action = Action(1, "a really long method in:barry")
    action.pure mustEqual "a really long method"
    action.inApp mustEqual "barry"
    action.in mustEqual Nil
    action.me mustEqual "action/a/really/long/method"
    action.returnValue mustEqual None
  }

  "simple action with no args but return value" in {
    val action = Action(1, "@id <= method in:barry")
    action.pure mustEqual "method"
    action.inApp mustEqual "barry"
    action.in mustEqual Nil
    action.me mustEqual "action/method"
    action.returnValue mustEqual Some("@id")
  }

  "simple action with no args with multiple whitespace in params" in {
    val action = Action(1, "method in:barry   ")
    action.pure mustEqual "method"
    action.inApp mustEqual "barry"
    action.in mustEqual Nil
    action.me mustEqual "action/method"
    action.returnValue mustEqual None
  }

  "simple action with no args with extra whitespace in method" in {
    val action = Action(1, "method    in:barry")
    action.pure mustEqual "method"
    action.inApp mustEqual "barry"
    action.in mustEqual Nil
    action.me mustEqual "action/method"
    action.returnValue mustEqual None
  }

  "simple action with no args with extra whitespace in reuturn" in {
    val action = Action(1, "   @id   <= method in:barry")
    action.pure mustEqual "method"
    action.inApp mustEqual "barry"
    action.in mustEqual Nil
    action.me mustEqual "action/method"
    action.returnValue mustEqual Some("@id")
  }
}