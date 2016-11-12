package com.algorithms

import scala.annotation.tailrec

/**
  * Calculates e exponential X
  * Created by dave on 10/11/16.
  */
object ExponentialX extends App{


  def calculateMult(n: Float, k: Int): Float= if(k==1) n else (1 until k).toList.foldLeft(n)((total, item) => total*n)
  def exponential(n: Float): Float= 1 + ((1 to 9).toList.map(k => calculateMult(n,k)/factorial(k))).sum

  def factorial(n: Long): Long = {
    @tailrec
    def factorialAccumulator(acc: Long, n: Long): Long = {
      if (n == 0) acc
      else factorialAccumulator(n*acc, n-1)
    }
    factorialAccumulator(1, n)
  }

  println(exponential(20))

}
