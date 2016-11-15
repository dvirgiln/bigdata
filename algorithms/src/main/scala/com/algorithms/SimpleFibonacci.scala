package com.algorithms

import scala.io.StdIn

/**
  *
  * Fibonnati function created without any catching mechanism.
  * Created by dave on 10/11/16.
  */
object SimpleFibonacci {
  import scala.annotation.tailrec
  def fibonacci(n: Int): BigInt= {
    @tailrec def fibonacciRecursive(iteration:Int, remaining: Int, previous: BigInt=0, next: BigInt=1): BigInt=
      remaining match {
        case m if(m < 0) => throw new IllegalArgumentException(s"Not allowed values lower than 0: input=$m")
        case m if(m == 0) => previous
        case m if(m ==1) => next
        case _ =>
          val result=next+ previous
          fibonacciRecursive(iteration+1, remaining -1, next, result)

      }
    fibonacciRecursive(0, n-1)
  }

  def main(args: Array[String]) {
    /** This will handle the input and output**/
    println(fibonacci(StdIn.readInt))

  }


}
