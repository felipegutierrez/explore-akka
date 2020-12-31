package org.github.felipegutierrez.explore.akka.classic.streams.graphs

import akka.actor.ActorSystem
import akka.stream.scaladsl.{GraphDSL, RunnableGraph, Sink, Source, ZipWith}
import akka.stream.{ClosedShape, UniformFanInShape}

object StreamOpenGraphsWithUniformShapes {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    implicit val system = ActorSystem("StreamOpenGraphsWithUniformShapes")

    // Step 1 - create the GraphDSL
    val max3StaticGraph = GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._
      // Step 2 - add necessary components of this graph
      val max1 = builder.add(ZipWith[Int, Int, Int]((a, b) => Math.max(a, b)))
      val max2 = builder.add(ZipWith[Int, Int, Int]((a, b) => Math.max(a, b)))

      // Step 3 - tying up the components
      max1.out ~> max2.in0

      // Step 4 - return a closed shape
      UniformFanInShape(max2.out, max1.in0, max1.in1, max2.in1)
    }

    val source1 = Source(1 to 10)
    val source2 = Source((1 to 10).map(_ => 5))
    val source3 = Source((1 to 10).reverse)
    val maxSink = Sink.foreach[Int](x => println(s"max: $x"))

    // Define the runnable graph
    // Step 1
    val max3RunnableGraph = RunnableGraph.fromGraph(
      GraphDSL.create() { implicit builder =>
        import GraphDSL.Implicits._
        // step 2
        val max3Shape = builder.add(max3StaticGraph)
        // step 3
        source1 ~> max3Shape.in(0)
        source2 ~> max3Shape.in(1)
        source3 ~> max3Shape.in(2)
        max3Shape.out ~> maxSink
        // step 4
        ClosedShape
      }
    )

    max3RunnableGraph.run()
  }
}
