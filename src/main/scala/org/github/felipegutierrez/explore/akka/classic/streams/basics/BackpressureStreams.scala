package org.github.felipegutierrez.explore.akka.classic.streams.basics

import akka.actor.ActorSystem
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, Sink, Source}

object BackpressureStreams {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    implicit val system = ActorSystem("BackpressureStreams")

    val fastSource = Source(1 to 1000)
    val flow = Flow[Int].map { x =>
      println(s"Flow $x")
      x + 1
    }
    val slowSink = Sink.foreach[Int] { x =>
      Thread.sleep(1000)
      println(s"Sink: $x")
    }

    // Fusion of operators. This is not backpressure because everything is in the same Actor
    /*
    fastSource
      .via(slowFlow)
      .to(slowSink)
      .run()
     */
    // We have to disable the operator fusion by allowing Async boundary. Now we have backpressure protocol enable
    /*
    fastSource.async
      .via(flow).async
      .to(slowSink).async
      .run()
     */

    /*
    reactions to backpressure (in order):
    - try to slow down if possible
    - buffer elements until there's more demand
    - drop down elements from the buffer if it overflows
    - tear down/kill the whole stream (failure)
   */
    val bufferedFlow = flow.buffer(10, overflowStrategy = OverflowStrategy.dropHead)
    fastSource.async
      .via(bufferedFlow).async
      .to(slowSink)
      .run()
    /*
      1-16: nobody is backpressured
      17-26: flow will buffer, flow will start dropping at the next element
      26-1000: flow will always drop the oldest element
        => 991-1000 => 992 - 1001 => sink
     */

    /*
    overflow strategies:
    - drop head = oldest
    - drop tail = newest
    - drop new = exact element to be added = keeps the buffer
    - drop the entire buffer
    - backpressure signal
    - fail
   */

    // throttling
    import scala.concurrent.duration._
    fastSource.throttle(10, 1 second).runWith(Sink.foreach(println))
  }
}
