package im.mange.shoreditch.engine.registry

case class Test(rawLines: Seq[String], id: Long = TestIdCounter.next) {
  private val lines = rawLines.filterNot(l => l.trim.isEmpty || l.trim.startsWith("-")).toList
  val name = lines.head
  val content = lines.drop(1)
}
