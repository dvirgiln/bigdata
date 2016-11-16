package com.example.temperatures.actors

import akka.actor.ActorRef

/**
  * Created by dave on 16/11/16.
  */
object ActorMessages {
  val DEFAULT_NUM_RESULTS=10

  case class MaxTemperature(initialSender: Option[ActorRef]= None, numResults: Int=DEFAULT_NUM_RESULTS)
  case class MinTemperature(initialSender: Option[ActorRef]= None, numResults: Int=DEFAULT_NUM_RESULTS)
  case class MaxTemperatureAverage(initialSender: Option[ActorRef]= None)
  case class MinTemperatureAverage(initialSender: Option[ActorRef]= None)
  case object ResponseSent
}
