package org.github.felipegutierrez.explore.akka.classic.streams.graphs

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Broadcast, Concat, Flow, GraphDSL, Sink, Source}
import akka.stream.{FlowShape, SinkShape, SourceShape}

object StreamOpenGraphs extends App {

  run()

  def run() = {
    implicit val system = ActorSystem("StreamOpenGraphs")

    /*
    A composite source that concatenates 2 sources
    - emits ALL the elements from the first source
    - then ALL the elements from the second
   */

    val firstSource = Source(1 to 10)
    val secondSource = Source(42 to 1000)

    // a complex source
    // step 1
    val sourceGraph = Source.fromGraph(
      GraphDSL.create() { implicit builder =>
        import GraphDSL.Implicits._

        // step 2: declaring components
        val concat = builder.add(Concat[Int](2))

        // step 3: tying them together
        firstSource ~> concat
        secondSource ~> concat

        // step 4
        SourceShape(concat.out)
      }
    )
    // sourceGraph.to(Sink.foreach(println)).run()

    // a Complex sink
    val sink1 = Sink.foreach[Int](x => println(s"Meaningful thing 1: $x"))
    val sink2 = Sink.foreach[Int](x => println(s"Meaningful thing 2: $x"))

    // step 1
    val sinkGraph = Sink.fromGraph(
      GraphDSL.create() { implicit builder =>
        import GraphDSL.Implicits._

        // step 2 - add a broadcast
        val broadcast = builder.add(Broadcast[Int](2))

        // step 3 - tie components together
        broadcast ~> sink1
        broadcast ~> sink2

        // step 4 - return the shape
        SinkShape(broadcast.in)
      }
    )

    firstSource.to(sinkGraph).run()

    // Exercise
    // Write your own flow that's composed of two other flows
    //    * - one that adds 1 to a number
    //    * - one that does number * 10
    val incrementer = Flow[Int].map(_ + 1)
    val multiplier = Flow[Int].map(_ * 10)
    // step 1
    val complexFlowIncrementer = Flow.fromGraph(
      GraphDSL.create() { implicit builder =>
        import GraphDSL.Implicits._

        // step 2 - define auxiliary SHAPES
        val incrementerShape = builder.add(incrementer)
        val multiplierShape = builder.add(multiplier)

        // step 3 - connect the SHAPES
        incrementerShape ~> multiplierShape

        FlowShape(incrementerShape.in, multiplierShape.out) // SHAPE
      }
    )

    firstSource.via(complexFlowIncrementer).to(Sink.foreach(println)).run()

    /**
     * Exercise: flow from a sink and a source?
     */
    def fromSinkAndSource[A, B](sink: Sink[A, _], source: Source[B, _]): Flow[A, B, _] =
    // step 1
      Flow.fromGraph(
        GraphDSL.create() { implicit builder =>
          // step 2: declare the SHAPES
          val sourceShape = builder.add(source)
          val sinkShape = builder.add(sink)

          // step 3
          // step 4 - return the shape
          FlowShape(sinkShape.in, sourceShape.out)
        }
      )

    val f = Flow.fromSinkAndSourceCoupled(Sink.foreach[String](println), Source(1 to 10))
  }
}
