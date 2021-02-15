package org.github.felipegutierrez.explore.recap.codility

object OddOccurrencesInArrayS {
  def run(args: Array[String]): Unit = {
    println(solution(Array[Int](9, 3, 9, 3, 9, 7, 9)))
  }

  def solution(a: Array[Int]): Int = {
    if (a.length == 0) 0
    else if (a.length % 2 == 0) 0
    else if (a.length > 1000000) 0
    else {
      var unpaired = a(0)
      for (i <- 1 until a.length) {
        unpaired = unpaired ^ a(i)
      }
      unpaired
    }
  }

  def solutionRec(a: Array[Int]): Int = {
    if (a.length == 0) 0
    else if (a.length % 2 == 0) 0
    else if (a.length > 1000000) 0
    else {
      def unpairedValues(unpaired: Int, position: Int): Int = {
        if (position == a.length) unpaired
        else unpairedValues(unpaired ^ a(position), position + 1)
      }

      unpairedValues(a(0), 1)
    }
  }
}
