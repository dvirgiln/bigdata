package com.algorithms

import scala.annotation.tailrec
import scala.util.Try

/**
  * Created by dave on 11/11/16.
  */
object ReverseFactorization extends App {
  val str1 = Console.readLine
  val str2 = Console.readLine


  val firstLine = Try(str1.split(" ").map(Integer.parseInt(_))).getOrElse(throw new IllegalArgumentException("First Line does not contains numeric values"))
  if (firstLine.length != 2) throw new IllegalArgumentException("The first line should contain exactly 2 numbers separated by one space")
  val n = firstLine(0)
  val k = firstLine(1)
  val secondLine = Try(str2.split(" ").map(Integer.parseInt(_))).getOrElse(throw new IllegalArgumentException("Second Line does not contains numeric values"))
  if (secondLine.length != k) throw new IllegalArgumentException(s"The expected number of elements in the second line should be exactly $k")
  val possibleDivisors = secondLine.toSet

  val divisors = generateDivisors(n, possibleDivisors.toList)
  val result = generatePermutations(divisors.toSet, n).mkString(" ")
  println(result)


  def generateDivisors(n: Int, numbers: List[Int]) = numbers.filter(n % _ == 0).sorted

  def generatePermutations(items: Set[Int], expected: Int): List[Int] = {
    var found: Option[List[Int]] = None
    var resultFound: List[Int] = List[Int]()
    def f(current: List[Int], remaining: Set[Int], expected: Int): Unit = {
      val value = current.reduce(_ * _)
      found match {
        case None =>
          if (value == expected) setResult(current)
          else if (!(value > expected || remaining.isEmpty)) remaining.map { item =>
            val a = (item :: current).reverse
            f(a, remaining - item, expected)
          }
        case Some(items) if (current.length <= items.length) => {
          if (value == expected) {
            val currentResult = calculateResult(Some(current))
            val comparation = compare(currentResult, resultFound)
            if (comparation == -1)
              found = Some(current)
          }
          else if (!(value > expected || remaining.isEmpty)) remaining.map { item =>
            val a = (item :: current).reverse
            f(a, remaining - item, expected)
          }
        }
        case _ =>
      }
    }

    def setResult(list: List[Int]) = {
      found = Some(list)
      resultFound = calculateResult(Some(list))
    }
    f(List[Int](1), items, expected)
    calculateResult(found)

  }

  def calculateResult(current: Option[List[Int]]): List[Int] = current match {
    case None => List(-1)
    case Some(value) =>{
      ((value.toSet - 1).toList.sorted.foldLeft(List[Int](1)){(total, item) =>
        val value=total.head * item
        value :: total}).reverse
    }
  }

  def compare(first: List[Int], second: List[Int]): Int = {
    if (first.length < second.length) -1
    else if (first.length > second.length) 1
    else {
      val value = first.zip(second).foldLeft(0) { case (total, item) =>
        if (total == 0) {
          if (item._1 < item._2) -1
          else if (item._1 > item._2) 1
          else 0
        }
        else total
      }
      value

    }
  }


}
