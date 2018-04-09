package com.david.bank.transaction

import akka.actor.{ Actor, ActorLogging, ActorRef, LoggingFSM, Props, Stash }
import com.david.bank.transaction.TransactionActor.CreateTransaction
import com.david.bank.transaction.TransactionUsersValidatorActor.{ ValidateTransaction, ValidatedTransaction }
import com.david.bank.user.UserRegistryActor.ValidateUser
import com.david.bank.user.UserValidation

final case class TransactionValidationError(transaction: Option[Transaction], errors: Seq[String])

object TransactionUsersValidatorActor {
  final case class ValidateTransaction(transaction: Transaction, caller: ActorRef)
  final case class ValidatedTransaction(transaction: Transaction, errors: Seq[String], caller: ActorRef)

  def props: Props = Props[TransactionUsersValidatorActor]
}

class TransactionUsersValidatorActor(userActor: ActorRef) extends LoggingFSM[TransactionValidationStatus, TransactionValidationError] {

  startWith(NonValidated, TransactionValidationError(None, Seq.empty[String]))

  when(NonValidated) {
    case Event(ValidateTransaction(transaction, controllerCaller), _) =>
      userActor ! ValidateUser(transaction.senderId, controllerCaller, sender)
      stay.using(TransactionValidationError(Some(transaction), Seq.empty[String]))
    case Event(UserValidation(id, exists, controllerCaller, transactionCaller), validation @ TransactionValidationError(Some(Transaction(senderId, _, _, _, _)), errors)) =>
      userActor ! ValidateUser(validation.transaction.get.senderId, controllerCaller, transactionCaller)
      exists match {
        case false => goto(ValidatedSender).using(validation.copy(errors = errors :+ s""))
        case true => goto(ValidatedSender).using(validation)
      }
  }
  when(ValidatedSender) {
    case Event(UserValidation(id, exists, controllerCaller, transactionCaller), validation @ TransactionValidationError(Some(Transaction(_, receiverId, _, _, _)), errors)) =>
      val finalErrors = exists match {
        case true => errors
        case false => errors :+ s"The receiver $receiverId doesnt exist"
      }
      transactionCaller ! ValidatedTransaction(validation.transaction.get, errors, controllerCaller)
      context.stop(self)
      goto(NonValidated).using(TransactionValidationError(None, Seq.empty[String]))
  }
}

sealed trait TransactionValidationStatus

case object NonValidated extends TransactionValidationStatus
case object ValidatedSender extends TransactionValidationStatus
case object ValidatedReceiver extends TransactionValidationStatus

