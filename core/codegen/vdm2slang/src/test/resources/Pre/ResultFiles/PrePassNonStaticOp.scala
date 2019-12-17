// #Sireum
import org.sireum._
@record class Entry() {

  def op(x: Z): Z = {
    Contract(Requires(x > 1))
    return x

  }
  def Run(): Z = {
    val e: Entry = Entry()
    val r: Z = e.op(42)
    return r

  }

}
