package org.github.felipegutierrez.explore.recap

object AdvancedHighOrderFunction {
  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    val simpleFunction = (x: Int) => x + 1
    val g = nTimes(simpleFunction, 30)
    println(s"result: ${g}")
  }

  def higherOrderFunctionWithPartialFunction(list: List[Int]): List[Int] = {
    list.map {
      case 1 => 11
      case 2 => 22
      case 5 => 55
    }
  }

  def nTimes(f: Int => Int, n: Int): Int => Int = {
    if (n <= 0) (x: Int) => x // the identity function
    else (x: Int) => nTimes(f, n - 1)(f(x))
  }
}
