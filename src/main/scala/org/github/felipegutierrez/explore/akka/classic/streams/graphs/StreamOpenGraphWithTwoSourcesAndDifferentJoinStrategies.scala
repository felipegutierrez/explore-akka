package org.github.felipegutierrez.explore.akka.classic.streams.graphs

import akka.actor.ActorSystem
import akka.stream.ClosedShape
import akka.stream.scaladsl.{Concat, Flow, GraphDSL, Merge, Partition, RunnableGraph, Sink, Source}

import scala.concurrent.duration._

/** Write a graph that consumes 2 sources and can decide to chose different strategies to join at runtime */
object StreamOpenGraphWithTwoSourcesAndDifferentJoinStrategies extends App {

  run()

  def run() = {
    implicit val system = ActorSystem("StreamOpenGraphWithTwoSourcesAndDifferentJoinStrategies")

    val MULTI = 10
    val DIVIDE = 2
    val slowSource = Source(1 to 1000).throttle(5, 1 second)
    val fastSource = Source(1 to 1000).throttle(50, 1 second)

    val strategy01 = Flow[Int].map { x =>
      val result = x * MULTI
      print(s" | strategy01 $x * $MULTI -> $result")
      result
    }
    val strategy02 = Flow[Int].map { x =>
      val result = x / DIVIDE
      print(s" | strategy02 $x / $DIVIDE -> $result")
      result
    }

    def strategicJoinDecision(value: Int, multiple: Int): Int = if (value % multiple == 0) 0 else 1

    // Step 1 - setting up the fundamental for a stream graph
    val switchJoinStrategies = RunnableGraph.fromGraph(
      GraphDSL.create() { implicit builder =>
        import GraphDSL.Implicits._

        // Step 2 - add partition and merge strategy
        val concatShape = builder.add(Concat[Int](2))
        val partitionDecisionShape = builder.add(Partition[Int](2, strategicJoinDecision(_, 10)))
        val strategy01Shape = builder.add(strategy01)
        val strategy02Shape = builder.add(strategy02)
        val mergeStrategyShape = builder.add(Merge[Int](2))
        val sinkShape = builder.add(Sink.foreach[Int](println))

        // Step 3 - tying up the components
        slowSource ~> concatShape.in(0)
        fastSource ~> concatShape.in(1)
        concatShape.out ~> partitionDecisionShape
        partitionDecisionShape.out(0) ~> strategy01Shape ~> mergeStrategyShape.in(0)
        partitionDecisionShape.out(1) ~> strategy02Shape ~> mergeStrategyShape.in(1)
        mergeStrategyShape ~> sinkShape

        // Step 4 - return the shape
        ClosedShape
      }
    )
    // run the graph and materialize it
    val graph = switchJoinStrategies.run()
  }
}
