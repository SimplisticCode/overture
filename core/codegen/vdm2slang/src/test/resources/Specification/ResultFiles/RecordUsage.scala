// #Sireum
import org.sireum._
@datatype class R1(val x: Z) {}

@datatype class R2(val r1: R1, val x: Z) {}

@record class A() {
  val a: R2 = R2(R1(2), 3)
  val b: R2 = A.a
  val c: R1 = a.r1
  val d: Z = a.r1.x

  def op1(): Z = {
    var r: R1 = R1(2)
    r.x = 3
    return r.x

  }
  def op2(): Z = {
    val a: R1 = R1(5)
    val b: R1 = a
    return b.x

  }
  def op3(): Z = {
    var a: R2 = R2(R1(2), 3)
    var b: R1 = a.r1
    a.x = 1
    a.r1.x = 2
    return a.r1.x

  }
  def op4(): R2 = {
    var a: R2 = R2(R1(2), 3)
    return a

  }
  def op5(): R1 = {
    var a: R2 = R2(R1(2), 3)
    return a.r1

  }

}
