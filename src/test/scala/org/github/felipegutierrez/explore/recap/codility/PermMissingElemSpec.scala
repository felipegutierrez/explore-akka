package org.github.felipegutierrez.explore.recap.codility

import org.scalatest.flatspec.AnyFlatSpec

class PermMissingElemSpec extends AnyFlatSpec {
  "the permutation missing element" should
    "return the missing element in an array" in {
    assertResult(4)(PermMissingElemS.solution(Array[Int](2, 3, 1, 5)))
    assertResult(4)(PermMissingElemS.solution(Array[Int](2, 3, 1, 5, 7, 6, 9, 8)))
    assertResult(10)(PermMissingElemS.solution(Array[Int](2, 3, 1, 5, 7, 6, 4, 9, 8, 11)))
  }
  "the permutation missing element with formula => expected sum = ((size + 1) * (size + 2)) / 2" should
    "return the missing element in an array" in {
    assertResult(4)(PermMissingElemS.solutionSimple(Array[Int](2, 3, 1, 5)))
    assertResult(4)(PermMissingElemS.solutionSimple(Array[Int](2, 3, 1, 5, 7, 6, 9, 8)))
    assertResult(10)(PermMissingElemS.solutionSimple(Array[Int](2, 3, 1, 5, 7, 6, 4, 9, 8, 11)))
  }
}
