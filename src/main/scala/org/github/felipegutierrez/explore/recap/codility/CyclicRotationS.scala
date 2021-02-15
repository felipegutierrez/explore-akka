package org.github.felipegutierrez.explore.recap.codility

object CyclicRotationS {

  def main(args: Array[String]): Unit = {
    val A = Array[Int](3, 8, 9, 7, 6)
    val K: Int = 3
    val result = solution(A, K)
    result.foreach(v => print(s" $v,"))
    println()
  }

  def solution(a: Array[Int], k: Int): Array[Int] = {
    var result = new Array[Int](a.length)
    if (a.length == k) a
    else {
      var newK = k
      while (k > a.length) newK = k - a.length
      var count = 0
      // the rotation from K - 1 to the end
      for (i <- (newK - 1) until a.length) {
        result(count) = a(i)
        count += 1
      }
      // the remaining items
      for (i <- 0 until (newK - 1)) {
        result(count) = a(i)
        count += 1
      }
      result
    }
  }

  def solutionSplit(a: Array[Int], k: Int): Array[Int] = {
    if (a.length < 2) a
    else {
      // get the reminder using MOD (%) operator if the array is K is bigger than the array,
      // then split the array where we need to rotate it
      val (left, right) = a.splitAt(a.length - (k % a.length))
      right ++ left
    }
  }
}
