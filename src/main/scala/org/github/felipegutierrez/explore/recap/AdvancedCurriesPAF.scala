package org.github.felipegutierrez.explore.recap

object AdvancedCurriesPAF {
  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {

    val advancedCurriesPAF = new AdvancedCurriesPAF()
    val add1: Int => Int = advancedCurriesPAF.curriedAdded(4)
    val add2 = advancedCurriesPAF.curriedAdded(4) _ // the compiler inspect the type "Int => Int" because the _

    val add3 = advancedCurriesPAF.simpleAddFunction(1, 2)
    println(add3)

    val add4 = advancedCurriesPAF.simpleAddMethod(1, 2)
    println(add4)

    val add5 = advancedCurriesPAF.curriedAdded(1)(2)
    println(add5)

    val add6 = advancedCurriesPAF.curriedAdded(1) _ // Partial Applied Function which forces the compiler
    val add7 = add6(2)
    println(add7)

    val add8 = advancedCurriesPAF.simpleAddFunction.curried(1)
    val add9 = add8(2)
    println(add9)

    val add10 = advancedCurriesPAF.curriedAdded(1)(_) // Partial Applied Function which forces the compiler
    val add11 = add10(2)
    println(add11)

    val insertName = advancedCurriesPAF.concatenator("Hello ", _: String, ", how are you?")
    println(insertName("Felipe"))

    val numbers = List(Math.PI, Math.E, 1, 9.8, 1.3e-12)
    // compiler does sweet eta-expansion for us
    println(numbers.map(advancedCurriesPAF.simpleFormat))
    println(numbers.map(advancedCurriesPAF.seriousFormat))
    println(numbers.map(advancedCurriesPAF.preciseFormat))
  }

  class AdvancedCurriesPAF {
    val simpleFormat = curriedFormatter("%4.2f") _ // lift
    val seriousFormat = curriedFormatter("%8.6f") _
    val preciseFormat = curriedFormatter("%14.12f") _

    val superAdder: Int => Int => Int = x => y => x + y
    val simpleAddFunction = (x: Int, y: Int) => x + y

    // method
    def curriedAdded(x: Int)(y: Int): Int = x + y

    def simpleAddMethod(x: Int, y: Int): Int = x + y

    def curriedAddMethod(x: Int)(y: Int): Int = x + y

    def concatenator(a: String, b: String, c: String): String = a + b + c

    def curriedFormatter(s: String)(number: Double): String = s.format(number)
  }

}
