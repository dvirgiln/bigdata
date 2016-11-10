package com.codility

/**
  *
  * Basic functions
  *
  * Created by dave on 09/11/16.
  */
object IntroductionFuncs {

  def f(num:Int) : List[Int] = (1 to num).toList

  def removeOdds(arr:List[Int]):List[Int] = {
    arr.zipWithIndex.filter(_._2%2!=0).map(_._1)

  }

  def sumTheNumbersInOddPositions(arr:List[Int]):Int = arr.zipWithIndex.filter(_._2 %2 != 0).map(_._1).reduceLeft((a, b) => a+ b)

  def sumTOddNumbers(arr:List[Int]):Int = arr.filter(_ %2 != 0).reduceLeft((a, b) => a+ b)

  def countList(arr:List[Any]):Int = arr.foldLeft(0)((total, x) => total +1)

  def absoluteList(arr:List[Int]):List[Int] = {

    def abs(n: Int): Int = if(n < 0) (n * -1) else n
    arr match {
      case head :: tail => abs(head) :: absoluteList(tail)
      case Nil => Nil
    }
  }

}

object TestMain extends App{
  println(IntroductionFuncs.f(4).mkString(","))
}
