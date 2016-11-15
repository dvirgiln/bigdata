package com.algorithms

/**
  * Calculates the number of pentagonal points using cache to store previous executions. The formula used is: = N* (3*N - 1) / 2
  * Created by dave on 09/11/16.
  */
object PentagonalNumbers extends App{

  val cache= scala.collection.mutable.Map[Int,Long] ()
  val testCases=scala.io.StdIn.readInt
  var latestKey: Int= 1
  cache.put(1, 1)
  if(testCases > 0){
    val inputs=(1 to testCases).map(input =>scala.io.StdIn.readInt)
    inputs.map {input =>
      val result= getPentagonalNumbers(input)
      Console.print(result+ "\n")
    }
  }
  else {
    throw new IllegalArgumentException(s"The number of test cases introduced is not correct. $testCases should be greater than 0")
  }


  def getPentagonalNumbersPreviousVal(n: Int): Long={
    if(n>latestKey){
      while (latestKey<n){
        val latest=cache.get(latestKey).get
        val currentLatest=latestKey
        latestKey=latestKey +1
        val value=latest+ (currentLatest+1)*3 -2
        cache.put(latestKey, value)
      }

    }
    cache(n)
  }
  def getPentagonalNumbers(n: Int): Long={
    val cacheResult=cache.get(n)
    cacheResult match {
      case Some(res) => res
      case None => {
        val longN=n.toLong
        val result= longN* (3*longN - 1) / 2
        cache.put(n,result)
        result
      }
    }

  }
}
