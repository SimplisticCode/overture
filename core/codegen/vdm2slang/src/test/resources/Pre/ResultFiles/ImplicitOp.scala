// #Sireum
import org.sireum._
@record class Entry() {

  def Run(): Z = {
    val b: B = !true
    if (b) {
      return addImpl(5, 7)

    } else {
      return 5 + 7

    }

  }
  def addImpl(left: Z, right: Z): Z = {
    Contract(
      Requires(true),
      Ensures(left + right.equals(res))
    )
    halt("Not implemented")
  }

}
