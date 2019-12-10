// #Sireum
import org.sireum._x
@record class Entry
{
  def Run(): Z =
  {
    if (All(SetUtil.CreateSetFromSeq(ISZ(5)).elements)(x => x > 2))
    {
      return 42
    }
    else
    {
      return 49
    }

  }
}