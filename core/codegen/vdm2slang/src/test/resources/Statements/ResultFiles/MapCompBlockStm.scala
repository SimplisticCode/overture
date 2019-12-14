// #Sireum
import org.sireum._
@record class Entry() {
  val s: ISZ[Z] = ISZ(1, 2, 3)

  def Run(): Map[Z, Z] = {
    return Test()
  }
  def Test(): Map[Z, Z] = {
    var a: Map[Z, Z] = Map.empty ++ (SeqUtil
      .Elems(Entry.s)
      .elements
      .filter(x => 1.equals(1))
      .map(x => x, x + 1))
      .elements
    return a

  }

}