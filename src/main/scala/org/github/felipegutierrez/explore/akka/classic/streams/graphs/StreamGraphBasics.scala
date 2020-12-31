package org.github.felipegutierrez.explore.akka.classic.streams.graphs

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ClosedShape
import akka.stream.scaladsl.{Balance, Broadcast, Flow, GraphDSL, Merge, RunnableGraph, Sink, Source, Zip}

import scala.concurrent.duration._

object StreamGraphBasics {

//  def main(args: Array[String]): Unit = {
//    run()
//    run2()
//    run3()
//  }

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

  def run2() = {
    implicit val system = ActorSystem("StreamGraphBasics")

    val input = Source(1 to 1000)
    val incrementer = Flow[Int].map { x =>
      print(s"$x + 2 | ")
      x + 2
    }
    val multiplier = Flow[Int].map { x =>
      print(s"$x * 20 ")
      x * 20
    }
    val output1 = Sink.foreach[Int] { x =>
      println(s"output 1: $x")
    }
    val output2 = Sink.foreach[Int] { x =>
      println(s"output 2: $x")
    }

    // Step 1 - setting up the fundamental for a stream graph
    val graph = RunnableGraph.fromGraph(
      GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
        // Step 2 - add necessary components of this graph
        // fan-out operator for 1 input and 2 outputs
        val broadcast = builder.add(Broadcast[Int](2))
        // fan-in operator for 2 inputs and 1 output
        // val zip = builder.add(Zip[Int, Int])

        // Step 3 - tying up the components
        import GraphDSL.Implicits._ // brings some nice operator into the scope
        input ~> broadcast
        broadcast.out(0) ~> incrementer ~> output1
        broadcast.out(1) ~> multiplier ~> output2
        //zip.out ~> output

        // Step 4 - return a closed shape
        ClosedShape
      } // graph
    ) // runnable graph

    // run the graph and materialize it
    graph.run()
  }

  def run3() = {
    implicit val system = ActorSystem("StreamGraphBasics")

    val fastSource = Source(1 to 1000).throttle(100, 1 second)
    val slowSource = Source(1 to 1000).throttle(10, 1 second)
    val sink0 = Sink.fold[Int, Int](0)((count, element) => {
      println(s"sink 0, number of elements: $count")
      count + 1
    })
    val sink1 = Sink.fold[Int, Int](0)((count, element) => {
      println(s"sink 1, number of elements: $count")
      count + 1
    })

    // Step 1 - setting up the fundamental for a stream graph
    val graph = RunnableGraph.fromGraph(
      GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
        // Step 2 - add necessary components of this graph
        // fan-in operator for 2 inputs and 1 output
        val merge = builder.add(Merge[Int](2))
        // fan-out operator for 1 input and 2 outputs
        val balance = builder.add(Balance[Int](2))

        // Step 3 - tying up the components
        import GraphDSL.Implicits._ // brings some nice operator into the scope
        fastSource ~> merge.in(0)
        slowSource ~> merge.in(1)
        merge.out ~> balance.in
        balance.out(0) ~> sink0
        balance.out(1) ~> sink1

        // Step 4 - return a closed shape
        ClosedShape
      } // graph
    ) // runnable graph

    // run the graph and materialize it
    graph.run()
  }
}
