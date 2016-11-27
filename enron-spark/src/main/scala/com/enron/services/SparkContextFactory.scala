package com.enron.services

import org.apache.spark.sql.{SQLContext, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by dave on 16/11/16.
  */
object SparkContextFactory {

  val builder=SparkSession.builder().appName("enron-services").master("local[8]")

  def sparkSession= builder.getOrCreate()
  def sparkContext: SparkContext = sparkSession.sparkContext

  def sqlContext: SQLContext = sparkSession.sqlContext


}
