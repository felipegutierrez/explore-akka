package org.github.felipegutierrez.explore.recap

object AdvancedLazyEvaluation {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    // filtering with lazy vals
    def lessThan30(i: Int): Boolean = {
      println(s"$i is less than 30?")
      i < 30
    }

    def greaterThan20(i: Int): Boolean = {
      println(s"$i is greater than 20?")
      i > 20
    }

    val numbers = List(1, 25, 40, 5, 23)
    val lt30 = numbers.filter(lessThan30) // List(1, 25, 5, 23)
    val gt20 = lt30.filter(greaterThan20)
    println(gt20)

    val lt30lazy = numbers.withFilter(lessThan30) // lazy vals under the hood
    val gt20lazy = lt30lazy.withFilter(greaterThan20)
    println
    gt20lazy.foreach(println)

    // for-comprehensions use withFilter with guards
    val res01 = for {
      a <- List(1, 2, 3) if a % 2 == 0 // use lazy vals!
    } yield a + 1
    println("for-comprehensions use withFilter with guards and lazy vals")
    println(res01)
    val res02 = List(1, 2, 3).withFilter(_ % 2 == 0).map(_ + 1) // List[Int]
    println("they are the same of using withFilter")
    println(res02)
  }

}
