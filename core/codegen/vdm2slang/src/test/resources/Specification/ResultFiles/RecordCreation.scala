// #Sireum
import org.sireum._
@datatype class Vector2D(val x: Z, val y: Z) {}

@record class Records() {
  var v: Vector2D = Vector2D(1, 1)
  @pure def f(): Vector2D = {
    Vector2D(1, 2)
  }

}
