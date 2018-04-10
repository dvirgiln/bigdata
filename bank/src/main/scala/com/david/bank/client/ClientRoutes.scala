package com.david.bank.transaction

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{ delete, get, post }
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.ask
import akka.util.Timeout
import com.david.bank.client.ClientActor._
import com.david.bank.client.{ ClientBasicInfo, ClientDetailedInfo, ClientsBasicInfo, Deposit }
import com.david.bank.transaction.TransactionActor.{ CreateTransaction, GetTransactions, TransactionErrors }
import com.david.bank.util.JsonSupport

import scala.concurrent.Future

trait ClientRoutes extends JsonSupport {

  // we leave these abstract, since they will be provided by the App
  implicit def system: ActorSystem

  lazy val logClient = Logging(system, classOf[ClientRoutes])

  implicit val timeout: Timeout

  def clientActor: ActorRef

  lazy val clientRoutes: Route =
    pathPrefix("clients") {
      concat(
        pathEnd {
          concat(
            get {
              logClient.info("Getting all the Basic Clients Information")
              val clients: Future[ClientsBasicInfo] =
                (clientActor ? GetAllClientsBasicInfo).mapTo[ClientsBasicInfo]
              complete(clients)
            },
            post {
              entity(as[ClientBasicInfo]) { basicClient =>
                logClient.info(s"Creating a new client $basicClient")
                val basicClientCreated: Future[OperationPerformed] =
                  (clientActor ? CreateClient(basicClient.user, basicClient.balance)).mapTo[OperationPerformed]
                complete((StatusCodes.Created, basicClientCreated))
              }
            }
          )
        },
        path(Segment) { userId =>
          concat(
            get {
              parameter('detailed.as[Boolean].?) { detailed =>
                detailed match {
                  case Some(true) =>
                    logClient.info(s"Getting detailed information for user $userId")
                    val detailedClient: Future[Option[ClientDetailedInfo]] =
                      (clientActor ? GetClientDetailedInfo(userId.toInt)).mapTo[Option[ClientDetailedInfo]]
                    rejectEmptyResponse {
                      complete(detailedClient)
                    }
                  case _ =>
                    logClient.info(s"Getting basic information for user $userId")
                    val basicClient: Future[Option[ClientBasicInfo]] =
                      (clientActor ? GetClientBasicInfo(userId.toInt)).mapTo[Option[ClientBasicInfo]]
                    rejectEmptyResponse {
                      complete(basicClient)
                    }
                }

              }
            }
          )
        },
        pathSuffix("deposit") {
          post {
            entity(as[Deposit]) { depositJson =>
              logClient.info(s"Creating a deposit of ${depositJson.deposit} to userId ${depositJson.userId}")
              val depositCreated: Future[OperationPerformed] =
                (clientActor ? CreateDeposit(depositJson.userId, depositJson.deposit)).mapTo[OperationPerformed]
              rejectEmptyResponse {
                complete(depositCreated)
              }
            }
          }

        }

      )
    }

}
