package com.algorithms

/**
  *The idea is to permute the characters on the odd index to the even position. For instance: abcd would be converted to badc.
  *
  * The program should allow multiple inputs. Specifying in the first stdin the number of testcases.
  *
  * 2 implementations have been done. One more slow using foldLeft and another faster, using vars
  *
  * Created by dave on 10/11/16.
  */
object StringOddPermutation extends App{
  val testCases=scala.io.StdIn.readInt
  if(testCases > 0){
    val inputs=(1 to testCases).map(input =>scala.io.StdIn.readLine())
    inputs.map {input =>
      if(input.length %2 !=0) throw new IllegalArgumentException(s"The input $input should contain a even lenght")
      val result= calculatePermutation2(input)
      Console.print(result+ "\n")
    }
  }

  def calculatePermutation(input: String): String={
    val inputWithIndez= input.zipWithIndex
    val odd=inputWithIndez.filter(_._2 % 2!=0).map(_._1)
    val even=inputWithIndez.filter(_._2 % 2==0).map(_._1)

    even.zip(odd).foldLeft("")((total, iter)=> total.concat(iter._2.toString).concat(iter._1.toString ))
  }

  def calculatePermutation2(input: String): String={
    var i=0
    val buffer=new StringBuffer()
    var previous: Char='a'
    input.foreach{character =>
      if(i % 2 ==0) previous=character
      else buffer.append(character).append(previous)
      i=i+1
    }
    buffer.toString
  }
}
