package im.mange.shoreditch.engine.model

import im.mange.shoreditch.engine.registry.TestIdCounter

case class Test(rawLines: Seq[String], id: String = TestIdCounter.next) {
  private val lines = rawLines.filterNot(l => l.trim.isEmpty || l.trim.startsWith("-")).toList
  val name = lines.head
  val content = lines.drop(1)
}
