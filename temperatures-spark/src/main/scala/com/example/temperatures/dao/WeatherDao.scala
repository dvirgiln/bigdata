package com.example.temperatures.dao

import scala.concurrent.Future

/**
  * API to query weather data.
  *
  * Created by dave on 21/09/16.
  */

case class WeatherRecord(stationId: String, date: java.util.Date, typeRecord: String, value: Int )

trait WeatherDao {
  def getUpperMinimumTemperatures(numberRecords: Int): Future[List[WeatherRecord]]

  def getLowerMinimumTemperatures(numberRecords: Int): Future[List[WeatherRecord]]

  def getAverageMinimumTemperatures(): Future[Double]

  def getUpperMaxTemperatures(numberRecords: Int): Future[List[WeatherRecord]]

  def getLowerMaxTemperatures(numberRecords: Int): Future[List[WeatherRecord]]

  def getAverageMaxTemperatures(): Future[Double]
}
