package org.github.felipegutierrez.explore.akka.classic.streams.monitoring

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.duration._
import scala.util.Random

object FirstStreamMonitoring {
  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    implicit val system = ActorSystem("FirstStreamMonitoring")

    val source = Source(Stream.from(1)).throttle(1, 1 second)

    /** Simulating workload fluctuation: A Flow that expand the event to a random number of multiple events */
    val flow = Flow[Int].extrapolate { element =>
      Stream.continually(Random.nextInt(100)).take(Random.nextInt(100)).iterator
    }
    val monitorFlow = Flow.fromGraph(new MonitorProcessingTimerFlow[Int](5 seconds)("monitorFlow"))
    val sink = Sink.foreach[Int](println)

    val graph = source
      .via(flow)
      .via(monitorFlow)
      .to(sink)
    graph.run()
  }
}
