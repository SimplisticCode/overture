// #Sireum
import org.sireum._
@record class DEFAULT() {

  @pure def f(x: Z): Z = {
    Contract(
      Requires(x > 4),
      Ensures(x > 5)
    )
    x + 1
  }

}
