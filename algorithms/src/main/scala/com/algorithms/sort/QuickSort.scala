package com.algorithms.sort

/**
  * Created by dave on 17/11/16.
  */
object QuickSort extends App{
  def sort(xs: Array[Int]): Array[Int] = {
    if (xs.length <= 1) xs
    else {
      val pivot = xs(xs.length / 2)
      Array.concat(
        sort(xs filter (pivot >)),
        xs filter (pivot ==),
        sort(xs filter (pivot <)))
    }
  }

  println(sort(Array(3,43,53,2,3,5,6,99)).mkString(","))
}
