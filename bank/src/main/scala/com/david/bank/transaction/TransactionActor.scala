package com.david.bank.transaction

import java.util.Date

import akka.actor.{ Actor, ActorLogging, ActorRef, Props, Stash }
import akka.stream.{ ActorMaterializer, ActorMaterializerSettings }
import com.david.bank.BankConstants._
import com.david.bank.QuickstartServer.{ system, userRegistryActor }
import com.david.bank.transaction.TransactionUsersValidatorActor.{ ValidateTransaction, ValidatedTransaction }
import com.david.bank.user.User

import scala.concurrent._
import ExecutionContext.Implicits.global

object TransactionStatus extends Enumeration {
  type TransactionStatus = Value
  val REJECTED, PENDING, CONFIRMED = Value

  def withNameOpt(s: String): Option[Value] = values.find(_.toString == s)
}
final case class Transaction(senderId: String, receiverId: String, amount: Double, date: Date, status: Option[TransactionStatus.Value] = None)
final case class Transactions(transactions: Seq[Transaction])

object TransactionActor {
  final case class CreateTransaction(transaction: Transaction)
  final case object GetTransactions
  final case class GetTransactions(userId: String)
  final case class TransactionErrors(t: Transaction, errors: Seq[String])

  def props: Props = Props[TransactionActor]
}

class TransactionActor(userActor: ActorRef) extends Actor with ActorLogging with Stash {

  import TransactionActor._

  override def receive: Receive = {
    case CreateTransaction(transaction) =>
      val userValidator: ActorRef = system.actorOf(Props(new TransactionUsersValidatorActor(userActor)), "transactionActor")
      userValidator ! ValidateTransaction(transaction, sender)
    case ValidatedTransaction(t, errors, caller) if errors isEmpty =>
      val transaction = Right(TransactionService.create(t))
      caller ! transaction
    case ValidatedTransaction(t, errors, caller) if !errors.isEmpty =>
      caller ! Left(TransactionErrors(t, errors))
    case GetTransactions =>
      sender ! TransactionService.getAll
    case GetTransactions(userId) =>
      sender ! TransactionService.getAll(userId)

  }
}