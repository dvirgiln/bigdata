package com.david.bank.transaction

import java.util.Date

import akka.actor.{ Actor, ActorLogging, ActorRef, Props, Stash }
import akka.stream.{ ActorMaterializer, ActorMaterializerSettings }
import com.david.bank.BankConstants._
import com.david.bank.QuickstartServer.{ system }
import com.david.bank.client.{ ClientBasicInfo, ClientDetailedInfo, ClientsBasicInfo }
import com.david.bank.transaction.TransactionUsersValidatorActor.{ ValidateTransaction, ValidatedTransaction }
import com.david.bank.user.User

import scala.concurrent._
import ExecutionContext.Implicits.global

object TransactionStatus extends Enumeration {
  type TransactionStatus = Value
  val REJECTED, PENDING, CONFIRMED = Value

  def withNameOpt(s: String): Option[Value] = values.find(_.toString == s)
}
final case class Transaction(id: Option[Int], senderId: Int, receiverId: Int, amount: Double, transactionDate: Option[Date], status: Option[TransactionStatus.Value] = None)
final case class Transactions(transactions: Seq[Transaction])

object TransactionActor {
  final case class CreateTransaction(transaction: Transaction)
  final case object GetTransactions
  final case class GetTransactions(userId: Int)
  final case class TransactionErrors(t: Transaction, errors: Seq[String])
  final case class GetTransactionsBasic(user: User, caller: ActorRef)
  final case class GetTransactionsDetailed(user: User, caller: ActorRef)
  final case class GetAllTransactionsBasic(users: Seq[User], caller: ActorRef)
  def props: Props = Props[TransactionActor]
}

class TransactionActor(userActor: ActorRef) extends Actor with ActorLogging with Stash {

  import TransactionActor._

  override def receive: Receive = {
    case CreateTransaction(transaction) =>
      val userValidator: ActorRef = system.actorOf(Props(new TransactionUsersValidatorActor(userActor)), s"TransactionUserValidator${transaction.senderId}_${transaction.receiverId}")
      userValidator ! ValidateTransaction(transaction, sender)
    case ValidatedTransaction(t, errors, caller) if errors isEmpty =>
      val transaction = Right(TransactionService.add(t))
      caller ! transaction
    case ValidatedTransaction(t, errors, caller) if !errors.isEmpty =>
      caller ! Left(TransactionErrors(t, errors))
    case GetTransactions =>
      sender ! Transactions(TransactionService.getAll)
    case GetTransactions(userId) =>
      sender ! Transactions(TransactionService.getAll(userId))
    case GetTransactionsBasic(user, caller) =>
      val balance = TransactionService.getBalance(user.id.get)
      caller ! Some(ClientBasicInfo(user, balance))
    case GetTransactionsDetailed(user @ User(Some(id), _, _, _), caller) =>
      val balance = TransactionService.getBalance(id)
      val transactions = TransactionService.getAll(id)
      caller ! Some(ClientDetailedInfo(user, balance, transactions))
    case GetAllTransactionsBasic(users, caller) =>
      val msg = ClientsBasicInfo(users.map { u =>
        val balance = TransactionService.getBalance(u.id.get)
        ClientBasicInfo(u, balance)
      })
      caller ! msg
  }
}