package org.github.felipegutierrez.explore.akka.classic.streams.graphs

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, MergePreferred, RunnableGraph, Sink, Source, Zip}
import akka.stream.{ClosedShape, OverflowStrategy, UniformFanInShape}

import scala.concurrent.duration._

object StreamOpenGraphsCycles {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //    run1()
  //    run2()
  //  }

  def run() = {
    implicit val system = ActorSystem("StreamOpenGraphsCycles")

    val accelerator = GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._
      val sourceShape = builder.add(Source(1 to 100).throttle(1, 1 second))
      val mergeShape = builder.add(MergePreferred[Int](1))
      val incrementerShape = builder.add(Flow[Int].map { x =>
        println(s"accelerating $x")
        Thread.sleep(1000)
        x + 1
      })

      sourceShape ~> mergeShape ~> incrementerShape
      mergeShape.preferred <~ incrementerShape

      ClosedShape
    }

    val runnableGraph = RunnableGraph.fromGraph(accelerator).run()

  }

  def run1() = {
    /*
      Solution 2: buffers
      cycles risk deadlocking
      - add bounds to the number of elements in the cycle
      boundedness vs liveness
     */
    implicit val system = ActorSystem("StreamOpenGraphsCycles1")
    val bufferedRepeater = GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._

      val sourceShape = builder.add(Source(1 to 100))
      val mergeShape = builder.add(Merge[Int](2))
      val repeaterShape = builder.add(Flow[Int].buffer(10, OverflowStrategy.dropHead).map { x =>
        println(s"Accelerating $x")
        Thread.sleep(100)
        x
      })

      sourceShape ~> mergeShape ~> repeaterShape
      mergeShape <~ repeaterShape

      ClosedShape
    }

    RunnableGraph.fromGraph(bufferedRepeater).run()
  }

  def run2() = {
    /**
     * Challenge: create a fan-in shape
     * - two inputs which will be fed with EXACTLY ONE number (1 and 1)
     * - output will emit an INFINITE FIBONACCI SEQUENCE based off those 2 numbers
     * 1, 2, 3, 5, 8 ...
     *
     * Hint: Use ZipWith and cycles, MergePreferred
     */
    implicit val system = ActorSystem("StreamOpenGraphsCyclesFibonacci")

    val fibonacciGenerator = GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._

      val zipShape = builder.add(Zip[BigInt, BigInt])
      val mergeShape = builder.add(MergePreferred[(BigInt, BigInt)](1))
      val fibonacciShape = builder.add(Flow[(BigInt, BigInt)].map { pair =>
        val last = pair._1
        val previous = pair._2
        Thread.sleep(1000)
        (last + previous, last)
      })
      val broadcastShape = builder.add(Broadcast[(BigInt, BigInt)](2))
      val extractLastShape = builder.add(Flow[(BigInt, BigInt)].map(pair => pair._1))

      zipShape.out ~> mergeShape ~> fibonacciShape ~> broadcastShape ~> extractLastShape
      mergeShape.preferred <~ broadcastShape

      UniformFanInShape(extractLastShape.out, zipShape.in0, zipShape.in1)
    }

    val fiboGraph = RunnableGraph.fromGraph(
      GraphDSL.create() { implicit builder =>
        import GraphDSL.Implicits._
        val source01 = builder.add(Source.single[BigInt](1))
        val source02 = builder.add(Source.single[BigInt](1))
        val sink = builder.add(Sink.foreach[BigInt](println))
        val fibo = builder.add(fibonacciGenerator)
        source01 ~> fibo.in(0)
        source02 ~> fibo.in(1)
        fibo ~> sink
        ClosedShape
      }
    )

    fiboGraph.run()
  }
}
