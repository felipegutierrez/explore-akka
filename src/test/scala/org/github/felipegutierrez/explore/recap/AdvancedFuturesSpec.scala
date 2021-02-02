package org.github.felipegutierrez.explore.recap

import org.scalatest.flatspec.AnyFlatSpec

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.Success

class AdvancedFuturesSpec extends AnyFlatSpec {

  import AdvancedFutures._

  if (Runtime.getRuntime().availableProcessors() >= 4) {
    "a future with timeout" should
      "return success when there is a AWAIT timeout inside" in {
      val res = BankingApp.purchase("Daniel", "iPhone 12", "rock the jvm store", 3000)
      // println(res.equals("SUCCESS"))
      assertResult("SUCCESS")(res)
    }
    "a method which returns the first or the last Future to complete" should
      "return the Futures in the respect order" in {
      import scala.concurrent.ExecutionContext.Implicits.global
      val fast = Future {
        Thread.sleep(100)
        42
      }

      val slow = Future {
        Thread.sleep(500)
        45
      }
      val advancedFutures = new AdvancedFutures()
      advancedFutures.alwaysFutureFirstFinished(fast, slow).foreach(f => println("FIRST: " + f))
      advancedFutures.alwaysFutureLastFinished(fast, slow).foreach(l => println("LAST: " + l))

      Await.result(slow, 1 second)

      assert(fast.value.contains(Success(42)))
      assert(slow.value.contains(Success(45)))
    }
    "a retry method which computes a value until" should
      "return return a Future only when it matches a condition" in {
      import scala.concurrent.ExecutionContext.Implicits.global
      val random = new scala.util.Random()
      val action = () => Future {
        Thread.sleep(10)
        val nextValue = random.nextInt(50)
        println("generated " + nextValue)
        nextValue
      }
      val advancedFutures = new AdvancedFutures()
      val res = advancedFutures.retryUntil(action, (x: Int) => x < 5)
      res.foreach(result => println("settled at " + result))
      Await.result(res, 10.seconds)
      assert(res.value.contains(Success(0))
        || res.value.contains(Success(1))
        || res.value.contains(Success(2))
        || res.value.contains(Success(3))
        || res.value.contains(Success(4)))
    }
    "a fallback" should
      "return something no matter what happens" in {
      val profile01: Future[Profile] = SocialNetwork.fetchProfileNoMatterWhat("fb.id.1-zuck")
      Await.result(profile01, 1.seconds)
      assert(profile01.value.contains(Success(Profile("fb.id.1-zuck", "Mark"))))

      val profile02: Future[Profile] = SocialNetwork.fetchProfileNoMatterWhat("unknown id")
      Await.result(profile02, 1.seconds)
      assert(profile02.value.contains(Success(Profile("fb.id.0-dummy", "Forever alone"))))
    }
    it should
      "return a profile or another option" in {

      val profile01: Future[Profile] = SocialNetwork.fetchProfileOrElse("fb.id.1-zuck", "fb.id.0-dummy")
      Await.result(profile01, 1.seconds)
      assert(profile01.value.contains(Success(Profile("fb.id.1-zuck", "Mark"))))

      val profile02: Future[Profile] = SocialNetwork.fetchProfileOrElse("fb.id.1-zuckkkkkk", "fb.id.0-dummy")
      Await.result(profile02, 1.seconds)
      assert(profile02.value.contains(Success(Profile("fb.id.0-dummy", "Dummy"))))
    }
  }
}
