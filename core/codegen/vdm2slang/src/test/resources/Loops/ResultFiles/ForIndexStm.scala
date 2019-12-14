// #Sireum
import org.sireum._
@record class A() {

	def op(): Unit = {
		for (i <- 1 to 10 by i += 5) {
			/* skip */
		}
		for (j <- 10 to -1 by j += -2) {
			/* skip */
		}
		for (i <- 1 to 1 by i += 1) {
			/* skip */
		}
		for (i <- 1 to 0 by i += 1) {
			/* skip */
		}

	}

}
