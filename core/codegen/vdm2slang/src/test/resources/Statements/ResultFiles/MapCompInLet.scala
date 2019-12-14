// #Sireum
import org.sireum._
@record class Entry() {

  def Run(): Map[Z, Z] = {
    return Test()
  }
  def Test(): Map[Z, Z] = {
    val x: Map[Z, Z] = Map.empty ++ (SeqUtil
      .Elems(ISZ(1, 2, 3))
      .elements
      .filter(x => 1.equals(1))
      .map(x => x * 2, x))
      .elements
    return x

  }

}