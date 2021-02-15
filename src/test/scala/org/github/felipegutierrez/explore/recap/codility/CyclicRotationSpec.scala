package org.github.felipegutierrez.explore.recap.codility

import org.scalatest.flatspec.AnyFlatSpec

class CyclicRotationSpec extends AnyFlatSpec {
  "my cyclic rotation" should
    "rotate an array by the factor K" in {
    assertResult(Array[Int](9, 7, 6, 3, 8))(CyclicRotationS.solution(Array[Int](3, 8, 9, 7, 6), 3))
    assertResult(Array[Int](0, 0, 0))(CyclicRotationS.solution(Array[Int](0, 0, 0), 1))
    assertResult(Array[Int](1, 2, 3, 4))(CyclicRotationS.solution(Array[Int](1, 2, 3, 4), 4))
  }
  "my cyclic rotation using the splitAt solution" should
    "rotate an array by the factor K" in {
    assertResult(Array[Int](9, 7, 6, 3, 8))(CyclicRotationS.solutionSplit(Array[Int](3, 8, 9, 7, 6), 3))
    assertResult(Array[Int](0, 0, 0))(CyclicRotationS.solutionSplit(Array[Int](0, 0, 0), 1))
    assertResult(Array[Int](1, 2, 3, 4))(CyclicRotationS.solutionSplit(Array[Int](1, 2, 3, 4), 4))
  }
}
