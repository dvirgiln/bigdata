
package com.addbrain
/**
  * Created by dave on 22/05/16.
  */
object AddBrainFunctions extends App {



  /**
    *
    * @param num An integer and  is assumed to be at least 0.
    * @return A list of integers with the first entry being num, and the subsequent ones
    *         are gotten by omitting the left hand most digit one by one.
    *         Eg  num=0 returns List(0).
    *         num=1234 returns List(1234,234,34,4)
    */
  def leftTruncate(num: Int): List[Int] = leftTruncateRecursive(num, List[Int]())

  private def leftTruncateRecursive(number: Int, current: List[Int]): List[Int] = {
    val calculated = number % Math.pow(10, current.size + 1).toInt :: current
    if (number == calculated.head) calculated
    else leftTruncateRecursive(number, calculated)
  }


}
