package com.david.bank.user

import com.david.bank.BankConstants

object UserService {
  private var users = Set.empty[User]
  private var sequenceId = 1


  def init()= users += BankConstants.MAIN_BANK.copy(id = Some(0))

  def getAll(): Seq[User]= users.toSeq

  def add(user: User) : User = {
    val userToStore=user.copy(id = Some(sequenceId))
    users += userToStore
    sequenceId = sequenceId + 1
    userToStore
  }

  def get(id: Int) : Option[User] = users.find(_.id.get == id)

  def exists(id: Int): Boolean= users.exists(_.id.get == id)

  init()
}
