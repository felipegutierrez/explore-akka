package org.github.felipegutierrez.explore.akka.recap

import org.scalatest.flatspec.AnyFlatSpec

class AdvancedPatternMatchingSpec extends AnyFlatSpec {

  import AdvancedPatternMatching._

  "A List with only one parameter" should
    "use :: Nil Pattern matching" in {
    val result = aListWith(List(1))
    assert(result == "the only element is 1")
  }
  it should "return anything else" in {
    val result = aListWith(List(1, 2))
    assert(result.startsWith("anything else"))
  }

  "a class that is not a case class " should
    "match if it uses the unapply method" in {
    val bob = new PersonIsNotCaseClass("Bob", 23)
    val result = patternMatchingWithNotCaseClass(bob)
    assert(result == "hi, my name is Bob and I am 23 years old.")
  }

  "a pattern matching" should
    "use Boolean in the unapply method" in {
    val result01 = checkNumber(new MathProperty(1))
    assert(result01.equals("this is single digit"))

    val result02 = checkNumber(new MathProperty(12))
    assert(result02.equals("this is even digit"))

    val result03 = checkNumber(new MathProperty(13))
    assert(result03.equals("this is odd digit"))
  }

  "a list pattern matching" should
    "use * for unapplySeq" in {
    val result01 = checkMyList(Cons(1, Cons(2, Cons(3, Empty))))
    assert(result01.equals("starting with 1, 2"))

    val result02 = checkMyList(Cons(11, Cons(22, Cons(33, Empty))))
    assert(result02.equals("anything else"))
  }
}
