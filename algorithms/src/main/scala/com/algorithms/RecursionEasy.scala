package com.algorithms

/**
  * Contains EaseRecursion examples.
  * Created by dave on 10/11/16.
  */
object RecursionEasy {


  def reduceRepeatedChar(t: String): String=t.foldLeft("")((finalString, character)=> if(!finalString.contains(character)) finalString.concat(character+"") else finalString)

}
