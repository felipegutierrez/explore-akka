package org.github.felipegutierrez.explore.recap.codility

object CountDiv {

  def solution(a: Int, b: Int, k: Int): Int = {
    if (a > b) return 0
    var count = 0
    for (i <- a to b) if (i % k == 0) count += 1
    count
  }

  def solutionWithoutLoop(a: Int, b: Int, k: Int): Int = {
    if (a > b) return 0
    // number of divisible values smaller than A
    val tempA = a / k
    // number of divisible values smaller than B
    val tempB = b / k
    // number if divisible numbers
    val result = tempB - tempA
    if (a % k == 0) result + 1
    else result
  }
}
