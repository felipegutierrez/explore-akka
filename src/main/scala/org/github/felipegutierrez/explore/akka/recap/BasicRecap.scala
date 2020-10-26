package org.github.felipegutierrez.explore.akka.recap

import scala.annotation.tailrec
import scala.util.Try

object BasicRecap extends App {

  run()

  def run(): Unit = {
    val aCondition: Boolean = false

    val aExpression = if (aCondition) 1 else 0

    val aCodeBlock = {
      if (aCondition) 1
      0
    }

    println(s"factorial of 6: ${factorial(6)}")
    println(s"factorialWithTailrec of 6: ${factorialWithTailrec(6)}")


    val aTypeUnit = println("Hello Scala")
    val aDog: Animal = new Dog
    // method notations
    val aCrocodile = new Crocodile
    // anonymous classes
    val aCarnivore = new Carnivore {
      override def eat(a: Animal): Unit = println("roar")
    }
    // exceptions
    val aPotentialFailure = try {
      throw new RuntimeException("I am innocent. I swear!")
    } catch {
      case e: Exception => "I caught an exception!"
    } finally {
      println("side effects to print no matter what happens inside the try")
    }

    // functional programming
    val incrementer = new Function[Int, Int] {
      override def apply(v1: Int): Int = v1 + 1
    }
    val incremented = incrementer(42)
    println(incremented)
    val incrementedAgain = incrementer.apply(42)
    println(incrementedAgain)

    val anonymousIncrementer = (x: Int) => x + 1
    val anonymousIncremented = anonymousIncrementer(42)
    println(anonymousIncremented)

    val myIncrementedList = List(40, 100, 3, 20, 38).map(anonymousIncrementer)
    println(myIncrementedList)

    // for comprehensions
    println("for comprehensions")
    val pairs = for {
      num <- List(1, 2, 3, 4, 5)
      char <- List('a', 'b')
    } yield num + "-" + char
    println(pairs)
    val pairsAgain = List(1, 2, 3, 4, 5).flatMap(num => List('a', 'b').map(char => num + "-" + char))
    println(pairsAgain)

    // Option and Try
    val anOption = Some(2)
    val aTry = Try {
      throw new RuntimeException
    }


    // functions
    def aFunction(x: Int): Int = x + 1

    aCrocodile.eat(aDog)
    aCrocodile eat aDog
    aCarnivore eat aDog


  }

  // Object oriented
  trait Carnivore {
    def eat(a: Animal): Unit
  }

  // generics
  abstract class MyList[+A]

  class Animal

  class Dog extends Animal

  class Crocodile extends Animal with Carnivore {
    override def eat(a: Animal): Unit = println("crunch!")
  }

  // case classes
  case class Person(name: String, age: Int)

  // companion objects
  object MyList

  def factorial(n: Int): Int = {
    def factorial(n: Int, acc: Int): Int = {
      if (n <= 0) acc
      else factorial(n - 1, acc * n)
    }

    factorial(n, 1)
  }

  def factorialWithTailrec(n: Int): Int = {
    // recursion: stack and tail
    @tailrec def factorialWithTailrec(n: Int, accumulator: Int): Int = {
      if (n <= 0) accumulator
      else factorialWithTailrec(n - 1, n * accumulator)
    }
    factorialWithTailrec(n, 1)
  }
}
