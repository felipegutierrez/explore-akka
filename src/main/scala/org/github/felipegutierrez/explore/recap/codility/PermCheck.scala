package org.github.felipegutierrez.explore.recap.codility

object PermCheck {
  def solution(a: Array[Int]): Int = {
    val expectSum = sumAll(a.length)
    if (expectSum == a.sum) 1
    else 0
  }

  def sumAll(n: Int): Int = {
    if (n == 0) 0
    else (n + sumAll(n - 1))
  }


  def solutionWithPA(a: Array[Int]): Int = {
    val expectSum = sumAllUsingPA(a.length)
    if (expectSum == a.sum) 1
    else 0
  }

  /**
   * prograssao aritimetica: ((a_1 + a_n) * n) / 2
   *
   * @param n
   * @return
   */
  def sumAllUsingPA(n: Int): Int = {
    ((1 + n) * n) / 2
  }

  def solutionWithSet(a: Array[Int]): Int = {
    val buf = a.toSet
    if (buf.size == a.length && buf.max == a.length) 1
    else 0
  }
}
