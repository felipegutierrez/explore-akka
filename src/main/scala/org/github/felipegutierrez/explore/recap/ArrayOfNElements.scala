package org.github.felipegutierrez.explore.recap

object ArrayOfNElements {
//  def main(args: Array[String]): Unit = {
//    run()
//  }

  def run() = {
    println(f(scala.io.StdIn.readInt()))
  }

  def f(num: Int): List[Int] = {
    val list = List.fill(num) {0}
    print(list + "\n")
    list
  }
}
