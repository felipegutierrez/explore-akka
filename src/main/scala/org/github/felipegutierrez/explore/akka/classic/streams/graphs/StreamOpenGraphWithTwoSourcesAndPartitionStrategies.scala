package org.github.felipegutierrez.explore.akka.classic.streams.graphs

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ClosedShape
import akka.stream.scaladsl.{Flow, GraphDSL, Merge, Partition, RunnableGraph, Sink, Source}

import scala.concurrent.duration._

/** Write a graph that consumes 2 sources and can decide to chose different strategies to join at runtime */
object StreamOpenGraphWithTwoSourcesAndPartitionStrategies {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    implicit val system = ActorSystem("StreamOpenGraphWithTwoSourcesAndDifferentJoinStrategies")

    val incrementSource: Source[Int, NotUsed] = Source(1 to 10).throttle(1, 1 second)
    val decrementSource: Source[Int, NotUsed] = Source(10 to 20).throttle(1, 1 second)

    def strategicJoinDecision(value: (Int, Int)): Int = {
      println(s"value._1: ${value._1} value._2: ${value._2}")
      if (value._1 < value._2) 0 else 1
    }

    def tokenizerSource(key: Int) = {
      Flow[Int].map { value =>
        (key, value)
      }
    }

    // Step 1 - setting up the fundamental for a stream graph
    val switchJoinStrategies = RunnableGraph.fromGraph(
      GraphDSL.create() { implicit builder =>
        import GraphDSL.Implicits._

        // Step 2 - add partition and merge strategy
        val tokenizerShape00 = builder.add(tokenizerSource(0))
        val tokenizerShape01 = builder.add(tokenizerSource(1))

        val mergeTupleShape = builder.add(Merge[(Int, Int)](2))
        val partitionDecisionShape = builder.add(Partition[(Int, Int)](2, strategicJoinDecision(_)))

        val strategy01Shape = builder.add(Flow[(Int, Int)].map { pair =>
          val result = pair._1 + pair._2
          print(s"strategy 0 [${pair._1} + ${pair._2}] = $result")
          result
        })
        val strategy02Shape = builder.add(Flow[(Int, Int)].map { pair =>
          val result = pair._1 - pair._2
          print(s"strategy 1 [${pair._1} - ${pair._2}] = $result")
          result
        })
        val mergeShape = builder.add(Merge[Int](2))
        val sinkShape = builder.add(Sink.foreach[Int](x => println(s" > sink: $x")))

        // Step 3 - tying up the components
        incrementSource ~> tokenizerShape00 ~> mergeTupleShape.in(0)
        decrementSource ~> tokenizerShape01 ~> mergeTupleShape.in(1)
        mergeTupleShape.out ~> partitionDecisionShape
        partitionDecisionShape.out(0) ~> strategy01Shape ~> mergeShape.in(0)
        partitionDecisionShape.out(1) ~> strategy02Shape ~> mergeShape.in(1)
        mergeShape ~> sinkShape

        // Step 4 - return the shape
        ClosedShape
      }
    )
    // run the graph and materialize it
    val graph = switchJoinStrategies.run()
  }
}
