package com.codility

import scala.annotation.tailrec

/**
  * Calculates the pascal triangle. With an input of the number of rows, calculate it using the pascal triangle formula. This is an example for n=5:
  *
  * 1
  * 1 1
  * 1 2 1
  * 1 3 3 1
  * 1 4 6 4 1
  *
  * Created by dave on 10/11/16.
  */
class PascalTriangle {
  def f(rows: Int): Unit = {
    var counter = 1
    println(counter)
    while (counter < rows) {
      print("1 ")
      calculatePascalTriangle(counter)
      println()
      counter = counter + 1
    }
  }

  def calculatePascalTriangle(n: Int): Unit = {
    (1 to n).toList.foreach(value => print(pascalFormula(n, value) + " "))
  }

  def pascalFormula(row: Int, col: Int) = factorial(row) / (factorial(col) * factorial(row - col))

  def factorial(n: Long): Long = {
    @tailrec
    def factorialAccumulator(acc: Long, n: Long): Long = {
      if (n == 0) acc
      else factorialAccumulator(n * acc, n - 1)
    }
    factorialAccumulator(1, n)
  }
}

object PascalTriangle extends App {
  val trianglePascal = new PascalTriangle
  trianglePascal.f(4)
}