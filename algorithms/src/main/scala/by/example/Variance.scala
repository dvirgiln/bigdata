package by.example

/**
  * Created by dave on 19/11/16.
  */
class Variance {
  class A
  class B extends A
  class C extends B
  val printB: B => Unit = { b => println("Blah blah") }
  val printA: A => Unit = { a => println("Blah blah blah") }

  def needsB(f: B => Unit, b: B) = f(b)

  needsB(printB, new B)

  needsB(printA, new C)


  class Animal { val sound = "rustle" }

  class Bird extends Animal { override val sound = "call" }

  class Chicken extends Bird { override val sound = "cluck" }

  val getTweet: (Bird => String) = ((a: Animal) => a.sound ) //contravariant on the parameter

  val hatch: (() => Bird) = (() => new Chicken ) //covariant on the type
}
