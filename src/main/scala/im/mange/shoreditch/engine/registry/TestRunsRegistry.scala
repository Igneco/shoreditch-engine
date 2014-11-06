package im.mange.shoreditch.engine.registry

import scala.reflect.io.Directory

case class TestRunsRegistry(directory: String) {
  private val dir = Directory(directory)

  def load = {
    if (!dir.exists) dir.createDirectory()
    dir.files.filter(_.extension == "json").toSeq
  }
}
