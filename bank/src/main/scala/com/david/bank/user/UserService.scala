package com.david.bank.user

import com.david.bank.BankConstants

import scala.collection.mutable

object UserService {
  private var users = mutable.Map.empty[Int, User]
  private var sequenceId = 1

  def init() = {
    sequenceId = 1
    users = mutable.Map.empty[Int, User]
    users += (0 -> BankConstants.MAIN_BANK.copy(id = Some(0)))
  }

  def getAll(): Seq[User] = users.values.toSeq.sortBy(_.id)

  def add(user: User): User = {
    val userToStore = user.copy(id = Some(sequenceId))
    users += (sequenceId -> userToStore)
    sequenceId = sequenceId + 1
    userToStore
  }

  def update(user: User): Boolean = {
    users.get(user.id.get).headOption match {
      case Some(_) =>
        users(user.id.get) = user
        true
      case None => false
    }
  }

  def get(id: Int): Option[User] = users.get(id)

  def exists(id: Int): Boolean = users.contains(id)

  init()
}
