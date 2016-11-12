package com.algorithms

import scala.annotation.tailrec
import scala.io.Source
import scala.util.Try

/**
  * Calculates Fibonnacci from an input List of elements.
  *
  * Requires a catching mechanism included inside of the simple fibonnacci function.
  *
  * Created by dave on 07/11/16.
  */
object Fibonacci extends App{

  val cache= scala.collection.mutable.Map[Int,BigInt] ()
  val testCases=Console.readInt()
  var latestKey: Int= 1
  cache.put(0,0)
  cache.put(1,1)
  if(testCases > 0){
    val inputs=(1 to testCases).map(input =>Console.readInt())
    inputs.map {input =>
      val result= fibonacci(input)
      println(result % (BigInt(10).pow(8)+7))}
  }
  else {
    throw new IllegalArgumentException(s"The number of test cases introduced is not correct. $testCases should be greater than 0")
  }


  def fibonacci(n: Int): BigInt= {
    def fibonacciCache(remaining: Int): BigInt= {
      if(latestKey >= remaining) cache(remaining)
      else
       fibonacciRecursive(latestKey+1, remaining- latestKey +1, cache.get(latestKey-1).get, cache.get(latestKey).get)

    }
    @tailrec def fibonacciRecursive(iteration:Int, remaining: Int, previous: BigInt=0, next: BigInt=1): BigInt=
        remaining match {
          case m if(m < 0) => throw new IllegalArgumentException(s"Not allowed values lower than 0: input=$m")
          case m if(m == 0) => previous
          case m if(m ==1) => next
          case _ =>
            val result=next+ previous
            cache.put(iteration, result)
            latestKey=iteration
            fibonacciRecursive(iteration+1, remaining -1, next, result)

    }
    fibonacciCache(n)
  }

  def f(arr:List[Int]):List[Int] =arr.reverse
}
