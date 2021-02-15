package org.github.felipegutierrez.explore.recap.codility

import org.scalatest.flatspec.AnyFlatSpec

class OddOccurrencesInArraySpec extends AnyFlatSpec {
  "my odd occurrences in an array" should
    "find number that does not have a pair" in {
    assertResult(0)(OddOccurrencesInArrayS.solution(Array(9, 3, 9, 3, 9, 7)))
    assertResult(7)(OddOccurrencesInArrayS.solution(Array(9, 3, 9, 3, 9, 7, 9)))
    assertResult(77)(OddOccurrencesInArrayS.solution(Array(1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 77, 5, 7)))
    assertResult(77)(OddOccurrencesInArrayS.solution(Array(1, 3, 3, 77, 7, 1, 5, 7, 1, 1, 3, 5, 7, 7, 3, 5, 5)))
  }

  "my recursive odd occurrences in an array" should
    "find number that does not have a pair" in {
    assertResult(0)(OddOccurrencesInArrayS.solutionRec(Array(9, 3, 9, 3, 9, 7)))
    assertResult(7)(OddOccurrencesInArrayS.solutionRec(Array(9, 3, 9, 3, 9, 7, 9)))
    assertResult(77)(OddOccurrencesInArrayS.solutionRec(Array(1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 77, 5, 7)))
    assertResult(77)(OddOccurrencesInArrayS.solutionRec(Array(1, 3, 3, 77, 7, 1, 5, 7, 1, 1, 3, 5, 7, 7, 3, 5, 5)))
  }
}
