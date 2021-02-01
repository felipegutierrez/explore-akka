package org.github.felipegutierrez.explore.recap

import org.scalatest.flatspec.AnyFlatSpec

class AdvancedHighOrderFunctionSpec extends AnyFlatSpec {

  import AdvancedHighOrderFunction._

  "a higher order function that contains a partial function" should
    "crash if any value is not defined inside the partial function" in {
    val listOne = List(1, 2, 5)
    val listOneResult = higherOrderFunctionWithPartialFunction(listOne)
    assertResult(List(11, 22, 55))(listOneResult)

    try {
      val listTwo = List(1, 2, 3)
      higherOrderFunctionWithPartialFunction(listTwo)
    } catch {
      case exception => assert(exception.toString.contains("MatchError"))
    }
  }
}
