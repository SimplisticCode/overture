// #Sireum
import org.sireum._

@record class Entry {
  def Run(): B = {
    return Exists(ISZ(1).elements)(x => x.equals(1))
  }
}