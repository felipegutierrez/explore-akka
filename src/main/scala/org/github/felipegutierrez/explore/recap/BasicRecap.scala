package org.github.felipegutierrez.explore.recap

import scala.annotation.tailrec
import scala.util.Try

object BasicRecap {
//  def main(args: Array[String]): Unit = {
//    run()
//  }

  def run(): Unit = {
    testForComprehensions(List(1, 2, 3, 4, 5), List("a", "b"), "-")
    testFlatMap(List(1, 2, 3, 4, 5), List("a", "b"), "-")

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
    val incremented = incrementer(42)
    println(incremented)
    val incrementedAgain = incrementer.apply(42)
    println(incrementedAgain)

    val anonymousIncremented = anonymousIncrementer(42)
    println(anonymousIncremented)

    val myIncrementedList = List(40, 100, 3, 20, 38).map(anonymousIncrementer)
    println(myIncrementedList)

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

  val incrementer = new Function[Int, Int] {
    override def apply(v1: Int): Int = v1 + 1
  }

  val anonymousIncrementer = (x: Int) => x + 1

  def testForComprehensions(listOne: List[Int], listTwo: List[String], separator: String): List[String] = {
    println("for comprehensions")
    val pairs = for {
      num <- listOne
      char <- listTwo
    } yield num + separator + char
    println(pairs)
    pairs
  }

  def testFlatMap(listOne: List[Int], listTwo: List[String], separator: String): List[String] = {
    println("flatMap")
    val pairs = listOne.flatMap(num => listTwo.map(char => num + separator + char))
    println(pairs)
    pairs
  }

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
}
