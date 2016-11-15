package com.example.temperatures.dao

/**
  * API to query weather data.
  *
  * Created by dave on 21/09/16.
  */

case class WeatherRecord(stationId: String, date: java.util.Date, typeRecord: String, value: Int )

trait WeatherDao {
  def getUpperMinimumTemperatures(numberRecords: Int): List[WeatherRecord]

  def getLowerMinimumTemperatures(numberRecords: Int): List[WeatherRecord]

  def getAverageMinimumTemperatures(): Double

  def getUpperMaxTemperatures(numberRecords: Int): List[WeatherRecord]

  def getLowerMaxTemperatures(numberRecords: Int): List[WeatherRecord]

  def getAverageMaxTemperatures(): Double
}
