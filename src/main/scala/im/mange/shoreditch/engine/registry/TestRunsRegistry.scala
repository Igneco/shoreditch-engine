package im.mange.shoreditch.engine.registry

import scala.reflect.io.Directory

object TestRunsRegistry {
  private val directory = Directory("registry/testruns/")

  def load = {
    if (!directory.exists) directory.createDirectory()
    directory.files.filter(_.extension == "json").toList
  }
}
