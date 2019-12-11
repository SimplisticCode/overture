// #Sireum
import org.sireum._

@enum object Team {
  'Argentina
  'Austria
  'Belgium
  'Brazil
  'Bulgaria
  'Cameroon
  'Chile
  'Colombia
  'Croatia
  'Denmark
  'England
  'France
  'Germany
  'Holland
  'Iran
  'Italy
  'Jamaica
  'Japan
  'Mexico
  'Morocco
  'Nigeria
  'Norway
  'Paraguay
  'Rumania
  'SaudiArabia
  'Scotland
  'SouthAfrica
  'SouthKorea
  'Spain
  'Tunisia
  'UnitedStates
  'Yugoslavia
}

@enum object GroupName {
  'A
  'B
  'C
  'D
  'E
  'F
  'G
  'H
}

@datatype class Score(val team: Z, val won: Z, val drawn: Z, val lost: Z, val points: Z) {

}

@record class GroupPhase {

  @pure def sc_init(ts: Set[Z]): Set[Score] = {
    Set.empty ++ ts.elements.filter(t => true).map(t => Score(t, 0, 0, 0, 0))
  }


  @pure def clear_winner(scs: Set[Score]): B = {
    Exists(scs.elements)(sc => All(SetUtil.SetDifference(scs, SetUtil.CreateSetFromSeq(ISZ(sc))).elements)(sc' => sc.points > sc'.points) )
  }
}