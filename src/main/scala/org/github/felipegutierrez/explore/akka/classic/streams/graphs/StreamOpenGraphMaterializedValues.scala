package org.github.felipegutierrez.explore.akka.classic.streams.graphs

import akka.actor.ActorSystem
import akka.stream.SinkShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Sink, Source}

object StreamOpenGraphMaterializedValues extends App {

  run()

  def run() = {
    implicit val system = ActorSystem("StreamOpenGraphMaterializedValues")

    val wordSource = Source(List("Akka", "stream", "is", "awesome", "and", "I", "am", "loving", "it"))
    val printer = Sink.foreach[String](println)
    val counter = Sink.fold[Int, String](0)((count, value) => count + 1)

    // Step 1
    val complexWordSink = Sink.fromGraph(
      GraphDSL.create() { implicit builder =>
        import GraphDSL.Implicits._
        // step 2 - shapes
        val broadcast = builder.add(Broadcast[String](2))
        val lowercaseFlow = builder.add(Flow[String].filter(word => word == word.toLowerCase))
        val shortStringFilter = builder.add(Flow[String].filter(_.length < 5))

        // step 3 - tie components together
        broadcast ~> lowercaseFlow ~> printer
        broadcast ~> shortStringFilter ~> counter

        // Step 4 - the Shape
        SinkShape(broadcast.in)
      }
    )
  }
}
