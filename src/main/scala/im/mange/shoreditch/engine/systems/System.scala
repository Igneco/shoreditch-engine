package im.mange.shoreditch.engine.systems

import im.mange.shoreditch.engine.systems
import im.mange.shoreditch.engine.registry.SystemIdCounter

case class System(id: Long, name: String, alias: String, env: String, url: String)

//TODO: enforce alias being a single word, no spaces - and only lowercase
//TODO: name could ultimately be optional
//TODO: enforce alias must be unique
object System {
  def apply(config: String): systems.System = {
    val bits = config.split(",")
    if (bits.length != 4) throw new RuntimeException("Invalid system: " + config)
    systems.System(SystemIdCounter.next, bits(0), bits(1), bits(2), bits(3))
  }
}