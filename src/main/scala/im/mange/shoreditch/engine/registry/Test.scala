package im.mange.shoreditch.engine.registry

//TODO: a test should have a name, possibly the first line or some sort of tag?
case class Test(id: Long, rawLines: Seq[String]) {
  val name = rawLines.head
  val content = rawLines.drop(1)
}
