import im.mange.shoreditch.engine.hipster.Check
import org.scalatest.{MustMatchers, WordSpec}

class CheckSpec extends WordSpec with MustMatchers {

  "simple check" in {
    val check = Check(1, "check method in:barry => arg1")
    check.rawParams mustEqual Array("arg1")
    check.pure mustEqual "check method"
    check.in mustEqual "barry"
  }
}