package org.github.felipegutierrez.explore.akka.recap

import org.scalatest.flatspec.AsyncFlatSpec

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.Success

class AdvancedFuturesAsyncSpec extends AsyncFlatSpec {

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
    Await.result(res, 2.seconds)
    assert(res.value.contains(Success(42)))
  }
}
