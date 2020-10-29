package org.github.felipegutierrez.explore.akka.recap

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.flatspec.AsyncFlatSpec

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt
import scala.util.Success

class AdvancedFuturesAsyncSpec extends AsyncFlatSpec with ScalaFutures with IntegrationPatience {

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
//    it should
//      "return the second future in sequence" in {
  //    val advancedFutures = new AdvancedFutures()
  //    val futureA = Future[String] {
  //      println("futureA")
  //      "futureA"
  //    }
  //    val futureB = Future[Int] {
  //      println("futureB")
  //      10
  //    }
  //    val res: Future[Int] = advancedFutures.inSequence[String, Int](futureA, futureB)
  //    assert(res == Future)
  //    whenReady(Future.successful("Done")) { testImage =>
  //        testImage must be equalTo "Done"
  //    }
  // Await.result(res, Duration("10 seconds")) must not have length(0)
  //    Await.result(futureB, 2.seconds)
  //    println(res)
  //    assert(res == Future)
//    }
}
