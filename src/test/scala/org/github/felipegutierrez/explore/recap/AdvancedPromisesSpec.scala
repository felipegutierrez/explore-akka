package org.github.felipegutierrez.explore.recap

import org.scalatest.flatspec.AnyFlatSpec

import scala.util.{Failure, Success, Try}

class AdvancedPromisesSpec extends AnyFlatSpec {

  "a controllable Future with Promise" should
    "return the value of the Future always" in {
    val value: Int = 10
    val expected: String = s"${AdvancedPromises.MSG} ${(value / AdvancedPromises.DIV)}"
    val result: Option[Try[String]] = AdvancedPromises.computeControllableFuture(value)
    result match {
      case Some(Success(value)) => assertResult(expected)(value)
      case Some(Failure(exception: Exception)) => fail("controllable Future with Promise failed")
      case None => fail("controllable Future with Promise has not return any message")
    }
  }
}
