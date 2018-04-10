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
import com.david.bank.transaction.TransactionActor.{ CreateTransaction, GetTransactions, TransactionErrors }
import com.david.bank.util.JsonSupport
import scala.concurrent.duration._
import scala.concurrent.Future
trait TransactionRoutes extends JsonSupport {

  // we leave these abstract, since they will be provided by the App
  implicit def system: ActorSystem

  lazy val logTransaction = Logging(system, classOf[TransactionRoutes])
  implicit val timeout: Timeout
  def transactionActor: ActorRef

  lazy val transactionRoutes: Route =
    pathPrefix("transactions") {
      concat(
        pathEnd {
          concat(
            get {
              logTransaction.info("Getting all the transactions")
              //#retrieve-user-info
              val transactions: Future[Transactions] =
                (transactionActor ? GetTransactions).mapTo[Transactions]
              complete(transactions)
            },
            post {
              entity(as[Transaction]) { transaction =>
                logTransaction.info(s"Creating a new transaction $transaction")
                val transactionCreated: Future[Either[TransactionErrors, Transaction]] =
                  (transactionActor ? CreateTransaction(transaction)).mapTo[Either[TransactionErrors, Transaction]]
                onSuccess(transactionCreated) { t =>
                  logTransaction.info(s"Created transaction $t")
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
              logTransaction.info(s"Getting all the transactions for user $userId")
              val transactions: Future[Transactions] =
                (transactionActor ? GetTransactions(userId.toInt)).mapTo[Transactions]
              complete(transactions)
            }
          )
        }
      )
    }

}
