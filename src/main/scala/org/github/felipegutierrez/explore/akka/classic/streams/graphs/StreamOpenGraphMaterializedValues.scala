package org.github.felipegutierrez.explore.akka.classic.streams.graphs

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Keep, Sink, Source}
import akka.stream.{FlowShape, SinkShape}

import scala.concurrent.Future
import scala.util.{Failure, Success}

object StreamOpenGraphMaterializedValues extends App {

  run()

  def run() = {
    implicit val system = ActorSystem("StreamOpenGraphMaterializedValues")

    val wordSource = Source(List("Akka", "stream", "is", "awesome", "and", "I", "am", "loving", "it"))
    val printer = Sink.foreach[String](println)
    val counter = Sink.fold[Int, String](0)((count, value) => count + 1)

    // Step 1
    val complexWordSink = Sink.fromGraph(
      GraphDSL.create(printer, counter)((printerMatValue, counterMatValue) => counterMatValue) { implicit builder =>
        (printerShape, counterShape) => {
          import GraphDSL.Implicits._
          // step 2 - shapes
          val broadcast = builder.add(Broadcast[String](2))
          val lowercaseFlow = builder.add(Flow[String].filter(word => word == word.toLowerCase))
          val shortStringFilter = builder.add(Flow[String].filter(_.length < 3))

          // step 3 - tie components together
          broadcast ~> lowercaseFlow ~> printerShape
          broadcast ~> shortStringFilter ~> counterShape

          // Step 4 - the Shape
          SinkShape(broadcast.in)
        }
      }
    )

    import system.dispatcher
    val shortStringsCounterFuture: Future[Int] = wordSource.toMat(complexWordSink)(Keep.right).run()
    shortStringsCounterFuture.onComplete {
      case Success(value) => println(s"total of words: $value")
      case Failure(exception) => println(s"fail because: $exception")
    }

    val simpleSource = Source(1 to 42)
    val simpleFlow = Flow[Int].map(x => x)
    val simpleSink = Sink.ignore

    val enhancedFlowCountFuture = simpleSource
      .viaMat(enhanceFlow(simpleFlow))(Keep.right)
      .toMat(simpleSink)(Keep.left)
      .run()

    enhancedFlowCountFuture.onComplete {
      case Success(count) => println(s"$count elements went through the enhanced flow")
      case _ => println("Something failed")
    }
  }

  def enhanceFlow[A, B](flow: Flow[A, B, _]): Flow[A, B, Future[Int]] = {

    val sinkCounter = Sink.fold[Int, B](0)((count, value) => count + 1)

    // Step 1
    val complexWordSink = Flow.fromGraph(
      GraphDSL.create(sinkCounter) { implicit builder =>
        counterSinkShape =>
          import GraphDSL.Implicits._
          // step 2 - shapes
          val broadcast = builder.add(Broadcast[B](2))
          val originalFlowShape = builder.add(flow)
          // step 3 - tie components together
          originalFlowShape ~> broadcast ~> counterSinkShape
          // step 4 - the Shape
          FlowShape(originalFlowShape.in, broadcast.out(1))
      }
    )
    complexWordSink
  }
}
