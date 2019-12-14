// #Sireum
import org.sireum._
@datatype class R1(val x: Z) {}

@datatype class R2(val r: R1) {}

@datatype class R3(val x: Z, val r: R2) {}

@record class A() {
  val x: R3 = R3(1, R2(R1(2)))
  val y: R3 = A.x

}
