package com.algorithms.sort

/**
  * Created by dave on 17/11/16.
  */
object MergeSort extends App{
  def mergeSort(xs: List[Int]): List[Int] = {
    val n = xs.length / 2
    if (n == 0) xs
    else {
      def merge(xs: List[Int], ys: List[Int]): List[Int] =
        (xs, ys) match {
          case(Nil, ys) => ys
          case(xs, Nil) => xs
          case(x :: xs1, y :: ys1) =>
            if (x < y) x::merge(xs1, ys)
            else y :: merge(xs, ys1)
        }
      val (left, right) = xs splitAt(n)
      merge(mergeSort(left), mergeSort(right))
    }
  }

  println(mergeSort(List(25,3,34,33,1,7,4)))
}
