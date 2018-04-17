package com.david.cats.free.logo

object LogoDomain {
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
}
