package com.david.bank.user

//#user-registry-actor
import akka.actor.{ Actor, ActorLogging, ActorRef, Props }

//#user-case-classes
final case class User(id: String, name: String, age: Int, countryOfResidence: String)
final case class Users(users: Seq[User])
final case class UserValidation(id: String, exists: Boolean, caller: ActorRef, caller2: ActorRef)

//#user-case-classes

object UserRegistryActor {
  final case class ActionPerformed(description: String)
  final case object GetUsers
  final case class CreateUser(user: User)
  final case class GetUser(id: String)
  final case class ValidateUser(id: String, caller: ActorRef, caller2: ActorRef)

  def props: Props = Props[UserRegistryActor]
}

class UserRegistryActor extends Actor with ActorLogging {
  import UserRegistryActor._

  var users = Set.empty[User]

  def receive: Receive = {
    case GetUsers =>
      sender() ! Users(users.toSeq)
    case CreateUser(user) =>
      users += user
      sender() ! ActionPerformed(s"User ${user.name} created.")
    case GetUser(id) =>
      sender() ! users.find(_.id == id)
    case ValidateUser(id, caller, caller2) =>
      sender() ! UserValidation(id, users.exists(_.id == id), caller, caller2)
  }
}
//#user-registry-actor