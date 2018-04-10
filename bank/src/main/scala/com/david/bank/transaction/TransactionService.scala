package com.david.bank.transaction

import java.util.Date

import com.david.bank.BankConstants

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.concurrent.Future

object TransactionService {

  var transactions = Set.empty[Transaction]
  var sequenceId = 1
  init()

  def init(): Unit = {
    val initialTransaction = Transaction(Some(0), -1, BankConstants.MAIN_BANK.id.get, Double.MaxValue, Some(new Date()), Some(TransactionStatus.CONFIRMED))
    transactions += initialTransaction
  }
  def add(t: Transaction): Transaction = {
    val transaction = validateTransaction(t) match {
      case true => t.copy(id = Some(sequenceId), status = Some(TransactionStatus.PENDING), transactionDate = Some(new Date()))
      case false => t.copy(id = Some(sequenceId), status = Some(TransactionStatus.REJECTED), transactionDate = Some(new Date()))
    }
    sequenceId = sequenceId + 1
    transactions += transaction
    transaction
  }

  def getAll(): Seq[Transaction] = transactions.toSeq.sortBy(_.transactionDate)

  def getAll(userId: Int): Seq[Transaction] = transactions.filter { case Transaction(_, s, r, _, _, _) => r == userId || s == userId }.toSeq.sortBy(_.transactionDate)

  def getBalance(userId: Int): Double = {
    val all=getAll(userId)

      all.filterNot(_.status == TransactionStatus.REJECTED).foldLeft(0.0) { (balance, transaction) =>
      transaction match {
        case Transaction(_, s, _, amount, _, status) if s == userId => balance - amount
        case Transaction(_, _, r, amount, _, _) if r == userId => balance + amount
      }
    }
  }

  def validateTransaction(t: Transaction): Boolean = getBalance(t.senderId) >= t.amount

}
