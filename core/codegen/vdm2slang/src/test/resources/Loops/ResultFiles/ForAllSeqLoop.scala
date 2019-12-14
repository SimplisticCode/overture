// #Sireum
import org.sireum._
@datatype class Rec(val x: Z) {}

@record class A() {

	def op(): Unit = {
		val x: ISZ[Rec] = ISZ(Rec(1), Rec(2), Rec(3))
		for (e <- x.elements) {
			{
				/* skip */
			}

		}

	}
	def op1(): Unit = {
		for (n <- ISZ(1, 2, 3).elements) {
			/* skip */
		}
	}
	def op2(): Unit = {
		for (e <- ISZ().elements) {
			/* skip */
		}
	}
	def op3(): Unit = {
		for (n <- f().elements) {
			/* skip */
		}
	}
	def op4(): Unit = {
		for (n <- ISZOps(ISZ(1, 2, 3)).reverse.elements) {
			/* skip */
		}
	}
	@pure def f(): ISZ[Z] = {
		ISZ(1, 2, 3)
	}

}
