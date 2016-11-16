package com.example.temperatures.actors

import java.util.Date

import akka.actor.{Actor, ActorLogging}
import com.example.temperatures.actors.ActorMessages._
import com.example.temperatures.dao.{SparkWeatherDao, WeatherDao, WeatherRecord}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by dave on 16/11/16.
  */
class DataActor extends Actor with ActorLogging{

  val dao: WeatherDao= SparkWeatherDao
  override def receive: Receive = {
    case MaxTemperature(Some(initialSender), numResults) ⇒
      log.info("Getting Max Temperature from DAO:")
      sender() ! ResponseSent
      initialSender ! dao.getUpperMaxTemperatures(numResults)
    case MinTemperature(Some(initialSender), numResults)  ⇒
      log.info("Getting Min Temperature from DAO:")
      sender() ! ResponseSent
      initialSender ! dao.getLowerMinimumTemperatures(numResults)
    case MaxTemperatureAverage(Some(initialSender)) ⇒
      log.info("Getting Max Temperature Average from DAO:")
      sender() ! ResponseSent
      initialSender ! dao.getAverageMaxTemperatures()
    case MinTemperatureAverage(Some(initialSender)) ⇒
      log.info("Getting Min Temperature Average from DAO:")
      sender() ! ResponseSent
      initialSender ! dao.getAverageMinimumTemperatures()
  }

}
