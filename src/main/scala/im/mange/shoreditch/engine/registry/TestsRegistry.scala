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
case class TestsRegistry(directory: String) {
  private val dir = Directory(directory)

  def load = {
    if (!dir.exists) createTest(TestIdCounter.next, exampleTemplate)
    dir.files.map(f => Test(f.lines().toList, f.name.split("\\.").head)).toList
  }

  private def createTest(id: String, content: String) {
    dir.createDirectory(force = true)
    Filepath(directory + "/" + id + ".hip").write(content)
  }

  //TODO: probably should not be here anymore
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
