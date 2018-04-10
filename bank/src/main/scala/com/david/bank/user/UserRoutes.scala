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
                user.id match {
                  case None =>
                    val userCreated: Future[User] =
                      (userActor ? CreateUser(user)).mapTo[User]
                    onSuccess(userCreated) { user =>
                      complete((StatusCodes.Created, user))
                    }
                  case Some(_) =>
                    complete(StatusCodes.BadRequest, "The ID should not be included for new resources")
                }

              }
            },
            put {
              entity(as[User]) { user =>
                user.id match {
                  case None =>
                    complete(StatusCodes.BadRequest, "The ID should be included for new resources")
                  case Some(_) =>
                    val userUpdated: Future[Boolean] =
                      (userActor ? UpdateUser(user)).mapTo[Boolean]
                    onSuccess(userUpdated) { updated =>
                      updated match {
                        case true => complete(StatusCodes.OK)
                        case false => complete(StatusCodes.NotFound)
                      }

                    }
                }

              }
            }

          )
        }
      )
    }
}
