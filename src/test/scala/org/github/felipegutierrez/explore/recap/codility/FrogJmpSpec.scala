package org.github.felipegutierrez.explore.recap.codility

import org.scalatest.flatspec.AnyFlatSpec

class FrogJmpSpec extends AnyFlatSpec {
  "my Frog jump application" should
    "return the number of minimal jumps in the lake" in {
    assertResult(3)(FrogJmpS.solution(10, 85, 30))
    assertResult(3)(FrogJmpS.solutionWithoutMath(10, 85, 30))
  }

}
