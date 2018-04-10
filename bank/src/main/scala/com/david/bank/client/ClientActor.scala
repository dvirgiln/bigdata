package com.david.bank.client

import java.util.Date

import akka.actor.{ Actor, ActorLogging, ActorRef, Props, Stash }
import com.david.bank.transaction.TransactionActor.{ CreateTransaction, GetAllTransactionsBasic, GetTransactionsBasic, GetTransactionsDetailed }
import com.david.bank.transaction.{ Transaction, TransactionActor, TransactionStatus }
import com.david.bank.user.User
import com.david.bank.user.UserActor._
import com.david.bank.BankConstants._

final case class ClientsBasicInfo(clients: Seq[ClientBasicInfo])

final case class ClientBasicInfo(user: User, balance: Double)

final case class ClientDetailedInfo(user: User, balance: Double, transactions: Seq[Transaction])
final case class Error(error: String)
final case class Deposit(userId: Int, deposit: Double)

object ClientActor {

  final case class CreateClient(user: User, initialDeposit: Double)

  final case class CreateDeposit(userId: Int, deposit: Double)

  final case class GetClientBasicInfo(userId: Int)

  final case object GetAllClientsBasicInfo

  final case class GetAllClientsBasicInfo(users: Seq[User], caller: ActorRef)

  final case class GetClientDetailedInfo(userId: Int)

  final case class OperationPerformed(message: String)

  final case class CreateTransactionAfterUserCreated(userId: Int, initialDeposit: Double, caller: ActorRef)

  def props: Props = Props[ClientActor]
}

class ClientActor(userActor: ActorRef, transactionActor: ActorRef) extends Actor with ActorLogging with Stash {

  import ClientActor._

  override def receive: Receive = {
    case CreateClient(user, initialDeposit) =>
      userActor ! CreateUserWithInitialDeposit(user, initialDeposit, sender)
    case CreateTransactionAfterUserCreated(userId, initialDeposit, caller) =>
      transactionActor ! CreateTransaction(Transaction(None, MAIN_BANK.id.get, userId, initialDeposit, Some(new Date()), Some(TransactionStatus.CONFIRMED)))
      caller ! OperationPerformed(s"Created user with userId $userId  with an initial deposit of $initialDeposit")
    case GetClientBasicInfo(userId) => userActor ! GetUserBasicInformation(userId, sender)
    case GetClientDetailedInfo(userId) => userActor ! GetUserDetailedInformation(userId, sender)
    case UserBasicInformation(user, caller) => transactionActor ! GetTransactionsBasic(user, caller)
    case UserDetailedInformation(user, caller) => transactionActor ! GetTransactionsDetailed(user, caller)
    case CreateDeposit(userId, initialDeposit) => userActor ! ValidateUserWithDeposit(userId, initialDeposit, sender)
    case ValidatedUserForDeposit(user, deposit, caller) =>
      transactionActor ! CreateTransaction(Transaction(None, MAIN_BANK.id.get, user.id.get, deposit, Some(new Date()), Some(TransactionStatus.CONFIRMED)))
      caller ! OperationPerformed(s"Deposit of $deposit confirmed for the account of \n $user")
    case GetAllClientsBasicInfo =>
    case GetAllClientsBasicInfo(users, caller) =>
      transactionActor ! GetAllTransactionsBasic(users, caller)
    case UserNotExist(userId, caller) =>
      caller ! None
  }
}
