// #Sireum
import org.sireum._
@record class DEFAULT() {

  @pure def f(x: Z, y: Z): Z = {
    Contract(Requires(x > 4, y != 0))
    x + 1 + y
  }

}
