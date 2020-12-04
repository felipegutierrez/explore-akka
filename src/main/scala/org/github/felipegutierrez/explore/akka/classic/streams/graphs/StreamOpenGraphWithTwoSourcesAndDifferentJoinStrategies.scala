package org.github.felipegutierrez.explore.akka.classic.streams.graphs

import akka.actor.ActorSystem
import akka.stream.ClosedShape
import akka.stream.scaladsl.{Flow, GraphDSL, Merge, Partition, RunnableGraph, Sink, Source, Zip}

import scala.concurrent.duration._

/** Write a graph that consumes 2 sources and can decide to chose different strategies to join at runtime */
object StreamOpenGraphWithTwoSourcesAndDifferentJoinStrategies extends App {

  run()

  def run() = {
    implicit val system = ActorSystem("StreamOpenGraphWithTwoSourcesAndDifferentJoinStrategies")

    val crescentSource = Source(1 to 1000).throttle(10, 1 second)
    val decrescentSource = Source(1000 to 2000).throttle(10, 1 second)

    val strategy0 = Flow[(Int, Int)].map { pair =>
      val result = pair._1 + pair._2
      print(s"strategy 0 [${pair._1} + ${pair._2}] = $result")
      result
    }
    val strategy1 = Flow[(Int, Int)].map { pair =>
      val result = pair._1 - pair._2
      print(s"strategy 1 [${pair._1} - ${pair._2}] = $result")
      result
    }

    def strategicJoinDecision(value: (Int, Int)): Int = if (value._1 < value._2) 0 else 1

    // Step 1 - setting up the fundamental for a stream graph
    val switchJoinStrategies = RunnableGraph.fromGraph(
      GraphDSL.create() { implicit builder =>
        import GraphDSL.Implicits._

        // Step 2 - add partition and merge strategy
        val zipShape = builder.add(Zip[Int, Int])
        val partitionDecisionShape = builder.add(Partition[(Int, Int)](2, strategicJoinDecision(_)))
        val strategy01Shape = builder.add(strategy0)
        val strategy02Shape = builder.add(strategy1)
        val mergeStrategyShape = builder.add(Merge[Int](2))
        val sinkShape = builder.add(Sink.foreach[Int](x => println(s" > sink: $x")))

        // Step 3 - tying up the components
        crescentSource ~> zipShape.in0
        decrescentSource ~> zipShape.in1
        zipShape.out ~> partitionDecisionShape
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
