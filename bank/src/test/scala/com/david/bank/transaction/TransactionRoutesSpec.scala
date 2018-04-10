package com.david.bank.transaction
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
import com.david.bank.transaction.{ Transaction, TransactionActor, TransactionRoutes, Transactions }
import com.david.bank.user.UserActor.CreateUser
import com.david.bank.user.{ User, UserActor, UserService }
import akka.pattern.ask
import com.david.bank.client.ClientActor
import com.david.bank.client.ClientActor.{ CreateDeposit, OperationPerformed }
import com.david.bank.transaction.TransactionActor.TransactionErrors

import scala.concurrent.Future
import scala.concurrent.duration._

class TransactionRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest
    with TransactionRoutes with BeforeAndAfterAll {
  implicit lazy val timeout = Timeout(100.seconds)
  val userActor: ActorRef = system.actorOf(UserActor.props, "userRegistryActor")
  val transactionActor: ActorRef = system.actorOf(Props(new TransactionActor(userActor)), "transactionActor")
  val clientActor: ActorRef = system.actorOf(Props(new ClientActor(userActor, transactionActor)), "clientActor")
  lazy val routes = transactionRoutes

  override def beforeAll() {
    UserService.init
    TransactionService.init
  }
  import UserCreationTest._
  "TransactionRoutes" should {
    "return the initial transaction done to the MAIN BANK (GET /transactions)" in {
      val request = HttpRequest(uri = "/transactions")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should startWith("""{"transactions":[{"receiverId":0,"amount":1.7976931348623157E+308,"senderId":-1,"id":0,"status":"CONFIRMED","""")
      }
    }

    "add one transaction (POST /transactions)" in {

      val transaction = Transaction(None, DAVID.id.get, JUAN.id.get, 2000)
      val transactionEntity = Marshal(transaction).to[MessageEntity].futureValue
      val request = Post(uri = "/transactions").withEntity(transactionEntity)
      request ~> routes ~> check {
        status should ===(StatusCodes.Created)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should startWith(s"""{"receiverId":${JUAN.id.get},"amount":2000.0,"senderId":${DAVID.id.get},"id":2,"status":"PENDING","transactionDate":""")
      }

    }

    "add a second transaction (POST /transactions)" in {

      val transaction = Transaction(None, DAVID.id.get, JUAN.id.get, 1000)
      val transactionEntity = Marshal(transaction).to[MessageEntity].futureValue
      val request = Post(uri = "/transactions").withEntity(transactionEntity)
      request ~> routes ~> check {
        status should ===(StatusCodes.Created)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should startWith(
          s"""{"receiverId":${JUAN.id.get},"amount":1000.0,
             |"senderId":${DAVID.id.get},"id":3,"status":"PENDING","transactionDate":""".stripMargin.replaceAll("\n", "")
        )
      }

    }

    "list the transactions after inserting them (should be 4) (GET /transactions)" in {
      /*Should be 4 because there are:
        1) Initial Transaction to the MAIN BANK
        2) First Deposit MAIN BANK -> user1
        3) user1 send 2000 to user2
        4) user2 send 1000 to user1
      */
      val request = HttpRequest(uri = "/transactions")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        val transactions = entityAs[Transactions]
        transactions.transactions.size should be(4)
      }
    }

    "handle errors in case that the users doesnt exists (POST /transactions)" in {
      val transaction = Transaction(None, 25, 54, 1000)
      val transactionEntity = Marshal(transaction).to[MessageEntity].futureValue
      val request = Post(uri = "/transactions").withEntity(transactionEntity)
      request ~> routes ~> check {
        status should ===(StatusCodes.NotFound)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"t":{"senderId":25,"receiverId":54,"amount":1000.0},"errors":["The sender 25 doesnt exist","The receiver 54 doesnt exist"]}""")
      }
    }

    "Create a REJECTED transaction in case there is not enough money (amount > account balance) (POST /transactions)" in {
      val transaction = Transaction(None, DAVID.id.get, JUAN.id.get, 10000)
      val transactionEntity = Marshal(transaction).to[MessageEntity].futureValue
      val request = Post(uri = "/transactions").withEntity(transactionEntity)
      request ~> routes ~> check {
        status should ===(StatusCodes.Created)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should startWith(s"""{"receiverId":${JUAN.id.get},"amount":10000.0,"senderId":${DAVID.id.get},"id":4,"status":"REJECTED","transactionDate":""")
      }
    }
  }

  object UserCreationTest {

    val user = User(None, "David", 42, "UK")
    val DAVID = (userActor ? CreateUser(user)).mapTo[User].futureValue
    val user2 = User(None, "Juan", 33, "UK")
    val JUAN = (userActor ? CreateUser(user2)).mapTo[User].futureValue

    (clientActor ? CreateDeposit(DAVID.id.get, 5000)).mapTo[OperationPerformed].futureValue
  }

}