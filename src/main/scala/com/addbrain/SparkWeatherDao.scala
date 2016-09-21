package com.addbrain
import java.sql.Date

import com.datastax.spark.connector._
import com.datastax.spark.connector.cql._
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark._
import org.apache.spark.rdd.RDD
/**
  * Implementation of the weather dao that retrieves the data from a cassandra database, using spark.
  *
  * Created by dave on 20/09/16.
  */
object SparkWeatherDao extends WeatherDao{

  private val sc=SparkContext.getOrCreate()
  private val data=sc.cassandraTable("addbrain" , "weather")
  private val records=data.map(row => WeatherRecord(row.getString(0),row.getDate(1), row.getString(2), row.getInt(3)))
  private val filterMin=records.filter(_.typeRecord == "TMIN")
  private val filterMax=records.filter(_.typeRecord == "TMAX")

  def getUpperMinimumTemperatures(numRecords: Int): List[WeatherRecord]=filterMin.takeOrdered(numRecords)(Ordering[Int].reverse.on(_.value)).toList

  def getLowerMinimumTemperatures(numRecords: Int): List[WeatherRecord]=filterMin.takeOrdered(numRecords)(Ordering[Int].on(_.value)).toList

  def getUpperMaxTemperatures(numRecords: Int): List[WeatherRecord]=filterMax.takeOrdered(numRecords)(Ordering[Int].reverse.on(_.value)).toList

  def getLowerMaxTemperatures(numRecords: Int): List[WeatherRecord]=filterMax.takeOrdered(numRecords)(Ordering[Int].on(_.value)).toList

  def getAverageMaxTemperatures(): Double=getAverage(filterMax)

  def getAverageMinimumTemperatures(): Double=getAverage(filterMin)

  /*
    Interesting function that makes the average of Weather records, using the spark aggregate function, that is similar to the scala predef foldLeft.
   */
  private def getAverage(rdd: RDD[WeatherRecord]): Double={
    def accumulatorAverage(first: (Int,Int), second: (Int, Int)):(Int,Int)= (first._1+second._1, first._2 + second. _2)
    def aggregateExecutor(aggregation: (Int,Int), record: WeatherRecord):(Int,Int) = (aggregation._1+record.value,aggregation._2 + 1)
    val average=rdd.aggregate((0,0))(aggregateExecutor, accumulatorAverage)
    average._1/average._2
  }


}
