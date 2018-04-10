package com.david.bank.user

import akka.actor.{ ActorRef, ActorSystem }
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

import scala.concurrent.duration._

class UserRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest
    with UserRoutes with BeforeAndAfterAll {

  implicit lazy val timeout = Timeout(5.seconds)

  val userActor: ActorRef = system.actorOf(UserActor.props, "userRegistryActor")

  lazy val routes = userRoutes

  override def beforeAll() {
    UserService.init
  }

  "UserRoutes" should {
    "return the initial user when no other user is added (GET /users)" in {
      val request = HttpRequest(uri = "/users")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"users":[{"id":0,"name":"Main Bank","age":100,"countryOfResidence":"UK"}]}""")
      }
    }

    "add one user (POST /users)" in {
      val user = User(None, "David", 42, "UK")
      val userEntity = Marshal(user).to[MessageEntity].futureValue
      val request = Post(uri = "/users").withEntity(userEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.Created)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"id":1,"name":"David","age":42,"countryOfResidence":"UK"}""")
      }
    }

    "return a 400 when the user to be created contains already an ID (POST /users)" in {
      val user = User(Some(1), "David", 42, "UK")
      val userEntity = Marshal(user).to[MessageEntity].futureValue
      val request = Post(uri = "/users").withEntity(userEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.BadRequest)
        contentType should ===(ContentTypes.`text/plain(UTF-8)`)
        entityAs[String] should ===("""The ID should not be included for new resources""")
      }
    }

    "list users (GET /users)" in {
      val request = HttpRequest(uri = "/users")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        val expectedResult =
          """{"users":[{"id":0,"name":"Main Bank","age":100,"countryOfResidence":"UK"},
            |{"id":1,"name":"David","age":42,"countryOfResidence":"UK"}]}""".stripMargin.replaceAll("\n", "")
        entityAs[String] should ===(expectedResult)
      }
    }
    "get a user (GET /users)" in {
      val request = HttpRequest(uri = "/users/1")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"id":1,"name":"David","age":42,"countryOfResidence":"UK"}""")
      }
    }

    "handle error in case an user doesnt exist (GET /users)" in {
      val request = HttpRequest(uri = "/users/25")

      request ~> Route.seal(routes) ~> check {
        status shouldEqual StatusCodes.NotFound
      }
    }

    "update user details in case user exist already (PUT /users)" in {
      val user = User(Some(1), "David", 33, "US")
      val userEntity = Marshal(user).to[MessageEntity].futureValue
      val request = Put(uri = "/users").withEntity(userEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
      }
    }

    "return an error 400 in case the id is not provided as part of the update (PUT /users)" in {
      val user = User(None, "David", 33, "US")
      val userEntity = Marshal(user).to[MessageEntity].futureValue
      val request = Put(uri = "/users").withEntity(userEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.BadRequest)
        contentType should ===(ContentTypes.`text/plain(UTF-8)`)
        entityAs[String] should ===("""The ID should be included for new resources""")
      }
    }

    "return an error 404 in case the id of the user provided doesnt exist (PUT /users)" in {
      val user = User(Some(33), "David", 33, "US")
      val userEntity = Marshal(user).to[MessageEntity].futureValue
      val request = Put(uri = "/users").withEntity(userEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.NotFound)
      }
    }
  }

}
