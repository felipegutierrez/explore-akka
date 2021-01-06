package org.github.felipegutierrez.explore.akka.classic.streams.graphs

import akka.actor.ActorSystem
import akka.stream.ClosedShape
import akka.stream.scaladsl.{GraphDSL, Merge, Partition, RunnableGraph, Sink, Source}

import scala.concurrent.duration._

object AkkaStreamWithKinesis extends App {
  implicit val system = ActorSystem("AkkaStreamWithKinesisSystem")

  val source = Source(1 to 1000).throttle(5, 1 second)
  val sink = Sink.foreach[Int](println(_))

  val graph = RunnableGraph.fromGraph(
    GraphDSL.create(source, sink)
    ((source, sink) => Seq(source, sink)) {
      implicit builder =>
        (source, sink) =>
          import akka.stream.scaladsl.GraphDSL.Implicits._
          val partition = builder.add(Partition[Int](2, flow => {
            1
          }))
          val merge = builder.add(Merge[Int](2))

          source ~> partition.in
          partition.out(0) ~> merge.in(0)
          partition.out(1) ~> merge.in(1)
          merge.out ~> sink

          ClosedShape
    }).run()
}
