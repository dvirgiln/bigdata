package com.david.bank.user

//#user-registry-actor
import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import com.david.bank.BankConstants
import com.david.bank.client.ClientActor.{ CreateClient, CreateTransactionAfterUserCreated, GetAllClientsBasicInfo }

//#user-case-classes
final case class User(id: Option[Int], name: String, age: Int, countryOfResidence: String)
final case class Users(users: Seq[User])
final case class UserValidation(id: Int, exists: Boolean, caller: ActorRef, caller2: ActorRef)

//#user-case-classes

object UserActor {
  final case class ActionPerformed(description: String)
  final case object GetUsers
  final case class CreateUser(user: User)
  final case class GetUser(id: Int)
  final case class ValidateUser(id: Int, caller: ActorRef, caller2: ActorRef)
  final case class CreateUserWithInitialDeposit(user: User, initialDeposit: Double, caller: ActorRef)
  final case class ValidateUserWithDeposit(userId: Int, initialDeposit: Double, caller: ActorRef)
  final case class GetUserBasicInformation(id: Int, caller: ActorRef)
  final case class GetUserDetailedInformation(id: Int, caller: ActorRef)
  final case class UserNotExist(id: Int, caller: ActorRef)
  final case class UserBasicInformation(user: User, caller: ActorRef)
  final case class UserDetailedInformation(user: User, caller: ActorRef)
  final case class ValidatedUserForDeposit(user: User, deposit: Double, caller: ActorRef)
  final case class GetAllUsers(caller: ActorRef)
  def props: Props = Props[UserActor]
}

class UserActor extends Actor with ActorLogging {
  import UserActor._
  import UserService._

  def receive: Receive = {
    case GetUsers =>
      sender() ! Users(getAll)
    case CreateUser(user) =>
      val stored = add(user)
      sender() ! ActionPerformed(s"User $stored created.")
    case GetUser(id) =>
      sender() ! get(id)
    case ValidateUser(id, caller, caller2) =>
      sender() ! UserValidation(id, exists(id), caller, caller2)
    case CreateUserWithInitialDeposit(user, initialDeposit, caller) =>
      val storedUser = add(user)
      sender ! CreateTransactionAfterUserCreated(storedUser.id.get, initialDeposit, caller)
    case GetUserBasicInformation(userId, caller) =>
      val msg = get(userId).toList.headOption match {
        case None => UserNotExist(userId, caller)
        case Some(user) => UserBasicInformation(user, caller)
      }
      sender ! msg
    case GetUserDetailedInformation(userId, caller) =>
      val msg = get(userId).toList.headOption match {
        case None => UserNotExist(userId, caller)
        case Some(user) => UserDetailedInformation(user, caller)
      }
      sender ! msg
    case ValidateUserWithDeposit(userId, deposit, caller) =>
      val msg = get(userId).toList.headOption match {
        case None => UserNotExist(userId, caller)
        case Some(user) => ValidatedUserForDeposit(user, deposit, caller)
      }
      sender ! msg
    case GetAllUsers(caller) => sender ! GetAllClientsBasicInfo(getAll.filterNot(_.id.get == 0).toSeq, caller)
  }
}
//#user-registry-actor