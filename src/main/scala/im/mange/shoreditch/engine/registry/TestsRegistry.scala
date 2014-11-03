package im.mange.shoreditch.engine.registry

import scala.reflect.io.Directory
import im.mange.shoreditch.engine.Filepath

//TODO: when you run a test, it ends up in runs
//when you click a run, it watches it in the results window
//this works for scratch pad too, you just auto populate
//scratchpad runs are also saved, so you cna see old results
//maybe leave scratch pad to the last, since it might be more trouble
//than it's worth right now

//TODO: should probably be one directory per test id
//TODO: factor out common stuff with SystemsRegistry
//TODO: should be sorted by something, id or description maybe
object TestsRegistry {
  private val directory = Directory("registry/tests")

  def load = {
    if (!directory.exists) createTest(TestIdCounter.next, exampleTemplate)
    directory.files.map(f => Test(f.name.split("\\.").head.replace("TR", "").toLong, f.lines().filterNot(l => l.trim.isEmpty || l.trim.startsWith("-")).toList)).toList
  }

  private def createTest(id: Long, content: String) {
    directory.createDirectory(force = true)
    Filepath("registry/tests/TR" + id + ".hip").write(content)
  }

  private val exampleTemplate =
    """Bookings must be reserved and paid for
      |@pnr <= create reservation in:booking => from:LHR to:JFK
      |check reservation confirmed in:reservations => @pnr
      |check payment confirmed in:finance => @pnr
    """.stripMargin

  //TODO: implement the @seat bit
  //"check seat reserved in:seating => @ref @seat"

  //Demo services
  //the script:
  //make payment (optional)
  //choose seat  (optional)
  //check ticket issued
  //TODO:
  //Frequent Flyers always get upgrade policy, check class of travel - cattle
  //TODO:
  //cancel booking -> everything cancelled
}
