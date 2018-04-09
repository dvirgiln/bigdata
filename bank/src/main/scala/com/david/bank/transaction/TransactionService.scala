package com.david.bank.transaction

import com.david.bank.BankConstants.DATABASE_NAME
import com.david.bank.user.User
import org.mongodb.scala.MongoCollection
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.concurrent.Future

object TransactionService {

  var transactions = Set.empty[Transaction]
  def create(t: Transaction): Transaction = {
    val transaction = validateTransaction(t) match {
      case true => t.copy(status = Some(TransactionStatus.PENDING))
      case false => t.copy(status = Some(TransactionStatus.REJECTED))
    }
    transactions += transaction
    transaction
  }

  def getAll(): Transactions = Transactions(transactions.toSeq)

  def getAll(userId: String): Transactions = Transactions(transactions.filter { case Transaction(s, r, _, _, _) => r == userId || s == userId }.toSeq)

  def getBalance(userId: String): Double = getAll(userId).transactions.filterNot(_.status == TransactionStatus.REJECTED).foldLeft(0.0) { (balance, transaction) =>
    transaction match {
      case Transaction(s, _, amount, _, status) => balance - amount
      case Transaction(_, r, amount, _, _) => balance + amount
    }
  }

  def validateTransaction(t: Transaction): Boolean = getBalance(t.senderId) >= t.amount

}
