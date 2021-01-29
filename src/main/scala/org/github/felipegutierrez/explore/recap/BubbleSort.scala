package org.github.felipegutierrez.explore.recap

object BubbleSort {
  def main(args: Array[String]): Unit = {
    val arr = Array(5, 3, 2, 5, 6, 77, 99, 88)
    var temp = 0
    var fixed = false

    while (fixed == false) {
      fixed = true
      for (i <- 0 until arr.length - 1) {
        if (i + 1 < arr.length && arr(i) > arr(i + 1)) {
          temp = arr(i + 1)
          arr(i + 1) = arr(i)
          arr(i) = temp
          fixed = false
        }
      }
    }

    arr.foreach(println)
  }
}
