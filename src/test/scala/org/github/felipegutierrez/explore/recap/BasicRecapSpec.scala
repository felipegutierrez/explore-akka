package org.github.felipegutierrez.explore.recap

import org.scalatest.flatspec.AnyFlatSpec

class BasicRecapSpec extends AnyFlatSpec {

  "a for comprehensions" should
    "return the concatenate values in a list like a flatMap" in {
    val listOne = List(1, 2, 3, 4, 5)
    val listTwo = List("a", "b")
    val separator = "-"
    val resultOne = BasicRecap.testForComprehensions(listOne, listTwo, separator)
    val resultTwo = BasicRecap.testFlatMap(listOne, listTwo, separator)
    assert(resultOne == resultTwo)
  }
  "a factorial with recursion" should
    "return the same value when compared to a factorial with recursion and @tailrec" in {
    val factorial = BasicRecap.factorial(10)
    val factorialWithTailrec = BasicRecap.factorialWithTailrec(10)
    assert(factorial == factorialWithTailrec)
  }
  "an incrementer using functional programming" should
    "return same value when using the apply method" in {
    val incremented = BasicRecap.incrementer(42)
    val incrementedAgain = BasicRecap.incrementer.apply(42)
    assert(incremented == incrementedAgain)
  }
  "an incrementer using functional programming" should
    "return same value when compared to an anonymous incrementer" in {
    val incremented = BasicRecap.incrementer(42)
    val anonymousIncremented = BasicRecap.anonymousIncrementer(42)
    assert(incremented == anonymousIncremented)

    val myIncrementedList = List(40, 100, 3, 20, 38).map(BasicRecap.incrementer)
    val myIncrementedListAnonymous = List(40, 100, 3, 20, 38).map(BasicRecap.anonymousIncrementer)
    assert(myIncrementedList == myIncrementedListAnonymous)
  }
}
