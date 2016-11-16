package com.example.temperatures.dao

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by dave on 16/11/16.
  */
object SparkContextFactory {

  def createSparkContext(): SparkContext ={
    val conf= new SparkConf().setAppName("temperatures").setMaster("local[8]").set("spark.cassandra.connection.host","localhost")
    SparkContext.getOrCreate(conf)
  }
}
