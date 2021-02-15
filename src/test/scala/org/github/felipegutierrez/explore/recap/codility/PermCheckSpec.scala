package org.github.felipegutierrez.explore.recap.codility

import org.scalatest.flatspec.AnyFlatSpec

class PermCheckSpec extends AnyFlatSpec {
  "my permutation check" should
    "verify if the elements on an array is a sequence" in {
    assertResult(1)(PermCheck.solution(Array[Int](4, 1, 3, 2)))
    assertResult(0)(PermCheck.solution(Array[Int](4, 1, 3)))
  }

  "my permutation check using arithmetic progression" should
    "verify if the elements on an array is a sequence" in {
    assertResult(1)(PermCheck.solutionWithPA(Array[Int](4, 1, 3, 2)))
    assertResult(0)(PermCheck.solutionWithPA(Array[Int](4, 1, 3)))
  }

  "my permutation check using Set" should
    "verify if the elements on an array is a sequence" in {
    assertResult(1)(PermCheck.solutionWithSet(Array[Int](4, 1, 3, 2)))
    assertResult(0)(PermCheck.solutionWithSet(Array[Int](4, 1, 3)))
  }
}
