package com.david.bank.client
import akka.actor.{ ActorRef, ActorSystem, Props }
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{ ContentTypes, HttpRequest, MessageEntity, StatusCodes }
import akka.http.scaladsl.server.Directives.rejectEmptyResponse
import akka.http.scaladsl.server.{ MissingQueryParamRejection, Route }
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.ActorMaterializer
import akka.util.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ BeforeAndAfterAll, Matchers, Sequential, WordSpec }
import akka.http.scaladsl.server.Directives._
import com.david.bank.QuickstartServer.{ system, transactionActor, userActor }
import com.david.bank.transaction._
import com.david.bank.user.UserActor.CreateUser
import com.david.bank.user.{ User, UserActor, UserService }
import akka.pattern.ask
import com.david.bank.client.ClientActor
import com.david.bank.client.ClientActor.{ CreateClient, CreateDeposit, GetClientDetailedInfo, OperationPerformed }
import com.david.bank.transaction.TransactionActor.{ CreateTransaction, TransactionErrors }

import scala.concurrent.Future
import scala.concurrent.duration._

class ClientRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest
    with ClientRoutes with BeforeAndAfterAll {
  implicit lazy val timeout = Timeout(100.seconds)
  val userActor: ActorRef = system.actorOf(UserActor.props, "userRegistryActor")
  val transactionActor: ActorRef = system.actorOf(Props(new TransactionActor(userActor)), "transactionActor")
  val clientActor: ActorRef = system.actorOf(Props(new ClientActor(userActor, transactionActor)), "clientActor")
  lazy val routes = clientRoutes

  override def beforeAll() {
    UserService.init
    TransactionService.init
  }
  "ClientRoutes" should {
    "create clients with an initial deposit (POST /clients)" in {
      val client = ClientBasicInfo(User(None, "David", 42, "UK"), 25000)
      val clientEntity = Marshal(client).to[MessageEntity].futureValue
      val request = Post(uri = "/clients").withEntity(clientEntity)
      request ~> routes ~> check {
        status should ===(StatusCodes.Created)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===(s"""{"id":1}""")
      }

    }

    "list current users (GET /clients)" in {
      val request = HttpRequest(uri = "/clients")
      val userId44 = (clientActor ? CreateClient(User(None, "Eduart", 33, "UK"), 10000)).mapTo[Int].futureValue
      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        val clients = entityAs[ClientsBasicInfo]
        clients.clients.size should be > 0
        clients.clients.filter(_.user.id.get == userId44).toString should ===("List(ClientBasicInfo(User(Some(2),Eduart,33,UK),10000.0))")

      }
    }

    "create transactions and get basic information (GET /clients{userId})" in {

      val userId = (clientActor ? CreateClient(User(None, "Alex", 42, "UK"), 10000)).mapTo[Int].futureValue
      val userId2 = (clientActor ? CreateClient(User(None, "Marc", 33, "UK"), 10000)).mapTo[Int].futureValue
      val t1 = (transactionActor ? CreateTransaction(Transaction(None, userId, userId2, 1000))).mapTo[Either[TransactionErrors, Transaction]].futureValue
      val t2 = (transactionActor ? CreateTransaction(Transaction(None, userId, userId2, 1000))).mapTo[Either[TransactionErrors, Transaction]].futureValue
      val t3 = (transactionActor ? CreateTransaction(Transaction(None, userId, userId2, 1000))).mapTo[Either[TransactionErrors, Transaction]].futureValue

      val request = HttpRequest(uri = s"/clients/${userId}")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"user":{"id":3,"name":"Alex","age":42,"countryOfResidence":"UK"},"balance":7000.0}""")
      }
    }

    "create transactions and get detailed information (GET /clients{userId}?detailed)" in {
      val userId = (clientActor ? CreateClient(User(None, "Adrian", 42, "UK"), 10000)).mapTo[Int].futureValue
      val userId2 = (clientActor ? CreateClient(User(None, "Robert", 33, "UK"), 10000)).mapTo[Int].futureValue
      val t1 = (transactionActor ? CreateTransaction(Transaction(None, userId, userId2, 1000))).mapTo[Either[TransactionErrors, Transaction]].futureValue
      val t2 = (transactionActor ? CreateTransaction(Transaction(None, userId, userId2, 1000))).mapTo[Either[TransactionErrors, Transaction]].futureValue
      val t3 = (transactionActor ? CreateTransaction(Transaction(None, userId, userId2, 1000))).mapTo[Either[TransactionErrors, Transaction]].futureValue

      val request = HttpRequest(uri = s"/clients/${userId}?detailed=true")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        val detailedClient = entityAs[ClientDetailedInfo]
        val filteredDates = detailedClient.transactions.map(t => t.copy(transactionDate = None))
        val clientDetailedNoDates = detailedClient.copy(transactions = filteredDates)

        clientDetailedNoDates.toString should ===("""ClientDetailedInfo(User(Some(5),Adrian,42,UK),7000.0,List(Transaction(Some(8),0,5,10000.0,None,Some(PENDING)), Transaction(Some(10),5,6,1000.0,None,Some(PENDING)), Transaction(Some(11),5,6,1000.0,None,Some(PENDING)), Transaction(Some(12),5,6,1000.0,None,Some(PENDING))))""")
      }
    }

    "test initial deposit  (POST /clients/deposit)" in {

    }

  }

}