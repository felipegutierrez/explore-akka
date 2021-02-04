package org.github.felipegutierrez.explore.recap

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

object AdvancedPromises {

  val MSG = "I produced this string with number"
  val DIV: Int = 3

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    val result: Option[Try[String]] = computeControllableFuture(20)
    result match {
      case Some(Success(value)) => println(s"success: $value")
      case Some(Failure(exception: Exception)) => println(s"exception: $exception")
      case None => println(s"non value returned")
    }
  }

  def computeControllableFuture(arg: Int): Option[Try[String]] = {
    // val aFuture: Future[String] = gimmeMyFuture(10)
    val aFuture: Future[String] = runByPromise[String](produceSomething(arg))

    // step 3 - consume the future
    // aFuture.onComplete { case Success(s) => println(s"I've received: $s") }
    aFuture.map(s => println(s"I've received: $s"))
    // aFuture.onComplete(tryComplete(promise, _))
    // Await.ready(aFuture, 3 seconds)
    Await.ready(aFuture, Duration.Inf)
    // Await.result(aFuture, 3 seconds)
    aFuture.value
  }

  def produceSomething(arg: Int): String = {
    println("processing an expensive logic in another thread ....")
    Thread.sleep(1000)
    s"$MSG ${(arg / DIV)}"
  }

  def runByPromise[T](block: => T)(implicit ec: ExecutionContext): Future[T] = {
    // step 1 - create the promise
    val p = Promise[T]
    // step 5 - call the producer asynchronously: thread 2 - "producer"
    ec.execute { () =>
      try {
        // step 4 - producer logic to fulfill the promise
        p.success(block)
      } catch {
        case NonFatal(e) => p.failure(e)
      }
    }
    // step 2 - extract the future
    p.future
  }
}
