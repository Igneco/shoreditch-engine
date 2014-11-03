package im.mange.shoreditch.hipster

import im.mange.shoreditch.engine.registry.StepIdCounter

case class StepFactory() {
  private val idCounter = StepIdCounter()

  def create(line: String) ={
    if (line.trim.toLowerCase.startsWith("check")) Check(idCounter.next, line)
    else Action(idCounter.next, line)
  }
}
