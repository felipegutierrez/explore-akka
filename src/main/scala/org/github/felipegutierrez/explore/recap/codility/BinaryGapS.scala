package org.github.felipegutierrez.explore.recap.codility

object BinaryGapS {
  def run(args: Array[String]): Unit = {
    val list: List[Int] = List[Int](1041, 32, 9, 529, 20, Int.MaxValue)
    list.foreach(number => println(s"number $number has max binary gap: ${solution(number)}"))
  }

  def solution(n: Int): Int = {
    var nTemp = n
    var currentGap = 0
    var largestGap = 0
    var counting = false
    while (nTemp > 0) {
      if (counting == false) { // for the first "1"
        if ((nTemp & 1) == 1) { // the AND bit operation, when we find the first bit that is equal to 1 we start counting
          counting = true
        }
      } else {
        if ((nTemp & 1) == 0) currentGap += 1 // the AND bit operations says that bit is equal to 0, so we count
        else { // the AND bit operations says that bit is equal to 1, so we have to restart the counting
          if (currentGap > largestGap) largestGap = currentGap // a new largest gap was fount
          currentGap = 0 // reset current gap counting
        }
      }
      nTemp = nTemp >> 1 // move one bit to the right
    }
    largestGap
  }

  def solutionPatternMatch(n: Int): Int = {
    def go(n: Int, currGap: Option[Int], maxGap: Int): Int = {
      if (n == 0) maxGap
      else (n & 1, currGap) match {
        // the AND bit operations says that bit is equal to 0, so we count
        case (0, Some(gap)) => go(n >> 1, Some(gap + 1), maxGap)
        // the AND bit operations says that bit is equal to 1, so arrived at a bit 1 and we need to get the maximum gap
        case (1, Some(gap)) => go(n >> 1, Some(0), math.max(maxGap, gap))
        // the AND bit operations says that bit is equal to 1, so arrived in the end
        case (1, None) => go(n >> 1, Some(0), maxGap)
        // move to the bit on the wight
        case _ => go(n >> 1, currGap, maxGap)
      }
    }

    // start the counting
    go(n, None, 0)
  }
}
