package org.github.felipegutierrez.explore.recap.codility

object MissingInteger {
  def solution(a: Array[Int]): Int = {
    var continue = true
    var i = 1
    while (continue == true && i <= a.length) {
      if (!a.contains(i)) continue = false
      else i += 1
    }
    i
  }

  def solutionWithStream(a: Array[Int]): Int = {
    val buf = a.filter(_ > 0).toSet
    (1 to Int.MaxValue).toStream.filter(!buf.contains(_)).head
  }
}
