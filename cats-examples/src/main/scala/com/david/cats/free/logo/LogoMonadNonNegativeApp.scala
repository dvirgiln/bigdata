package com.david.cats.free.logo

import cats.free.Free
import cats.~>
import com.david.cats.free.logo.LogoInstructions._
import com.david.cats.free.logo.LogoDomain.{Degree, Position}

object InterpretOpt extends (Instruction ~> Option) with LogoImpl {
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

object LogoMonadNonNegativeApp extends App {
  import LogoFreeLift._
  import cats.implicits._
  val program2: (Position => Free[Instruction, Unit]) = {
    s: Position =>
      for {
        p1 <- forward(s, 10)
        p2 <- left(p1, Degree(90))
        p3 <- forward(p2, 10)
        p4 <- backward(p3, 10)
        _ <- showPosition(p4)
      } yield ()
  }
  val startPosition = Position(0.0, 0.0, Degree(0))
  println("Started program")
  program2(startPosition).foldMap(InterpretOpt)
  println("Finished program")
}