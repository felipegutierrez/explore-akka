package org.github.felipegutierrez.explore.recap.codility

object MinAvgTwoSlice {
  /**
   * we know by the description of the challenge that the minimum average of slices is the
   * average of 2 positions or 3 positions.
   *
   * @param a
   * @return
   */
  def solution(a: Array[Int]): Int = {
    // save the minimum average of slices
    var minStartPosition = 0
    var minAvg = Int.MaxValue

    var i = 0
    //  for "i< A.length -2"
    while (i < a.length - 2) {
      val avg1 = (a(i) + a(i + 1)) / 2
      val avg2 = (a(i) + a(i + 1) + a(i + 2)) / 3
      val minCurrAvg = Math.min(avg1, avg2)

      i += 1

      if (minCurrAvg < minAvg) {
        minAvg = minCurrAvg
        minStartPosition = i
      }
    }

    // note: for the last missing case
    // case: avg of length of 2 "A[A.length-2] + A[A.length-1]"
    val lastAvg = (a(a.length - 1) + a(a.length - 2)) / 2
    if (lastAvg < minAvg) minStartPosition = a.length - 2

    minStartPosition
  }
}
