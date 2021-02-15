package org.github.felipegutierrez.explore.recap.codility

import org.scalatest.flatspec.AnyFlatSpec

class MinAvgTwoSliceSpec extends AnyFlatSpec {
  "a minimum average of two slices algorithm" should
    "return the position where this slice starts" in {
    assertResult(1)(MinAvgTwoSlice.solution(Array[Int](4, 2, 2, 5, 1, 5, 8)))
  }
}
