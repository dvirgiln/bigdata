package com.david.bank

//#quick-start-server
import java.util.logging.Logger

import akka.actor.{ ActorRef, ActorSystem, Props }
import akka.http.scaladsl.server.{ HttpApp, Route }
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.david.bank.client.ClientActor
import com.david.bank.transaction.{ ClientRoutes, TransactionActor, TransactionRoutes }
import com.david.bank.user.{ UserActor, UserRoutes }
import scala.concurrent.duration._
object QuickstartServer extends HttpApp with App with TransactionRoutes with UserRoutes with ClientRoutes {
  // Required by the `ask` (?) method below

  val logger = Logger.getLogger(QuickstartServer.getClass.getName)
  logger.info("Initializating Server")
  // set up ActorSystem and other dependencies here
  implicit val system: ActorSystem = ActorSystem("transactionsHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit lazy val timeout = Timeout(100.seconds) // usually we'd obtain the timeout from the system's configuration

  val userActor: ActorRef = system.actorOf(UserActor.props, "userRegistryActor")
  val transactionActor: ActorRef = system.actorOf(Props(new TransactionActor(userActor)), "transactionActor")
  val clientActor: ActorRef = system.actorOf(Props(new ClientActor(userActor, transactionActor)), "clientActor")

  lazy val routes: Route = transactionRoutes ~ userRoutes ~ clientRoutes

  logger.info("Server started at http://localhost:8080/ ")
  startServer("0.0.0.0", 8080)

}

