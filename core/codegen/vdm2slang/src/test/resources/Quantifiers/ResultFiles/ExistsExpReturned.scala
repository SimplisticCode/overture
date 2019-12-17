// #Sireum
import org.sireum._
@record class Entry() {

  def Run(): B = {
    return Exists(SetUtil.CreateSetFromSeq(ISZ(1, 2, 3)))(x => x < 1)

  }

}
