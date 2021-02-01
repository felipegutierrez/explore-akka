package org.github.felipegutierrez.explore.recap

import org.scalatest.flatspec.AnyFlatSpec

class AdvancedMonadsSpec extends AnyFlatSpec {

  import AdvancedMonads._

  "my AttemptMonad" should
    "return success or failure" in {
    val value = 5

    val add10AttemptMonad = AttemptMonad(value).flatMap[Int](x => AttemptMonad(x + 10))
    println(add10AttemptMonad)
    assert(add10AttemptMonad.toString.contains("SuccessMonad(15)"))

    val add10AttemptMonadDouble = AttemptMonad(5.5).flatMap(x => AttemptMonad(x + 10))
    println(add10AttemptMonadDouble)
    assert(add10AttemptMonadDouble.toString.contains("SuccessMonad(15.5)"))

    val divide10AttemptMonadError = AttemptMonad(10).flatMap(x => AttemptMonad(x / 0))
    println(divide10AttemptMonadError)
    assert(divide10AttemptMonadError.toString.contains("ArithmeticException"))
  }
  "my AttemptMonad with RuntimeException" should
    "fail" in {
    val result = attemptRuntimeException
    assert(result.toString.contains("RuntimeException"))
  }
  "my lazy monad" should
    "not evaluate the inner expression before I try to use it" in {
    var evaluatedValue: String = ""
    val value: String = "Today I don't feel like doing anything"
    val lazyInstance = LazyMonad {
      println("I am evaluating the inner Lazy Monad =)")
      evaluatedValue = value
    }
    assertResult("")(evaluatedValue)
    lazyInstance.use
    assertResult(value)(evaluatedValue)
  }
  "my lazy monad that also evaluates flatMaps" should
    "not evaluate the inner expression of a flatMap before I try to use it" in {
    var evaluatedValue: Int = 0
    val value: Int = 10
    val lazyInstance = LazyMonad {
      evaluatedValue = value
      println("I am evaluating the inner Lazy Monad =)")
      5
    }
    val flatMappedInstance = lazyInstance.flatMap(x => LazyMonad {
      10 * x
    })
    assertResult(0)(evaluatedValue)
    assertResult(50)(flatMappedInstance.use)
    assertResult(value)(evaluatedValue)
  }
  "my lazy monad that is very lazy" should
    "evaluate inner expression only once" in {
    var evaluatedValue: Int = 0
    val value: Int = 10
    val lazyInstance = LazyMonad {
      evaluatedValue = evaluatedValue + value
      println("I am evaluating the inner Lazy Monad =)")
      4
    }
    val flatMappedInstance01 = lazyInstance.flatMap(x => LazyMonad {
      10 * x
    })
    val flatMappedInstance02 = lazyInstance.flatMap(x => LazyMonad {
      10 * x
    })
    val flatMappedInstance03 = lazyInstance.flatMap(x => LazyMonad {
      10 * x
    })
    assertResult(0)(evaluatedValue)
    assertResult(40)(flatMappedInstance01.use)
    assertResult(40)(flatMappedInstance02.use)
    assertResult(40)(flatMappedInstance03.use)
    assertResult(value)(evaluatedValue)
  }
}
