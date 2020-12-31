package org.github.felipegutierrez.explore.akka.classic.streams.graphs

import akka.actor.ActorSystem
import akka.stream.FlowShape
import akka.stream.scaladsl.{Flow, GraphDSL, Merge, Partition, Sink, Source}

import scala.concurrent.duration._

object StreamOpenGraphsWithMultipleFlows {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    implicit val system = ActorSystem("StreamOpenGraphsWithMultipleFlows")

    val slowSource = Source(1 to 1000).throttle(5, 1 second)
    val INC = 5
    val MULTI = 10
    val DIVIDE = 2

    val incrementer = Flow[Int].map { x =>
      val result = x + INC
      print(s" | incrementing $x + $INC -> $result")
      result
    }
    val multiplier = Flow[Int].map { x =>
      val result = x * MULTI
      print(s" | multiplying $x * $MULTI -> $result")
      result
    }
    val divider = Flow[Int].map { x =>
      val result = x / DIVIDE
      print(s" | dividing $x / $DIVIDE -> $result")
      result
    }

    def isMultipleOf(value: Int, multiple: Int): Int = if (value % multiple == 0) 0 else 1

    /** Write an open graph that can decide to chose different Flows at runtime */
    // Step 1 - setting up the fundamental for a stream graph
    val complexFlowIncrementer = Flow.fromGraph(
      GraphDSL.create() { implicit builder =>
        import GraphDSL.Implicits._

        // Step 2 - add necessary components of this graph
        val incrementerShape = builder.add(incrementer)
        val multiplierShape = builder.add(multiplier)
        val dividerShape = builder.add(divider)
        // add partition and merge
        val partition = builder.add(Partition[Int](2, isMultipleOf(_, 10)))
        val merge = builder.add(Merge[Int](2))

        // Step 3 - tying up the components
        incrementerShape ~> partition
        partition.out(0) ~> dividerShape ~> merge.in(0)
        partition.out(1) ~> multiplierShape ~> merge.in(1)

        // Step 4 - return the shape
        FlowShape(incrementerShape.in, merge.out)
      }
    )
    // run the graph and materialize it
    val graph = slowSource
      .via(complexFlowIncrementer)
      .to(Sink.foreach(x => println(s" | result: $x")))
    graph.run()
  }
}
