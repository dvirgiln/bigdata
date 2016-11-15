package com.algorithms

/**
  *
  * The first line of input contains the string .
  * The second line contains .
  * *
  * Output Format
  * *
  * Print the mingled string, .
  *
  * Sample Input #01
  * *
  * hacker
  * ranker
  * Sample Output #01
  * *
  * hraacnkkeerr
  * Created by dave on 10/11/16.
  */
object StringMingling extends App{
  val str1 = scala.io.StdIn.readLine
  val str2 = scala.io.StdIn.readLine
  val a='b'
  if (str1.length != str2.length) throw new IllegalArgumentException("Both inputs should have the same lenght")

  val result= str1.zip(str2).foldLeft("")((total, iteration)=> total.concat(iteration._1.toChar.toString + iteration._2.toChar.toString + ""))

  println(result)
}
