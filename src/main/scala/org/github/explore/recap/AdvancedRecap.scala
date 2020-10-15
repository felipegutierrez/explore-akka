package org.github.explore.recap

object AdvancedRecap extends App {

  // partial functions
  val partialFunction: PartialFunction[Int, Int] = {
    case 1 => 11
    case 2 => 22
    case 3 => 33
    case 5 => 55
  }
  val pf = (x: Int) => x match {
    case 1 => 11
    case 2 => 22
    case 3 => 33
    case 5 => 55
    case _ => 0
  }
  val func: (Int => Int) = partialFunction
  println("using partial functions .................")
  println(partialFunction(1))
  println(pf(4))
  println(partialFunction(3))
  try {
    println(partialFunction(4))
  } catch {
    case e: Exception => println("Exception: " + e.getMessage)
  }
  println(func(1))
  println(func(5))

  // lifting
  println("Lifting partial functions ................")
  val lifted = partialFunction.lift
  println(lifted(3))
  println(lifted(4444))

  // orElse
  println("orElse ................")
  val pfChain = partialFunction.orElse[Int, Int] {
    case 60 => 9000
  }
  println(pfChain(5))
  println(pfChain(60))
  try {
    println(pfChain(5648))
  } catch {
    case e: Exception => println("Exception: " + e.getMessage)
  }
}
