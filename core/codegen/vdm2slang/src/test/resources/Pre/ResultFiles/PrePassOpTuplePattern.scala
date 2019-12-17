// #Sireum
import org.sireum._
@record class DEFAULT() {

  @pure def f(x: Z): Z = {
    Contract(
      Requires(x > 4),
      Ensures(Result.equals(x + 1), Result > 4)
    )
    halt("Not implemented")
  }

}
