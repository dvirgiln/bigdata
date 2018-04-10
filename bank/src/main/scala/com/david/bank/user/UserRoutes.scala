package com.david.bank.user

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
import com.david.bank.user.UserActor._
import com.david.bank.util.JsonSupport
import scala.concurrent.duration._
import scala.concurrent.Future

trait UserRoutes extends JsonSupport {
  // we leave these abstract, since they will be provided by the App
  implicit def system: ActorSystem

  lazy val logUser = Logging(system, classOf[UserRoutes])
  implicit val timeout: Timeout
  def userActor: ActorRef

  lazy val userRoutes: Route =
    pathPrefix("users") {
      concat(
        path(Segment) { userId =>
          concat(
            get {
              logUser.info(s"Getting user $userId")
              val maybeUser: Future[Option[User]] =
                (userActor ? GetUser(userId.toInt)).mapTo[Option[User]]
              rejectEmptyResponse {
                complete(maybeUser)
              }
            }
          )
        },
        pathEnd {
          concat(
            get {
              val users: Future[Users] =
                (userActor ? GetUsers).mapTo[Users]
              complete(users)
            },
            post {
              entity(as[User]) { user =>
                val userCreated: Future[ActionPerformed] =
                  (userActor ? CreateUser(user)).mapTo[ActionPerformed]
                onSuccess(userCreated) { performed =>
                  logUser.info("Created user [{}]: {}", user.name, performed.description)
                  complete((StatusCodes.Created, performed))
                }
              }
            }
          )
        }
      )
    }
}
