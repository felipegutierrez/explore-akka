package org.github.felipegutierrez.explore.recap

import org.scalatest.flatspec.AnyFlatSpec

class AdvancedCurriesPAFSpec extends AnyFlatSpec {

  import AdvancedCurriesPAF._

  "my curried function" should
    "return same result of a simple method" in {
    val advancedCurriesPAF = new AdvancedCurriesPAF()
    val curriedAdd4Function: Int => Int = advancedCurriesPAF.superAdder(4)
    val simpleAddFunction = advancedCurriesPAF.simpleAddFunction
    val simpleAdd4Function = (x: Int) => advancedCurriesPAF.simpleAddFunction(4, x)
    assert(curriedAdd4Function(2) == simpleAddFunction(4, 2))
    assert(curriedAdd4Function(2) == simpleAdd4Function(2))

    val curriedAddMethod = advancedCurriesPAF.curriedAdded(4)(2)
    val simpleAddMethod = advancedCurriesPAF.simpleAddMethod(4, 2)
    assert(curriedAddMethod == simpleAddMethod)
  }
  "my curried function" should
    "convert the expression using ETA-expansion automatically for me when using _ (underscore) after a curried function with only one parameter defined" in {
    val advancedCurriesPAF = new AdvancedCurriesPAF()
    val curriedAdd4FunctionWithETAexpasion = advancedCurriesPAF.curriedAdded(4) _
    assert(curriedAdd4FunctionWithETAexpasion(2) == 6)

    val curriedAdd4FunctionWithETAexpasion2 = advancedCurriesPAF.curriedAdded(4)(_)
    assert(curriedAdd4FunctionWithETAexpasion2(2) == 6)
  }
  "my set implementation of Set" should
    "add values and another set" in {
    val advancedCurriesPAF = new AdvancedCurriesPAF()
    val a = "hello "
    val b = "Felipe"
    val c = "how are you?"
    val insertName = advancedCurriesPAF.concatenator(a, _: String, c)
    val res = insertName(b)
    assert(res.equals(a + b + c))
  }
  "the compiler" should
    "do the ETA expansion using curried formatter" in {
    val advancedCurriesPAF = new AdvancedCurriesPAF()
    val numbers = List(Math.PI, Math.E, 1, 9.8, 1.3e-12)

    // compiler does sweet eta-expansion for us
    assert(numbers.map(advancedCurriesPAF.simpleFormat) == List("3.14", "2.72", "1.00", "9.80", "0.00"))
    assert(numbers.map(advancedCurriesPAF.seriousFormat) == List("3.141593", "2.718282", "1.000000", "9.800000", "0.000000"))
    assert(numbers.map(advancedCurriesPAF.preciseFormat) == List("3.141592653590", "2.718281828459", "1.000000000000", "9.800000000000", "0.000000000001"))
  }
}
