// #Sireum
import org.sireum._
@record class Entry() {

  def Run(): B = {
    return Exists(SetUtil.CreateSetFromSeq(ISZ(1)).elements)(x =>
      All(SetUtil.CreateSetFromSeq(ISZ(2)).elements)(y => 1.equals(1)))

  }

}
