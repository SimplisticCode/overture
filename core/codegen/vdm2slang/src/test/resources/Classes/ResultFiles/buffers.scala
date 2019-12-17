// #Sireum
import org.sireum._
@record class Buffers() {
  var b1: Z = 0
  var b2: Z = 0
  var b3: Z = 0

  def Add(x: Z): Unit = {
    Contract(
      Requires(x <= 5, b1 + b2 + b3 + x <= 40),
      Modifies(b3, b2, b1),
      Ensures(b1 + b2 + b3.equals(IN(b1) + IN(b2) + IN(b3) + x))
    )
    if (x + b1 < b2) {
      b1 = b1 + x
    } else if (b2 + x <= b3) {
      b2 = b2 + x
    } else {
      b3 = b3 + x
    }

  }
  def Remove(x: Z): Unit = {
    Contract(
      Requires(x <= 5, x <= b1 + b2 + b3),
      Modifies(b1, b2, b3),
      Ensures(b1 + b2 + b3 + x.equals(IN(b1) + IN(b2) + IN(b3)))
    )
    if (x + b2 <= b3) {
      b3 = b3 - x
    } else if (x + b1 <= b2) {
      b2 = b2 - x
    } else {
      b1 = b1 - x
    }

  }

}
