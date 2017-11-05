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
  private val filterMin=records.filter(_.typeRecord == "TMIN")
  private val filterMax=records.filter(_.typeRecord == "TMAX")

  override def getUpperMinimumTemperatures(numRecords: Int): Future[List[WeatherRecord]]=Future(filterMin.takeOrdered(numRecords)(Ordering[Int].reverse.on(_.value)).toList)

  override def getLowerMinimumTemperatures(numRecords: Int): Future[List[WeatherRecord]]=Future(filterMin.takeOrdered(numRecords)(Ordering[Int].on(_.value)).toList)

  override def getUpperMaxTemperatures(numRecords: Int): Future[List[WeatherRecord]]=Future(filterMax.takeOrdered(numRecords)(Ordering[Int].reverse.on(_.value)).toList)

  override def getLowerMaxTemperatures(numRecords: Int): Future[List[WeatherRecord]]=Future(filterMax.takeOrdered(numRecords)(Ordering[Int].on(_.value)).toList)

  override def getAverageMaxTemperatures(): Future[Double]=Future(getAverage(filterMax))

  override def getAverageMinimumTemperatures(): Future[Double]=Future(getAverage(filterMin))

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
