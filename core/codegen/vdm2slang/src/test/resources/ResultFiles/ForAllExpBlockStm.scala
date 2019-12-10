// #Sireum
import org.sireum._

@record class Entry {

  val s: ISZ[Z] = ISZ(1, 2, 3)

  def Run(): B = {
    return Test()
  }

  def Test(): B = {
    var a: B = All(SetUtil.CreateSetFromSeq(ISZ(1, 2, 3)).elements)(x => x + y > 3)
    return a
  }
}

	