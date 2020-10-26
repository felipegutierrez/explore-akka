package org.github.felipegutierrez.explore.akka.recap

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

object AdvancedRecap extends App {

  run()

  def run(): Unit = {
    type ReceiveFunction = PartialFunction[Any, Unit]

    println("using partial functions .................")
    val partialFunction: PartialFunction[Int, Int] = {
      case 1 => 11
      case 2 => 22
      case 3 => 33
      case 5 => 55
    }
    println(partialFunction(1))
    try {
      println(pf(4))
    } catch {
      case e: Exception => println("Exception: " + e.getMessage)
    }
    println(partialFunction(3))
    try {
      println(partialFunction(4))
    } catch {
      case e: Exception => println("Exception: " + e.getMessage)
    }

    val anInstance: Action = new Action {
      override def act(x: Int): Int = x + 1
    }
    val anotherInstance: Action = (x: Int) => (x + 1)
    println(s"anInstance: ${anInstance.act(1)}")
    println(s"anotherInstance: ${anotherInstance.act(1)}")

    val prependedList = 1 :: 2 :: List(3, 4)
    println(s"prependedList ${prependedList.toString()}")
    val samePrependedList = List(3, 4).::(2).::(1)
    println(s"samePrependedList ${samePrependedList.toString()}")

    // orElse
    println("orElse ................")
    val pfChain = partialFunction.orElse[Int, Int] {
      case 60 => 9000
    }
    println(pfChain(5))
    println(pfChain(60))
    try {
      println(pfChain(5648))
    } catch {
      case e: Exception => println("Exception: " + e.getMessage)
    }


    val func: (Int => Int) = partialFunction
    println(func(1))
    println(func(5))
    println("type aliases ....................")


    println("Lifting partial functions ................")
    val lifted = partialFunction.lift
    println(lifted(3))
    println(lifted(4444))


    def receive: ReceiveFunction = {
      case 1 => println("hello")
      case _ => println("confused ...")
    }


    println("implicit defs ....................")


    implicit def fromStringToPerson(string: String): Person = Person(string)

    println("Peter".greet)


    println("Lassie".bark)


    println("implicits .................")
    implicit val inverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
    val listInverseOrder = List(1, 2, 3, 4, 5).sorted
    println(listInverseOrder)

    import scala.concurrent.ExecutionContext.Implicits.global

    val future = Future {
      "hello from the future"
    }
    future onComplete {
      case Success(value) => println(s"success: $value")
      case Failure(exception) => println(s"Exception: $exception.getMessage")
    }
    Await.result(future, 2 seconds)


    println("companion objects of the types included in the call ............")


    val personListSorted: List[Person] = List(Person("Bob"), Person("X9"), Person("Alice"), Person("Peter")).sorted
    println(personListSorted)

    val aMutableContainer: MutableContainer = new MutableContainer
    aMutableContainer.member = 42
    println(s"aMutableContainer.member ${aMutableContainer.member}")
  }

  trait Action {
    def act(x: Int): Int
  }

  println("implicit classes ....................")

  implicit class Dog(name: String) {
    def bark = println("bark!!")
  }

  // syntax sugar for setters and getter in mutable containers
  class MutableContainer {
    private var internalMember: Int = 0

    def member = internalMember // getter
    def member_=(value: Int): Unit = internalMember = value // setter
  }

  case class Person(name: String) {
    def greet = s"Hi, my name is $name"
  }

  object Person {
    implicit val personOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
  }

  val pf = (x: Int) => x match {
    case 1 => 11
    case 2 => 22
    case 3 => 33
    case 5 => 55
    case _ => 0
  }
}
