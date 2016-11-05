package com.codility

/**
  * Created by dave on 05/11/16.
  *
  * This program returns K with the value of the index (starting from 1) where the number of stating "(" is exactly the same than the closing brackets from K+1
  *
  * For instance for an input "(())))(" K=4 because  (())  contains 2 open brackets and  ))( contains two closing brackets.
  */
class SameNumberBrackets {
  def calculate(s: String): Int = {
    //The first value of the tuple store K and the second value store the current iteration
    val result=s.foldLeft(0, s){(value, element) =>
      if(element == '('){
        val lastIndex=value._2.lastIndexOf(')')
        if(lastIndex != -1){
          (value._1 +1 , value._2.substring(1,lastIndex))
        }
        else (value._1, "")
      }
      else if(!value._2.isEmpty){
        if(value._2.length>1) (value._1+1, value._2.substring(1))
        else (value._1+1,"")
      }
      else (value._1,"")
    }
    result._1
  }
}

object SameNumberBrackets extends App{
  val task3Input="(())))("
  val solution=new SameNumberBrackets
  val resultTask3=solution.calculate(task3Input)
  println(s"K=$resultTask3")
}
