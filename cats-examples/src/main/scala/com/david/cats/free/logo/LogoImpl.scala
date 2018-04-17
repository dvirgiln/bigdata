package com.david.cats.free.logo

import com.david.cats.free.logo.LogoDomain.{Degree, Position}

trait LogoImpl{
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