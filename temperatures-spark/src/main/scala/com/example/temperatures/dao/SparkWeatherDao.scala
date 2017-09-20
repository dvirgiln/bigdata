package com.example.temperatures.dao

import com.datastax.spark.connector._
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
/**
  * Implementation of the weather dao that retrieves the data from a cassandra database, using spark.
  *
  * Created by dave on 20/09/16.
  */
object SparkWeatherDao extends WeatherDao{

  private val sc=SparkContextFactory.createSparkContext
  private val data=sc.cassandraTable("database" , "weather")
  private val records=data.map(row => WeatherRecord(row.getString(0),row.getDate(1), row.getString(2), row.getInt(3)))




}
