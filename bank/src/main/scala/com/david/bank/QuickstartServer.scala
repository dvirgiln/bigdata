package com.david.bank

//#quick-start-server
import akka.actor.{ ActorRef, ActorSystem, Props }
import akka.http.scaladsl.server.{ HttpApp, Route }
import akka.stream.ActorMaterializer
import com.david.bank.transaction.TransactionActor
import com.david.bank.user.UserRegistryActor

object QuickstartServer extends HttpApp with App with Routes {

  // set up ActorSystem and other dependencies here
  implicit val system: ActorSystem = ActorSystem("transactionsHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val userRegistryActor: ActorRef = system.actorOf(UserRegistryActor.props, "userRegistryActor")
  val transactionActor: ActorRef = system.actorOf(Props(new TransactionActor(userRegistryActor)), "transactionActor")

  lazy val routes: Route = mainRoutes

  println(s"Server online at http://localhost:8080/")

  startServer("0.0.0.0", 8080)
}

