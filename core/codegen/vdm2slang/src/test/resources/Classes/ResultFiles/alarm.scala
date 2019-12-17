// #Sireum
import org.sireum._
@datatype class Plant(
                       val schedule: Map[Token, Set[Expert]],
                       val alarms: Set[Alarm]) {}

@datatype class Expert(
                        val expertid: Token,
                        val quali: Set[Qualification.Type]) {}

@enum object Qualification {
  'Bio
  'Chem
  'Elec
  'Mech
}
@datatype class Alarm(val alarmtext: String, val quali: Qualification.Type) {}

@record class alarm() {

  def RunTest(): Set[Token] = {
    val a1: Alarm = Alarm("Mechanical fault", Qualification.Mech)
    val a2: Alarm = Alarm("Tank overflow", Qualification.Chem)
    val ex1: Expert =
      Expert(1, Set.empty ++ ISZ(Qualification.Mech, Qualification.Bio))
    val ex2: Expert = Expert(2, Set.empty ++ ISZ(Qualification.Elec))
    val ex3: Expert = Expert(
      3,
      Set.empty ++ ISZ(
        Qualification.Chem,
        Qualification.Bio,
        Qualification.Mech))
    val ex4: Expert =
      Expert(4, Set.empty ++ ISZ(Qualification.Elec, Qualification.Chem))
    val p1: Token = "Monday day"
    val p2: Token = "Monday night"
    val plant: Plant = Plant(
      Map.empty ++ ISZ(
        (p1, Set.empty ++ ISZ(ex1, ex4)),
        (p2, Set.empty ++ ISZ(ex2, ex3))),
      Set.empty ++ ISZ(a1, a2))
    return ExpertIsOnDuty(ex1, plant)

  }
  @pure def NumberOfExperts(peri: Token, plant: Plant): Z = {
    Contract(Requires(MapUtil.Dom(plant.schedule).contains(peri)), Modifies())
    plant.schedule.get(peri).size
  }
  @pure def ExpertIsOnDuty(ex: Expert, plant: Plant): Set[Token] = {
    {}
    Set.empty ++ MapUtil
      .Dom(plant.schedule)
      .elements
      .filter(
        peri =>
          plant.schedule
            .get(peri)
            .contains(ex))
      .map(peri => peri)

  }
  @pure def ExpertToPage(a: Alarm, peri: Token, plant: Plant): Expert = {
    Contract(
      Requires(
        MapUtil.Dom(plant.schedule).contains(peri),
        plant.alarms.contains(a)),
      Modifies(),
      Ensures(
        plant.schedule
          .get(peri)
          .contains(r),
        r.quali.contains(a.quali))
    )
    halt("Not implemented")
  }
  @pure def QualificationOK(
                             exs: Set[Expert],
                             reqquali: Qualification.Type): B = {
    Exists(exs.elements)(ex => ex.quali.contains(reqquali))
  }

}
