package com.algorithms

/**
  * Created by dave on 04/11/16.
  */
object ExecutionSolution extends App{

  val test1=Array(Array(0, 1, 9, 3), Array(7, 5, 8, 3), Array(9, 2, 9, 4), Array(4, 6, 7, 1))

  val value=SaddlePoint.solution(test1)
  println(value)
}


import scala.collection.JavaConverters._

// you can write to stdout for debugging purposes, e.g.
// println("this is a debug message")

object SaddlePoint {


  /*
     I CONSIDER THE EXERCICE IS NOT WELL EXPLAINED AND THE EXPECTED RESULT IS NOT THE ONE THAT APPEARS ON CODILITY:

  A[0][0] = 0    A[0][1] = 1    A[0][2] = 9    A[0][3] = 3
  A[1][0] = 7    A[1][1] = 5    A[1][2] = 8    A[1][3] = 3
  A[2][0] = 9    A[2][1] = 2    A[2][2] = 9    A[2][3] = 4
  A[3][0] = 4    A[3][1] = 6    A[3][2] = 7    A[3][3] = 1

  It is said that element on position (1,1) is local minimum on its row, but that is false, because there is another element with less value A(1,3)
  The same for the rest of the explanation.
   */


  private def getColumn(i: Int, j: Int, a: Array[Array[Int]]): List[Int]={
    val withIndex=a.zipWithIndex
    val result=withIndex.map{ case (row,index) =>
      if(i!=index && j<row.length){
        Some(row(j))
      }
        else{
        None
      }
    }
    result.flatten.toList
  }

  private def isSaddlePoint(input: Int, rowValues: List[Int], colValues: List[Int]): Boolean={
    input match {
      case value if(!rowValues.exists(_>value) && !colValues.exists(_<value)) => true
      case value if(!colValues.exists(_>value) && !rowValues.exists(_<value)) => true
      case _ => false
    }
  }

  def solution(a: Array[Array[Int]]): Int = {
    var totalSaddle = 0

    val result=  for{
        i <- 0 until a.length
        j <- 0 until a(i).length
      }yield{
        val rowValues= {
          if(j ==0) a(i).toList.tail
          else if(j > a(i).length-1) a(i).toList
          else if(j == a(i).length-1) a(i).init.toList
          else  (a(i).slice(0, j) ++ a(i).slice(j+1, a(i).length)).toList
        }
        val columnValues= getColumn(i,j,a)
       isSaddlePoint(a(i)(j), rowValues,columnValues)
      }
    result.count(saddlePoint => saddlePoint)
  }
}
