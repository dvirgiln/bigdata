import java.util.{Calendar, Date}

/**
  * Created by dave on 05/11/17.
  */
class Exercices{

  case class DateType(year: Int, month: Int, day: Int)

  /*
   Converts a Sequence of 3 integers into a Date Type. It is supposed that it is coming in the first item the year, then the month and then the day.
  */
  private def fromSeqToDate(seq: Seq[Int]): Option[DateType]= seq match {
    case year :: month :: day :: Nil if year>=2000 && year <3000 && month>=1 && month<=12 && day>=1 && day<=31=>  Some(DateType(year,month, day))
    case _ => None
  }

  /*
    Convert a Date Type object to a Java Util instance. Additionally check if the day is in weekend and if so, then return the next monday.
   */
  private def fromDateTypeToDateUtilAvoidWeekends(a: DateType): Date= {
    val cal=Calendar.getInstance()
    cal.set(a.year,a.month, a.day)
    cal.get(Calendar.DAY_OF_WEEK) match {
      case Calendar.SATURDAY => cal.add(Calendar.DATE, 2)
      case Calendar.SUNDAY => cal.add(Calendar.DATE,1)
      case _ =>
    }
    cal.getTime
  }


  /*
    Given a possibly ambiguous date "A/B/C", where A,B,C are integers between 0 and 2999,
     output the earliest possible legal date between Jan 1, 2000 and Dec 31, 2999 (inclusive) using them as day,
     month and year (but not necessarily in that order).
   */
  def earlierDate(input: String): Date={

    //First read the date using a regex
    val date = """(\d{1,4})/(\d{1,4})/(\d{1,4})""".r

    input match {
      case date(a, b, c)  => {
        val numbers= Seq(a.toInt, b.toInt, c.toInt)
        if(numbers.filter(a => !(a>0 && a< 3000)).size>0) throw new Exception("Format not correct. All numbers should be greater than 0 and lower than 3000")
        //In the list of numbers check all the permutations, convert to date and then get the next legal day and order the list
        val combinations=numbers.permutations.flatMap(fromSeqToDate).map(fromDateTypeToDateUtilAvoidWeekends).toList.sorted
        combinations.head
      }
      case _ => throw new Exception("Format not correct")
    }
  }


  /*
    Write a function that flattens an Option of the collection to a collection (should work for List, Set, Map etc.):
      - If it receives Some(collection) as a parameter - should return collection
      - If it receives None as a parameter  - should return empty collection of the same type as original collection.
   */
  def flattenOption[T, C >: Traversable[T]](input: Option[C]):C= input.getOrElse(Seq.empty)


}


