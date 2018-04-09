import java.util.Calendar

import org.scalatest.{FlatSpec, Matchers}
import org.scalatest._
/**
  * Created by dave on 05/11/17.
  */
class ExercicesTest extends FlatSpec with Matchers{
  val app= new Exercices()
  "A date in an specific order " should "return the earlier legal date" in{
    val cal=Calendar.getInstance()
    cal.set(2003,5,11)
    app.earlierDate("05/2003/11") should be equals (cal.getTime)
    cal.set(2003,11,13)
    app.earlierDate("13/2003/11") should be equals (cal.getTime)

    intercept[Exception]{
      app.earlierDate("13/20034/11")
    }

    intercept[Exception]{
      app.earlierDate("13/aaaa/11")
    }

  }

  "A Option[Traversable] " should " be flatten and return a Traversable instance" in{
    app.flattenOption(Some(List(1,2,3))) should be equals(List(1,2,3))
    app.flattenOption(Some(Seq(1,2,3))) should be equals(Seq(1,2,3))
    app.flattenOption(Some(Map(1 ->2, 2-> 3))) should be equals(Map(1 ->2, 2 -> 3))
    app.flattenOption(Some(Some(1))) should be equals(Some(1))
  }

  "A Option[Traversable] " should "  that contains None should returns an Empty instance" in{
    app.flattenOption(None) should be equals(Seq.empty)
  }
}
