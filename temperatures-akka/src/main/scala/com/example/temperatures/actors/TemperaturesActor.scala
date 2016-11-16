package com.example.temperatures.actors

import akka.actor.{Actor, ActorLogging, ActorRef}
import UserActor._
import akka.actor._
import ActorMessages.{MaxTemperature, MaxTemperatureAverage, MinTemperature, MinTemperatureAverage, _}
/**
  * Created by dave on 16/11/16.
  */


class TemperaturesActor(dataActor: ActorRef) extends Actor with ActorLogging{


  override def receive: Receive = {
    case MaxTemperature ⇒
      log.info("Calling to get data Max Temperature")
      dataActor ! MaxTemperature(Some(sender()))
      context.become(receiveWaitingResponse)
    case MinTemperature ⇒
      log.info("Calling to get data Max Temperature")
      dataActor ! MinTemperature(Some(sender()))
      context.become(receiveWaitingResponse)
    case MaxTemperatureAverage ⇒
      log.info("Calling to get data Max Temperature")
      dataActor ! MaxTemperatureAverage(Some(sender()))
      context.become(receiveWaitingResponse)
    case MinTemperatureAverage ⇒
      log.info("Calling to get data Max Temperature")
      dataActor ! MinTemperatureAverage(Some(sender()))
      context.become(receiveWaitingResponse)
  }

  def receiveWaitingResponse: Receive={
    case ResponseSent ⇒
      log.info("Response sent to sender")
      context.become(receive)
  }
}
