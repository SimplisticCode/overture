// #Sireum
import org.sireum._
@record class Entry() {

  def Run(): Z = {
    val i: Z = 1 {
      for (i <- i to 10 by i ++) {
        val i: Z = i
        return i

      }
      return 0

    }

  }

}
