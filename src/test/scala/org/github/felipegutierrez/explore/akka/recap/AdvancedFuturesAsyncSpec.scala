package org.github.felipegutierrez.explore.akka.recap

import org.scalatest.flatspec.AnyFlatSpec

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.Success

// AsyncFlatSpec with ScalaFutures with IntegrationPatience
class AdvancedFuturesAsyncSpec extends AnyFlatSpec {

  import AdvancedFutures._

//  "a future" should
//    "create in the future and return None" in {
//    if (Runtime.getRuntime.availableProcessors() >= 4) {
//      val advancedFutures = new AdvancedFutures()
//      val res = advancedFutures.createTheFuture()
//      assert(res == None)
//    } else {
//      assert(true)
//    }
//  }
//  it should
//    "return success in the future" in {
//    if (Runtime.getRuntime.availableProcessors() >= 4) {
//      val advancedFutures = new AdvancedFutures()
//      val res: Future[Int] = advancedFutures.callTheFuture()
//      Await.result(res, 2.seconds)
//      assert(res.value.contains(Success(42)))
//    } else {
//      assert(true)
//    }
//  }
}
