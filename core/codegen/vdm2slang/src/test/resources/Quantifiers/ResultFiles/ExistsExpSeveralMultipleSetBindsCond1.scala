// #Sireum
import org.sireum._
@record class Entry() {

  def Run(): B = {
    return Exists(Set.empty ++ ISZ(1, 2, 3).elements)(a =>
      Exists(Set.empty ++ ISZ(1, 2, 3).elements)(b =>
        Exists(Set.empty ++ ISZ(4, 5, 6).elements)(c =>
          Exists(Set.empty ++ ISZ(4, 5, 6).elements)(d =>
            Exists(Set.empty ++ ISZ(7, 8, 9).elements)(e =>
              Exists(Set.empty ++ ISZ(7, 8, 9).elements)(f => 1.equals(1)))))))

  }

}
