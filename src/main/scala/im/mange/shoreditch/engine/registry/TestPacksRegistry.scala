package im.mange.shoreditch.engine.registry

import scala.reflect.io.Directory
import im.mange.shoreditch.engine.Filepath
import im.mange.shoreditch.engine.model.{TestPack, Test}

case class TestPacksRegistry(directory: String, testsRegistry: TestsRegistry) {
  private val dir = Directory(directory)

  def load = {
    if (!dir.exists) Nil
    dir.files.map(f => {
      val lines = f.lines().toList
      val testsInPack = lines.drop(1).filterNot(_.trim.isEmpty).filterNot(_.trim.startsWith("-")).map(id => testsRegistry.find(id).getOrElse(throw new RuntimeException("Unable to load Test: " + id)))
      TestPack(f.name.split("\\.").head, lines.head, testsInPack)
    }).toList
  }
}
