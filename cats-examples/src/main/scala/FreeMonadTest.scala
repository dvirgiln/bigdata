
import cats.free.Free
import cats.{Id, ~>}
case class Position(x: Double, y: Double, heading: Degree)
case class Degree(val grades: Int) {
  private val isRightPositive = grades % 360 == 0
  private val isDownPositive  = grades % 270 == 0
  private val isLeftDownDiagonalPositive  = grades % 225 == 0
  private val isRightDownDiagonalPositive  = grades % 315 == 0
  private val isLeftPositive = grades % 180 == 0 && !isRightPositive
  private val isUpPositive = grades % 90 == 0  && !isRightPositive && !isDownPositive
  private val isLeftUpDiagonalPositive = grades % 135 ==0 && !isDownPositive
  private val isRightUpDiagonalPositive = grades % 45 == 0 && !isUpPositive && !isLeftPositive && ! isLeftUpDiagonalPositive && !isDownPositive && !isLeftDownDiagonalPositive && !isRightDownDiagonalPositive && !isRightPositive

  private val isRightNegative = grades % 360 == 0
  private val isLeftUpDiagonalNegative = grades % 225 ==0
  private val isUpNegative = grades % 270 == 0
  private val isRightUpDiagonalNegative = grades % 315 == 0
  private val isLeftNegative = grades % 180 == 0 && !isRightNegative
  private val isLeftDownDiagonalNegative  = grades % 135 == 0 && !isRightUpDiagonalPositive
  private val isDownNegative  = grades % 90 == 0 && !isLeftNegative && !isUpNegative && !isRightNegative
  private val isRightDownDiagonalNegative  = grades % 45 == 0 && !isUpNegative && !isLeftNegative && ! isLeftUpDiagonalNegative && !isDownNegative && !isLeftDownDiagonalNegative && !isRightUpDiagonalNegative && !isRightNegative


  val isRight = grades match {
    case x if(x >= 0) => isRightPositive
    case x if(x < 0) => isRightNegative
  }
  val isDown = grades match {
    case x if(x >= 0) => isDownPositive
    case x if(x < 0) => isDownNegative
  }
  val isLeftDownDiagonal = grades match {
    case x if(x >= 0) => isLeftDownDiagonalPositive
    case x if(x < 0) => isLeftDownDiagonalNegative
  }
  val isRightDownDiagonal = grades match {
    case x if(x >= 0) => isRightDownDiagonalPositive
    case x if(x < 0) => isRightDownDiagonalNegative
  }
  val isLeft = grades match {
    case x if(x >= 0) => isLeftPositive
    case x if(x < 0) => isLeftNegative
  }
  val isUp = grades match {
    case x if(x >= 0) => isUpPositive
    case x if(x < 0) => isUpNegative
  }
  val isLeftUpDiagonal = grades match {
    case x if(x >= 0) => isLeftUpDiagonalPositive
    case x if(x < 0) => isLeftUpDiagonalNegative
  }
  val isRightUpDiagonal = grades match {
    case x if(x >= 0) => isRightUpDiagonalPositive
    case x if(x < 0) => isRightUpDiagonalNegative
  }



}

object Logo {
  sealed trait Instruction[A]
  case class Forward(position: Position, length: Int) extends Instruction[Position]
  case class Backward(position: Position, length: Int) extends Instruction[Position]
  case class RotateLeft(position: Position, degree: Degree) extends Instruction[Position]
  case class RotateRight(position: Position, degree: Degree) extends Instruction[Position]
  case class ShowPosition(position: Position) extends Instruction[Unit]
}
import Logo._

object LogoComputations{
  import cats.free.Free._
  def forward(pos: Position, l: Int): Free[Instruction, Position] = Free.liftF(Forward(pos, l))
  def backward(pos: Position, l: Int): Free[Instruction, Position] = Free.liftF(Backward(pos, l))
  def left(pos: Position, degree: Degree): Free[Instruction, Position] = Free.liftF(RotateLeft(pos, degree))
  def right(pos: Position, degree: Degree): Free[Instruction, Position] = Free.liftF(RotateRight(pos, degree))
  def showPosition(pos: Position): Free[Instruction, Unit] = Free.liftF(ShowPosition(pos))

}
object LogoApp extends App {
  import LogoComputations._
  val program: (Position => Free[Instruction, Position]) = {
    start: Position =>
      for {
        p1 <- forward(start, 10)
        p2 <- right(p1, Degree(90))
        p3 <- forward(p2, 10)
      } yield p3
  }
  println("Started program")
  val startPosition = Position(0.0, 0.0, Degree(0))
  val positionEnd= program(startPosition).foldMap(InterpreterId)
  println(s"End Position= $positionEnd")
  println("Finished program")
}



trait LogoOperations{
  def forward(pos: Position, l: Int): Position = {
    pos.heading match {
      case x if(x.isRight) => pos.copy(x=pos.x + l)
      case x if(x.isLeft) => pos.copy(x=pos.x - l)
      case x if(x.isUp) => pos.copy(y=pos.y + l)
      case x if(x.isDown) => pos.copy(y=pos.y - l)
      case x if(x.isRightUpDiagonal) => pos.copy(x=pos.x + l, y= pos.x + l)
      case x if(x.isLeftUpDiagonal) => pos.copy(x=pos.x - l, y= pos.x + l)
      case x if(x.isLeftDownDiagonal) => pos.copy(x=pos.x - l, y= pos.x - l)
      case x if(x.isRightDownDiagonal) => pos.copy(x=pos.x + l, y= pos.x - l)
    }

  }
  def backward(pos: Position, l: Int): Position = {
    pos.heading match {
      case x if(x.isRight) => pos.copy(x=pos.x - l)
      case x if(x.isLeft) => pos.copy(x=pos.x + l)
      case x if(x.isUp) => pos.copy(y=pos.y - l)
      case x if(x.isDown) => pos.copy(y=pos.y + l)
      case x if(x.isRightUpDiagonal) => pos.copy(x=pos.x - l, y= pos.x - l)
      case x if(x.isLeftUpDiagonal) => pos.copy(x=pos.x + l, y= pos.x - l)
      case x if(x.isLeftDownDiagonal) => pos.copy(x=pos.x + l, y= pos.x + l)
      case x if(x.isRightDownDiagonal) => pos.copy(x=pos.x - l, y= pos.x + l)
    }

  }
  def left(pos: Position, degree: Degree): Position = pos.copy(heading=Degree(pos.heading.grades + degree.grades))
  def right(pos: Position, degree: Degree): Position = pos.copy(heading=Degree(pos.heading.grades - degree.grades))

}


object InterpreterId extends (Instruction ~> Id) with LogoOperations {

  override def apply[A](fa: Instruction[A]): Id[A] = fa match {
    case Forward(p, length) => forward(p, length)
    case Backward(p, length) => backward(p, length)
    case RotateLeft(p, degree) => left(p, degree)
    case RotateRight(p, degree) => right(p, degree)
    case ShowPosition(p) => println(s"showing position $p")
  }
}

object InterpretOpt extends (Instruction ~> Option) with LogoOperations {
  val nonNegative: (Position) => Option[Position] = {
    p => if (p.x >= 0 &&p.y >= 0) Some(p) else None
  }

  override def apply[A](fa: Instruction[A]) = fa match {
    case Forward(p, length) => nonNegative(forward(p, length))
    case Backward(p, length) => nonNegative(backward(p, length))
    case RotateLeft(p, degree) => Some(left(p, degree))
    case RotateRight(p, degree) => Some(right(p, degree))
    case ShowPosition(p) => Some(println(s"showing position $p"))
  }
}

object LogoNegativeApp extends App {
  import LogoComputations._
  import cats._, cats.data._, cats.implicits._
  val program2: (Position => Free[Instruction, Unit]) = {
    s: Position =>
      for {
        p1 <- {
          forward(s, 10)
        }
        p2 <- {
          left(p1, Degree(90))
        }
        p3 <- {
          forward(p2, 10)
        }

        p4 <- {
          backward(p3, 10)
        }//Here the computation stops, because result will be None
        _ <- showPosition(p4)
      } yield ()
  }
  val startPosition = Position(0.0, 0.0, Degree(0))
  println("Started program")
  program2(startPosition).foldMap(InterpretOpt)
  println("Finished program")
}