import im.mange.shoreditch.engine.hipster.Check
import org.scalatest.{MustMatchers, WordSpec}

//TOOD: we need a corresponding ActionSpec
class CheckSpec extends WordSpec with MustMatchers {

  "simple check" in {
    val check = Check(1, "check method in:barry => arg1")
    check.pure mustEqual "check method"
    check.in mustEqual "barry"
    check.rawParams mustEqual Array("arg1")
    check.me mustEqual "check/method"
    check.serviceKey mustEqual "barry/check/method/@?"
  }

  "simple check with multiple args" in {
    val check = Check(1, "check method in:barry => arg1 arg2 arg3")
    check.pure mustEqual "check method"
    check.in mustEqual "barry"
    check.rawParams mustEqual Array("arg1", "arg2", "arg3")
    check.me mustEqual "check/method"
    check.serviceKey mustEqual "barry/check/method/@?/@?/@?"
  }

  "simple check with multiple method bits" in {
    val check = Check(1, "check a really long method in:barry => arg1")
    check.pure mustEqual "check a really long method"
    check.in mustEqual "barry"
    check.rawParams mustEqual Array("arg1")
    check.me mustEqual "check/a/really/long/method"
    check.serviceKey mustEqual "barry/check/a/really/long/method/@?"
  }

  "simple check with no args" in {
    val check = Check(1, "check method in:barry")
    check.pure mustEqual "check method"
    check.in mustEqual "barry"
    check.rawParams mustEqual Array()
    check.me mustEqual "check/method"
    check.serviceKey mustEqual "barry/check/method"
  }

  "simple check with no args with extra whitespace in params" in {
    val check = Check(1, "check method in:barry   ")
    check.pure mustEqual "check method"
    check.in mustEqual "barry"
    check.rawParams mustEqual Array()
    check.me mustEqual "check/method"
    check.serviceKey mustEqual "barry/check/method"
  }

  "simple check with no args with extra whitespace in method" in {
    val check = Check(1, "check method   in:barry")
    check.pure mustEqual "check method"
    check.in mustEqual "barry"
    check.rawParams mustEqual Array()
    check.me mustEqual "check/method"
    check.serviceKey mustEqual "barry/check/method"
  }
}