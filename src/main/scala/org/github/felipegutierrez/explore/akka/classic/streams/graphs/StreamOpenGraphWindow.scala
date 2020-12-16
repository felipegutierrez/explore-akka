package org.github.felipegutierrez.explore.akka.classic.streams.graphs

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.{Flow, GraphDSL, Merge, RunnableGraph, Sink, Source}

import scala.concurrent.duration._

object StreamOpenGraphWindow {

  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    implicit val system = ActorSystem("StreamOpenGraphWindow")

    val sourceNegative = Source(Stream.from(0, -1)).throttle(1, 1 second)
    val sourcePositive = Source(Stream.from(0)).throttle(1, 1 second)

    // Step 1 - setting up the fundamental for a stream graph
    val windowRunnableGraph = RunnableGraph.fromGraph(
      GraphDSL.create() { implicit builder =>
        import GraphDSL.Implicits._
        // Step 2 - create shapes
        val mergeShape = builder.add(Merge[Int](2))
        // val windowTimeFlow = Flow.fromGraph(new WindowProcessingTimerFlow(5000 millisecond))
        // val windowFlowShape = builder.add(windowTimeFlow)
        val windowEventFlow = Flow.fromGraph(new WindowEventFlow(10))
        val windowFlowShape = builder.add(windowEventFlow)
        val sinkShape = builder.add(Sink.foreach[Int](x => println(s"\nsink: $x")))

        // Step 3 - tying up the components
        sourceNegative ~> mergeShape.in(0)
        sourcePositive ~> mergeShape.in(1)
        mergeShape.out ~> windowFlowShape ~> sinkShape

        // Step 4 - return the shape
        ClosedShape
      }
    )
    // run the graph and materialize it
    val graph = windowRunnableGraph.run()
  }
}
