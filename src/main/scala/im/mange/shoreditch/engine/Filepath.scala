package im.mange.shoreditch.engine

import java.io.File

case class Filepath(filepath: String) {
  private val file = new File(filepath)

  def write(content: String) {
    val pw = new java.io.PrintWriter(file)
    try pw.write(content) finally pw.close()
  }
}
