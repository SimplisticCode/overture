// #Sireum
import org.sireum._
@record class A() {

	def op(): Unit = {
		for (i <- 1 to 1 by i++) {
			/* skip */
		}
		for (i <- 4 to 1 by i++) {
			/* skip */
		}

	}

}
