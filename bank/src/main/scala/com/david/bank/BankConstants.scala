package com.david.bank

import com.david.bank.user.User

object BankConstants {
  val DATABASE_NAME = "transactions"
  val MAIN_BANK = User(Some(0), "Main Bank", 100, "UK")
}
