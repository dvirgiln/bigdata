/**
  * Created by dave on 20/09/16.
  */
import org.scalatest._
import AddBrainFunctions._

class AddBrainFunctionsTestSpec extends FlatSpec with MustMatchers {

  "The AddBran Left Truncate function" must "list positive numbers" in {
    leftTruncate(1340) must be(List(1340,340,40,0))
    leftTruncate(1345) must be(List(1345,345,45,5))
    leftTruncate(-1345) must be(List(-1345,-345,-45,-5))
    leftTruncate(0) must be(List(0))
  }
}
