// #Sireum
import org.sireum._
@record class Entry() {
  val s: ISZ[Z] = ISZ(1, 2, 3)

  def Run(): B = {
    return Test()
  }
  def Test(): B = {
    var a: B = All(SetUtil.CreateSetFromSeq(ISZ(1, 2, 3)))(x =>
      All(SetUtil.CreateSetFromSeq(ISZ(1, 2, 3)))(y => x + y > 3))
    return a

  }

}
