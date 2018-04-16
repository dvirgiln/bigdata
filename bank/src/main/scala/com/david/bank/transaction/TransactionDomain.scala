package com.david.bank.transaction

import java.util.Date

object TransactionDomain {
  final case class Transaction(id: Option[Int], senderId: Int, receiverId: Int, amount: Double, transactionDate: Option[Date] = None, status: Option[TransactionStatus.Value] = None)
  final case class Transactions(transactions: Seq[Transaction])
  object TransactionStatus extends Enumeration {
    type TransactionStatus = Value
    val REJECTED, PENDING, CONFIRMED = Value

    def withNameOpt(s: String): Option[Value] = values.find(_.toString == s)
  }
}
