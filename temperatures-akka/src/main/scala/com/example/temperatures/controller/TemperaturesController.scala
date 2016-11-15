package com.example.temperatures.controller

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import java.io.IOException

import akka.actor.Status.Failure

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.math._
import spray.json.DefaultJsonProtocol

import scala.concurrent.ExecutionContext.Implicits.global


case class SimpleDouble(value: Double)

trait Protocols extends DefaultJsonProtocol {
  implicit val simpleValue = jsonFormat1(SimpleDouble.apply)

}

/**
  * Created by dave on 15/11/16.
  */
trait TemperaturesController extends Protocols{


  val temperatures = {
    logRequestResult("temperatures-http") {pathPrefix("temperatures"){
      path("min") {
        pathEnd{
          get{
            complete {
              val minFuture = Future(-1)
              minFuture.map[ToResponseMarshallable] {
                case value => SimpleDouble(value)
              }
            }
          }
        } ~
        path("average"){
          get{
            complete {
              val averageFuture = Future(1)
              averageFuture.map[ToResponseMarshallable] {
                case value => SimpleDouble(value)
              }
            }
          }
        }
      }  ~
        path("max") {
          pathEnd{
            get{
              complete {
                val maxFuture = Future(45)
                maxFuture.map[ToResponseMarshallable] {
                  case value => SimpleDouble(value)
                }
              }
            }
          } ~
            path("average"){
              get{
                complete {
                  val averageFuture = Future(33)
                  averageFuture.map[ToResponseMarshallable] {
                    case value => SimpleDouble(value)
                  }
                }
              }
            }
        }
    }

    }
  }
}


