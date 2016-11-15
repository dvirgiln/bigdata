package com.algorithms

/**
  * Should find the common prefis from 2 strings
  *
  * Sample Input 0
  * *
  * abcdefpr
  * abcpqr
  * Sample Output 0
  * *
  * 3 abc
  * 5 defpr
  * 3 pqr
  * Sample Input 1
  * *
  * kitkat
  * kit
  *
  * Created by dave on 10/11/16.
  */
object StringCommonPrefix extends App{

  val str1 = scala.io.StdIn.readLine
  val str2 = scala.io.StdIn.readLine


  if (str1.length > str2.length) generateOutput(str1, str2)
  else generateOutput(str2, str1,true)


  def generateOutput(str1: String, str2: String, reverted: Boolean=false): Unit = {
    var index = 0
    val prefixBuilder = new StringBuilder
    val input1Builder = new StringBuilder
    val input2Builder = new StringBuilder
    var differ = false
    val str2Length = str2.length
    val lenghStr1=str1.length
    while (index < lenghStr1) {
      val str1Char = str1.charAt(index)
      if (!differ && index < str2Length) {
        if (str1Char == str2.charAt(index)) {
          prefixBuilder.append(str1Char)
        }
        else{
          differ=true
          input1Builder.append(str1Char)
          input2Builder.append(str2.charAt(index))
        }
      }
      else if (differ && index < str2Length) {
        input1Builder.append(str1Char)
        input2Builder.append(str2.charAt(index))
      }
      else if(index >= str2Length){
        input1Builder.append(str1Char)
      }

      index = index + 1
    }
    val prefix=prefixBuilder.toString
    val input1= input1Builder.toString()
    val input2= input2Builder.toString()

    println(prefix.length+" "+ prefix)
    if(reverted){
      println(input2.length+" "+ input2)
      println(input1.length+" "+ input1)
    }
    else{
      println(input1.length+" "+ input1)
      println(input2.length+" "+ input2)
    }
  }
}
