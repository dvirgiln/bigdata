package com.example.temperatures.controller

import akka.actor.{ActorRef, ActorSystem}
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

import com.example.temperatures.actors.ActorMessages._
import akka.actor.Status.Failure
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.math._
import spray.json.DefaultJsonProtocol

import scala.concurrent.ExecutionContext.Implicits.global
import akka.pattern.ask
import akka.util.Timeout
import com.example.temperatures.actors.ActorMessages
import com.example.temperatures.dao.WeatherRecord
case class SimpleDouble(value: Double)

trait Protocols extends DefaultJsonProtocol with DateMarshalling{
  implicit val simpleValue = jsonFormat1(SimpleDouble.apply)
  implicit val weatherRecord = jsonFormat4(WeatherRecord.apply)

  implicit val timeout = Timeout(200 seconds)
}

/**
  * Created by dave on 15/11/16.
  */
trait TemperaturesController extends Protocols{

  val temperaturesActor: ActorRef

  val temperatures = {
    logRequestResult("temperatures-http") {pathPrefix("temperatures"){
      pathPrefix("min") {
        pathEnd{
          get{
            complete {
              val minFuture = (temperaturesActor ? MinTemperature).mapTo[Future[Seq[WeatherRecord]]]
              minFuture.map[ToResponseMarshallable] {
                case value => value
              }
            }
          }
        } ~
        path("average"){
          get{
            complete {
              val averageFuture = (temperaturesActor ? MinTemperatureAverage).mapTo[Future[Double]]
              averageFuture.flatMap(_.map[ToResponseMarshallable] {
                case value: Double => SimpleDouble(value)
              })
            }
          }
        }
      }  ~
        pathPrefix("max") {
          pathEnd{
            get{
              complete {
                val maxFuture = (temperaturesActor ? MaxTemperature).mapTo[Future[Seq[WeatherRecord]]]
                maxFuture.flatMap(_.map[ToResponseMarshallable] {
                  case value => value
                })
              }
            }
          } ~
            path("average"){
              get{
                complete {
                  val averageFuture = (temperaturesActor ? MaxTemperatureAverage).mapTo[Future[Double]]
                  averageFuture.flatMap(_.map[ToResponseMarshallable] {
                    case value => SimpleDouble(value)
                  })
                }
              }
            }
        }
    }

    }
  }
}


