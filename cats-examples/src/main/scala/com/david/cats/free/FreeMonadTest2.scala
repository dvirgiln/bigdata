package com.david.cats.free

import cats.free.Free




object OrderDomain {
  type Symbol = String
  type Response = String
  sealed trait Orders[A]
  case class Buy(stock: Symbol, amount: Int) extends Orders[Response]
  case class Sell(stock: Symbol, amount: Int) extends Orders[Response]

  type OrdersF[A] = Free[Orders, A]
}

object OrderComputations{
  import OrderDomain._
  import cats.free.Free._

  def buy(stock: Symbol, amount: Int): OrdersF[Response] = liftF[Orders, Response](Buy(stock, amount))
  def sell(stock: Symbol, amount: Int): OrdersF[Response] = liftF[Orders, Response](Sell(stock, amount))
}

object OrderInterpreter{
  import OrderDomain._
  import cats.{Id, ~>}

  def orderPrinter: Orders ~> Id =
    new (Orders ~> Id) {
      def apply[A](fa: Orders[A]): Id[A] = fa match {
        case Buy(stock, amount) =>
          println(s"Buying $amount of $stock")
          "ok"
        case Sell(stock, amount) =>
          println(s"Selling $amount of $stock")
          "ok"
      }
    }
}
object OrdersApp extends App{
  import OrderComputations._
  import OrderDomain._
  import OrderInterpreter._
  val smartTrade: OrdersF[Response] = for {
    _ <- buy("APPL", 50)
    _ <- buy("MSFT", 10)
    rsp <- sell("GOOG", 200)
  } yield rsp

  val a=smartTrade.foldMap(orderPrinter)
  println(s"a is $a")
}

