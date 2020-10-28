package org.github.felipegutierrez.explore.akka.recap

import org.scalatest.flatspec.AnyFlatSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class AdvancedFuturesSpec extends AnyFlatSpec {

  import AdvancedFutures._

  "a future" should
    "create in the future and return None" in {
    val advancedFutures = new AdvancedFutures()
    val res = advancedFutures.createTheFuture()
    assert(res == None)
  }
  it should
    "return success in the future" in {
    val advancedFutures = new AdvancedFutures()
    val res: Future[Int] = advancedFutures.callTheFuture()
    res.onComplete {
      case Success(meaningOfLife) => assert(true)
      case Failure(e) => assert(false)
    }
  }
}
