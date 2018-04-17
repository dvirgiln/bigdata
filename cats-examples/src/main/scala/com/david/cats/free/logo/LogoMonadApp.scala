package com.david.cats.free.logo

import cats.free.Free
import cats.{Id, ~>}
import LogoDomain._

import com.david.cats.free.logo.LogoInstructions._

object InterpreterId extends (Instruction ~> Id) with LogoImpl {

  override def apply[A](fa: Instruction[A]): Id[A] = fa match {
    case Forward(p, length) => forward(p, length)
    case Backward(p, length) => backward(p, length)
    case RotateLeft(p, degree) => left(p, degree)
    case RotateRight(p, degree) => right(p, degree)
    case ShowPosition(p) => println(s"showing position $p")
  }
}


object LogoMonadApp extends App {

  val program: (Position => Free[Instruction, Position]) = {
    start: Position =>
      for {
        p1 <- LogoFreeLift.forward(start, 10)
        p2 <- LogoFreeLift.right(p1, Degree(90))
        p3 <- LogoFreeLift.forward(p2, 10)
      } yield p3
  }
  println("Started program")
  val startPosition = Position(0.0, 0.0, Degree(0))
  val positionEnd= program(startPosition).foldMap(InterpreterId)
  println(s"End Position= $positionEnd")
  println("Finished program")
}







