package com.david.bank

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
import com.david.bank.transaction.{ Transaction, Transactions }
import com.david.bank.transaction.TransactionActor.{ CreateTransaction, GetTransactions, TransactionErrors }
import com.david.bank.user.{ User, Users }
import com.david.bank.user.UserRegistryActor._
import com.david.bank.util.JsonSupport

import scala.concurrent.Future
import scala.concurrent.duration._

//#user-routes-class
trait Routes extends JsonSupport {
  //#user-routes-class

  // we leave these abstract, since they will be provided by the App
  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[Routes])

  // other dependencies that UserRoutes use
  def userRegistryActor: ActorRef

  // other dependencies that UserRoutes use
  def transactionActor: ActorRef
  // Required by the `ask` (?) method below
  implicit lazy val timeout = Timeout(5.seconds) // usually we'd obtain the timeout from the system's configuration

  //#all-routes
  //#users-get-post
  //#users-get-delete   
  lazy val mainRoutes: Route =
    pathPrefix("users") {
      concat(
        pathEnd {
          concat(
            get {
              val users: Future[Users] =
                (userRegistryActor ? GetUsers).mapTo[Users]
              complete(users)
            },
            post {
              entity(as[User]) { user =>
                val userCreated: Future[ActionPerformed] =
                  (userRegistryActor ? CreateUser(user)).mapTo[ActionPerformed]
                onSuccess(userCreated) { performed =>
                  log.info("Created user [{}]: {}", user.name, performed.description)
                  complete((StatusCodes.Created, performed))
                }
              }
            }
          )
        },
        path(Segment) { name =>
          concat(
            get {
              val maybeUser: Future[Option[User]] =
                (userRegistryActor ? GetUser(name)).mapTo[Option[User]]
              rejectEmptyResponse {
                complete(maybeUser)
              }
            }
          )
        }
      )
    } ~
      pathPrefix("transactions") {
        concat(
          pathEnd {
            concat(
              get {
                log.info("Getting all the transactions")
                //#retrieve-user-info
                val transactions: Future[Transactions] =
                  (transactionActor ? GetTransactions).mapTo[Transactions]
                complete(transactions)
              },
              post {
                entity(as[Transaction]) { transaction =>
                  log.info(s"Creating a new transaction $transaction")
                  val transactionCreated: Future[Either[TransactionErrors, Transaction]] =
                    (transactionActor ? CreateTransaction(transaction)).mapTo[Either[TransactionErrors, Transaction]]
                  onSuccess(transactionCreated) { t =>
                    log.info(s"Created transaction $t")
                    if (t.isRight) {
                      complete((StatusCodes.Created, t.right.get))
                    } else {
                      complete((StatusCodes.NotFound, t.left.get))
                    }

                  }
                }
              }
            )
          },
          path(Segment) { userId =>
            concat(
              get {
                log.info(s"Getting all the transactions for user $userId")
                val transactions: Future[Transactions] =
                  (transactionActor ? GetTransactions(userId)).mapTo[Transactions]
                complete(transactions)
              }
            )
          }
        )
      }
}
