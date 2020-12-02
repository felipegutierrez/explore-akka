package org.github.felipegutierrez.explore.akka.classic.streams.graphs

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ClosedShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source, Zip}

object StreamGraphBasics extends App {

  run()

  def run() = {
    implicit val system = ActorSystem("StreamGraphBasics")

    val input = Source(1 to 1000)
    val incrementer = Flow[Int].map { x =>
      print(s"$x + 1 | ")
      x + 1
    }
    val multiplier = Flow[Int].map { x =>
      print(s"$x * 10 ")
      x * 10
    }
    val output = Sink.foreach[(Int, Int)] { x =>
      println(s"result: $x")
    }

    // Step 1 - setting up the fundamental for a stream graph
    val graph = RunnableGraph.fromGraph(
      GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
        // Step 2 - add necessary components of this graph
        // fan-out operator for 1 input and 2 outputs
        val broadcast = builder.add(Broadcast[Int](2))
        // fan-in operator for 2 inputs and 1 output
        val zip = builder.add(Zip[Int, Int])

        // Step 3 - tying up the components
        import GraphDSL.Implicits._ // brings some nice operator into the scope
        input ~> broadcast
        broadcast.out(0) ~> incrementer ~> zip.in0
        broadcast.out(1) ~> multiplier ~> zip.in1
        zip.out ~> output

        // Step 4 - return a closed shape
        ClosedShape
      } // graph
    ) // runnable graph

    // run the graph and materialize it
    graph.run()
  }
}
