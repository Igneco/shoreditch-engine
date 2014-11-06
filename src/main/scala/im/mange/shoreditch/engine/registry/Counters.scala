package im.mange.shoreditch.engine.registry

//TODO: these should  all be in registry ...
object SystemIdCounter {
  private var count = 0L

  def next = synchronized {
    count += 1
    count
  }
}

object TestIdCounter {
  private var count = 0L

  def next = synchronized {
    count += 1
    s"T$count"
  }
}

object TestRunIdCounter {
  //TODO: bit iffy this
  private val initialValue = TestRunsRegistry("registry/testruns").load.size
  private var count = initialValue

  def next = synchronized {
    count += 1
    s"TR$count"
  }
}

case class StepIdCounter() {
  private var count = 0L

  def next = synchronized {
    count += 1
    count
  }
}
