package org.github.felipegutierrez.explore.akka.recap

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.flatspec.{AnyFlatSpec, AsyncFlatSpec}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.Success

class AdvancedFuturesAsyncSpec extends AnyFlatSpec {

  import AdvancedFutures._

  val cores = Runtime.getRuntime.availableProcessors()

  if (cores >= 4) {
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
}
