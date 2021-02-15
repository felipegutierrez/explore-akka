package org.github.felipegutierrez.explore.recap.codility

object PermMissingElemS {

  def solution(a: Array[Int]): Int = {
    def sumAll(n: Int): Int = {
      if (n == 0) 0
      else (n + sumAll(n - 1))
    }

    val expect = sumAll(a.length + 1)
    var real = 0
    for (i <- 0 until a.length) {
      real += a(i)
    }
    (expect - real)
  }

  /**
   * using the concept of expected sum = ((size + 1) * (size + 2)) / 2
   *
   * @param a
   * @return
   */
  def solutionSimple(a: Array[Int]): Int = {
    val size = a.length
    val expected = ((size + 1) * (size + 2)) / 2
    expected - a.sum
  }
}
