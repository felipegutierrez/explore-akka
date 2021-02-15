package org.github.felipegutierrez.explore.recap.codility

import org.scalatest.flatspec.AnyFlatSpec

class BinaryGapSpec extends AnyFlatSpec {

  "my binary maximum gap finder" should
    "find the maximum gap of 0's in the binary representation of any int" in {
    assertResult(5)(BinaryGapS.solution(1041))
    assertResult(0)(BinaryGapS.solution(32))
    assertResult(0)(BinaryGapS.solution(15))
    assertResult(2)(BinaryGapS.solution(9))
    assertResult(4)(BinaryGapS.solution(529))
    assertResult(1)(BinaryGapS.solution(20))
    assertResult(0)(BinaryGapS.solution(Int.MaxValue))
  }

  "my binary maximum gap finder with pattern match" should
    "find the maximum gap of 0's in the binary representation of any int" in {
    assertResult(5)(BinaryGapS.solutionPatternMatch(1041))
    assertResult(0)(BinaryGapS.solutionPatternMatch(32))
    assertResult(0)(BinaryGapS.solutionPatternMatch(15))
    assertResult(2)(BinaryGapS.solutionPatternMatch(9))
    assertResult(4)(BinaryGapS.solutionPatternMatch(529))
    assertResult(1)(BinaryGapS.solutionPatternMatch(20))
    assertResult(0)(BinaryGapS.solutionPatternMatch(Int.MaxValue))
  }
}
