package com.algorithms

/**
  *
  * Sample Input #00
  * *
  * abcaaabbb
  * Sample Output #00
  * *
  * abca3b3
  * Sample Input #01
  * *
  * abcd
  * Sample Output #01
  * *
  * abcd
  * Sample Input #02
  * *
  * aaabaaaaccaaaaba
  * Sample Output #02
  * *
  * a3ba4c2a4ba
  * Created by dave on 10/11/16.
  */
object StringRepetitionPattern extends App{
  val str=scala.io.StdIn.readLine

  calculateCharactersRepetition(str.toList).map{character => print(character._1)
    if(character._2>1) print(character._2)
  }
  println
  def calculateCharactersRepetition(str: List[Char], previous: Option[Char]= None, counter: Int=0):List[(Char, Int)]={
    str match {
      case head :: tail if(!previous.isDefined) => calculateCharactersRepetition(tail, Some(head), counter +1)
      case head :: tail if(previous.isDefined && head == previous.get) => calculateCharactersRepetition(tail, previous, counter +1)
      case head :: tail if(previous.isDefined && head != previous.get) => (previous.get, counter) :: calculateCharactersRepetition(tail, Some(head), 1)
      case Nil if(previous.isDefined)=> List((previous.get, counter))
      case Nil => List()
    }
  }
}
