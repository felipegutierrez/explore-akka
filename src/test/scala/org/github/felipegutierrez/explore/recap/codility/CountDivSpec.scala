package org.github.felipegutierrez.explore.recap.codility

import org.scalatest.flatspec.AnyFlatSpec

class CountDivSpec extends AnyFlatSpec {
  "my count number divisible per K" should
    "the amount of number that are divisible by K" in {
    assertResult(3)(CountDiv.solution(6, 11, 2))
    assertResult(4)(CountDiv.solution(6, 12, 2))
    assertResult(3)(CountDiv.solution(6, 12, 3))
    assertResult(2)(CountDiv.solution(6, 12, 4))
    assertResult(6)(CountDiv.solution(0, 10, 2))
  }
  "my count number divisible per K with A greater than B" should
    "return 0" in {
    assertResult(0)(CountDiv.solution(16, 11, 2))
  }

  "my count number divisible per K without a loop" should
    "the amount of number that are divisible by K" in {
    assertResult(3)(CountDiv.solutionWithoutLoop(6, 11, 2))
    assertResult(4)(CountDiv.solutionWithoutLoop(6, 12, 2))
    assertResult(3)(CountDiv.solutionWithoutLoop(6, 12, 3))
    assertResult(2)(CountDiv.solutionWithoutLoop(6, 12, 4))
    assertResult(6)(CountDiv.solutionWithoutLoop(0, 10, 2))
    assertResult(0)(CountDiv.solutionWithoutLoop(16, 11, 2))
  }
}
