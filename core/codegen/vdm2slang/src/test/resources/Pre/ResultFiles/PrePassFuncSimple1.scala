// #Sireum
import org.sireum._
@record class Entry() {

  def Run(): Z = {
    return f(5)

  }
  @pure def f(x: Z): Z = {
    Contract(Requires(x > 0))
    x
  }

}
