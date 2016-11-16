package com.example.temperatures

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.example.temperatures.actors.{DataActor, TemperaturesActor}
import com.typesafe.config.ConfigFactory

/**
  * Created by dave on 15/11/16.
  */
object TemperaturesApp extends App with AssemblyRoutes{

  implicit val system = ActorSystem("temperatures-app")
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val dataActor:ActorRef=system.actorOf(Props(new DataActor), "dataActor")
  override val temperaturesActor:ActorRef=system.actorOf(Props(new TemperaturesActor(dataActor)), "temperaturesActor")
  val config = ConfigFactory.load()
  val logger = Logging(system, getClass)

  Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
}
