// #Sireum
import org.sireum._


@record class A()
{
  var n : Z =2

  def getN():Z=
  {
    return n
  }

  def o1():Z=
  {
    return     A.f() +     A.g(3) +     h(1, 2) +     op2(3) +     A.op2(3)

  }

  def op2(x : Z):Z=
  {
    return 2 + x

  }


  @pure def f():Z =
  {
    2
  }

  @pure def g(x : Z):Z =
  {
    x
  }

  @pure def h(x : Z, y : Z):Z =
  {
    x + y
  }
}