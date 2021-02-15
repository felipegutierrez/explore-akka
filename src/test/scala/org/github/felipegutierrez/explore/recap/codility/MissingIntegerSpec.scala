package org.github.felipegutierrez.explore.recap.codility

import org.scalatest.flatspec.AnyFlatSpec

class MissingIntegerSpec extends AnyFlatSpec {
  "my missing integer application" should
    "return the smallest positive integer greater than 0 that does not occur in a given array" in {
    assertResult(5)(MissingInteger.solution(Array[Int](1, 3, 6, 4, 1, 2)))
    assertResult(4)(MissingInteger.solution(Array[Int](1, 2, 3)))
    assertResult(1)(MissingInteger.solution(Array[Int](-1, -3)))
  }

  "my missing integer application using stream" should
    "return the smallest positive integer greater than 0 that does not occur in a given array" in {
    assertResult(5)(MissingInteger.solutionWithStream(Array[Int](1, 3, 6, 4, 1, 2)))
    assertResult(4)(MissingInteger.solutionWithStream(Array[Int](1, 2, 3)))
    assertResult(1)(MissingInteger.solutionWithStream(Array[Int](-1, -3)))
  }
}
