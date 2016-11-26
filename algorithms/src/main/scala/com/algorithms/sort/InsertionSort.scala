package com.algorithms.sort

import scala.util.Try

/**
  * Created by dave on 13/11/16.
  */
object InsertionSort extends App{

  val n = scala.io.StdIn.readInt
  val str2 = scala.io.StdIn.readLine

  val secondLine = Try(str2.split(" ").map(Integer.parseInt(_))).getOrElse(throw new IllegalArgumentException("Second Line does not contains numeric values"))
  if (secondLine.length != n) throw new IllegalArgumentException(s"The expected number of elements in the second line should be exactly $n")
  val list = secondLine.toList.toArray
  val value= scala.collection.mutable.ArrayBuffer[Int]() ++= list
  if(list.length> 1){
    var i=list.length-2
    var element= list.last
    var done: Boolean=false
    while(i>=0 && !done){
      if(value(i) > element) value(i+1)=value(i)
      else {
        done=true
        value(i+1)=element
      }
      println(value.mkString(" "))
      i=i - 1
    }
    if(!done){
      value(0)=element
      println(value.mkString(" "))
    }
  }



  def isort(xs: List[Int]): List[Int] = xs match {
    case List() => List()
    case x :: xs1 => insert(x, isort(xs1))
  }
  
  def insert(x: Int, xs: List[Int]): List[Int] = xs match {
    case List() => List(x)
    case y :: ys => if (x <= y) x :: xs else y :: insert(x, ys)
  }

}
