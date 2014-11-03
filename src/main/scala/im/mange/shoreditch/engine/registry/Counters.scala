package im.mange.shoreditch.engine.registry

//TODO: some or all of these shouldnt be in registry ...
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
    count
  }
}

object TestRunIdCounter {
  private val initialValue = TestRunsRegistry.load.size
  private var count = initialValue

  def next = synchronized {
    count += 1
    count
  }
}

case class StepIdCounter() {
  private var count = 0L

  def next = synchronized {
    count += 1
    count
  }
}
